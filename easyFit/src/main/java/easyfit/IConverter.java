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
package easyfit;

/**
 * The interface to be implemented by converter classes.
 */
public interface IConverter
{
    public String convertExpected(String value);
    public String convertActual(String value);
}