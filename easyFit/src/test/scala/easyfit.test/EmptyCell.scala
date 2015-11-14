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
class EmptyCell extends EasyTest
{
  "A Query table" should "format an empty cell as ignored" in
    {
      val (in, out, expected) = getTestData()

      runQueryTest(in, out, expected)
    }

  "A Row table" should "format an empty cell as ignored" in
    {
      val (in, out, expected) = getTestData()

      runRowTest(in, out.iterator, expected)
    }

  def getTestData(): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq("C1")

    val (in1, out1) = (Seq(""), Seq("c1r1"))
    val (in2, out2) = (Seq(""), Seq(""))

    val exp1 = Seq("pass")
    val exp2 = Seq("ignore: c1r1")
    val exp3 = Seq("ignore")

    return (
      Seq(header, in1, in2),
      Seq(out1, out2),
      Seq(exp1, exp2, exp3)
      )
  }
}