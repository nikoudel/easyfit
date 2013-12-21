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

import scala.collection.JavaConversions.mapAsJavaMap
import easyfit.cells.{RowCell, Header}

/**
 * Enables stubbing for unit testing.
 */
trait IConnector
{
  def executeRow(header: Seq[Header], row: Seq[RowCell]): Seq[String]

  def executeQuery(header: Seq[Header], arguments: Map[String, String]): Seq[Seq[String]]
}

/**
 * "Connects" Row and Query tables to SUT by converting cell
 * sequences to JSON and calling HttpConnection.
 * @param sutAction name of SUT method to call
 */
class Connector(sutAction: String) extends IConnector
{
  val httpConnection = new HttpConnection(sutAction)

  def executeRow(
    headers: Seq[Header],
    row: Seq[RowCell]): Seq[String] =
  {
    val outgoingData = JSONConverter.rowToJSON(headers, row)

    val incomingData = httpConnection.post(outgoingData)

    val resultMap = JSONConverter.objectToMap(incomingData)

    compileResults(headers, resultMap)
  }

  def executeQuery(
    header: Seq[Header],
    arguments: Map[String, String]): (Seq[Seq[String]]) =
  {
    val javaArgs = if (arguments == null) null else mapAsJavaMap(arguments)

    val incomingData = httpConnection.get(javaArgs)

    val resultMaps = JSONConverter.arrayToMaps(incomingData)

    val results = new Array[Seq[String]](resultMaps.length)

    for ((entry, i) <- resultMaps.zipWithIndex)
    {
      results(i) = compileResults(header, entry)
    }

    results
  }

  private def compileResults(
    header: Seq[Header],
    resultMap: Map[String, String]): Seq[String] =
  {
    val results = new Array[String](header.length)

    for ((hCell, i) <- header.zipWithIndex)
    {
      if (resultMap.contains(hCell.sutInput()))
      {
        results(i) = resultMap(hCell.sutInput())
      }
      else
      {
        results(i) = ""
        hCell.setMissing()
      }
    }

    results
  }
}