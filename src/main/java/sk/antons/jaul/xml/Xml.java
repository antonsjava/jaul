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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import sk.antons.jaul.Is;

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

    
}
