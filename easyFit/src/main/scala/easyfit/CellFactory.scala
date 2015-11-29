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
package easyfit

import easyfit.Strings.InvalidColumnCount
import easyfit.Strings.MissingColumns
import easyfit.cells.{SurplusQueryCell, QueryCell, RowCell, Header}

/**
 * Creates sequences of different cells.
 */
object CellFactory
{
  def createQueryCells(headers: Seq[Header], tableRow: java.util.List[String]): Seq[QueryCell] =
  {
    if (headers.length != tableRow.size)
    {
      throw new StopTestException(InvalidColumnCount)
    }

    val cellArray = new Array[QueryCell](headers.length)

    for (i <- 0 until cellArray.length)
    {
      cellArray(i) = createQueryCell(headers(i), tableRow.get(i))
    }

    cellArray
  }

  def createSurplusCells(headers: Seq[Header], resultRow: Seq[String]): Seq[SurplusQueryCell] =
  {
    if (headers.length != resultRow.length)
    {
      throw new StopTestException(InvalidColumnCount)
    }

    val cellArray = new Array[SurplusQueryCell](resultRow.length)

    val (h, c) = (headers.iterator, resultRow.iterator)

    for (i <- 0 until resultRow.length)
    {
      val header = h.next()

      cellArray(i) = new SurplusQueryCell(c.next(),  header.fetchConverter())
    }

    cellArray
  }

  def createRow(headers: Seq[Header], tableRow: java.util.List[String]): Seq[RowCell] =
  {
    if (headers.length != tableRow.size)
    {
      throw new StopTestException(InvalidColumnCount)
    }

    val cellArray = new Array[RowCell](tableRow.size)

    for (i <- 0 until cellArray.length)
    {
      cellArray(i) = createRowCell(headers(i), tableRow.get(i))
    }

    cellArray
  }

  def splitConverterHeader(header: String): (String, String) =
  {
    val tokens = header.split(":")

    if (tokens.length == 2)
    {
      return (tokens(0), tokens(1))
    }

    (null, header)
  }

  private def createRowCell(header: Header, cellValue: String): RowCell =
  {
    new RowCell(cellValue, header.fetchConverter())
  }

  private def createQueryCell(header: Header, cellValue: String): QueryCell =
  {
    new QueryCell(cellValue, header.fetchConverter())
  }

  def createHeader(headerValues: java.util.List[String]): Seq[Header] =
  {
    if (headerValues.size < 1)
    {
      throw new StopTestException(MissingColumns)
    }

    val headerArray = new Array[Header](headerValues.size)

    for (i <- 0 until headerArray.length)
    {
      headerArray(i) = new Header(headerValues.get(i))
    }

    headerArray
  }
}