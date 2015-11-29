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

import easyfit._
import easyfit.cells.{RowCell, Header}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ReadOnlyColumns extends EasyTest
{
  "A Row table" should "clear question-column values before passing them to SUT" in
    {
      val (in, out, expected) = getTestData()

      runRowTest(in, out.iterator, expected, ensureFirstColumnIsEmpty)
    }

  it should "not apply converters to read-only columns" in
    {
      Store.setConverter("ac", new AdderConverter(" + 1", " + 2"))

      val (in, out, expected) = getTestDataWithConverter()

      runRowTest(in, out.iterator, expected, ensureConverterColumnIsEmpty)
    }

  private def getTestData(): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq("C1?", "C2")

    val (in1, out1) = (Seq("$v1=", "r1c2"), Seq("r1c1", "r1c2"))
    val (in2, out2) = (Seq("r2c1", "r2c2"), Seq("r2c1", "r2c2"))

    val exp1 = Seq("pass", "pass")
    val exp2 = Seq("pass: $v1 <- [r1c1]", "pass: r1c2")
    val exp3 = Seq("pass: r2c1", "pass: r2c2")

    return (
      Seq(header, in1, in2),
      Seq(out1, out2),
      Seq(exp1, exp2, exp3)
      )
  }

  private def ensureFirstColumnIsEmpty(header: Seq[Header], row: Seq[RowCell])
  {
    header(0).Value should be("C1?")
    row(0).getSutInput(true) should be(null)

    header(1).Value should be("C2")
    row(1).getSutInput(false).length should be (4)
  }

  private def getTestDataWithConverter(): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq("ac:C1?", "ac:C2?")

    val (in1, out1) = (Seq("$v1=", "r1c2"), Seq("r1c1", "r1c2"))

    val exp1 = Seq("pass", "pass")
    val exp2 = Seq("pass: $v1 <- [r1c1 + 2]", "fail: [r1c2] != [r1c2 + 2]")

    return (
      Seq(header, in1),
      Seq(out1),
      Seq(exp1, exp2)
      )
  }

  private def ensureConverterColumnIsEmpty(header: Seq[Header], row: Seq[RowCell])
  {
    header(0).Value should be("ac:C1?")
    header(1).Value should be("ac:C2?")

    row(0).getSutInput(true) should be(null)
    row(1).getSutInput(true) should be(null)
  }
}
