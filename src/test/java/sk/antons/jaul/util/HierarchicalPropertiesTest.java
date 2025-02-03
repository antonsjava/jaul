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
public class HierarchicalPropertiesTest {

    @Test
	public void property() throws Exception {
        Properties prop = new Properties();
        prop.put("foo.bar..name", "value3");
        prop.put("foo..name", "value2");
        prop.put("name", "value1");
        prop.put("prop2", "v2");
        HierarchicalProperties hprops = HierarchicalProperties.instance(prop);
        Assert.assertEquals("value1", hprops.property("name"));
        Assert.assertEquals("value2", hprops.property("foo..name"));
        Assert.assertEquals("value3", hprops.property("foo.bar..name"));
        Assert.assertEquals("value3", hprops.property("foo.bar.child..name"));
        Assert.assertEquals("v2", hprops.property("prop2"));
        Assert.assertEquals("v2", hprops.property("..prop2"));
        Assert.assertEquals("v2", hprops.property("p1..prop2"));
        Assert.assertEquals("v2", hprops.property("p2.p1..prop2"));
        Assert.assertEquals("v2", hprops.property("p3.p2.p1..prop2"));
        Assert.assertNull(hprops.property("p3.p2.p1..prop3"));
        Assert.assertNull(hprops.property(null));
        Assert.assertEquals("xx", hprops.property("p3.p2.p1..prop4", "xx"));
        Assert.assertEquals("xx", hprops.property(null, "xx"));
    }


}
