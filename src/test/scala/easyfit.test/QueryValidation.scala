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

class QueryValidation extends EasyTest
{
  "A Query table" should "fail when created without an Action" in
    {
      failMessage(() => new Query("").doTable(null)) should be(MissingAction)
    }

  it should "fail when created without a header" in
    {
      failMessage(() => new Query(action).doTable(null)) should be(QueryTableMissingHeader)
    }

  it should "fail when created without columns" in
    {
      val row = seqAsJavaList(Seq[String]())
      val table = seqAsJavaList(Seq(row, row))

      failMessage(() => new Query(action).doTable(table)) should be(MissingColumns)
    }

  it should "fail when created with empty columns" in
    {
      val row = seqAsJavaList(Seq("C1", ""))
      val table = seqAsJavaList(Seq(row, row))

      failMessage(() => new Query(action).doTable(table)) should be(InvalidColumnName)
    }

  it should "fail when a header doesn't match a row" in
    {
      val header = Seq("C1!")
      val (in1, out1) = (Seq("", ""), Seq("", ""))
      val exp1 = Seq() // should fail before getting to value matching

      val f = () => runQueryTest(Seq(header, in1), Seq(out1), Seq(exp1))

      failMessage(f) should be(InvalidColumnCount)
    }

  it should "throw a StopTestException if there is an undefined variable" in
    {
      val header = Seq("C1")
      val (in1, out1) = (Seq("$undefined"), Seq("123"))
      val exp1 = Seq() // should fail before getting to value matching

      val f = () => runQueryTest(Seq(header, in1), Seq(out1), Seq(exp1))

      failMessage(f) should be(UndefinedVariable + ": " + "$undefined")
    }
}