package pravda.evm.translate.opcode

import fastparse.byte.all._
import pravda.common.domain.Address
import pravda.evm.EVM._
import pravda.evm.EvmSandbox
import pravda.evm.abi.parse.AbiParser.AbiFunction
import pravda.vm.Data.Primitive.BigInt
import pravda.vm.Effect.{StorageRead, StorageWrite}
import pravda.vm.Error.UserError
import pravda.vm.VmSandbox
import utest._

object RunTests extends TestSuite {

  import VmSandbox.{ExpectationsWithoutWatts => Expectations}

  val tests = Tests {

    val preconditions = VmSandbox.Preconditions(balances = Map.empty, `watts-limit` = 1000L)
    val abi = List(AbiFunction(true, "", Nil, Nil, true, "", None))

    "DUP" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x80"), Dup(1)), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(128)), BigInt(scala.BigInt(128)))))

      EvmSandbox.runCode(preconditions, List(Push(hex"0x80"), Push(hex"0x60"), Dup(2)), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(128)), BigInt(scala.BigInt(96)), BigInt(scala.BigInt(128)))))
    }

    "SWAP" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x80"), Push(hex"0x60"), Swap(1)), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(96)), BigInt(scala.BigInt(128)))))

      EvmSandbox.runCode(preconditions, List(Push(hex"0x80"), Push(hex"0x60"), Push(hex"0x40"), Swap(2)), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(64)), BigInt(scala.BigInt(96)), BigInt(scala.BigInt(128)))))
    }

    //TODO tests with overflow

    "ADD" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x80"), Push(hex"0x60"), Add), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(224)))))
    }

    "MUL" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x80"), Push(hex"0x60"), Mul), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(12288)))))
    }

    "DIV" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x2"), Push(hex"0x60"), Div), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(48)))))
    }

    "MOD" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x2"), Push(hex"0x60"), Mod), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(0)))))

      EvmSandbox.runCode(preconditions, List(Push(hex"0x7"), Push(hex"0x60"), Mod), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(5)))))
    }

    "SUB" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x60"), Push(hex"0x80"), Sub), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(32)))))
    }

    "ADDMOD" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x3"), Push(hex"0x80"), Push(hex"0x60"), AddMod), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(2)))))
    }

    "MULMOD" - {
      EvmSandbox.runCode(preconditions, List(Push(hex"0x5"), Push(hex"0x80"), Push(hex"0x60"), MulMod), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(3)))))
    }

    "OR" - {
      val x = hex"0x01"
      val y = hex"0x02"
      val expectation = scala.BigInt(1, x.toArray) | scala.BigInt(1, y.toArray)
      EvmSandbox.runCode(preconditions, List(Push(x), Push(y), Or), abi) ==>
        Right(Expectations(stack = Seq(BigInt(expectation))))

      EvmSandbox.runCode(preconditions, List(Push(hex"0x1"), Push(hex"0x3"), Or), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(3)))))
    }

    "AND" - {
      val x = hex"0x01"
      val y = hex"0x02"
      val expectation = scala.BigInt(1, x.toArray) & scala.BigInt(1, y.toArray)

      EvmSandbox.runCode(preconditions, List(Push(x), Push(y), And), abi) ==>
        Right(Expectations(stack = Seq(BigInt(expectation))))
      EvmSandbox.runCode(preconditions, List(Push(hex"0x1"), Push(hex"0x3"), And), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(1)))))
    }

    "XOR" - {
      val x = hex"0x426"
      val y = hex"0x284"
      val expectation = scala.BigInt(1, x.toArray) ^ scala.BigInt(1, y.toArray)

      EvmSandbox.runCode(preconditions, List(Push(x), Push(y), Xor), abi) ==>
        Right(Expectations(stack = Seq(BigInt(expectation))))
    }

    "BYTE" - {
      val x = hex"0x426"
      var pos = hex"0x1f"

      EvmSandbox.runCode(preconditions, List(Push(x), Push(pos), Byte), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(x.last.toInt)))))

      pos = hex"0x1e"
      val expectation = x.reverse.tail.head.toInt
      EvmSandbox.runCode(preconditions, List(Push(x), Push(pos), Byte), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(expectation)))))
    }

    "LT" - {
      val x = hex"0x4"
      val y = hex"0x2"
      EvmSandbox.runCode(preconditions, List(Push(x), Push(y), Lt), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(1)))))

      EvmSandbox.runCode(preconditions, List(Push(y), Push(x), Lt), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(0)))))
    }

    "GT" - {
      val x = hex"0x4"
      val y = hex"0x2"
      EvmSandbox.runCode(preconditions, List(Push(x), Push(y), Gt), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(0)))))

      EvmSandbox.runCode(preconditions, List(Push(y), Push(x), Gt), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(1)))))
    }

    "EQ" - {
      val x = hex"0x4"
      val y = hex"0x2"
      EvmSandbox.runCode(preconditions, List(Push(x), Push(y), Eq), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(0)))))

      EvmSandbox.runCode(preconditions, List(Push(x), Push(x), Eq), abi) ==>
        Right(Expectations(stack = Seq(BigInt(scala.BigInt(1)))))
    }

    val `0` = hex"0x0"
    val `1` = hex"0x1"
    val `3` = hex"0x3"
    val `4` = hex"0x4"
    val `10` = hex"0xa"

    "JUMPS" - {
      "One jump" - {
        EvmSandbox.runAddressedCode(
          preconditions,
          List(
            0 -> Push(`4`),
            2 -> Push(`0`),
            3 -> CodeCopy,
            4 -> Push(`4`),
            4 -> Push(`3`),
            5 -> Jump,
            5 -> Push(`4`),
            6 -> Push(`4`),
            7 -> Push(`4`),
            7 -> JumpDest,
          ),
          abi
        ) ==> Right(
          Expectations(
            stack = Seq(BigInt(scala.BigInt(4)))
          ))

        EvmSandbox.runAddressedCode(
          preconditions,
          List(
            0 -> Push(`4`),
            2 -> Push(`0`),
            3 -> CodeCopy,
            4 -> Push(`4`),
            5 -> Push(`1`),
            6 -> Push(`3`),
            5 -> JumpI,
            5 -> Push(`4`),
            6 -> Push(`4`),
            7 -> Push(`4`),
            7 -> JumpDest,
          ),
          abi
        ) ==> Right(
          Expectations(
            stack = Seq(BigInt(scala.BigInt(4)))
          ))

        EvmSandbox.runAddressedCode(
          preconditions,
          List(
            0 -> Push(`4`),
            2 -> Push(`0`),
            3 -> CodeCopy,
            4 -> Push(`4`),
            5 -> Push(`0`),
            6 -> Push(`3`),
            5 -> JumpI,
            5 -> Push(`4`),
            6 -> Push(`4`),
            7 -> Push(`4`),
            7 -> JumpDest,
          ),
          abi
        ) ==> Right(
          Expectations(
            stack =
              Seq(BigInt(scala.BigInt(4)), BigInt(scala.BigInt(4)), BigInt(scala.BigInt(4)), BigInt(scala.BigInt(4)))
          ))
      }
      "Several jumps" - {
        EvmSandbox.runAddressedCode(
          preconditions,
          List(
            0 -> Push(`4`),
            2 -> Push(`0`),
            3 -> CodeCopy,
            4 -> Push(`4`),
            5 -> Push(`1`),
            6 -> Push(`3`),
            5 -> JumpI,
            5 -> Push(`4`),
            6 -> Push(`4`),
            7 -> Push(`4`),
            7 -> JumpDest,
            8 -> Push(`1`),
            9 -> Push(`10`),
            10 -> Jump,
            11 -> Push(`4`),
            12 -> Push(`4`),
            13 -> Push(`4`),
            14 -> JumpDest,
          ),
          abi
        ) ==> Right(
          Expectations(
            stack = Seq(BigInt(scala.BigInt(4)), BigInt(scala.BigInt(1)))
          ))

        EvmSandbox.runAddressedCode(
          preconditions,
          List(
            0 -> Push(`4`),
            2 -> Push(`0`),
            3 -> CodeCopy,
            4 -> Push(`4`),
            6 -> Push(`3`),
            5 -> Jump,
            5 -> Push(`4`),
            6 -> Push(`4`),
            7 -> Push(`4`),
            7 -> JumpDest,
            8 -> Push(`1`),
            9 -> Push(`10`),
            10 -> Jump,
            11 -> Push(`4`),
            12 -> Push(`4`),
            13 -> Push(`4`),
            14 -> JumpDest,
          ),
          abi
        ) ==> Right(
          Expectations(
            stack = Seq(BigInt(scala.BigInt(4)), BigInt(scala.BigInt(1)))
          ))
      }

      "Jump to bad destination" - {
        EvmSandbox.runAddressedCode(
          preconditions,
          List(
            0 -> Push(`4`),
            2 -> Push(`0`),
            3 -> CodeCopy,
            4 -> Push(`4`),
            4 -> Push(`4`),
            5 -> Jump,
            5 -> Push(`4`),
            6 -> Push(`4`),
            7 -> Push(`4`),
            7 -> JumpDest,
          ),
          abi
        ) ==>
          Right(
            Expectations(stack = Seq(BigInt(scala.BigInt(4)), BigInt(scala.BigInt(4))),
                         error = Some(UserError("Incorrect destination"))))

      }
    }

    "SSTORE SLOAD" - {

      EvmSandbox.runCode(preconditions,
                         List(
                           Push(`4`),
                           Push(`3`),
                           SStore,
                           Push(`3`),
                           SLoad
                         ),
                         abi) ==> Right(
        Expectations(
          effects = Seq(
            StorageWrite(Address.Void, BigInt(scala.BigInt(3)), None, BigInt(scala.BigInt(4))),
            StorageRead(Address.Void, BigInt(scala.BigInt(3)), Some(BigInt(scala.BigInt(4))))
          ),
          stack = Seq(BigInt(scala.BigInt(4)))
        ))
    }

  }

}
