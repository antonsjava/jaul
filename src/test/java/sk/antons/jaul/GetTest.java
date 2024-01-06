/*
 * Copyright 2015 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.jaul;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class GetTest {

    @Test
	public void firstCollection() throws Exception {
        List value = null;
        Assert.assertNull("null", Get.first(value));
        value = new ArrayList();
        Assert.assertNull("empty", Get.first(value));
        value.add("");
        Assert.assertNotNull("notempty", Get.first(value));
    }

    @Test
	public void safeInteger() throws Exception {
        Integer value = null;
        Assert.assertTrue("null", Get.safeInt(value) == 0);
        value = 12;
        Assert.assertTrue("not null", Get.safeInt(value) == 12);
    }

    @Test
	public void sizeString() throws Exception {
        String value = null;
        Assert.assertTrue("null", Get.size(value) == 0);
        value = "ssd";
        Assert.assertTrue("not null", Get.size(value) == 3);
    }

    @Test
	public void sizeCollection() throws Exception {
        List value = null;
        Assert.assertTrue("null", Get.size(value) == 0);
        value = new ArrayList();
        Assert.assertTrue("empty", Get.size(value) == 0);
        value.add("");
        Assert.assertTrue("not null not empty", Get.size(value) == 1);
    }

    @Test
	public void sizeMap() throws Exception {
        Map value = null;
        Assert.assertTrue("null", Get.size(value) == 0);
        value = new HashMap();
        Assert.assertTrue("empty", Get.size(value) == 0);
        value.put("", "");
        Assert.assertTrue("not null not empty", Get.size(value) == 1);
    }

    @Test
	public void sizeArray() throws Exception {
        String[] value = null;
        Assert.assertTrue("null", Get.size(value) == 0);
        value = new String[] {};
        Assert.assertTrue("empty", Get.size(value) == 0);
        value = new String[] {""};
        Assert.assertTrue("not null not empty", Get.size(value) == 1);
    }

    @Test
	public void sizePrimArray() throws Exception {
        int[] value = null;
        Assert.assertTrue("null", Get.size(value) == 0);
        value = new int[] {};
        Assert.assertTrue("empty", Get.size(value) == 0);
        value = new int[] {12};
        Assert.assertTrue("not null not empty", Get.size(value) == 1);
    }

    @Test
	public void trim() throws Exception {
        String value = null;
        Assert.assertTrue("null", Get.trim(value) == null);
        value = "jablko";
        Assert.assertTrue("ok", "jablko".equals(Get.trim(value)));
        value = " jablko ";
        Assert.assertTrue("not ok", "jablko".equals(Get.trim(value)));
    }

    @Test
	public void fromBool() throws Exception {
        String value = null;
        Assert.assertFalse("null", Get.from(value).booleanValue());
        Assert.assertTrue("null default", Get.from(value).booleanValue(true));
        value = "jablko";
        Assert.assertFalse("bad", Get.from(value).booleanValue());
        value = "true";
        Assert.assertTrue("ok", Get.from(value).booleanValue());
    }

    @Test
	public void fromInt() throws Exception {
        String value = null;
        Assert.assertTrue("null", Get.from(value).intValue() == 0);
        Assert.assertTrue("null default", Get.from(value).intValue(12) == 12);
        value = "ss";
        try {
            Get.from(value).intValue();
            Assert.fail("bad");
        } catch (NumberFormatException e) {
        }
        Assert.assertTrue("bad ignore", Get.from(value, true, true).intValue(12) == 12);
        value = "12";
        Assert.assertTrue("ok", Get.from(value).intValue() == 12);
    }

    @Test
	public void fromDate() throws Exception {
        String value = null;
        Assert.assertTrue("null", Get.from(value).dateValue("dd.MM.yyyy") == null);
        Assert.assertTrue("null default", Get.from(value).dateValue(new Date(), "dd.MM.yyyy") != null);
        value = "ss";
        try {
            Get.from(value).dateValue("dd.MM.yyyy");
            Assert.fail("bad");
        } catch (Exception e) {
        }
        Assert.assertTrue("bad ignore", Get.from(value, true, true).dateValue(new Date(), "dd.MM.yyyy") != null);
        value = "12.11.2032";
        Assert.assertTrue("ok", Get.from(value).dateValue("dd.MM.yyyy") != null);
    }

}
