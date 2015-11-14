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
package easyfit.cells

import easyfit.{Store, StopTestException}
import easyfit.Strings.InvalidColumnName
import easyfit.Strings.UndefinedFilter

/**
 * Represents a header cell in Row and Query tables.
 * @param value initial value in a header cell
 */
class Header(value: String)
{
  private var isMissing = false
  private var filtersFetched = false
  private var expFilter: String => String = null
  private var actFilter: String => String = null

  def Value = value
  def IsMissing = isMissing

  if (value == null || value.isEmpty)
  {
    throw new StopTestException(InvalidColumnName)
  }

  def setMissing()
  {
    isMissing = true
  }

  def isEmptySutInput: Boolean =
  {
    value.endsWith("?")
  }

  def filterName(): String =
  {
    val tokens = value.split(":")

    if (tokens.length == 2)
    {
      return tokens(0)
    }

    ""
  }

  def sutInput(): String =
  {
    var sInput = value

    if (value.endsWith("!") || value.endsWith("?"))
    {
      sInput = value.substring(0, value.length - 1)
    }

    val tokens = sInput.split(":")

    if (tokens.length == 2)
    {
      return tokens(1)
    }

    sInput
  }

  def format(): String =
  {
    if (isMissing)
    {
      return String.format("fail: %s (MISSING)", value)
    }

    if (value.endsWith("!"))
    {
      return "ignore"
    }

    "pass"
  }

  def fetchFilters(): (String => String, String => String) =
  {
    if (filtersFetched)
    {
      return (expFilter, actFilter)
    }

    val fName = filterName()

    if (fName != "")
    {
      expFilter = Store.getExpectedFilter(fName)
      actFilter = Store.getActualFilter(fName)

      if (expFilter == null && actFilter == null)
      {
        throw new StopTestException(UndefinedFilter + ": " + fName)
      }
    }

    filtersFetched = true

    (expFilter, actFilter)
  }
}
