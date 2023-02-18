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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.AsRuntimeEx;

/**
 * Simplified XML API. The class represents XML element.
 * Following are restricted:
 * <ul>
 * <li> process instructions are not supported/ignored
 * <li> comments are not supported/ignored
 * <li> texts are allowed only in list elements.
 * <li> no namespace logic is supported 
 * </ul>
 * API allows to create and manipulate such limited XMLs like this
 * <pre>
 * 
 *   Elem root = Elem.of("root")
 *       .addAttr("attr1", "value1")
 *       .addAttr("ns:attr2", "value2")
 *       .addChild(Elem.of("child1")
 *                    .addChild(Elem.of("to:list")
 *                                 .text("listdata")))
 *        .addChild(Elem.of("child2").text("value33"))
 *        .addChild(Elem.of("child2").text("value44"));
 * 
 *   String xml = root.export().toString();
 *   String formatedxml = root.export().indent("  ").toString();
 *
 *   List<Elem> list = root.find("child1", "list").all());
 *   Elem descendant = root.find("child1", "list").first());
 * 
 *   List<String> listtext = root.find("child1", "list").allText());
 *   String descendanttext = root.find("child1", "list").firstText());
 *
 *   root.find("child2").first().replace(Elem.of("child5").text("value88"));
 *    
 *   root.attribute(1).delete();
 *
 *   Elem anotherroot = Elem.parse(new FileInputStream("/tmp/example.xml"));
 * 
 * </pre>
 * @author antons
 */
public class Elem {
    Name name;
    Elem parent;
    List<Attr> attrs;
    List<Elem> children;
    String text;

    private Elem() {}
    
    /**
     * Name of element
     * @return 
     */
    public Name name() { return name; } 

    /**
     * Text inside element.
     * @return text of element or null.
     */
    public String text() { return text; }
    /**
     * Sets text to element. Texts are supported only on leaves.
     * @param value text to ve set (simple escaping is processed here)
     * @return this Elem.
     */
    public Elem text(String value) { 
        if(children != null) throw new IllegalStateException("It is possible to set text only to list element. This element has child");
        this.text = value;
        return this; 
    }

    /**
     * Construct new Elem witn name 
     * @param name name od elelement like 'root' or 'ns2:root';
     * @return new created elelemt
     */
    public static Elem of(String name) {
        if(Is.empty(name)) throw new IllegalArgumentException("element name must be specified");
        Elem elem = new Elem();
        elem.name = Name.of(name);
        return elem;
    }
    
    /**
     * Returns value of first attribute with specified name. 
     * It returns null is attribute with such name not exists.
     * @param name name of the attribute 
     * @return value of specified attribute
     */
    public String attr(String name) {
        if(Is.empty(name)) return null;
        if(Is.empty(attrs)) return null;
        for(Attr attr : attrs) {
            if(name.equals(attr.name.name)) return attr.value;
            else if(name.equals(attr.name.prefixname)) return attr.value;
        }
        return null;
    }
    
    /**
     * Returns first attribute with specified name. 
     * It returns null is attribute with such name not exists.
     * @param name name of the attribute 
     * @return first specified attribute
     */
    public Attr attribute(String name) {
        if(Is.empty(name)) return null;
        if(Is.empty(attrs)) return null;
        for(Attr attr : attrs) {
            if(name.equals(attr.name.name)) return attr;
            else if(name.equals(attr.name.prefixname)) return attr;
        }
        return null;
    }
    
    /**
     * Returns all attributes with specified name. 
     * @param name name of the attribute 
     * @return all specified attributes
     */
    public List<Attr> attributes(String name) {
        List<Attr> list = new ArrayList<Attr>();
        if(Is.empty(name)) return list;
        if(Is.empty(attrs)) return list;
        for(Attr attr : attrs) {
            if(name.equals(attr.name.name)) list.add(attr);
            else if(name.equals(attr.name.prefixname)) list.add(attr);
        }
        return list;
    }
    
    /**
     * Size of directly nested elements.
     * @return Size of directly nested elements.
     */
    public int childrenSize() { return Get.size(children); }

    /**
     * Returns nth nested element or null if such element not exists. 
     * @param index index of nested element
     * @return specified nested element
     */
    public Elem child(int index) { return Get.nth(children, index); }
    
    /**
     * Add child element to this element. Element is added to end.
     * @param elem element to add
     * @return this Elem
     */
    public Elem addChild(Elem elem) { return addChild(elem, -1); }
    
