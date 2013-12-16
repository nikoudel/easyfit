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

import easyfit.IConnector
import easyfit.cells.{RowCell, Header}

/**
 * Enables unit testing by abstracting real SUT away.
 * @param resultRows rows to return on each call to executeRow
 * @param resultTable table to return by executeQuery
 */
class ConnectorMock(
  resultRows: Iterator[Seq[String]],
  resultTable: Seq[Seq[String]]) extends IConnector
{
  // Row and query "actions" to be applied on each call
  // in order to simulate behaviors such as SUT input
  // inspection (ReadOnlyColumns) and setting columns as
  // missing (MissingColumns).
  var rowAction: (Seq[Header], Seq[RowCell]) => Unit = null
  var queryHeaderAction: Seq[Header] => Unit = null

  def setRowAction(action: (Seq[Header], Seq[RowCell]) => Unit)
  {
    this.rowAction = action
  }

  def setQueryHeaderAction(action: Seq[Header] => Unit)
  {
    this.queryHeaderAction = action
  }

  override def executeRow(
    header: Seq[Header],
    row: Seq[RowCell]): Seq[String] =
  {
    if (rowAction != null)
    {
      rowAction.apply(header, row)
    }

    if (resultRows.hasNext)
    {
      resultRows.next()
    }
    else
    {
      throw new RuntimeException("empty iterator")
    }
  }

  override def executeQuery(
    header: Seq[Header],
    arguments: Map[String, String]): Seq[Seq[String]] =
  {
    if (queryHeaderAction != null)
    {
      queryHeaderAction.apply(header)
    }

    resultTable
  }
}