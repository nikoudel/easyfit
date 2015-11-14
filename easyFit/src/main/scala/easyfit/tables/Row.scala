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

import easyfit._
import easyfit.Strings.MissingAction
import easyfit.Strings.RowTableMissingRows

/**
 * Represents Row table.
 */
class Row(action: String, sut: IConnector)
{
  def this(action: String)
  {
    this(action, new Connector(action))
  }

  if (action == null || action.isEmpty)
  {
    throw new StopTestException(MissingAction)
  }

  def doTable(javaTable: java.util.List[java.util.List[String]]): java.util.List[java.util.List[String]] =
  {
    if (javaTable == null || javaTable.size < 2)
    {
      throw new StopTestException(RowTableMissingRows)
    }

    val it = javaTable.iterator()

    val header = CellFactory.createHeader(it.next())

    val result = new java.util.ArrayList[java.util.List[String]](javaTable.size)

    while (it.hasNext)
    {
      val row = CellFactory.createRow(header, it.next())

      val sutOutputRow = sut.executeRow(header, row)

      if (result.isEmpty)
      {
        result.add(ListFactory.createHeader(header))
      }

      result.add(ListFactory.createRow(header, row, sutOutputRow))
    }

    result
  }
}