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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.TextFile;

/**
 * Helper class for creating xml document. Simplify document creation and maps 
 * exceptions to runtime exception.
 * 
 * @author antons
 */
public class Xml {

    /**
     * Parse xml from input stream
     * @param stream - with xml data
     * @return parsed xml document
     */
    public static Document document(InputStream stream) {
        if(stream == null) return null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://xml.org/sax/features/namespaces", false);
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(stream);
            return db.parse(is);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse xml document", e);
        }
    }
    
    /**
     * Parse xml from input string
     * @param xml - string with xml data
     * @return parsed xml document
     */
    public static Document document(String xml) {
        if(xml == null) return null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://xml.org/sax/features/namespaces", false);
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return db.parse(is);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse xml document", e);
        }
    }
    
    /**
     * Parse xml from file
     * @param filename - name of the file with xml content
     * @return parsed xml document
     */
    public static Document documentFromFile(String filename) {
        if(filename == null) return null;
        try {
            return document(new FileInputStream(filename));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse xml document from file " + filename, e);
        }
    }
    
    /**
     * Parse xml from file
     * @param filename - file with xml content
     * @return parsed xml document
     */
    public static Document documentFromFile(File file) {
        if(file== null) return null;
        try {
            return document(new FileInputStream(file));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse xml document from file " + file, e);
        }
    }

    /**
     * Escapes "'<>& characters to XML compliant escape sequences.
     * If you handle also &000; sequences use also Unicode.escapeHtml() method.
     * @param value to be escaped
     * @return escaped value
     */
    public static String escape(String value) {
        if(Is.empty(value)) return value;
        StringBuilder sb = new StringBuilder();
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if(c == '\"') sb.append("&quot;");
            else if(c == '\'') sb.append("&apos;");
            else if(c == '<') sb.append("&lt;");
            else if(c == '>') sb.append("&gt;");
            else if(c == '&') sb.append("&amp;");
            else sb.append(c);
        }
        return sb.toString();
    }
    
    /**
     * Escapes XML compliant escape sequences into "'<>& characters.
     * If you handle also &000; sequences use also Unicode.unescapeHtml() method.
     * @param value escaped value
     * @return un-escaped value
     */
    public static String unescape(String value) {
/*
  0 - none
  1 - &
  2 - &q
  3 - &qu
  4 - &quo
  5 - &quot
  6 - &l
  7 - &lt
  8 - &g
  9 - &gt
  10 - &a
  11 - &ap
  12 - &apo
  13 - &apos
  14 - &am
  15 - &amp
*/

        if(Is.empty(value)) return value;
        StringBuilder sb = new StringBuilder();
        int len = value.length();
        int state = 0;
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if(c == '&') state = 1;
            else if((c == 'q') && (state == 1)) state = 2;
            else if((c == 'u') && (state == 2)) state = 3;
            else if((c == 'o') && (state == 3)) state = 4;
            else if((c == 't') && (state == 4)) state = 5;
            else if((c == 'l') && (state == 1)) state = 6;
            else if((c == 't') && (state == 6)) state = 7;
            else if((c == 'g') && (state == 1)) state = 8;
            else if((c == 't') && (state == 8)) state = 9;
            else if((c == 'a') && (state == 1)) state = 10;
            else if((c == 'p') && (state == 10)) state = 11;
            else if((c == 'o') && (state == 11)) state = 12;
            else if((c == 's') && (state == 12)) state = 13;
            else if((c == 'm') && (state == 10)) state = 14;
            else if((c == 'p') && (state == 14)) state = 15;
            else if((c == ';') && (state == 15)) {sb.append('&'); state = 0; }
            else if((c == ';') && (state == 13)) {sb.append('\''); state = 0; }
            else if((c == ';') && (state == 9)) {sb.append('>'); state = 0; }
            else if((c == ';') && (state == 7)) {sb.append('<'); state = 0; }
            else if((c == ';') && (state == 5)) {sb.append('\"'); state = 0; }
            else {
                if(state == 1) sb.append("&");
                else if(state == 2) sb.append("&q");
                else if(state == 3) sb.append("&qu");
                else if(state == 4) sb.append("&quo");
                else if(state == 5) sb.append("&quot");
                else if(state == 6) sb.append("&l");
                else if(state == 7) sb.append("&lt");
                else if(state == 8) sb.append("&g");
                else if(state == 9) sb.append("&gt");
                else if(state == 10) sb.append("&a");
                else if(state == 11) sb.append("&ap");
                else if(state == 12) sb.append("&apo");
                else if(state == 13) sb.append("&apos");
                else if(state == 14) sb.append("&am");
                else if(state == 15) sb.append("&amp");
                state = 0;
                sb.append(c);
            }
        }
        return sb.toString();

    }

    /**
     * Converts XML document to string
     * @param doc - document to be converted
     * @param encoding - encoding (used only in declaration pragma)
     * @param indent - if resulting xml should be indented
     * @param declaration - if declaration pragma should be included
     * @return xml text generated from document 
     */
    public static String documentToString(Document doc, String encoding, boolean indent, boolean declaration) {
        if(doc == null) {
            return null;
        }

        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            if(declaration) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            } else {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            if(encoding != null) transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            if(indent) transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch(Exception ex) {
            throw new IllegalStateException("Error converting to String", ex);
        }
    }
    
    /**
     * Converts Element to string
     * @param element - document to be converted
     * @param encoding - encoding (used only in declaration pragma)
     * @param indent - if resulting xml should be indented
     * @param declaration - if declaration pragma should be included
     * @return xml text generated from document 
     */
    public static String elementToString(Element doc, String encoding, boolean indent, boolean declaration) {
        if(doc == null) {
            return null;
        }

        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            if(declaration) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            } else {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            if(encoding != null) transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            if(indent) transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch(Exception ex) {
            throw new IllegalStateException("Error converting to String", ex);
        }
    }
    

    /**
     * Converts XML document to string and save it to file.
     * @param doc - document to be converted
     * @param file - file to be generated
     * @param encoding - encoding (used only in declaration pragma)
     * @param indent - if resulting xml should be indented
     * @param declaration - if declaration pragma should be included
     */
    public static void documentToFile(Document doc, File file, String encoding, boolean indent, boolean declaration) {
        if(doc == null) return;
        try {
            if(encoding == null) encoding = "UTF-8";
            String xml = documentToString(doc, encoding, indent, declaration);
            TextFile.save(file.getAbsolutePath(), encoding, xml);
        } catch(Exception ex) {
            throw new IllegalStateException("Error converting to String of save to file ", ex);
        }
    }

    /**
     * Converts XML document to string and save it to file.
     * @param doc - document to be converted
     * @param file - file to be generated
     * @param encoding - encoding (used only in declaration pragma)
     * @param indent - if resulting xml should be indented
     * @param declaration - if declaration pragma should be included
     */
    public static void documentToFile(Document doc, String file, String encoding, boolean indent, boolean declaration) {
        if(doc == null) return;
        try {
            if(encoding == null) encoding = "UTF-8";
            String xml = documentToString(doc, encoding, indent, declaration);
            TextFile.save(file, encoding, xml);
        } catch(Exception ex) {
            throw new IllegalStateException("Error converting to String of save to file ", ex);
        }
    }

    /**
     * Removes unnecessary whitespace nodes from doc tree.
     * Usefull for printing oneline xmls.
     * @param node 
     */
    public static void trimWhiteSpaces(Node node) {
        if(node == null) return;
        if(node.getNodeType() != Node.ELEMENT_NODE) return;
        NodeList children = node.getChildNodes();
        if(children == null) return;
        if((children.getLength() < 1)) return;
        if((children.getLength() < 2) && (children.item(0).getNodeType() == Node.TEXT_NODE)) return;
        for(int i = children.getLength()-1; i >= 0; i--) {
            Node child = children.item(i);
            if(child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getTextContent();
                if(!Is.empty(text)) text = text.trim();
                if(Is.empty(text)) {
                    node.removeChild(child);
                } else {
                }
            } else {
                trimWhiteSpaces(child);
            }
        }
    }
    
}
