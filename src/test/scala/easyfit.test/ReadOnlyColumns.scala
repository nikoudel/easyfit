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

import easyfit.cells.{RowCell, Header}

class ReadOnlyColumns extends EasyTest
{
  "A Row table" should "clear question-column values before passing them to SUT" in
    {
      val (in, out, expected) = getTestData()

      runRowTest(in, out.iterator, expected, ensureFirstColumnIsEmpty)
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
    row(0).getSutInput(true) should be("")

    header(1).Value should be("C2")
    row(1).getSutInput(false) should not be ("")
  }
}