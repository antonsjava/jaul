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
public class ReplaceTest {
	
    @Test
	public void all() throws Exception {
        String where = null;
        String what = null;
        String bywhat = null;
        String value = null;
        value = Replace.all(where, what, bywhat);
        Assert.assertNull("null null null '" + value + "'", value);
        where = "abcba";
        value = Replace.all(where, what, bywhat);
        Assert.assertTrue("x null null '" + value + "'", "abcba".equals(value));
        bywhat = "x";
        value = Replace.all(where, what, bywhat);
        Assert.assertTrue("x none x '" + value + "'", "abcba".equals(value));
        what = "z";
        value = Replace.all(where, what, bywhat);
        Assert.assertTrue("x z x '" + value + "'", "abcba".equals(value));
        what = "a";
        value = Replace.all(where, what, bywhat);
        Assert.assertTrue("x a x '" + value + "'", "xbcbx".equals(value));
        what = "b";
        value = Replace.all(where, what, bywhat);
        Assert.assertTrue("x b x '" + value + "'", "axcxa".equals(value));
        what = "c";
        value = Replace.all(where, what, bywhat);
        Assert.assertTrue("x c x '" + value + "'", "abxba".equals(value));
    }
    
    @Test
	public void first() throws Exception {
        String where = null;
        String what = null;
        String bywhat = null;
        String value = null;
        value = Replace.first(where, what, bywhat);
        Assert.assertNull("null null null '" + value + "'", value);
        where = "abcba";
        value = Replace.first(where, what, bywhat);
        Assert.assertTrue("x null null '" + value + "'", "abcba".equals(value));
        bywhat = "x";
        value = Replace.first(where, what, bywhat);
        Assert.assertTrue("x none x '" + value + "'", "abcba".equals(value));
        what = "z";
        value = Replace.first(where, what, bywhat);
        Assert.assertTrue("x z x '" + value + "'", "abcba".equals(value));
        what = "a";
        value = Replace.first(where, what, bywhat);
        Assert.assertTrue("x a x '" + value + "'", "xbcba".equals(value));
        what = "b";
        value = Replace.first(where, what, bywhat);
        Assert.assertTrue("x b x '" + value + "'", "axcba".equals(value));
        what = "c";
        value = Replace.first(where, what, bywhat);
        Assert.assertTrue("x c x '" + value + "'", "abxba".equals(value));
    }
    
    @Test
	public void last() throws Exception {
        String where = null;
        String what = null;
        String bywhat = null;
        String value = null;
        value = Replace.last(where, what, bywhat);
        Assert.assertNull("null null null '" + value + "'", value);
        where = "abcba";
        value = Replace.last(where, what, bywhat);
        Assert.assertTrue("x null null '" + value + "'", "abcba".equals(value));
        bywhat = "x";
        value = Replace.last(where, what, bywhat);
        Assert.assertTrue("x none x '" + value + "'", "abcba".equals(value));
        what = "z";
        value = Replace.last(where, what, bywhat);
        Assert.assertTrue("x z x '" + value + "'", "abcba".equals(value));
        what = "a";
        value = Replace.last(where, what, bywhat);
        Assert.assertTrue("x a x '" + value + "'", "abcbx".equals(value));
        what = "b";
        value = Replace.last(where, what, bywhat);
        Assert.assertTrue("x b x '" + value + "'", "abcxa".equals(value));
        what = "c";
        value = Replace.last(where, what, bywhat);
        Assert.assertTrue("x c x '" + value + "'", "abxba".equals(value));
    }
}
