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

import com.eclipsesource.json._

import easyfit.Strings.InvalidColumnCount
import easyfit.cells.{RowCell, Header}

/**
 * Handles JSON conversions.
 */
object JSONConverter
{
  def rowToJSON(header: Seq[Header], row: Seq[RowCell]): String =
  {
    if (header.size != row.size)
    {
      throw new StopTestException(InvalidColumnCount)
    }

    val jsonObject = new JsonObject

    for ((h, r) <- header.zip(row))
    {
      jsonObject.add(h.sutInput(), r.getSutInput(h.isEmptySutInput))
    }

    jsonObject.toString
  }

  def objectToMap(raw: String): Map[String, String] =
  {
    val data = if (raw == null || raw.isEmpty || raw == "null") "{}" else raw

    jsonObjectToMap(Json.parse(data).asObject())
  }

  def arrayToMaps(raw: String): Seq[Map[String, String]] =
  {
    val data = if (raw == null || raw.isEmpty || raw == "null") "[]" else raw

    val items = Json.parse(data).asArray()
    val maps = new Array[Map[String, String]](items.size)
    val it = items.iterator

    for (i <- 0 until maps.length)
    {
      maps(i) = jsonObjectToMap(it.next.asObject)
    }

    maps
  }

  private def jsonObjectToMap(obj: JsonObject): Map[String, String] =
  {
    val pairs = new Array[(String, String)](obj.size)
    val it = obj.iterator

    for (i <- 0 until pairs.length)
    {
      val item = it.next
      val (key, v) = (item.getName, item.getValue)

      val value = if (v.isString)
      {
        v.asString
      }
      else
      {
        v.toString
      }

      pairs(i) = (key, value)
    }

    Map.empty[String, String] ++ pairs
  }
}
