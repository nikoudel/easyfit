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
  private abstract class StoredFilter(val f: String => String)
  private class ExpFilter(f: String => String) extends StoredFilter(f)
  private class ActFilter(f: String => String) extends StoredFilter(f)

  private val Variables = new mutable.HashMap[String, String] with mutable.SynchronizedMap[String, String]
  private val Filters = new mutable.HashMap[String, StoredFilter] with mutable.SynchronizedMap[String, StoredFilter]
  private val Maps = new mutable.HashMap[String, Map[String, String]] with mutable.SynchronizedMap[String, Map[String, String]]
  private val Conf = new mutable.HashMap[String, String] with mutable.SynchronizedMap[String, String]

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

  def getActualFilter(key: String): String => String =
  {
    getItem(key, Filters) match
    {
      case filter: ActFilter => filter.f
      case _ => null
    }
  }

  def setActualFilter(key: String, value: String => String)
  {
    setItem(key, new ActFilter(value), Filters)
  }

  def getExpectedFilter(key: String): String => String =
  {
    getItem(key, Filters) match
    {
      case filter: ExpFilter => filter.f
      case _ => null
    }
  }

  def setExpectedFilter(key: String, value: String => String)
  {
    setItem(key, new ExpFilter(value), Filters)
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