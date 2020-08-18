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

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author antons
 */
public class EWTest {
	
    
    @Test
	public void tagOnly() throws Exception {
        String file = "src/test/resources/xml-test.xml";
        Document doc = Xml.documentFromFile(file, false);
        Assert.assertNotNull("doc", doc);
        EW root = EW.elem(doc.getDocumentElement());
        Assert.assertTrue("root", "root".equals(root.tag()));
        List<EW> nodes = root.children();
        Assert.assertTrue("children nodes", nodes.size() == 3);
        Assert.assertTrue("children nodes 1 tag", "node".equals(nodes.get(0).tag()));
        Assert.assertTrue("children nodes 1 order", "1".equals(nodes.get(0).attr("order")));
        Assert.assertTrue("children nodes 2 tag", "node".equals(nodes.get(1).tag()));
        Assert.assertTrue("children nodes 2 order", "2".equals(nodes.get(1).attr("order")));
        Assert.assertTrue("children nodes 1 tag", "name".equals(nodes.get(2).tag()));
        Assert.assertTrue("children nodes 1 order", "third".equals(nodes.get(2).attr("name")));
        
        nodes = root.childrenByTagName("node");
        Assert.assertTrue("children2 nodes", nodes.size() == 2);
        Assert.assertTrue("children2 nodes 1 tag", "node".equals(nodes.get(0).tag()));
        Assert.assertTrue("children2 nodes 1 order", "1".equals(nodes.get(0).attr("order")));
        Assert.assertTrue("children2 nodes 2 tag", "node".equals(nodes.get(1).tag()));
        Assert.assertTrue("children2 nodes 2 order", "2".equals(nodes.get(1).attr("order")));
        
        nodes = root.childrenByTagName("name");
        Assert.assertTrue("children3 nodes", nodes.size() == 1);
        Assert.assertTrue("children3 nodes 1 tag", "name".equals(nodes.get(0).tag()));
        Assert.assertTrue("children3 nodes 1 order", "third".equals(nodes.get(0).attr("name")));
        
        nodes = root.elementsByTagName("node");
        Assert.assertTrue("elements2 nodes", nodes.size() == 6);
        Assert.assertTrue("elements2 nodes 1 tag", "node".equals(nodes.get(0).tag()));
        Assert.assertTrue("elements2 nodes 1 order", "1".equals(nodes.get(0).attr("order")));
        Assert.assertTrue("elements2 nodes 2 tag", "node".equals(nodes.get(1).tag()));
        Assert.assertTrue("elements2 nodes 2 order", "1_1".equals(nodes.get(1).attr("order")));
        Assert.assertTrue("elements2 nodes 3 tag", "node".equals(nodes.get(5).tag()));
        Assert.assertTrue("elements2 nodes 3 order", "2_2".equals(nodes.get(5).attr("order")));
        
        nodes = root.elementsByTagName("name");
        Assert.assertTrue("elements3 nodes", nodes.size() == 3);
        Assert.assertTrue("elements3 nodes 1 tag", "name".equals(nodes.get(0).tag()));
        Assert.assertTrue("elements3 nodes 1 order", "first".equals(nodes.get(0).attr("name")));
        Assert.assertTrue("elements3 nodes 2 tag", "name".equals(nodes.get(1).tag()));
        Assert.assertTrue("elements3 nodes 2 order", "second".equals(nodes.get(1).attr("name")));
        Assert.assertTrue("elements3 nodes 3 tag", "name".equals(nodes.get(2).tag()));
        Assert.assertTrue("elements3 nodes 3 order", "third".equals(nodes.get(2).attr("name")));
        
    }
    
