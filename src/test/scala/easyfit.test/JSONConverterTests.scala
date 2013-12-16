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

import easyfit.JSONConverter
import easyfit.Strings.InvalidColumnCount
import easyfit.cells.{RowCell, Header}

class JSONConverterTests extends EasyTest
{
  "JSONConverter" should "throw StopTestException when convertRow is called with a missing header column" in
    {
      val header = Seq(new Header("C1"))
      val row = Seq(new RowCell("r1c1"), new RowCell("r1c2"))

      val f = () => JSONConverter.rowToJSON(header, row)

      failMessage(f) should be(InvalidColumnCount)
    }

  it should "convert a pair of Cell collections into a valid JSON string" in
    {
      val header = Seq(new Header("C1"), new Header("C2!"), new Header("C3?"))
      val row = Seq(new RowCell("r1c1"), new RowCell("r1c2"), new RowCell("r1c3"))

      val str = JSONConverter.rowToJSON(header, row)

      str should be( """{"C1":"r1c1","C2":"r1c2","C3":""}""")
    }

  it should "convert incoming row JSON into a map" in
    {
      val data = """{"C1":"r1c1","C2":23.11,"C3":"r1c3"}"""

      val map = JSONConverter.objectToMap(data)

      map.size should be(3)
      map should contain("C1" -> "r1c1")
      map should contain("C2" -> "23.11")
      map should contain("C3" -> "r1c3")
    }

  it should "convert incoming row JSON into a table (Seq[Map[String, String]])" in
    {
      val data = """[{"Id":1,"DeviceType":"Undefined"},{"Id":2,"DeviceType":"CeilingLight"}]"""

      val table = JSONConverter.arrayToMaps(data)

      table.size should be(2)
      table(0)("Id") should be("1")
      table(1)("Id") should be("2")
      table(1)("DeviceType") should be("CeilingLight")
    }
}