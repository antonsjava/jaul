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
    
}
