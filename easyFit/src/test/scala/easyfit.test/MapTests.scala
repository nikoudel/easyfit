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

import scala.collection.JavaConversions.seqAsJavaList
import easyfit._
import easyfit.Strings._
import easyfit.tables.Map
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class MapTests extends EasyTest
{
  "A Map table" should "fail when created without an identifier" in
    {
      failMessage(() => new Map("").doTable(null)) should be(MapMissingId)
    }

  it should "fail when created with less than two rows (header + operation row)" in
    {
      failMessage(() => new Map(action).doTable(null)) should be(MapTableMissingRows)
    }

  it should "fail when created with invalid columns" in
    {
      val row = seqAsJavaList(Seq[String]("", "", ""))
      val table = seqAsJavaList(Seq(row))

      failMessage(() => new Map(action).doTable(table)) should be(MapTableInvalidColumns)
    }

  it should "fail when created with empty key" in
    {
      val row = seqAsJavaList(Seq("", "asd"))
      val table = seqAsJavaList(Seq(row))

      failMessage(() => new Map(action).doTable(table)) should be(MapTableEmptyKey)
    }

  it should "fail when containing an undefined variable" in
    {
      val row = seqAsJavaList(Seq("key1", "$undefined"))
      val table = seqAsJavaList(Seq(row))

      val f = () => new Map(action).doTable(table)

      failMessage(f) should be(UndefinedVariable + ": " + "$undefined")
    }

  it should "add correct values into map store" in
    {
      val row1 = seqAsJavaList(Seq("key1", "value"))
      val row2 = seqAsJavaList(Seq("key2", "$value"))
      val table = seqAsJavaList(Seq(row1, row2))

      Store.setVariable("$value", "123")

      val results = new Map("test_map").doTable(table)

      val map = Store.getMap("test_map")

      map should not be null
      map("key1") should be("value")
      map("key2") should be("123")

      results.get(0).get(1) should be("pass")
      results.get(1).get(1) should be("pass: $value [123]")
    }
}