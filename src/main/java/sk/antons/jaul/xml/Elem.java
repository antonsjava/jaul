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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Stack;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;
import sk.antons.jaul.binary.Html;
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
 *   List&lt;Elem&gt; list = root.find("child1", "list").all());
 *   Elem descendant = root.find("child1", "list").first());
 *
 *   List&lt;String&gt; listtext = root.find("child1", "list").allText());
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
     * Parent of element
     * @return
     */
    public Elem parent() { return parent; }

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
        return texts(4096);
    }
    /**
     * Collects all texts from nested elements.
     * @param length expected length
     * @return collected texts
     */
    public String texts(int length) {
        StringBuilder sb = new StringBuilder(length);
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
     * @param is input xml
     * @return new tree of ELem instancies.
     */
    public static Elem parse(InputStream is) {
        InputSource iso = new InputSource(is);
        return parse(iso);
    }

    /**
     * Parse xml in reader to Elem tree.
     * @param reader input xml
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

    public static Elem parse(XMLStreamReader reader) {
        try {
            while(!reader.isStartElement()) reader.next();
            String prefix = reader.getPrefix();
            String name = reader.getLocalName();
            if(!Is.empty(prefix)) name = prefix + ":" + name;
            Elem elem = Elem.of(name);
            {
                int count = reader.getAttributeCount();
                for(int i = 0; i < count; i++) {
                    String pr = reader.getAttributePrefix(i);
                    String na = reader.getAttributeLocalName(i);
                    if(!Is.empty(pr)) na = pr + ":" + na;
                    String va = reader.getAttributeValue(i);
                    elem.addAttr(na, va);
                }
            }
            {
                int count = reader.getNamespaceCount();
                for(int i = 0; i < count; i++) {
                    String pr = reader.getNamespacePrefix(i);
                    String na = "xmlns";
                    if(!Is.empty(pr)) na = na + ":" + pr;
                    String va = reader.getNamespaceURI(i);
                    elem.addAttr(na, va);
                }
            }
            StringBuilder text = new StringBuilder(300);
            boolean conti = true;
            while(conti && reader.hasNext()) {
                int token = reader.next();
                switch(token) {
                    case XMLStreamConstants.END_ELEMENT:
                        conti = false;
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        Elem e = parse(reader);
                        elem.addChild(e);
                        text = null;
                        break;
                    case XMLStreamConstants.CDATA:
                        if(text != null) {
                            String tt = reader.getText();
                            //if(!Is.empty(tt)) text.append("<![CDATA[").append(tt).append("]]>");
                            if(!Is.empty(tt)) text.append(tt);
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if(text != null) {
                            String tt = reader.getText();
                            if(!Is.empty(tt)) text.append(tt);
                        }
                        break;
                    default:
                }
            }
            if(text != null) elem.text(text.toString());
            return elem;
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e);
        }
    }

    /**
     * Parse xml elem by elem.
     */
    public static ElemByElem elemByElem() {
        return new ElemByElem();
    }


    private static void processElemByElem(InputSource is, BiPredicate<String, Elem> elemChecker, Consumer<Elem> elemConsumer) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            ElemByElemParser parser = ElemByElemParser.instance().elemChecker(elemChecker).elemConsumer(elemConsumer);
            saxParser.parse(is , parser);
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
        public Attr value(String value) { this.value = value; return this; }

        private static Attr of() { return new Attr(); }

        public String toString() { return toString(null); }
        public String toString(Escaping escaping) {
            StringBuilder sb = new StringBuilder(300);
            sb.append(name.prefixedName());
            if(value != null) {
                sb.append("=\"").append(escape(value, escaping)).append('"');
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
        Escaping escaping = Escaping.SIMPLE;
        boolean indentAttrs = false;
        boolean sortAttrs = false;

        private Exporter() { }
        /**
         * True if xml declaration should be printed. (default false)
         */
        public Exporter declaration(boolean value) { this.declaration = value; return this; }
        /**
         * True if attributes should be indented. (default false)
         */
        public Exporter indentAttrs(boolean value) { this.indentAttrs = value; return this; }
        /**
         * True if attributes should be sorted by name. (default false)
         */
        public Exporter sortAttrs(boolean value) { this.sortAttrs = value; return this; }
        /**
         * Character encoding. (useful for output stream export) {default utf-8}
         */
        public Exporter encoding(String value) { this.encoding = value; return this; }
        /**
         * String to new used for indendation. (If null no formating is applied)
         */
        public Exporter indent(String value) { this.indent = value; return this; }
        /**
         * Type of defaulting. (default is SIMPLE)
         */
        public Exporter escaping(Escaping value) { this.escaping = value == null ? Escaping.SIMPLE : value; return this; }

        /**
         * Exports Elem to string.
         */
        public String toString(int expectedLength) {
            StringBuilder sb = new StringBuilder(expectedLength);
            toString(sb);
            return sb.toString();
        }

        /**
         * Exports Elem to string.
         */
        public String toString() {
            return toString(4096);
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
                        sb.append(' ').append(attr.toString(escaping));
                    }
                }
                if(Is.empty(elem.children) && Is.empty(elem.text)) {
                    sb.append("/>");
                } else {
                    sb.append('>');
                    if(Is.empty(elem.children)) {
                        sb.append(escape(elem.text, escaping));
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
                    boolean first = true;
                    String indentprefix = "";
                    if(indentAttrs) indentprefix = spaces(elem.name.prefixedName().length()+1);
                    if(sortAttrs) Collections.sort(attrs, AttrByNameComparator.instance());
                    for(Attr attr : elem.attrs) {
                        if(indentAttrs && (!first)) sb.append('\n').append(prefix).append(indentprefix);
                        first = false;
                        sb.append(' ').append(attr.toString(escaping));
                    }
                }
                if(Is.empty(elem.children) && Is.empty(elem.text)) {
                    sb.append("/>\n");
                } else {
                    if(Is.empty(elem.children)) {
                        sb.append(">");
                        sb.append(escape(elem.text, escaping));
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
        StringBuilder sb = new StringBuilder(4096);
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

    private static class ElemByElemParser extends DefaultHandler {

        BiPredicate<String, Elem> elemChecker;
        Consumer<Elem> elemConsumer;

        Elem root = null;
        Stack<Elem> stack = new Stack<Elem>();
        StringBuilder sb = new StringBuilder(4096);
        boolean lastWasStartElelemnt = false;
        boolean insideCheckedElem = false;
        StringBuilder path = new StringBuilder();

        public static ElemByElemParser instance() { return new ElemByElemParser(); }
        public ElemByElemParser elemChecker(BiPredicate<String, Elem> value) {this.elemChecker = value; return this; }
        public ElemByElemParser elemConsumer(Consumer<Elem> value) {this.elemConsumer = value; return this; }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            sb.setLength(0);
            if(qName != null) {
                Elem elem = Elem.of(qName);
                for(int i = 0; i < attributes.getLength(); i++) {
                    elem.addAttrRaw(attributes.getQName(i), attributes.getValue(i), -1);
                }
                path.append('/').append(elem.name.name());

                if(!insideCheckedElem && (elemChecker != null) && elemChecker.test(path.toString(), elem)) {
                    insideCheckedElem = true;
                    stack.clear();
                }
                if(insideCheckedElem) {
                    lastWasStartElelemnt = true;
                    if(root == null) root = elem;
                    if(stack.size() > 0) stack.peek().addChild(elem);
                    stack.add(elem);
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            Elem elem = stack.isEmpty() ? null : stack.pop();
            if((elem != null) && lastWasStartElelemnt) elem.text(sb.toString());
            lastWasStartElelemnt = false;
            if(path.length() > 0) {
                int pos = path.lastIndexOf("/");
                if(pos > -1) path.setLength(pos);
            }
            if(insideCheckedElem && (stack.isEmpty())) {
                if(elemConsumer != null) elemConsumer.accept(elem);
                insideCheckedElem = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if(lastWasStartElelemnt) sb.append(ch, start, length);
        }

    }

    public static class ElemByElem {
        BiPredicate<String, Elem> checker;
        Consumer<Elem> consumer;
        XMLStreamReader reader;

        public static ElemByElem instance() { return new ElemByElem(); }
        /**
         * returns true if elemment should be returned. Elem has only attributes and no subelements.
         * Path is ins form '/name/name/....'.
         * @param value
         * @return this
         */
        public ElemByElem checker(BiPredicate<String, Elem> value) {this.checker = value; return this; }
        /**
         * used in consume() method for consuming selected elements.
         * @param value
         * @return this
         */
        public ElemByElem consumer(Consumer<Elem> value) {this.consumer = value; return this; }
        /**
         * Source for all scanning methods.
         * @param reader
         * @return this
         */
        public ElemByElem source(XMLStreamReader reader) {this.reader = reader; return this; }
        /**
         * Source for all scanning methods.
         * @param xml
         * @return
         */
        public ElemByElem source(String xml) {
            try {
                XMLInputFactory factory = XMLInputFactory.newFactory();
                reader = factory.createXMLStreamReader(new StringReader(xml));
            } catch(Exception e) {
                throw AsRuntimeEx.of(e);
            }
            return this;
        }
        /**
         * Source for all scanning methods.
         * @param xml
         * @return
         */
        public ElemByElem source(InputStream xml) {
            try {
                XMLInputFactory factory = XMLInputFactory.newFactory();
                reader = factory.createXMLStreamReader(xml);
            } catch(Exception e) {
                throw AsRuntimeEx.of(e);
            }
            return this;
        }
        /**
         * Source for all scanning methods.
         * @param xml
         * @return
         */
        public ElemByElem source(Reader xml) {
            try {
                XMLInputFactory factory = XMLInputFactory.newFactory();
                reader = factory.createXMLStreamReader(xml);
            } catch(Exception e) {
                throw AsRuntimeEx.of(e);
            }
            return this;
        }

        /**
         * Iterator for all elements choosen by checker.
         * @return iterator
         */
        public Iterator<Elem> iterator() {
            if(reader == null) throw new IllegalArgumentException("No source specified");
            return new ElemIterator(reader);
        }
        /**
         * Atream of all elements choosen by checker.
         * @return
         */
        public Stream<Elem> stream() {
            return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    iterator()
                    , Spliterator.IMMUTABLE
                ), false);
        }
        /**
         * Traverse all eleements choosen by checker and consume them by consumer.
         */
        public void consume() {
            if(consumer == null) throw new IllegalArgumentException("No consumer specified");
            stream().forEach(consumer);
        }


        private class ElemIterator implements Iterator<Elem> {
            XMLStreamReader reader;
            StringBuilder path = new StringBuilder();

            Elem elem = null;
            boolean isEnd = false;

            ElemIterator(XMLStreamReader reader) {
                this.reader = reader;
                findnext();
            }

            @Override
            public boolean hasNext() {
                return (elem != null);
            }

            @Override
            public Elem next() {
                Elem rv = elem;
                findnext();
                return rv;
            }

            private void findnext() {
                if(isEnd) return;
                elem = nextone();
                isEnd = elem == null;
            }

            public Elem nextone() {
                try {
                    while(reader.hasNext()) {
                        int token = reader.next();
                        switch(token) {
                            case XMLStreamConstants.END_ELEMENT:
                                if(path.length()>0) {
                                    int pos =  path.lastIndexOf("/");
                                    if(pos  > -1) path.setLength(pos);
                                }
                                break;
                            case XMLStreamConstants.START_ELEMENT:
                                String prefix = reader.getPrefix();
                                String name = reader.getLocalName();
                                if(!Is.empty(prefix)) name = prefix + ":" + name;
                                Elem elem = Elem.of(name);
                                {
                                    int count = reader.getAttributeCount();
                                    for(int i = 0; i < count; i++) {
                                        String pr = reader.getAttributePrefix(i);
                                        String na = reader.getAttributeLocalName(i);
                                        if(!Is.empty(pr)) na = pr + ":" + na;
                                        String va = reader.getAttributeValue(i);
                                        elem.addAttr(na, va);
                                    }
                                }
                                {
                                    int count = reader.getNamespaceCount();
                                    for(int i = 0; i < count; i++) {
                                        String pr = reader.getNamespacePrefix(i);
                                        String na = "xmlns";
                                        if(!Is.empty(pr)) na = na + ":" + pr;
                                        String va = reader.getNamespaceURI(i);
                                        elem.addAttr(na, va);
                                    }
                                }

                                path.append('/').append(elem.name().name());

                                if((checker != null) && checker.test(path.toString(), elem)) {
                                    Elem el = Elem.parse(reader);
                                    int pos =  path.lastIndexOf("/");
                                    if(pos  > -1) path.setLength(pos);
                                    return el;
                                }
                                break;
                            default:
                        }
                    }
                    return null;
                } catch(Exception e) {
                    throw AsRuntimeEx.argument(e);
                }
            }

        }

    }

    public enum Escaping {
        /**
         * No escaping ....dangerous
         */
        NONE,
        /**
         * simple escaping - &amp; &lt;, *gt;
         */
        SIMPLE,
        /**
         * simple escaping + all non assii chars
         */
        FULL,
        /**
         * simple escaping + all non assii chars + nontrintable asii
         */
        FULL_NONPRINT;
    }

    private static String escape(String value, Escaping escaping) {
        if(value == null) return "";
        if(escaping == null) return value;
        switch(escaping) {
            case NONE:
                return value;
            case SIMPLE:
                return Html.escapeSimple(value);
            case FULL:
                return Html.escapeSimpleAndNonAscii(value);
            case FULL_NONPRINT:
                return Html.escapeSimpleAndNonAsciiAndNonPrintable(value);
            default:
                return value;
        }

    }

    private static String unescape(String value) {
        if(value == null) return "";
        return Html.unescape(value);
    }

    /**
     * Traverse xml tree nodes. (DFS preorder)
     * Each elem is put to predicate for processing
     * and if predicate returns true subtree is also traversed.
     * This code finds all elements with name 'person'
     *
     * <pre>
     * {@code Elem document = ...}
     * {@code final List<Elem> persons = new ArrayList<>();}
     * {@code document.traverse(e -> {if("person".equals(e.name().name())) persons.add(e); return true;})}
     * </pre>
     *
     * @param predicate process node and returns true if subtree should be traversed.
     */
    public void traverse(Predicate<Elem> predicate) {
        boolean cont = predicate.test(this);
        if(cont) {
            int len = this.childrenSize();
            for(int i = 0; i < len; i++) {
                Elem elem = this.child(i);
                elem.traverse(predicate);
            }
        }
    }

    static String[] prefixes = new String[20];
    private static String spaces(int len) {
        if(len < 0) len = 0;
        if(len >= 20 ) return spacesRaw(len);
        if(prefixes[len] != null) return prefixes[len];
        String rv = spacesRaw(len);
        prefixes[len] = rv;
        return rv;
    }

    private static String spacesRaw(int len) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++) sb.append(' ');
        return sb.toString();
    }

    private static class AttrByNameComparator implements Comparator<Attr> {

        @Override
        public int compare(Attr t1, Attr t2) {
            String n1 = t1 == null ? "" : (t1.name == null ? "" : (t1.name.name == null ? "" : t1.name.name));
            String n2 = t2 == null ? "" : (t2.name == null ? "" : (t2.name.name == null ? "" : t2.name.name));
            return n1.compareTo(n2);
        }

        public static AttrByNameComparator instance() { return new AttrByNameComparator(); }

    }

// -------------------- subclasses end ---------------


    public static void main(String[] argv) throws Exception {
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

        String xml = "<a attr=\"toto &lt; toto\">pokus pokus2 &amp;</a>";
        Elem eee2 = Elem.parse(xml);
        System.out.println(" ------------ parse -------------------");
        System.out.println(" - text: " + eee2.text());
        System.out.println(" - attr: " + eee2.attr("attr"));
        System.out.println(eee2.toString());
        System.out.println(eee2.export().toString());
        System.out.println(eee2.export().escaping(Escaping.FULL).toString());

        Elem eee3 = Elem.parse(new FileInputStream("/tmp/aaa/pokus.xml"));
            FileOutputStream fos = new FileOutputStream("/tmp/aaa/pokus2.xml");
            eee3.export().encoding("windows-1252")
                .declaration(true)
                .escaping(Elem.Escaping.FULL_NONPRINT)
                .indent("\t")
                .indentAttrs(true)
                .sortAttrs(true)
                .toOutputStream(fos);
            fos.flush();
            fos.close();

    }
}
