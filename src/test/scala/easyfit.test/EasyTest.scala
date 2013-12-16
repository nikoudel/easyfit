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

import easyfit.StopTestException
import easyfit.tables.Row
import easyfit.tables.Query
import org.scalatest.Matchers
import org.scalatest.FlatSpec
import scala.collection.JavaConversions.seqAsJavaList
import easyfit.cells.{RowCell, Header}

/**
 * A helper (base) class to reduce duplication and make tests concise.
 */
abstract class EasyTest extends FlatSpec with Matchers
{
  val action = "test action"

  def runRowTest(
    inputTable: Seq[Seq[String]],
    resultRows: Iterator[Seq[String]],
    expectedTable: Seq[Seq[String]],
    rowAction: (Seq[Header], Seq[RowCell]) => Unit = null)
  {
    val javaTable = asJavaArrayList(inputTable)
    val javaExpected = asJavaArrayList(expectedTable)

    val mock = new ConnectorMock(resultRows, null)

    if (rowAction != null)
    {
      mock.setRowAction(rowAction)
    }

    val result = new Row("test action", mock).doTable(javaTable)

    compareTables(result, javaExpected)
  }

  def runQueryTest(
    inputTable: Seq[Seq[String]],
    resultTable: Seq[Seq[String]],
    expectedTable: Seq[Seq[String]],
    arguments: String = null,
    headerAction: Seq[Header] => Unit = null)
  {
    val javaTable = asJavaArrayList(inputTable)
    val javaExpected = asJavaArrayList(expectedTable)

    val mock = new ConnectorMock(null, resultTable)

    if (headerAction != null)
    {
      mock.setQueryHeaderAction(headerAction)
    }

    val result = new Query("test action", arguments, mock).doTable(javaTable)

    compareTables(result, javaExpected)
  }

  def asJavaArrayList(table: Seq[Seq[String]]): java.util.ArrayList[java.util.List[String]] =
  {
    val list = new java.util.ArrayList[java.util.List[String]](table.length)

    table.foreach(row => list.add(seqAsJavaList(row)))

    list
  }

  def failMessage(doTable: () => Any): String =
  {
    the[Exception] thrownBy doTable() match
    {
      case stop: StopTestException => stop.getMessage
      case (other) => throw other
    }
  }

  def compareTables(
    actualTable: java.util.List[java.util.List[String]],
    expectedTable: java.util.List[java.util.List[String]])
  {
    withClue("lists being compared have different sizes")
    {
      actualTable.size should be(expectedTable.size)
    }

    for (i <- 0 until actualTable.size)
    {
      val actualRow = actualTable.get(i)
      val expectedRow = expectedTable.get(i)

      withClue("rows being compared have different sizes")
      {
        actualRow.size should be(expectedRow.size)
      }

      for (j <- 0 until expectedRow.size)
      {
        val expValue = expectedRow.get(j)
        val actValue = actualRow.get(j)

        withClue(s"value mismatch in row $i, column $j: ")
        {
          actValue should be(expValue)
        }
      }
    }
  }
}