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

/**
 * Error messages and other strings which can be localized.
 */
object Strings
{
  def isNullOrEmpty(value: String) = value == null || value.isEmpty

  def MissingAction = "Table action should not be empty."

  def MissingBaseURL = "Base URL should not be empty."

  def RowTableMissingRows = "Row table should have a header and at least one row."

  def MissingColumns = "Header should have at least one column."

  def InvalidColumnCount = "Column count must be the same for all table rows."

  def InvalidColumnName = "Column name should not be empty"

  def UndefinedVariable = "Undefined variable"

  def QueryTableMissingHeader = "Query table should have a header."

  def MapMissingId = "Map table should have an identifier (in the constructor)."

  def MapTableMissingRows = "Map table should have at least one row."

  def MapTableInvalidColumns = "Map table should always have two columns."

  def MapTableEmptyKey = "Empty key in a Map table is not allowed."

  def UndefinedMap = "Undefined map"

  def UndefinedFilter = "Undefined filter"

  def FilterTableMissingRows = "Filter table should have at least one row."

  def FilterTableInvalidColumns = "Filter table should always have two columns."

  def FailedCreatingFilter = "Failed creating a filter"

  def NotInstanceOfFilter = "Filter class %s does not implement interface %s."

  def InvalidFilterClass = "Failed parsing filter class name (e.g. com.example.SomeFilter)"

  def QueryInputFilter = "Query table can't have input filters"

  def ConnectionFailed = "Connection failed"

  def UnexpectedFailure = "Unexpected failure"

  def ConfigurationTableMissingRows = "Configuration table should have at least one row."

  def ConfigurationTableInvalidColumns = "Configuration table should always have two columns."

  def ConfigurationTableEmptyKey = "Empty key in a Configuration table is not allowed."

  def InvalidBaseURL = "Invalid base URL; expected pattern: http://localhost:56473/(controller).json"
}
