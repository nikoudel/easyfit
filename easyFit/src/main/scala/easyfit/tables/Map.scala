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

import easyfit.Strings.MapMissingId
import easyfit.Strings.MapTableMissingRows
import easyfit.Strings.MapTableInvalidColumns
import easyfit.Strings.MapTableEmptyKey
import easyfit.Strings.UndefinedVariable
import easyfit.Strings.UndefinedConverter
import easyfit.StopTestException
import easyfit.Store
import easyfit.CellFactory

import scala.collection.immutable

/**
 * Creates a map of key-value pairs to store query arguments.
 * @param mapId map ID to reference the map
 */
class Map(mapId: String)
{
  def doTable(javaTable: java.util.List[java.util.List[String]]): java.util.List[java.util.List[String]] =
  {
    validateMapTableStructure(javaTable)

    val result = new java.util.ArrayList[java.util.List[String]]()
    var map = createMap()
    val it = javaTable.iterator

    while (it.hasNext)
    {
      val row = it.next

      validateMapColumnStructure(row)

      map += getPair(row.get(0), row.get(1), result)
    }

    Store.setMap(mapId, map)

    result
  }

  private def validateMapTableStructure(table: java.util.List[java.util.List[String]]) =
  {
    if (mapId == null || mapId.isEmpty)
    {
      throw new StopTestException(MapMissingId)
    }

    if (table == null || table.size < 1)
    {
      throw new StopTestException(MapTableMissingRows)
    }
  }

  private def validateMapColumnStructure(row: java.util.List[String]) =
  {
    if (row.size != 2)
    {
      throw new StopTestException(MapTableInvalidColumns)
    }
  }

  private def createMap(): immutable.Map[String, String] =
  {
    val map = immutable.Map.empty[String, String]

    Store.setMap(mapId, map)

    map
  }

  private def getPair(header: String, value: String, result: java.util.List[java.util.List[String]]): (String, String) =
  {
    val (converterName, key) = CellFactory.splitConverterHeader(header)

    if (key == null || key == "")
    {
      throw new StopTestException(MapTableEmptyKey)
    }

    val row = new java.util.ArrayList[String]()

    if (value.startsWith("$"))
    {
      val varValue = Store.getVariable(value)

      if (varValue == null)
      {
        throw new StopTestException(UndefinedVariable + ": " + value)
      }
      else
      {
        val converted = applyConverter(converterName, varValue, true)

        row.add("pass")
        row.add(s"pass: $value [$converted]")

        result.add(row)

        return key -> applyConverter(converterName, varValue, false)
      }
    }

    val converted = applyConverter(converterName, value, true)

    row.add("pass")
    row.add(s"pass: $converted")

    result.add(row)

    key -> applyConverter(converterName, value, false)
  }

  private def applyConverter(converterName: String, value: String, bothConversions: Boolean): String =
  {
    if (converterName == null)
    {
      return value
    }

    var converter = Store.getConverter(converterName)

    if (converter == null)
    {
      throw new StopTestException(UndefinedConverter + ": " + converterName)
    }

    if(bothConversions)
    {
      return converter.convertActual(converter.convertExpected(value))
    }

    converter.convertExpected(value)
  }
}
