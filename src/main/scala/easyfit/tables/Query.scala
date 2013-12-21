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
package easyfit.tables

import easyfit.IConnector
import easyfit.Connector
import easyfit.StopTestException
import easyfit.Store
import easyfit.Strings.UndefinedMap
import easyfit.CellFactory
import easyfit.ListFactory
import easyfit.Strings.isNullOrEmpty
import easyfit.Strings.QueryTableMissingHeader
import easyfit.Strings.MissingAction

/**
 * Represents Query table.
 */
import scala.collection.immutable

class Query(action: String, parameterMapId: String, sut: IConnector)
{
  def this(action: String, parameterMapId: String)
  {
    this(action, parameterMapId, new Connector(action))
  }

  def this(action: String)
  {
    this(action, null)
  }

  if (action == null || action.isEmpty)
  {
    throw new StopTestException(MissingAction)
  }

  def max(a: Int, b: Int): Int = if (a > b) a else b

  def doTable(javaTable: java.util.List[java.util.List[String]]): java.util.List[java.util.List[String]] =
  {
    if (javaTable == null || javaTable.size < 1)
    {
      throw new StopTestException(QueryTableMissingHeader)
    }

    val table = javaTable.iterator()

    val header = CellFactory.createHeader(table.next())

    val sutOutput = sut.executeQuery(header, queryParameters())

    val resultSize = max(javaTable.size, sutOutput.length + 1)

    val result = new java.util.ArrayList[java.util.List[String]](resultSize)

    result.add(ListFactory.createHeader(header))

    val output = sutOutput.iterator

    while (table.hasNext && output.hasNext)
    {
      val (tableRow, outputRow) = (table.next(), output.next())

      val queryCells = CellFactory.createQueryCells(header, tableRow)

      result.add(ListFactory.createRow(header, queryCells, outputRow))
    }

    while (table.hasNext) //missing rows
    {
      val missingQueryCells = CellFactory.createQueryCells(header, table.next())

      result.add(ListFactory.createMissingRow(missingQueryCells))
    }

    while (output.hasNext) //surplus rows
    {
      val cells = CellFactory.createSurplusCells(header, output.next())

      result.add(ListFactory.createSurplusRow(cells))
    }

    result
  }

  def queryParameters(): immutable.Map[String, String] =
  {
    if (!isNullOrEmpty(parameterMapId))
    {
      val map = Store.getMap(parameterMapId)

      if (map == null)
      {
        throw new StopTestException(UndefinedMap + ": " + parameterMapId)
      }

      return map
    }

    null
  }
}
