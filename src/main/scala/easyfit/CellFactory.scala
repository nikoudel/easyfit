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
import easyfit.Strings.QueryInputFilter
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

      val (expFilter, actFilter) = header.fetchFilters()

      if (expFilter != null)
      {
        throw new StopTestException(QueryInputFilter + ": " + header.filterName)
      }

      cellArray(i) = new SurplusQueryCell(c.next(), actFilter)
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

  private def createRowCell(header: Header, cellValue: String): RowCell =
  {
    val (expFilter, actFilter) = header.fetchFilters()

    new RowCell(cellValue, expFilter, actFilter)
  }

  private def createQueryCell(header: Header, cellValue: String): QueryCell =
  {
    val (expFilter, actFilter) = header.fetchFilters()

    if (expFilter != null)
    {
      throw new StopTestException(QueryInputFilter + ": " + header.filterName())
    }

    new QueryCell(cellValue, actFilter)
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