    /**
     * Add child element to this element. Element is added to specified position.
     * @param elem element to add
     * @param index position where element should be added
     * @return this Elem
     */
    public Elem addChild(Elem elem, int index) {
        if(Is.empty(elem)) throw new IllegalArgumentException("elem must be specified");
        if(text != null) throw new IllegalStateException("It is possible to add child only to non text element. This element has text already - " + text);
        if(children == null) children = new ArrayList<Elem>();
        if(index < 0) index = children.size();
        elem.parent = this;
        children.add(index, elem);
        return this;
    }
    
    /**
     * Size of attributes of this element.
     * @return Size of attributes of this element.
     */
    public int attributeSize() { return Get.size(attrs); }
    
    /**
     * Returns nth attribute.
     * @param index inex of attribute to be returned
     * @return nth attribute
     */
    public Attr attribute(int index) { return Get.nth(attrs, index); }
    
    /**
     * Add new attribute to element. 
     * @param name name of attribute
     * @param value value of attribute
     * @return this Elem
     */
    public Elem addAttr(String name, String value) { return addAttr(name, value, -1); }
    
    /**
     * Add new attribute to element. Attribute will be aded to specified position.
     * @param name name of attribute
     * @param value value of attribute
     * @param index index where attribute should be added
     * @return this Elem
     */
    public Elem addAttr(String name, String value, int index) { return addAttrRaw(name, simpleEncode(value), index); }
    private Elem addAttrRaw(String name, String value, int index) {
        if(Is.empty(name)) throw new IllegalArgumentException("attribute name must be specified");
        if(attrs == null) attrs = new ArrayList<Attr>();
        if(index < 0) index = attrs.size();
        attrs.add(index, Attr.of().name(Name.of(name)).value(value).parent(this));
        return this;
    }


    private static String simpleEncode(String value) {
        if(Is.empty(value)) return value;
        StringBuilder sb = new StringBuilder(value.length()*2);
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch(c) {
                case '\r': sb.append("&#13;"); break;
                case '\n': sb.append("&#10;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '[':
                    if(value.startsWith("<![CDATA[", i-2)) {
                        sb.setLength(sb.length()-5);
                        int pos = value.indexOf("]]>", i);
                        if(pos > -1) {
                            sb.append(value.substring(i-2, pos+3));
                            i = pos + 2;
                        } else {
                            sb.append(value.substring(i-2));
                            i = value.length();
                        }
                    } else {
                        sb.append(c); 
                    }
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Builder for exporting xml data to string form.
     * @return Exporter
     */
    public Exporter export() { return this.new Exporter(); };
    
    /**
     * Start method for searchin sub elements by name.
     * @param path path to sub element
     * @return Finder
     */
    public Finder find(String... path) { return this.new Finder().path(path); };
 
    private int attrIndex(Attr attr) {
        return attrs == null ? -1 : attrs.indexOf(attr);
    }
    
    private int childIndex(Elem elem) {
        return children == null ? -1 : children.indexOf(elem);
    }
    
    /**
     * Disconnect this element from xml tree.
     * @return parent Elem
     */
    public Elem delete() {
        if(parent == null) return null;
        int pos = parent.childIndex(this);
        if(pos > -1) parent.children.remove(pos);
        Elem e = parent;
        parent = null;
        return e;
    }
    
    /**
     * Replace this element in xml tree. Work only if this element is not root.
     * @param elem mew element to replace this one
     * @return parent Elem
     */
    public Elem replace(Elem elem) {
        if(parent == null) return null;
        int pos = parent.childIndex(this);
        Elem e = parent.addChild(elem, pos);
        delete();
        return e;
    }
    
    /**
     * Add new element to xml just before this element. Work only if this element is not root.
     * @param elem mew element to be added to xml
     * @return parent Elem
     */
    public Elem prepend(Elem elem) {
        if(parent == null) return null;
        int pos = parent.childIndex(this);
        return parent.addChild(elem, pos);
    }

    /**
     * Add new element to xml just after this element. Work only if this element is not root.
     * @param elem mew element to be added to xml
     * @return parent Elem
     */
    public Elem append(Elem elem) {
        if(parent == null) return null;
        int pos = parent.childIndex(this);
        return parent.addChild(elem, pos+1);
    }

    /**
     * Collects all texts from nested elements.
     * @return collected texts
     */
    public String texts() {
        StringBuilder sb = new StringBuilder();
        texts(Elem.this, sb);
        return sb.toString();
    }
    
    private void texts(Elem elem, StringBuilder sb) {
        if(elem.text != null) {
            if(sb.length() > 0) sb.append(' ');
            sb.append(elem.text);
        } else {
            if(!Is.empty(elem.children)) {
                for(Elem el : elem.children) {
                    texts(el, sb);
                }
            }
        }
    }

    public String toString() {
        return export().toString();
    }

    /**
     * Parse xml in string to Elem tree.
     * @param xml input xml
     * @return new tree of ELem instancies.
     */
    public static Elem parse(String xml) {
        InputSource iso = new InputSource(new StringReader(xml));
        return parse(iso);
    }
    
    /**
     * Parse xml in input stream to Elem tree.
     * @param xml input xml
     * @return new tree of ELem instancies.
     */
    public static Elem parse(InputStream is) {
        InputSource iso = new InputSource(is);
        return parse(iso);
    }

    /**
     * Parse xml in reader to Elem tree.
     * @param xml input xml
     * @return new tree of ELem instancies.
     */
    public static Elem parse(Reader reader) {
        InputSource iso = new InputSource(reader);
        return parse(iso);
    }

    private static Elem parse(InputSource is) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            Parser parser = new Parser();
            saxParser.parse(is , parser);
            return parser.root;
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e);
        }
    }

