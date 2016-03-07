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
package sk.antons.jaul.binary;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class UnicodeTest {
	
    @Test
	public void enc() throws Exception {
        String value = null;
        String result = null;
        Assert.assertNull("null unescape", Unicode.unescape(result));
        Assert.assertNull("null escape", Unicode.escape(value, false));
        
        value = "";
        result = "";
        Assert.assertTrue("empty unescape", value.equals(Unicode.unescape(result)));
        Assert.assertTrue("empty escape", result.equals(Unicode.escape(value, false)));

        value = "Aa\u00e1\u00e4bc\u010c\u010dd\u010fEe\u00e9fghi\u00edjkLl\u013a\u013emn\u0147\u0148o\u00f3\u00f4pQqr\u0155s\u0161t\u0165u\u00favwxy\u00fdz\u017d\u017e";
        result = "Aa\\u00e1\\u00e4bc\\u010c\\u010dd\\u010fEe\\u00e9fghi\\u00edjkLl\\u013a\\u013emn\\u0147\\u0148o\\u00f3\\u00f4pQqr\\u0155s\\u0161t\\u0165u\\u00favwxy\\u00fdz\\u017d\\u017e";
        Assert.assertTrue("xx unescape", value.equals(Unicode.unescape(result)));
        Assert.assertTrue("xx escape", result.equals(Unicode.escape(value, false)));
        
    }
    
    @Test
	public void encJava() throws Exception {
        String value = null;
        String result = null;
        Assert.assertNull("null unescape", Unicode.unescapeJava(result));
        Assert.assertNull("null escape", Unicode.escapeJava(value));
        
        value = "";
        result = "";
        Assert.assertTrue("empty unescape", value.equals(Unicode.unescapeJava(result)));
        Assert.assertTrue("empty escape", result.equals(Unicode.escapeJava(value)));
        
        value = "\n\t\r\\";
        result = "\\n\\t\\r\\\\";
        Assert.assertTrue("zz unescape", value.equals(Unicode.unescapeJava(result)));
        Assert.assertTrue("zz escape" + Unicode.escapeJava(value), result.equals(Unicode.escapeJava(value)));

        value = "\n\t\r\\\"Aa\u00e1\u00e4bc\u010c\u010dd\u010fEe\u00e9fghi\u00edjkLl\u013a\u013emn\u0147\u0148o\u00f3\u00f4pQqr\u0155s\u0161t\u0165u\u00favwxy\u00fdz\u017d\u017e";
        result = "\\n\\t\\r\\\\\\\"Aa\\u00e1\\u00e4bc\\u010c\\u010dd\\u010fEe\\u00e9fghi\\u00edjkLl\\u013a\\u013emn\\u0147\\u0148o\\u00f3\\u00f4pQqr\\u0155s\\u0161t\\u0165u\\u00favwxy\\u00fdz\\u017d\\u017e";
        Assert.assertTrue("xx unescape", value.equals(Unicode.unescapeJava(result)));
        Assert.assertTrue("xx escape", result.equals(Unicode.escapeJava(value)));
        
    }
    
    @Test
	public void encHtml() throws Exception {
        String value = null;
        String result = null;
        Assert.assertNull("null unescape", Unicode.unescapeHtml(result));
        Assert.assertNull("null escape", Unicode.escapeHtml(value, false));
        
        value = "";
        result = "";
        Assert.assertTrue("empty unescape", value.equals(Unicode.unescapeHtml(result)));
        Assert.assertTrue("empty escape", result.equals(Unicode.escapeHtml(value, false)));

        value = "Aa\u00e1\u00e4bc\u010c\u010dd\u010fEe\u00e9fghi\u00edjkLl\u013a\u013emn\u0147\u0148o\u00f3\u00f4pQqr\u0155s\u0161t\u0165u\u00favwxy\u00fdz\u017d\u017e";
        result = "Aa&#225;&#228;bc&#268;&#269;d&#271;Ee&#233;fghi&#237;jkLl&#314;&#318;mn&#327;&#328;o&#243;&#244;pQqr&#341;s&#353;t&#357;u&#250;vwxy&#253;z&#381;&#382;";
        Assert.assertTrue("xx unescape", value.equals(Unicode.unescapeHtml(result)));
        Assert.assertTrue("xx escape", result.equals(Unicode.escapeHtml(value, false)));
        
    }
}
