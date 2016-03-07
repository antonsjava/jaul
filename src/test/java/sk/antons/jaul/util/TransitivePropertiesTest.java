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
package sk.antons.jaul.util;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class TransitivePropertiesTest {
	
    @Test
	public void closure() throws Exception {
        Properties prop = new Properties();
        prop.put("prop1", "value1");
        prop.put("prop2", "value2 ${prop1}");
        prop.put("prop3", "${prop2} value3");
        prop.put("prop4", "value4 ${prop3} value4");
        TransitiveProperties.makeClosure(prop);
        Assert.assertTrue("prop1", "value1".equals(prop.getProperty("prop1")));
        Assert.assertTrue("prop2", "value2 value1".equals(prop.getProperty("prop2")));
        Assert.assertTrue("prop3", "value2 value1 value3".equals(prop.getProperty("prop3")));
        Assert.assertTrue("prop4", "value4 value2 value1 value3 value4".equals(prop.getProperty("prop4")));
    }
    
    @Test
	public void wrap() throws Exception {
        Properties prop = new Properties();
        prop = TransitiveProperties.wrap(prop);
        prop.put("prop1", "value1");
        prop.put("prop2", "value2 ${prop1}");
        prop.put("prop3", "${prop2} value3");
        prop.put("prop4", "value4 ${prop3} value4");
        Assert.assertTrue("prop1", "value1".equals(prop.getProperty("prop1")));
        Assert.assertTrue("prop2", "value2 value1".equals(prop.getProperty("prop2")));
        Assert.assertTrue("prop3", "value2 value1 value3".equals(prop.getProperty("prop3")));
        Assert.assertTrue("prop4", "value4 value2 value1 value3 value4".equals(prop.getProperty("prop4")));
    }
    
}
