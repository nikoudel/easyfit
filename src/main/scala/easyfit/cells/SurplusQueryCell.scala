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

/**
 * Represents a cell in a surplus row in a Query table
 * @param sutResponse value coming from the SUT
 * @param actFilter "actual" filter or null
 */
class SurplusQueryCell(
  sutResponse: String,
  actFilter: String => String)
{
  def formatResult(): String =
  {
    var actual = sutResponse

    if (actFilter != null)
    {
      actual = actFilter.apply(actual)
    }

    String.format("fail: %s (SURPLUS)", actual)
  }

  /*
 O:
   1. callSUT
   2. outFilter.apply
   4. mark as SURPLUS
 */
}