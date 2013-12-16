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

class MissingColumns extends EasyTest
{
  "A Query table" should "format missing columns correctly" in
    {
      val (in, out, expected) = createTestData()

      runQueryTest(in, out, expected, null, setHeaderMissing)
    }

  "A Row table" should "format missing columns correctly" in
    {
      val (in, out, expected) = createTestData()

      runRowTest(in, out.iterator, expected, setHeaderMissing)
    }

  def createTestData(): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq("C1", "C2")

    val (in1, out1) = (Seq("c1r1", "c2r1"), Seq("", "c2r1"))
    val (in2, out2) = (Seq("c1r2", "c2r2"), Seq("", "c2r2"))

    val exp1 = Seq("fail: C1 (MISSING)", "pass")
    val exp2 = Seq("ignore: [c1r1] != []", "pass: c2r1")
    val exp3 = Seq("ignore: [c1r2] != []", "pass: c2r2")

    (
      Seq(header, in1, in2),
      Seq(out1, out2),
      Seq(exp1, exp2, exp3)
    )
  }

  private def setHeaderMissing(header: Seq[Header], row: Seq[RowCell])
  {
    header(0).setMissing()
  }

  private def setHeaderMissing(header: Seq[Header])
  {
    header(0).setMissing()
  }
}