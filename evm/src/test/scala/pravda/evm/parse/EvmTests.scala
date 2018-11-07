package pravda.evm.parse

import fastparse.byte.all._
import pravda.evm._
import pravda.evm.EVM._
import utest._

object EvmTests extends TestSuite {

  val tests = Tests {
    'SimpleStorage - {
      val bytes = evm.readSolidityBinFile("SimpleStorage.bin")

      Parser(bytes) ==>
        Right(
          List(
            Push(hex"0x80"),
            Push(hex"0x40"),
            MStore,
            CallValue,
            Dup(1),
            IsZero,
            Push(hex"0x0010"),
            JumpI,
            Push(hex"0x00"),
            Dup(1),
            Revert,
            JumpDest,
            Pop,
            Push(hex"0xdf"),
            Dup(1),
            Push(hex"0x001f"),
            Push(hex"0x00"),
            CodeCopy,
            Push(hex"0x00"),
            Return,
            Stop,
            Push(hex"0x80"),
            Push(hex"0x40"),
            MStore,
            Push(hex"0x04"),
            CallDataSize,
            Lt,
            Push(hex"0x49"),
            JumpI,
            Push(hex"0x00"),
            CallDataLoad,
            Push(hex"0x0100000000000000000000000000000000000000000000000000000000"),
            Swap(1),
            Div,
            Push(hex"0xffffffff"),
            And,
            Dup(1),
            Push(hex"0x60fe47b1"),
            Eq,
            Push(hex"0x4e"),
            JumpI,
            Dup(1),
            Push(hex"0x6d4ce63c"),
            Eq,
            Push(hex"0x78"),
            JumpI,
            JumpDest,
            Push(hex"0x00"),
            Dup(1),
            Revert,
            JumpDest,
            CallValue,
            Dup(1),
            IsZero,
            Push(hex"0x59"),
            JumpI,
            Push(hex"0x00"),
            Dup(1),
            Revert,
            JumpDest,
            Pop,
            Push(hex"0x76"),
            Push(hex"0x04"),
            Dup(1),
            CallDataSize,
            Sub,
            Dup(2),
            Add,
            Swap(1),
            Dup(1),
            Dup(1),
            CallDataLoad,
            Swap(1),
            Push(hex"0x20"),
            Add,
            Swap(1),
            Swap(3),
            Swap(2),
            Swap(1),
            Pop,
            Pop,
            Pop,
            Push(hex"0xa0"),
            Jump,
            JumpDest,
            Stop,
            JumpDest,
            CallValue,
            Dup(1),
            IsZero,
            Push(hex"0x83"),
            JumpI,
            Push(hex"0x00"),
            Dup(1),
            Revert,
            JumpDest,
            Pop,
            Push(hex"0x8a"),
            Push(hex"0xaa"),
            Jump,
            JumpDest,
            Push(hex"0x40"),
            MLoad,
            Dup(1),
            Dup(3),
            Dup(2),
            MStore,
            Push(hex"0x20"),
            Add,
            Swap(2),
            Pop,
            Pop,
            Push(hex"0x40"),
            MLoad,
            Dup(1),
            Swap(2),
            Sub,
            Swap(1),
            Return,
            JumpDest,
            Dup(1),
            Push(hex"0x00"),
            Dup(2),
            Swap(1),
            SStore,
            Pop,
            Pop,
            Jump,
            JumpDest,
            Push(hex"0x00"),
            Dup(1),
            SLoad,
            Swap(1),
            Pop,
            Swap(1),
            Jump,
            Stop
          ))

    }
  }
}
