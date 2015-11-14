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
 * The exception is named in a particular way (starts with StopTest)
 * which makes FitNesse to stop further test execution.
 * @param msg Message to be displayed by FitNesse
 * @param ex FitNesse can display exception call stack
 */
class StopTestException(msg: String, ex: Exception) extends Exception(msg, ex)
{
  def this(msg: String) = this(msg, null)
}