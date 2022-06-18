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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.antons.jaul.Is;

/**
 * Simple Element wrapper class. simplify traversing of xml dom by elements. 
 * Hides Node part of dom model. 
 * 
 * Instance of EW represents an element node of xml dom document. It brings methods for 
 * traversing the xml document by elements. 
 * 
 * any time you can create EW by EW.elem(element) and return back encapsulated 
 * element by ER.elem().
 *
 * Implementation is not reentrant. 
 * 
 * @author antons
 */
public class EW {

    private Element element;
    private List<Attr> attributes = null;

    private List<Attr> attributes() {
        if(attributes == null) {
            attributes = new ArrayList<Attr>();
            NamedNodeMap map = element.getAttributes();
            for(int i = 0; i < map.getLength(); i++) {
                Node n = map.item(i);
                Attr a = new Attr(n.getNodeName());
                a.value = n.getTextContent();
                attributes.add(a);
            }
        }
        return attributes;
    }

    private EW(Element element) {
        if(element == null) throw new IllegalArgumentException("Unable to create EW from null element.");
        this.element = element;
    }

    /**
     * Creates new instance of EW.
     * @param element
     * @return new created wrapper for provided element instance
     */
    public static EW elem(Element element) {
        return new EW(element);
    }

    /**
     * Returns encapsulated element.
     * @return  encapsulated element of this wrapper.
     */
    public Element elem() {
        return element;
    }
    
    private String attr(String ns, String name) {
        if(name == null) return null;
        for(Attr a : attributes()) {
            if((ns != null) && (!ns.equals(a.ns))) continue;
            if((name != null) && (!(name.equals(a.fullname) || name.equals(a.name)))) continue;
            return a.value;
        }
        return null;
    }

    /**
     * Reads value of given attribute
     * @param name - name of the attribute
     * @return value of specified attribute
     */
    public String attr(String name) {
        return attr(null, name);
    }

    /**
     * Reads value of given attribute
     * @param ns - name space
     * @param name - name of the attribute
     * @return value of specified attribute
     */
    public String attrNS(String ns, String name) {
        return attr(ns, name);
    }

    /**
     * Reads text content of current node.
     * @return elem().getTextContent()
     */
    public String text() {
        if(element == null) return null;
        return element.getTextContent();
    }
    
    /**
     * Returns parent element of this element if exists.
     * @return parent element or null if not exixsts.
     */
    public EW parent() {
        Node node = element.getParentNode();
        while(node != null) {
            if(node instanceof Element) return EW.elem((Element)node);
            node = node.getParentNode();
        }
        return null;
    }

