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
import easyfit.tables.ExpectedFilter
import easyfit.tables.ActualFilter
import easyfit.tables.FilterTable
import easyfit.Strings._
import easyfit.cells.{RowCell, Header}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class FilterTests extends EasyTest
{
  "An OutputFilter table" should "add filter objects" in
    {
      testFilterTable(new ActualFilter)
    }

  it should "fail on invalid class name" in
    {
      val in = Seq(Seq("f1", "invalid class name"))

      val f = () => new ActualFilter().doTable(asJavaArrayList(in))

      failMessage(f) should be(InvalidFilterClass + ": 'invalid class name'")
    }

  it should "fail on a filter not implementing the interface" in
    {
      val filterClassName = this.getClass.getName
      val interfaceName = classOf[easyfit.IFilter].getName

      val in = Seq(Seq("test filter", filterClassName))

      val f = () => new ActualFilter().doTable(asJavaArrayList(in))

      failMessage(f) should be(String.format(
        NotInstanceOfFilter, filterClassName, interfaceName))
    }

  it should "fail with a StopTestException on unexpected errors" in
    {
      val filterClassName = classOf[AbstractTestFilter].getName

      val in = Seq(Seq("test filter", filterClassName))

      val f = () => new ActualFilter().doTable(asJavaArrayList(in))

      failMessage(f) should be(FailedCreatingFilter)
    }

  it should "fail on missing rows" in
    {
      val in = Seq[Seq[String]]()

      val f = () => new ActualFilter().doTable(asJavaArrayList(in))

      failMessage(f) should be(FilterTableMissingRows)
    }

  it should "fail on invalid column count" in
    {
      val f = (row: Seq[String]) =>
      {
        () => new ActualFilter().doTable(asJavaArrayList(Seq(row)))
      }

      val data = Seq(
        Seq[String](), //zero, one and three columns
        Seq("c1"),
        Seq("c1", "c2", "c3"))

      for (row <- data)
      {
        failMessage(f(row)) should be(FilterTableInvalidColumns)
      }
    }

  "An InputFilter table" should "add filter objects" in
    {
      testFilterTable(new ExpectedFilter)

      // Note: no need to test the rest of the cases because
      // OutputFilter and InputFilter share most of the code.
    }

  "A row table" should "apply output filters correctly" in
    {
      val filterName = "A row table should apply output filters correctly"
      val strToAdd = " + 1"

      val (in, out, expected) = getTestData(filterName, "", strToAdd)

      Store.setActualFilter(filterName, filter(strToAdd))

      runRowTest(in, out.iterator, expected, checkSutInputs(""))
    }

  it should "apply input filters correctly" in
    {
      val filterName = "A row table apply input filters correctly"
      val strToAdd = " + 2"

      val (in, out, expected) = getTestData(filterName, strToAdd, "")

      Store.setExpectedFilter(filterName, filter(strToAdd))

      runRowTest(in, out.iterator, expected, checkSutInputs(strToAdd))
    }

  //no need to test in/out separately
  it should "fail on unknown filter" in
    {
      val (in, out, expected) = getTestData("u-filter", "", "")

      val f = () => runRowTest(in, out.iterator, expected)

      failMessage(f) should be(UndefinedFilter + ": " + "u-filter")
    }

  "A query table" should "apply output filters correctly" in
    {
      val filterName = "A query table apply output filters correctly"
      val strToAdd = " + 3"

      val (in, out, expected) = getTestData(filterName, "", strToAdd)

      Store.setActualFilter(filterName, filter(strToAdd))

      runQueryTest(in, out, expected)
    }

  it should "fail on unknown filter" in
    {
      val (in, out, expected) = getTestData("u-filter", "", "")

      val f = () => runQueryTest(in, out, expected)

      failMessage(f) should be(UndefinedFilter + ": " + "u-filter")
    }

  it should "fail on input filter" in
    {
      val filterName = "in-filter"

      Store.setExpectedFilter(filterName, filter(" + 1"))

      val (in, out, expected) = getTestData(filterName, "", "")

      val f = () => runQueryTest(in, out, expected)

      failMessage(f) should be(QueryInputFilter + ": " + filterName)
    }

  private def testFilterTable(filterTable: FilterTable)
  {
    val filterName = filterTable.getClass.getName

    val in = Seq(Seq(filterName, "easyfit.test.TestFilter"))

    val tableResult = filterTable.doTable(asJavaArrayList(in))

    val expectedResult = Seq(Seq("pass", "pass"))

    compareTables(tableResult, asJavaArrayList(expectedResult))

    val filter = filterTable match
    {
      case table: ExpectedFilter => Store.getExpectedFilter(filterName)
      case _ => Store.getActualFilter(filterName)
    }

    filter should not be null

    filter.apply("ping") should be("ping: it works!")
  }

  private def getTestData(filterName: String, strToExpected: String, strToActual: String): (Seq[Seq[String]], Seq[Seq[String]], Seq[Seq[String]]) =
  {
    val header = Seq(s"$filterName:C1")
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

  private def filter(strToAdd: String): String => String =
  {
    (in: String) => in + strToAdd
  }
}

class TestFilter() extends IFilter
{
  def apply(value: String): String =
  {
    value + ": it works!"
  }
}

// abstract class for testing instantiation exception
abstract class AbstractTestFilter() extends IFilter
{
  def apply(value: String): String =
  {
    value + ": this text should never appear!"
  }
}