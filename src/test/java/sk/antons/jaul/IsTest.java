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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class IsTest {

    @Test
	public void emptyObject() throws Exception {
        Object value = 12;
        Assert.assertFalse("not null", Is.empty(value));
        value = null;
        Assert.assertTrue("null", Is.empty(value));
    }

    @Test
	public void emptyString() throws Exception {
        String value = "jablko";
        Assert.assertFalse("not null not empty", Is.empty(value));
        value = "";
        Assert.assertTrue("empty", Is.empty(value));
        value = null;
        Assert.assertTrue("null", Is.empty(value));
    }

    @Test
	public void emptyCollection() throws Exception {
        List value = null;
        Assert.assertTrue("null", Is.empty(value));
        value = new ArrayList();
        Assert.assertTrue("empty", Is.empty(value));
        value.add("");
        Assert.assertFalse("not null not empty", Is.empty(value));
    }

    @Test
	public void emptyArray() throws Exception {
        Integer[] value = null;
        Assert.assertTrue("null", Is.empty(value));
        value = new Integer[]{};
        Assert.assertTrue("empty", Is.empty(value));
        value = new Integer[]{1};
        Assert.assertFalse("not null not empty", Is.empty(value));
    }

    @Test
	public void emptyPrimitiveArray() throws Exception {
        int[] value = null;
        Assert.assertTrue("null", Is.empty(value));
        value = new int[]{};
        Assert.assertTrue("empty", Is.empty(value));
        value = new int[]{1};
        Assert.assertFalse("not null not empty", Is.empty(value));
    }

    @Test
	public void emptyMap() throws Exception {
        Map value = null;
        Assert.assertTrue("null", Is.empty(value));
        value = new HashMap();
        Assert.assertTrue("empty", Is.empty(value));
        value.put("d", "d");
        Assert.assertFalse("not null not empty", Is.empty(value));
    }

    @Test
	public void zeroInteger() throws Exception {
        Integer value = null;
        Assert.assertTrue("null", Is.zero(value));
        value = 12;
        Assert.assertFalse("12", Is.zero(value));
        value = 0;
        Assert.assertTrue("0", Is.zero(value));
    }

    @Test
	public void zeroInt() throws Exception {
        int value = 0;
        Assert.assertTrue("0", Is.zero(value));
        value = 12;
        Assert.assertFalse("12", Is.zero(value));
    }


    @Test
	public void truthBoolean() throws Exception {
        Boolean value = null;
        Assert.assertFalse("null", Is.truth(value));
        value = true;
        Assert.assertTrue("true", Is.truth(value));
        value = false;
        Assert.assertFalse("false", Is.truth(value));
    }


    @Test
	public void truthBool() throws Exception {
        boolean value = false;
        Assert.assertFalse("false", Is.truth(value));
        value = true;
        Assert.assertTrue("true", Is.truth(value));
    }
}