    private static List<EW> toList(NodeList nl, String ns, String tag) {
        List<EW> list = new ArrayList<EW>();
        if(nl != null) {
            if(ns != null) ns = ns + ":";
            String tag2 = null;
            if(tag != null) tag2 = ":" + tag;
            for(int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n == null) continue;
                if(!(n instanceof Element)) continue;
                String fullname = n.getNodeName();
                if((ns != null) && (!fullname.startsWith(ns))) continue;
                if((tag != null) && (!(tag.equals(fullname) || (fullname.endsWith(tag2))))) continue;
                list.add(EW.elem((Element)n));
            }
        }
        return list;
    }
    
    private static List<EW> traverse(Element e, String ns, String tag) {
        List<EW> list = new ArrayList<EW>();
        if(ns != null) ns = ns + ":";
        String tag2 = null;
        if(tag != null) tag2 = ":" + tag;
        NodeList nl = e.getChildNodes();
        if(nl != null) {
            for(int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n == null) continue;
                if(!(n instanceof Element)) continue;
                traverse(list, (Element)n, ns, tag, tag2);
            }
        }
        return list;
    }

    private static void traverse(List<EW> list, Element e, String ns, String tag, String tag2) {
        if(e == null) return;
        String fullname = e.getNodeName();
        if((ns == null) || (fullname.startsWith(ns))) {
            if((tag == null) || ((tag.equals(fullname) || (fullname.endsWith(tag2))))) {
                list.add(EW.elem(e));
            }
        }
        
        NodeList nl = e.getChildNodes();
        if(nl != null) {
            for(int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n == null) continue;
                if(!(n instanceof Element)) continue;
                traverse(list, (Element)n, ns, tag, tag2);
            }
        }
    }
    
    private static EW toFirst(NodeList nl, String ns, String tag) {
        if(nl != null) {
            if(ns != null) ns = ns + ":";
            String tag2 = null;
            if(tag != null) tag2 = ":" + tag;
            for(int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n == null) continue;
                if(!(n instanceof Element)) continue;
                String fullname = n.getNodeName();
                if((ns != null) && (!fullname.startsWith(ns))) continue;
                if((tag != null) && (!(tag.equals(fullname) || (fullname.endsWith(tag2))))) continue;
                return EW.elem((Element)n);
            }
        }
        return null;
    }
    
    
    private static EW traverseFirst(Element e, String ns, String tag) {
        if(ns != null) ns = ns + ":";
        String tag2 = null;
        if(tag != null) tag2 = ":" + tag;
        NodeList nl = e.getChildNodes();
        if(nl != null) {
            for(int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n == null) continue;
                if(!(n instanceof Element)) continue;
                EW ew = traverseFirst((Element)n, ns, tag, tag2);
                if(ew != null) return ew;
            }
        }
        return null;
    }

    private static EW traverseFirst(Element e, String ns, String tag, String tag2) {
        if(e == null) return null;
        String fullname = e.getNodeName();
        if((ns == null) || (fullname.startsWith(ns))) {
            if((tag == null) || ((tag.equals(fullname) || (fullname.endsWith(tag2))))) {
                return EW.elem(e);
            }
        }
        
        NodeList nl = e.getChildNodes();
        if(nl != null) {
            for(int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if(n == null) continue;
                if(!(n instanceof Element)) continue;
                EW ew = traverseFirst((Element)n, ns, tag, tag2);
                if(ew != null) return ew;
            }
        }
        return null;
    }
    
    
    
    /**
     * list of all direct e
     * 
     * lements in xml. 
     * @return List of direct element descendants of this element. 
     */
    public List<EW> children() {
        return toList(element.getChildNodes(), null, null);
    }

    /**
     * list of direct elements in xml filtered by tag name. 
     * @param tag - tag name of selected children
     * @return List of direct element descendants of this element. 
     */
    public List<EW> childrenByTagName(String tag) {
        return toList(element.getChildNodes(), null, tag);
    } 

    /**
     * list of direct elements in xml filtered by tag name. 
     * @param ns - namespace prefix of tag name
     * @param tag - tag name of selected children
     * @return List of direct element descendants of this element. 
     */
    public List<EW> childrenByTagNameNS(String ns, String tag) {
        return toList(element.getChildNodes(), ns, tag);
    }
    
    /**
     * list of all descendant elements in xml filtered by tag name. 
     * @param tag - tag name of selected children
     * @return List of direct element descendants of this element. 
     */
    public List<EW> elementsByTagName(String tag) {
        return traverse(element, null, tag);
    } 

    /**
     * list of all descendant elements in xml filtered by tag name. 
     * @param ns - namespace prefix of tag name
     * @param tag - tag name of selected children
     * @return List of direct element descendants of this element. 
     */
    public List<EW> elementsByTagNameNS(String ns, String tag) {
        return traverse(element, ns, tag);
    }

    /**
     * return first child of this node
     * @return selected element
     */
    public EW firstChild() {
        return toFirst(element.getChildNodes(), null, null);
    }
    
    /**
     * return first child of this node filtered by tag name
     * @param tag - tag name of selected children
     * @return selected element
     */
    public EW firstChildByTagName(String tag) {
        return toFirst(element.getChildNodes(), null, tag);
    }

    /**
     * return first child of this node filtered by tag name
     * @param ns - namespace prefix of tag name
     * @param tag - tag name of selected children
     * @return selected element
     */
    public EW firstChildByTagNameNS(String ns, String tag) {
        return toFirst(element.getChildNodes(), ns, tag);
    }

    
    /**
     * return first descendant of this node filtered by tag name
     * @param tag - tag name of selected children
     * @return selected element
     */
    public EW firstElementByTagName(String tag) {
        return traverseFirst(element, null, tag);
    }

    /**
     * return first descendant of this node filtered by tag name
     * @param ns - namespace prefix of tag name
     * @param tag - tag name of selected children
     * @return selected element
     */
    public EW firstElementByTagNameNS(String ns, String tag) {
        return traverseFirst(element, ns, tag);
    }

    /**
     * Tag name of encapsulated element. element.getNodeName() without name 
     * space prefix.
     * @return tag name of encapsulated element
     */
    public String tag() {
        String value = element.getNodeName();
        int pos = value.lastIndexOf(":");
        if(pos > -1) return value.substring(pos+1);
        return value;
    }
    

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(element.getNodeName());
        for(Attr attribute : attributes()) {
            sb.append(" ").append(attribute.fullname).append("=\"").append(attribute.value).append("\"");
        }
        sb.append(">");
        return sb.toString();
    }

    public List<String> attrNames() {
        List<String> names = new ArrayList<String>();
        for(Attr attribute : attributes()) {
            names.add(attribute.fullname);
        }
        return names;
    }
 
    
    private static class Attr {
        private String fullname;
        private String name;
        private String ns;
        private String value;

        public Attr(String fullname) {
            if(fullname == null) throw new IllegalArgumentException("Unable to create attribute with null name");
            this.fullname = fullname;
            int pos = fullname.lastIndexOf(":");
            if(pos > -1) {
                ns = fullname.substring(0, pos);
                name = fullname.substring(pos + 1);
            } else {
                name = fullname;
            }
            
        }
    }
    

    /**
     * return text content of first child of this node filtered by tag name
     * @param tag - tag name of selected children
     * @return selected element
     */
    public String firstChildByTagNameTextContent(String tag) {
        EW ew = firstChildByTagName(tag);
        if(ew == null) return null;
        return ew.elem().getTextContent();
    }

    /**
     * return text content of first child of this node filtered by tag name
     * @param ns - namespace prefix of tag name
     * @param tag - tag name of selected children
     * @return selected element
     */
    public String firstChildByTagNameNSTextContent(String ns, String tag) {
        EW ew = firstChildByTagNameNS(ns, tag);
        if(ew == null) return null;
        return ew.elem().getTextContent();
    }

    /**
     * Finds first element defined by path.
     * @param path path defined by eleement names
     * @return element or null 
     */
    public EW firstByPath(String... path) {
        EW root = this;
        if(Is.empty(path)) return null;
        for(String name : path) {
            root = root.firstChildByTagName(name);
            if(root == null) return null;
        }
        return root;
    }
    
    /**
     * Finds first element defined by path and return its text
     * @param path path defined by eleement names
     * @return element text  or null 
     */
    public String firstByPathText(String... path) {
        EW ew = firstByPath(path);
        if(Is.empty(path)) return null;
        return ew.text();
    }
    
    /**
     * Finds all elements defined by path.
     * @param path path defined by eleement names
     * @return list of elements 
     */
    public List<EW> allByPath(String... path) {
        List<EW> list = new ArrayList<EW>();
        if(Is.empty(path)) return list;
        allByPath(list, path, 0);
        return list;
    }
    
    private void allByPath(List<EW> list, String[] path, int index) {
        List<EW> chlist = childrenByTagName(path[index]);
        if(Is.empty(chlist)) return;
        if(index == path.length-1) {
            list.addAll(chlist);
        } else {
            for(EW ew : chlist) {
                ew.allByPath(list, path, index+1);
            }
        }
    }

}
