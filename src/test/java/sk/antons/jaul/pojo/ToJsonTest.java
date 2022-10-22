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
package sk.antons.jaul.pojo;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class ToJsonTest {
	
    @Test
	public void object() throws Exception {
        JsonString js = JsonString.instance();
        js.objectStart();
        js.attr("attr1", "value1");
        js.attr("attr2", "value2");
        js.objectEnd();
        String text = js.toString();
        System.out.println(" -- " + text);
        Assert.assertNotNull(text);
        Assert.assertEquals("{\"attr1\":\"value1\",\"attr2\":\"value2\"}", text);
    }
    
    @Test
	public void array() throws Exception {
        JsonString js = JsonString.instance();
        js.arrayStart();
        js.value("value1");
        js.value("value2");
        js.value("value3");
        js.arrayEnd();
        String text = js.toString();
        System.out.println(" -- " + text);
        Assert.assertNotNull(text);
        Assert.assertEquals("[\"value1\",\"value2\",\"value3\"]", text);
    }
    
    @Test
	public void array2() throws Exception {
        JsonString js = JsonString.instance();
        js.objectStart();
        js.attrName("nested");
        js.arrayStart();
        js.objectStart();
        js.attr("pokus", "val\nue");
        js.objectEnd();
        js.objectStart();
        js.attr("pokus2", "value2");
        js.objectEnd();
        js.arrayEnd();
        js.attr("after", "value");
        js.objectEnd();
        String text = js.toString();
        System.out.println(" -- " + text);
        Assert.assertNotNull(text);
        Assert.assertEquals("{\"nested\":[{\"pokus\":\"val\\nue\"},{\"pokus2\":\"value2\"}],\"after\":\"value\"}", text);
    }
    
}
