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

import easyfit.IConverter

/**
 * Represents a cell in a surplus row in a Query table
 * @param sutResponse value coming from the SUT
 * @param converter IConverter or null
 */
class SurplusQueryCell(
  sutResponse: String,
  converter: IConverter)
{
  def formatResult(): String =
  {
    var actual = sutResponse

    if (converter != null)
    {
      actual = converter.convertActual(actual)
    }

    String.format("fail: %s (SURPLUS)", actual)
  }
}