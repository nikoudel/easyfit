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
package easyfit.test

import easyfit.Connector

import org.scalatest.Matchers
import org.scalatest.FlatSpec

class MiscTests extends FlatSpec with Matchers
{
  "Connector" should "parse URL template" in
    {
      val connector = new Connector("TestController")

      connector.parseTemplate("http://localhost:56473/(controller)") should be("http://localhost:56473/TestController")
      connector.parseTemplate("http://localhost:56473/(controller)/12") should be("http://localhost:56473/TestController/12")
    }
}