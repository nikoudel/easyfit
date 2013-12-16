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
import easyfit.cells.{SurplusQueryCell, QueryCell, TableCell, Header}

object ListFactory
{
  def createHeader(headers: Seq[Header]): java.util.List[String] =
  {
    val list = new java.util.ArrayList[String](headers.length)
    val it = headers.iterator

    while (it.hasNext)
    {
      list.add(it.next().format())
    }

    list
  }

  def createRow(headers: Seq[Header], cells: Seq[TableCell], sutOutputs: Seq[String]): java.util.List[String] =
  {
    if (headers.length != cells.length || headers.length != sutOutputs.length)
    {
      throw new StopTestException(InvalidColumnCount)
    }

    val list = new java.util.ArrayList[String](headers.length)

    val (hit, cit, sit) = (headers.iterator, cells.iterator, sutOutputs.iterator)

    for (i <- 0 until headers.length)
    {
      val (header, cell, sutOutput) = (hit.next(), cit.next(), sit.next())

      val ignoreResult = header.Value.endsWith("!") || header.IsMissing

      list.add(cell.formatResultAndSetVariable(sutOutput, ignoreResult))
    }

    list
  }

  def createMissingRow(cells: Seq[QueryCell]): java.util.List[String] =
  {
    val list = new java.util.ArrayList[String](cells.length)
    val it = cells.iterator

    while (it.hasNext)
    {
      list.add(it.next().formatAsMissing())
    }

    list
  }

  def createSurplusRow(cells: Seq[SurplusQueryCell]): java.util.List[String] =
  {
    val list = new java.util.ArrayList[String](cells.length)
    val it = cells.iterator

    while (it.hasNext)
    {
      list.add(it.next().formatResult())
    }

    list
  }
}