    /**
     * Deep clone of this element.  
     * @return deep clone of Elem
     */
    public Elem clone() {
        Elem elem = Elem.of(name.prefixname);
        elem.text = text;

        if(!Is.empty(attrs)) {
            for(Attr attr : attrs) {
                elem.addAttrRaw(attr.name.prefixname, attr.value, -1);
            }
        }
        
        if(!Is.empty(children)) {
            for(Elem el : children) {
                elem.addChild(el.clone());
            }
        }
        return elem;
    }
    
// -------------------- subclasses start ---------------

    /**
     * Attribute of element.
     */
    public static class Attr {
        Name name;
        String value;
        Elem parent;
        
        private Attr() {}
        /**
         * Name of attribute
         * @return 
         */
        public Name name() { return name; } 
        /**
         * Value of attribute
         * @return 
         */
        public String value() { return value; }
        private Attr name(Name value) { this.name = value; return this; } 
        private Attr parent(Elem value) { this.parent = value; return this; }
        private Attr valueRaw(String value) { this.value = value; return this; }
        private Attr value(String value) { 
            if(value != null) value = simpleEncode(value);
            this.value = value; 
            return this; 
        }
        
        private static Attr of() { return new Attr(); } 

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name.prefixedName());
            if(value != null) {
                sb.append("=\"").append(value).append('"');
            }
            return sb.toString();
        }

        /**
         * Deletes this attribute from element.
         * @return Element of this attribute
         */
        public Elem delete() {
            if(parent == null) return null;
            int pos = parent.attrIndex(this);
            if(pos > -1) parent.attrs.remove(pos);
            Elem e = parent;
            parent = null;
            return e;
        }
        
        /**
         * Add new attribute this element just before this attribute.
         * @return Element of this attribute
         */
        public Elem prepend(String name, String value) {
            if(parent == null) return null;
            int pos = parent.attrIndex(this);
            return parent.addAttr(name, value, pos);
        }

        /**
         * Add new attribute this element just after this attribute.
         * @return Element of this attribute
         */
        public Elem append(String name, String value) {
            if(parent == null) return null;
            int pos = parent.attrIndex(this);
            return parent.addAttr(name, value, pos+1);
        }
        
        /**
         * Replate this attribute with newone.
         * @return Element of this attribute
         */
        public Elem replace(String name, String value) {
            if(parent == null) return null;
            int pos = parent.attrIndex(this);
            Elem e = parent.addAttr(name, value, pos);
            delete();
            return e;
        }
        
    }
    
    /**
     * Name of element or attribute.
     */
    public static class Name {
        
        String name;
        String prefix;
        String prefixname;
        
        private Name() {}
        /**
         * Name without namespace prefix.
         * @return name without namespace prefix
         */
        public String name() { return name; } 
        /**
         * Namespace prefix if exists.
         * @return namespace prefix
         */
        public String prefix() { return prefix; }
        /**
         * Name with namespace prefix if exists. Otherwise only name.
         * @return name with namespace prefix
         */
        public String prefixedName() { return prefixname; }
        private Name name(String value) { 
            if(Is.empty(value)) throw new IllegalArgumentException("name can't be empty");
            int pos = value.indexOf(':');
            if(pos<0) {
                this.name = value; 
                this.prefix = null; 
                this.prefixname = value; 
            } else {
                this.name = value.substring(pos+1); 
                this.prefix = value.substring(0, pos); 
                this.prefixname = value; 
            }
            return this; 
        } 
        
        private static Name of(String name) { return new Name().name(name); } 

    }

    /**
     * Exports Elem to string form.
     */
    public class Exporter {
        boolean declaration = false;
        String encoding = "utf-8";
        String indent = null;
        boolean indentAttrs = false;
        
        private Exporter() { }
        /**
         * True if xml declaration should be printed. (default false)
         */
        public Exporter declaration(boolean value) { this.declaration = value; return this; } 
        /**
         * True if atributes should be indented. (default false)
         */
        public Exporter indentAttrs(boolean value) { this.indentAttrs = value; return this; } 
        /**
         * Character encoding. (useful for output stream export) {default utf-8}
         */
        public Exporter encoding(String value) { this.encoding = value; return this; } 
        /**
         * String to new used for indendation. (If null no formating is applied)
         */
        public Exporter indent(String value) { this.indent = value; return this; } 
        
        /**
         * Exports Elem to string. 
         */
        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString(sb);
            return sb.toString();
        }
        
        /**
         * Exports Elem to writer. 
         */
        public void toWriter(Writer writer) {
            toString(writer);
        }
        
        /**
         * Exports Elem to output stream. 
         */
        public void toOutputStream(OutputStream os) {
            try {
                Writer writer = new OutputStreamWriter(os, encoding);
                toWriter(writer);
                writer.flush();
            } catch(Exception e) {
                throw AsRuntimeEx.state(e);
            }
        }

        private void toString(Appendable sb) {
            try {
                if(indent == null) {
                    if(declaration) sb.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" standalone=\"no\" ?>");
                    toString(sb, Elem.this);
                } else {
                    if(declaration) sb.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" standalone=\"no\" ?>\n");
                    toPrettyString(sb, Elem.this, "");
                }
            } catch(Exception e) {
                throw AsRuntimeEx.state(e);
            }
        }
        
        private void toString(Appendable sb, Elem elem) {
            try {
                sb.append('<').append(elem.name.prefixedName());
                if(!Is.empty(elem.attrs)) {
                    for(Attr attr : elem.attrs) {
                        sb.append(' ').append(attr.toString());
                    }
                }
                if(Is.empty(elem.children) && Is.empty(elem.text)) {
                    sb.append("/>");
                } else {
                    sb.append('>');
                    if(Is.empty(elem.children)) {
                        sb.append(elem.text);
                    } else {
                        for(Elem elem2 : elem.children) {
                            toString(sb, elem2);
                        }
                    }
                    sb.append("</").append(elem.name.prefixedName()).append('>');
                }
            } catch(Exception e) {
                throw AsRuntimeEx.state(e);
            }
        }
        
        private void toPrettyString(Appendable sb, Elem elem, String prefix) {
            try {
                sb.append(prefix);
                sb.append('<').append(elem.name.prefixedName());
                if(!Is.empty(elem.attrs)) {
                    for(Attr attr : elem.attrs) {
                        if(indentAttrs) sb.append('\n').append(prefix).append(indent).append(indent);
                        sb.append(' ').append(attr.toString());
                    }
                }
                if(Is.empty(elem.children) && Is.empty(elem.text)) {
                    sb.append("/>\n");
                } else {
                    if(Is.empty(elem.children)) {
                        sb.append(">");
                        sb.append(elem.text);
                        sb.append("</").append(elem.name.prefixedName()).append(">\n");
                    } else {
                        sb.append(">\n");
                        for(Elem elem2 : elem.children) {
                            toPrettyString(sb, elem2, prefix + indent);
                        }
                        sb.append(prefix).append("</").append(elem.name.prefixedName()).append(">\n");
                    }
                }
            } catch(Exception e) {
                throw AsRuntimeEx.state(e);
            }
        }

    }

    /**
     * Helper for searching xml tree.
     */
    public class Finder {
        String[] path;
        
        private Finder() { }
        private Finder path(String... path) { this.path = path; return this; } 
        
        /**
         * finds all elements with specified path.
         * @return all elements with specified path
         */
        public List<Elem> all() {
            List<Elem> list = new ArrayList<Elem>();
            if(Is.empty(path)) return list;
            all(Elem.this, path, 0, list);
            return list;
        }
        
        private void all(Elem elem, String[] path, int index, List<Elem> list) {
            if(index>=path.length) {
                list.add(elem);
            } else {
                if(Is.empty(elem.children)) return;
                for(Elem el : elem.children) {
                    if(path[index].equals(el.name.name)) {
                        all(el, path, index+1, list);
                    }
                }
            }
        }
        
        /**
         * finds all elements with specified path and colles their tests.
         * @return texts from all elements with specified path
         */
        public List<String> allText() {
            List<String> list = new ArrayList<String>();
            if(Is.empty(path)) return list;
            allText(Elem.this, path, 0, list);
            return list;
        }
        
        private void allText(Elem elem, String[] path, int index, List<String> list) {
            if(index>=path.length) {
                list.add(elem.texts());
            } else {
                if(Is.empty(elem.children)) return;
                for(Elem el : elem.children) {
                    if(path[index].equals(el.name.name)) {
                        allText(el, path, index+1, list);
                    }
                }
            }
        }
        
        /**
         * Finds first element with specified path.
         * @return first element with specified path
         */
        public Elem first() {
            if(Is.empty(path)) return null;
            return first(Elem.this, path, 0);
        }
        
        private Elem first(Elem elem, String[] path, int index) {
            if(index>=path.length) {
                return elem;
            } else {
                if(!Is.empty(elem.children)) {
                    for(Elem el : elem.children) {
                        if(path[index].equals(el.name.name)) {
                            Elem e = first(el, path, index+1);
                            if(e != null) return e;
                        }
                    }
                }
            }
            return null;
        }
        
        /**
         * Finds first element with specified path and returns its text.
         * @return text of first element with specified path
         */
        public String firstText() {
            Elem e = first();
            return e == null ? null : e.texts();
        }
    }

    private static class Parser extends DefaultHandler {

        Elem root = null;
        Stack<Elem> stack = new Stack<Elem>();
        StringBuilder sb = new StringBuilder();
        boolean lastWasStartElelemnt = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            sb.setLength(0);
            lastWasStartElelemnt = true;
            if(qName != null) {
                Elem elem = Elem.of(qName);
                if(root == null) root = elem;
                if(stack.size() > 0) stack.peek().addChild(elem);
                stack.add(elem);

                for(int i = 0; i < attributes.getLength(); i++) {
                    elem.addAttrRaw(attributes.getQName(i), attributes.getValue(i), -1);
                }
            }

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName); 
            Elem elem = stack.pop();
            if((elem != null) && lastWasStartElelemnt) elem.text(sb.toString());
            lastWasStartElelemnt = false;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length); 
            if(lastWasStartElelemnt) sb.append(ch, start, length);
        }
    
    }

