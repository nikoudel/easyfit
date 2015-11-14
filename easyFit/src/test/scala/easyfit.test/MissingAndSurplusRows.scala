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
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class MissingAndSurplusRows extends EasyTest
{
  "A Query table" should "report missing rows" in
    {
      val header = Seq("C1", "C2")
      val in1 = Seq("qwe", "ert")
      val in2 = Seq("wer", "rty")

      val out1 = Seq("qwe", "ert")

      val exp1 = Seq("pass", "pass")
      val exp2 = Seq("pass: qwe", "pass: ert")
      val exp3 = Seq("fail: wer (MISSING)", "fail: rty (MISSING)")

      val inputTable = Seq(header, in1, in2)
      val resultTable = Seq(out1)
      val expectedTable = Seq(exp1, exp2, exp3)

      runQueryTest(inputTable, resultTable, expectedTable)
    }

  it should "report surplus rows" in
    {
      val header = Seq("C1", "C2")
      val in1 = Seq("qwe", "wer")

      val out1 = Seq("qwe", "wer")
      val out2 = Seq("ert", "rty")

      val exp1 = Seq("pass", "pass")
      val exp2 = Seq("pass: qwe", "pass: wer")
      val exp3 = Seq("fail: ert (SURPLUS)", "fail: rty (SURPLUS)")

      val inputTable = Seq(header, in1)
      val resultTable = Seq(out1, out2)
      val expectedTable = Seq(exp1, exp2, exp3)

      runQueryTest(inputTable, resultTable, expectedTable)
    }
}