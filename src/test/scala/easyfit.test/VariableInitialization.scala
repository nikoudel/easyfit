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

class VariableInitialization extends EasyTest
{
  "A Query table" should "initialize and redefine a variable" in
    {
      val (in, out, expected) = createTestData()

      runQueryTest(in, out, expected)
    }

  "A Row table" should "initialize and redefine a variable" in
    {
      val (in, out, expected) = createTestData()

      runRowTest(in, out.iterator, expected)
    }

  def createTestData(): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq("C1")

    val (in1, out1) = (Seq("$v1="), Seq("123"))
    val (in2, out2) = (Seq("$v1"), Seq("X"))
    val (in3, out3) = (Seq("$v1="), Seq("456"))
    val (in4, out4) = (Seq("$v1"), Seq("123"))
    val (in5, out5) = (Seq("$v1"), Seq("456"))

    val exp1 = Seq("pass") //header
    val exp2 = Seq("pass: $v1 <- [123]")
    val exp3 = Seq("fail: $v1 [123] != [X]")
    val exp4 = Seq("pass: $v1 <- [456]")
    val exp5 = Seq("fail: $v1 [456] != [123]")
    val exp6 = Seq("pass: $v1 [456]")

    (
      Seq(header, in1, in2, in3, in4, in5),
      Seq(out1, out2, out3, out4, out5),
      Seq(exp1, exp2, exp3, exp4, exp5, exp6)
    )
  }
}