    @Test
	public void tagWithNs() throws Exception {
        String file = "src/test/resources/xml-test.xml";
        Document doc = Xml.documentFromFile(file, false);
        Assert.assertNotNull("doc", doc);
        EW root = EW.elem(doc.getDocumentElement());
        Assert.assertTrue("root", "root".equals(root.tag()));
        List<EW> nodes = null;
        
        nodes = root.childrenByTagNameNS("qq", "node");
        Assert.assertTrue("children2 nodes", nodes.size() == 0);
        
        nodes = root.childrenByTagNameNS("qq", "name");
        Assert.assertTrue("children3 nodes", nodes.size() == 1);
        Assert.assertTrue("children3 nodes 1 tag", "name".equals(nodes.get(0).tag()));
        Assert.assertTrue("children3 nodes 1 order", "third".equals(nodes.get(0).attr("name")));
        
        nodes = root.elementsByTagNameNS("yy", "node");
        Assert.assertTrue("elements2 nodes", nodes.size() == 2);
        Assert.assertTrue("elements2 nodes 1 tag", "node".equals(nodes.get(0).tag()));
        Assert.assertTrue("elements2 nodes 1 order", "2_1".equals(nodes.get(0).attr("order")));
        Assert.assertTrue("elements2 nodes 2 tag", "node".equals(nodes.get(1).tag()));
        Assert.assertTrue("elements2 nodes 2 order", "2_2".equals(nodes.get(1).attr("order")));
        
        nodes = root.elementsByTagNameNS("xx", "name");
        Assert.assertTrue("elements3 nodes", nodes.size() == 1);
        Assert.assertTrue("elements3 nodes 1 tag", "name".equals(nodes.get(0).tag()));
        Assert.assertTrue("elements3 nodes 1 order", "second".equals(nodes.get(0).attr("name")));
        
    }

    @Test
	public void text() throws Exception {
        String file = "src/test/resources/xml-test.xml";
        Document doc = Xml.documentFromFile(file, false);
        Assert.assertNotNull("doc", doc);
        EW root = EW.elem(doc.getDocumentElement());
        Assert.assertTrue("root", "root".equals(root.tag()));
        List<EW> nodes = null;
        
        
        nodes = root.elementsByTagNameNS("yy", "node");
        Assert.assertTrue("elements2 nodes", nodes.size() == 2);
        Assert.assertTrue("elements2 nodes 1 tag", "node".equals(nodes.get(0).tag()));
        Assert.assertTrue("elements2 nodes 1 order", "yy1".equals(nodes.get(0).text()));
        Assert.assertTrue("elements2 nodes 2 tag", "node".equals(nodes.get(1).tag()));
        Assert.assertTrue("elements2 nodes 2 order", "yy2".equals(nodes.get(1).text()));
        
    }

    @Test
	public void attr() throws Exception {
        String file = "src/test/resources/xml-test.xml";
        Document doc = Xml.documentFromFile(file, false);
        Assert.assertNotNull("doc", doc);
        EW root = EW.elem(doc.getDocumentElement());
    
        Assert.assertTrue("attr before 1", root.attr("test") == null);
        Assert.assertTrue("attr before 2", "1".equals(root.attr("preset")));
        Assert.assertTrue("attr size", root.attrNames().size() == 1);
        
    }
    
    @Test
	public void firstTagOnly() throws Exception {
        String file = "src/test/resources/xml-test.xml";
        Document doc = Xml.documentFromFile(file, false);
        Assert.assertNotNull("doc", doc);
        EW root = EW.elem(doc.getDocumentElement());
        Assert.assertTrue("root", "root".equals(root.tag()));
        EW node = root.firstChild();
        Assert.assertTrue("children nodes 1 tag", "node".equals(node.tag()));
        Assert.assertTrue("children nodes 1 order", "1".equals(node.attr("order")));
        
        node = root.firstChildByTagName("node");
        Assert.assertTrue("children2 nodes 1 tag", "node".equals(node.tag()));
        Assert.assertTrue("children2 nodes 1 order", "1".equals(node.attr("order")));
        
        node = root.firstChildByTagName("name");
        Assert.assertTrue("children3 nodes 1 tag", "name".equals(node.tag()));
        Assert.assertTrue("children3 nodes 1 order", "third".equals(node.attr("name")));
        
        node = root.firstElementByTagName("node");
        Assert.assertTrue("elements2 nodes 1 tag", "node".equals(node.tag()));
        Assert.assertTrue("elements2 nodes 1 order", "1".equals(node.attr("order")));
        
        node = root.firstElementByTagName("name");
        Assert.assertTrue("elements3 nodes 1 tag", "name".equals(node.tag()));
        Assert.assertTrue("elements3 nodes 1 order", "first".equals(node.attr("name")));
        
    }
}
