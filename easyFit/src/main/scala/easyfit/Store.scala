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

import scala.collection.mutable

/**
 * Stores data shared between tables in a single test/suite execution (e.g. variables, filters, etc.)
 */
object Store
{
  private val Variables = new mutable.HashMap[String, String]
  private val Converters = new mutable.HashMap[String, IConverter]
  private val Maps = new mutable.HashMap[String, Map[String, String]]
  private val Conf = new mutable.HashMap[String, String]

  def getVariable(key: String): String =
  {
    getItem(key, Variables)
  }

  def setVariable(key: String, value: String)
  {
    setItem(key, value, Variables)
  }

  def getConfig(key: String): String =
  {
    getItem(key, Conf)
  }

  def setConfig(key: String, value: String)
  {
    setItem(key, value, Conf)
  }

  def getMap(key: String): Map[String, String] =
  {
    getItem(key, Maps)
  }

  def setMap(key: String, value: Map[String, String])
  {
    setItem(key, value, Maps)
  }

  def getConverter(key: String): IConverter =
  {
    getItem(key, Converters)
  }

  def setConverter(key: String, value: IConverter)
  {
    setItem(key, value, Converters)
  }

  private def getItem[T >: Null](key: String, store: mutable.HashMap[String, T]): T =
  {
    val tKey = Thread.currentThread.getName + "_" + key

    store.get(tKey).getOrElse(null)
  }

  private def setItem[T](key: String, value: T, store: mutable.HashMap[String, T])
  {
    val tKey = Thread.currentThread.getName + "_" + key

    store.put(tKey, value)
  }
}