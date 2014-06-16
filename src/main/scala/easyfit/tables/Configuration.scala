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

import easyfit.Strings.ConfigurationTableMissingRows
import easyfit.Strings.ConfigurationTableInvalidColumns
import easyfit.Strings.ConfigurationTableEmptyKey
import easyfit.Strings.UndefinedVariable
import easyfit.StopTestException
import easyfit.Store

/**
 * Creates a map of key-value pairs to store configuration parameters.
 */
class Configuration()
{
  def doTable(javaTable: java.util.List[java.util.List[String]]): java.util.List[java.util.List[String]] =
  {
    validateTableStructure(javaTable)

    val result = new java.util.ArrayList[java.util.List[String]]()
    val it = javaTable.iterator

    while (it.hasNext)
    {
      val row = it.next

      validateColumnStructure(row)

      savePair(row.get(0), row.get(1))
    }

    result
  }

  private def validateTableStructure(table: java.util.List[java.util.List[String]]) =
  {
    if (table == null || table.size < 1)
    {
      throw new StopTestException(ConfigurationTableMissingRows)
    }
  }

  private def validateColumnStructure(row: java.util.List[String]) =
  {
    if (row.size != 2)
    {
      throw new StopTestException(ConfigurationTableInvalidColumns)
    }
  }

  private def savePair(key: String, value: String) =
  {
    if (key == null || key == "")
    {
      throw new StopTestException(ConfigurationTableEmptyKey)
    }
    
    Store.setConfig(key, value)
  }
}
