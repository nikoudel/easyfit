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
import easyfit.IConverter

/**
 * Creates converters to be applied to expected and actual values (SUT inputs and outputs)
 */
class Converter()
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

      val converter = createConverter(row.get(1))

      Store.setConverter(row.get(0), converter)

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
      throw new StopTestException(ConverterTableMissingRows)
    }
  }

  private def validateFilterRowStructure(row: java.util.List[String]) =
  {
    if (row.size != 2)
    {
      throw new StopTestException(ConverterTableInvalidColumns)
    }
  }

  private def createConverter(declaration: String): IConverter =
  {
    val rx = """^(\w+\.)*\w+$""".r // e.g. com.example.converters.AddOne

    if (rx.findFirstIn(declaration).nonEmpty)
    {
      try
      {
        {
          val converterClass = Class.forName(declaration)

          converterClass.newInstance() match
          {
            case converter: IConverter => converter

            case (other) => throw new StopTestException(
              String.format(NotInstanceOfConverter,
                other.getClass.getName, classOf[IConverter].getName))
          }
        }
      }
      catch
      {
        case stopEx: StopTestException => throw stopEx

        case ex: Exception => throw new StopTestException(FailedCreatingConverter, ex)
      }
    }
    else
    {
      throw new StopTestException(s"$InvalidConverterClass: '$declaration'")
    }
  }
}