// -------------------- subclasses end ---------------


    public static void main(String[] argv) throws FileNotFoundException {
        Elem root = Elem.of("pokus")
            .addAttr("attr1", "value1")
            .addAttr("attr2", "val <![CDATA[ioooooi]]>u\ne<2")
            .addAttr("ns:attr3", "value2");
        root.addChild(Elem.of("child1").addChild(Elem.of("to:list").text("uplne dole")));
        root.addChild(Elem.of("child2").text("value33"));
        root.addChild(Elem.of("child2").text("value44"));
        System.out.println(" -- " + root.export().toString());
        System.out.println(" -- " + root.export().indent("  ").toString());

        System.out.println(" " + root.find("child1", "list").allText());
        System.out.println(" " + root.find("child2").allText());

        root.find("child2").first().replace(Elem.of("child5").text("value88"));
        System.out.println(" -- " + root.export().indent("  ").toString());

        
        root.export().indent("  ").toOutputStream(new FileOutputStream("/tmp/a.xml"));
        
        root.attribute(1).delete();
        Elem eee = Elem.parse(root.export().toString());
        System.out.println(" ---<<< " + eee.export().indent("  ").toString());
        System.out.println(" ---<<< " + root.attributes("attr1"));

        //Elem aaa = Elem.parse(new FileInputStream("/tmp/aaa/aviso-send.xsd"));
        //System.out.println(" .... " + aaa.export().indent("  ").toString());
        //aaa.export().indent("  ").toOutputStream(new FileOutputStream("/tmp/aaa/aviso-send2.xsd"));

        System.out.println(" ...oo. " + root.clone().export().indent("  ").toString());

    }
}
