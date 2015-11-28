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

import easyfit.{StopTestException, Store, IConverter}
import easyfit.Strings.UndefinedVariable

/**
 * Base class for Row and Query cells.
 * @param value initial cell value
 * @param converter instance of IConverter
 */
abstract class TableCell(
  value: String,
  converter: IConverter)
{
  var variable: String = null
  var expected: String = null

  setExpectedValue()

  private def setExpectedValue()
  {
    if(value.startsWith("$"))
    {
      variable = value
    }
    else
    {
      expected = value
    }

    if (variable != null)
    {
      if (variable.endsWith("="))
      {
        return //var. initialization
      }

      val varValue = Store.getVariable(variable)

      if (varValue == null)
      {
        throw new StopTestException(String.format("%s: %s", UndefinedVariable, variable))
      }

      expected = varValue
    }

    if (converter != null)
    {
      expected = converter.convertExpected(expected)
    }
  }

  def formatResultAndSetVariable(sutResponse: String, ignoreResult: Boolean): String =
  {
    var actual = sutResponse

    if (converter != null)
    {
      actual = converter.convertActual(actual)
    }

    val failAction = if (ignoreResult) "ignore" else "fail"

    if (variable != null)
    {
      if (variable.endsWith("="))
      {
        return initializeVariable(actual)
      }

      return formatVariable(actual, failAction)
    }

    formatNoVariable(actual, failAction)
  }

  def formatAsMissing(): String =
  {
    if (variable != null)
    {
      if (variable.endsWith("="))
      {
        return String.format("fail: %s (MISSING)", variable)
      }

      return String.format("fail: %s [%s] (MISSING)", variable, expected)
    }

    String.format("fail: %s (MISSING)", expected)
  }

  private def initializeVariable(actual: String): String =
  {
    val varName = variable.stripSuffix("=")

    Store.setVariable(varName, actual)

    String.format("pass: %s <- [%s]", varName, actual)
  }

  private def formatVariable(actual: String, failAction: String): String =
  {
    if (expected == actual)
    {
      String.format("pass: %s [%s]", variable, actual)
    }
    else
    {
      String.format("%s: %s [%s] != [%s]", failAction, variable, expected, actual)
    }
  }

  private def formatNoVariable(actual: String, failAction: String): String =
  {
    if(expected == "")
    {
      if(actual == "")
      {
        "ignore"
      }
      else
      {
        String.format("ignore: %s", actual)
      }
    }
    else if (expected == actual)
    {
      String.format("pass: %s", actual)
    }
    else
    {
      String.format("%s: [%s] != [%s]", failAction, expected, actual)
    }
  }
}
