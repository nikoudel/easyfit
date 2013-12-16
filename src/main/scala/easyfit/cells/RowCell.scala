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

class RowCell(
  value: String,
  expFilter: String => String,
  actFilter: String => String) extends TableCell(value, expFilter, actFilter)
{
  def this(value: String) = this(value, null, null)

  def getSutInput(emptySutInput: Boolean): String =
  {
    if (emptySutInput)
    {
      ""
    }
    else
    {
      expected
    }
  }

  /*
  Variable:
    1. Store.getVariable(varName)
    2. callSUT
    3. compare to 1

  Variable + E:
    1. Store.getVariable(varName)
    2. expFilter.apply
    3. callSUT
    4. compare to 2

   Variable + A:
    1. Store.getVariable(varName)
    2. callSUT
    3. actFilter.apply
    4. compare to 1

   Variable + EA:
    1. Store.getVariable(varName)
    2. expFilter.apply
    3. callSUT
    4. actFilter.apply
    5. compare to 2
   */
}
