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

import easyfit.Strings._
import easyfit.StopTestException
import easyfit.Store
import easyfit.IFilter

trait FilterSetter
{
  protected def setFilter(name: String, value: String => String)
}

/**
 * Creates filters to be applied to expected values (SUT inputs)
 */
class ExpectedFilter() extends FilterTable
{
  protected def setFilter(name: String, value: String => String)
  {
    Store.setExpectedFilter(name, value)
  }
}

/**
 * Creates filters to be applied to actual values (SUT outputs)
 */
class ActualFilter() extends FilterTable
{
  protected def setFilter(name: String, value: String => String)
  {
    Store.setActualFilter(name, value)
  }
}

/**
 * Abstract base class for ExpectedFilter and ActualFilter.
 */
abstract class FilterTable() extends FilterSetter
{
  def doTable(javaTable: java.util.List[java.util.List[String]]): java.util.List[java.util.List[String]] =
  {
    validateTableStructure(javaTable)

    val it = javaTable.iterator
    val result = new java.util.ArrayList[java.util.List[String]]

    while (it.hasNext)
    {
      val row = it.next

      validateFilterRowStructure(row)

      val filter = createFilter(row.get(1))

      setFilter(row.get(0), filter)

      val resRow = new java.util.ArrayList[String]

      resRow.add("pass")
      resRow.add("pass")

      result.add(resRow)
    }

    result
  }

  private def validateTableStructure(table: java.util.List[java.util.List[String]]) =
  {
    if (table == null || table.size < 1)
    {
      throw new StopTestException(FilterTableMissingRows)
    }
  }

  private def validateFilterRowStructure(row: java.util.List[String]) =
  {
    if (row.size != 2)
    {
      throw new StopTestException(FilterTableInvalidColumns)
    }
  }

  private def createFilter(filterDeclaration: String): String => String =
  {
    val rx = """(\w+\.)+\w+\.?""".r // e.g. com.example.filters.AddOne

    if (rx.findFirstIn(filterDeclaration).nonEmpty)
    {
      try
      {
        {
          val filterClass = Class.forName(filterDeclaration)

          filterClass.newInstance() match
          {
            case filter: IFilter => (value: String) => filter.apply(value)

            case (other) => throw new StopTestException(
              String.format(NotInstanceOfFilter,
                other.getClass.getName, classOf[IFilter].getName))
          }
        }
      }
      catch
      {
        case stopEx: StopTestException => throw stopEx

        case ex: Exception => throw new StopTestException(FailedCreatingFilter, ex)
      }
    }
    else
    {
      throw new StopTestException(s"$InvalidFilterClass: '$filterDeclaration'")
    }
  }
}
