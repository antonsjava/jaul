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
package sk.antons.jaul.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author antons
 */
public class XmlTest {
	
    @Test
	public void escape() throws Exception {
        String value = null;
        String result = null;
        Assert.assertNull("null unescape", Xml.unescape(result));
        Assert.assertNull("null escape", Xml.escape(value));
        
        value = "";
        result = "";
        Assert.assertTrue("empty unescape", value.equals(Xml.unescape(result)));
        Assert.assertTrue("empty escape", result.equals(Xml.escape(value)));

        value = "&<>\"'";
        result = "&amp;&lt;&gt;&quot;&apos;";
        Assert.assertTrue("xx unescape" + Xml.unescape(result), value.equals(Xml.unescape(result)));
        Assert.assertTrue("xx escape", result.equals(Xml.escape(value)));
    }
    
    @Test
	public void document() throws Exception {
        String file = "src/test/resources/xml-test.xml";
        Document doc = Xml.documentFromFile(file);
        Assert.assertNotNull("doc", doc);
    }
}
