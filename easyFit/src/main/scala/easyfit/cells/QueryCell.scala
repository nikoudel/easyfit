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
 * Represents a cell in a Query table.
 * @param value initial cell value
 * @param header cell header
 */
class QueryCell(
  value: String,
  header: Header) extends TableCell(value, header)
{
  /*
   Variable + A:
     1. callSUT
     2. actFilter.apply
     3. Store.getVariable(varName)
     4. compare to 2
   */
}
