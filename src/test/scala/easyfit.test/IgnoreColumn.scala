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

class IgnoreColumn extends EasyTest
{
  "A Query table" should "format an ignored column correctly" in
    {
      val (in, out, expected) = getTestData()

      runQueryTest(in, out, expected)
    }

  "A Row table" should "format ignored columns correctly" in
    {
      val (in, out, expected) = getTestData()

      runRowTest(in, out.iterator, expected)
    }

  def getTestData(): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq("C1!", "C2")

    val (in1, out1) = (Seq("c1r1", "c2r1"), Seq("c1r1", "c2r1"))
    val (in2, out2) = (Seq("c1r2", "c2r2"), Seq("X", "c2r2"))
    val (in3, out3) = (Seq("$v1=", "c2r3"), Seq("vv", "c2r3"))
    val (in4, out4) = (Seq("$v1", "c2r4"), Seq("c1r4", "c2r4"))
    val (in5, out5) = (Seq("$v1", "c2r5"), Seq("vv", "c2r5"))

    val exp1 = Seq("ignore", "pass") // C1!	: C1!
    val exp2 = Seq("pass: c1r1", "pass: c2r1") // c1r1	: c1r1
    val exp3 = Seq("ignore: [c1r2] != [X]", "pass: c2r2") // c1r2	: X
    val exp4 = Seq("pass: $v1 <- [vv]", "pass: c2r3") // $v1=	: vv
    val exp5 = Seq("ignore: $v1 [vv] != [c1r4]", "pass: c2r4") // $v1	: c1r4
    val exp6 = Seq("pass: $v1 [vv]", "pass: c2r5") // $v1	: vv

    return (
      Seq(header, in1, in2, in3, in4, in5),
      Seq(out1, out2, out3, out4, out5),
      Seq(exp1, exp2, exp3, exp4, exp5, exp6)
      )
  }
}