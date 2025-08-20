
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class ElemTest {

    @Test
	public void elemNameTest() throws Exception {
        Elem elem = Elem.of("pokus");
        Assert.assertEquals("pokus", elem.name().name());
        Assert.assertEquals("pokus", elem.name().prefixedName());
        Assert.assertNull(elem.name().prefix());
        elem = Elem.of("ns:pokus");
        Assert.assertEquals("pokus", elem.name().name());
        Assert.assertEquals("ns:pokus", elem.name().prefixedName());
        Assert.assertEquals("ns", elem.name().prefix());
    }

    @Test
	public void elemChildTest() throws Exception {
        Elem elem = Elem.of("root");
        Assert.assertEquals(0, elem.childrenSize());
        Assert.assertNull(elem.child(0));
        elem.addChild(Elem.of("child"));
        Assert.assertEquals(1, elem.childrenSize());
        Assert.assertNotNull(elem.child(0));
        elem.addChild(Elem.of("child"));
        Assert.assertEquals(2, elem.childrenSize());
        Assert.assertNotNull(elem.child(1));
        elem.child(0).delete();
        Assert.assertEquals(1, elem.childrenSize());
        Assert.assertNotNull(elem.child(0));
    }

    @Test
	public void elemAttrTest() throws Exception {
        Elem elem = Elem.of("root");
        Assert.assertEquals(0, elem.attributeSize());
        Assert.assertNull(elem.attribute(0));
        elem.addAttr("attr1", "value1");
        Assert.assertEquals(1, elem.attributeSize());
        Assert.assertNotNull(elem.attribute(0));
        Assert.assertNotNull(elem.attr("attr1"));
        Assert.assertNull(elem.attr("ns:attr1"));
        elem.addAttr("ns:attr1", "value2");
        Assert.assertEquals(2, elem.attributeSize());
        Assert.assertNotNull(elem.attribute(1));
        Assert.assertNotNull(elem.attr("attr1"));
        Assert.assertNotNull(elem.attr("ns:attr1"));
        Assert.assertEquals(1, elem.attributes("ns:attr1").size());
        Assert.assertEquals(2, elem.attributes("attr1").size());
        Assert.assertEquals(0, elem.attributes("attr2").size());
        elem.attribute(0).delete();
        Assert.assertEquals(1, elem.attributeSize());
        Assert.assertNotNull(elem.attribute(0));
        Assert.assertNotNull(elem.attr("attr1"));
        Assert.assertNotNull(elem.attr("ns:attr1"));
        Assert.assertEquals(1, elem.attributes("ns:attr1").size());
        Assert.assertEquals(1, elem.attributes("attr1").size());
        Assert.assertEquals(0, elem.attributes("attr2").size());
    }

    @Test
	public void elemFindTest() throws Exception {
        Elem elem = Elem.parse(structuredXml);
        Assert.assertEquals(0, elem.find("address", "number").all().size());
        Assert.assertEquals(1, elem.find("address", "city").allText().size());
        Assert.assertEquals("Bratislava", elem.find("address", "city").firstText());
        List<String> list = Arrays.asList("Sagan", "Miller", "Lem", "Adams");
        Assert.assertEquals(list, elem.find("book", "author", "surname").allText());
        List<Elem> books = elem.find("book").all();
        Assert.assertEquals("Solaris", books.get(2).find("title").first().text());
        Assert.assertEquals("4", books.get(3).attr("id"));
    }

    @Test
	public void elemModTest() throws Exception {
        Elem elem = Elem.parse(structuredXml);
        List<Elem> books = elem.find("book").all();
        Elem solaris = books.get(2);
        Elem title = solaris.find("title").first();
        title.text("Solaris2");
        books = elem.find("book").all();
        Assert.assertEquals("Solaris2", books.get(2).find("title").first().text());
    }

    @Test
	public void elemModStrTest() throws Exception {
        Elem elem = Elem.parse(structuredXml);
        List<Elem> books = elem.find("book").all();
        Elem solaris = books.get(2);
        Elem title = solaris.find("title").first();
        title.text("Solaris2");
        title.replace(Elem.of("title").text("Solaris3"));
        books = elem.find("book").all();
        Assert.assertEquals("Solaris3", books.get(2).find("title").firstText());
    }

    private int elemByElemCounter = 0;

    @Test
	public void elemByElem() throws Exception {
        elemByElemCounter = 0;
        Iterator<Elem> iter = Elem.elemByElem()
            .checker((path, elem) -> {
                System.out.println("checker path: " + path);
                System.out.println("checker elem: " + elem);
                return "/library/book/author".equals(path) || "/library/address".equals(path);
            })
            .consumer(elem -> {
                System.out.println("consumed: " + elem.toString());
                elemByElemCounter++;
            })
            .source(structuredXml)
            .iterator();
        while(iter.hasNext()) {
            Elem next = iter.next();
            System.out.println(" " + next);
            elemByElemCounter++;
        }
        Assert.assertEquals(5, elemByElemCounter);
    }

    private static String structuredXml=
"<library closed=\"false\">\n" +
"  <address>\n" +
"    <street>Nova</street>\n" +
"    <city>Bratislava</city>\n" +
"    <zip>80010</zip>\n" +
"  </address>\n" +
"  <book id=\"1\">\n" +
"    <title>Contact</title>\n" +
"    <author>\n" +
"        <name>Carl</name>\n" +
"        <surname>Sagan</surname>\n" +
"    </author>\n" +
"  </book>\n" +
"  <book id=\"2\">\n" +
"    <title>A Canticle for Leibowitz</title>\n" +
"    <author>\n" +
"        <name>Walter</name>\n" +
"        <surname>Miller</surname>\n" +
"    </author>\n" +
"  </book>\n" +
"  <book id=\"3\">\n" +
"    <title>Solaris</title>\n" +
"    <author>\n" +
"        <name>Stanislaw</name>\n" +
"        <surname>Lem</surname>\n" +
"    </author>\n" +
"  </book>\n" +
"  <book id=\"4\">\n" +
"    <title>The Hitchhiker's Guide to the Galaxy</title>\n" +
"    <author>\n" +
"        <name>Douglas</name>\n" +
"        <surname>Adams</surname>\n" +
"    </author>\n" +
"  </book>\n" +
"</library>";

}
