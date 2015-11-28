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
import easyfit.tables.Converter
import easyfit.Strings._
import easyfit.cells.{RowCell, Header}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ConverterTests extends EasyTest
{
  "A Converter table" should "add converter objects" in
    {
      val converterTable = new Converter()
      val className = converterTable.getClass.getName

      val in = Seq(Seq(className, "easyfit.test.AdderConverter"))

      val tableResult = converterTable.doTable(asJavaArrayList(in))

      val expectedResult = Seq(Seq("pass", "pass"))

      compareTables(tableResult, asJavaArrayList(expectedResult))

      val converter = Store.getConverter(className)

      converter should not be null

      converter.convertExpected("ping") should be("ping: expected converter works!")
      converter.convertActual("ping") should be("ping: actual converter works!")
    }

  it should "fail on invalid class name" in
    {
      val in = Seq(Seq("f1", "invalid class name"))

      val f = () => new Converter().doTable(asJavaArrayList(in))

      failMessage(f) should be(InvalidConverterClass + ": 'invalid class name'")
    }

  it should "fail on a converter not implementing the interface" in
    {
      val converterClassName = this.getClass.getName
      val interfaceName = classOf[easyfit.IConverter].getName

      val in = Seq(Seq("test converter", converterClassName))

      val f = () => new Converter().doTable(asJavaArrayList(in))

      failMessage(f) should be(String.format(
        NotInstanceOfConverter, converterClassName, interfaceName))
    }

  it should "fail with a StopTestException on unexpected errors" in
    {
      val converterClassName = classOf[AbstractTestConverter].getName

      val in = Seq(Seq("test converter", converterClassName))

      val f = () => new Converter().doTable(asJavaArrayList(in))

      failMessage(f) should be(FailedCreatingConverter)
    }

  it should "fail on missing rows" in
    {
      val in = Seq[Seq[String]]()

      val f = () => new Converter().doTable(asJavaArrayList(in))

      failMessage(f) should be(ConverterTableMissingRows)
    }

  it should "fail on invalid column count" in
    {
      val f = (row: Seq[String]) =>
      {
        () => new Converter().doTable(asJavaArrayList(Seq(row)))
      }

      val data = Seq(
        Seq[String](), //zero, one and three columns
        Seq("c1"),
        Seq("c1", "c2", "c3"))

      for (row <- data)
      {
        failMessage(f(row)) should be(ConverterTableInvalidColumns)
      }
    }

  "A row table" should "apply output converters correctly" in
    {
      val converterName = "A row table should apply output converters correctly"
      val strToExpected = ""
      val strToActual = " + 2"

      val (in, out, expected) = getTestData(converterName, strToExpected, strToActual)

      Store.setConverter(converterName, new AdderConverter(strToExpected, strToActual))

      runRowTest(in, out.iterator, expected, checkSutInputs(""))
    }

  it should "apply input converters correctly" in
    {
      val converterName = "A row table apply input converters correctly"
      val strToExpected = " + 1"
      val strToActual = " + 2"

      val (in, out, expected) = getTestData(converterName, strToExpected, strToActual)

      Store.setConverter(converterName, new AdderConverter(strToExpected, strToActual))

      runRowTest(in, out.iterator, expected, checkSutInputs(strToExpected))
    }

  //no need to test in/out separately
  it should "fail on unknown converter" in
    {
      val (in, out, expected) = getTestData("u-converter", "", "")

      val f = () => runRowTest(in, out.iterator, expected)

      failMessage(f) should be(UndefinedConverter + ": " + "u-converter")
    }

  "A query table" should "apply output converters correctly" in
    {
      val converterName = "A query table apply output converters correctly"
      val strToExpected = " + 1"
      val strToActual = " + 2"

      val (in, out, expected) = getTestData(converterName, strToExpected, strToActual)

      Store.setConverter(converterName, new AdderConverter(strToExpected, strToActual))

      runQueryTest(in, out, expected)
    }

  it should "fail on unknown converter" in
    {
      val (in, out, expected) = getTestData("u-converter", "", "")

      val f = () => runQueryTest(in, out, expected)

      failMessage(f) should be(UndefinedConverter + ": " + "u-converter")
    }

  private def getTestData(converterName: String, strToExpected: String, strToActual: String): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq(s"$converterName:C1")
    val (in1, out1) = (Seq("asd"), Seq("qwe"))
    val exp1 = Seq("pass") //header
    val exp2 = Seq("fail: [asd" + strToExpected + "] != [qwe" + strToActual + "]")

    (
      Seq(header, in1),
      Seq(out1),
      Seq(exp1, exp2)
    )
  }

  private def checkSutInputs(strToAdd: String): (Seq[Header], Seq[RowCell]) => Unit =
  {
    (header: Seq[Header], row: Seq[RowCell]) =>
    {
      header(0).sutInput should be("C1")
      row(0).getSutInput(emptySutInput = false) should be("asd" + strToAdd)
    }
  }
}

class AdderConverter(strToExpected: String, strToActual: String) extends IConverter
{
  def this() = this(
    ": expected converter works!",
    ": actual converter works!")

  def convertActual(value: String): String =
  {
    value + strToActual
  }

  def convertExpected(value: String): String =
  {
    value + strToExpected
  }
}

// abstract class for testing instantiation exception
abstract class AbstractTestConverter() extends IConverter
{
  def convertActual(value: String): String =
  {
    value + ": this text should never appear!"
  }

  def convertExpected(value: String): String =
  {
    value + ": this text should never appear!"
  }
}