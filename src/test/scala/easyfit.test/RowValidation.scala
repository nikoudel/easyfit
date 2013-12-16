/*******************************************************************************
  * Copyright (c) Nikolai Koudelia
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Nikolai Koudelia - initial API and implementation
  *******************************************************************************/
package easyfit.test

import scala.collection.JavaConversions.seqAsJavaList
import easyfit.Strings._
import easyfit.tables._

class RowValidation extends EasyTest
{
  "A Row table" should "fail when created without an Action" in
    {
      failMessage(() => new Row("").doTable(null)) should be(MissingAction)
    }

  it should "fail when created with less than two rows (header + operation row)" in
    {
      failMessage(() => new Row(action).doTable(null)) should be(RowTableMissingRows)
    }

  it should "fail when created without columns" in
    {
      val row = seqAsJavaList(Seq[String]())
      val table = seqAsJavaList(Seq(row, row))

      failMessage(() => new Row(action).doTable(table)) should be(MissingColumns)
    }

  it should "fail when created with empty columns" in
    {
      val row = seqAsJavaList(Seq("C1", ""))
      val table = seqAsJavaList(Seq(row, row))

      failMessage(() => new Row(action).doTable(table)) should be(InvalidColumnName)
    }

  it should "fail when a header doesn't match a row" in
    {
      val row1 = seqAsJavaList(Seq("C1"))
      val row2 = seqAsJavaList(Seq("", ""))
      val table = seqAsJavaList(Seq(row1, row2))

      failMessage(() => new Row(action).doTable(table)) should be(InvalidColumnCount)
    }

  it should "throw a StopTestException if there is an undefined variable" in
    {
      val header = Seq("C1")
      val (in1, out1) = (Seq("$undefined"), Seq("123"))
      val expHeaders = Seq("pass")
      val expRow1 = Seq("") // should fail before getting to value matching

      val f = () => runRowTest(Seq(header, in1), Seq(out1).iterator, Seq(expHeaders, expRow1))

      failMessage(f) should be(UndefinedVariable + ": " + "$undefined")
    }
}
