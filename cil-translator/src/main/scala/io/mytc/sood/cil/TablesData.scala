package io.mytc.sood.cil

import io.mytc.sood.cil.PE.Info._
import utils._
import fastparse.byte.all._

object TablesData {
  import TablesInfo._

  sealed trait TableRowData
  final case class MethodDefData(name: String) extends TableRowData
  final case class MemberRefData(cls: Long, name: String, signature: Bytes) extends TableRowData
  final case class FieldData(flags: Short, name: String, signature: Bytes) extends TableRowData
  case object Ignored extends TableRowData

  def fromInfo(peData: PeData): Validated[Seq[Seq[TableRowData]]] = {
    peData.tables.map(ts =>
      ts.map {
        case MethodDefRow(_, _, _, nameIdx, _, _) => Heaps.string(peData.stringHeap, nameIdx).map(MethodDefData)
        case MemberRefRow(cls, nameIdx, signatureIdx) => for {
          name <- Heaps.string(peData.stringHeap, nameIdx)
          signature <- Heaps.blob(peData.blobHeap, signatureIdx)
        } yield MemberRefData(cls, name, signature)
        case FieldRow(flags, nameIdx, signatureIdx) => for {
          name <- Heaps.string(peData.stringHeap, nameIdx)
          signature <- Heaps.blob(peData.blobHeap, signatureIdx)
        } yield FieldData(flags, name, signature)
        case _ => Right(Ignored)
      }.sequence
    ).sequence
  }
}
