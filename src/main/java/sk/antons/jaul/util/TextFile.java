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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import sk.antons.jaul.Is;

/**
 * Read and write text file to and from string.
 * 
 * @author antons
 */
public class TextFile {
    
    /**
     * Reads text file into string. 
     * @param is - input stream with texts file data
     * @param charset = charset name
     * @return String from file
     */
    public static String read(InputStream is, String charset) {
        if(is == null) return null;
        if(Is.empty(charset)) charset = "utf-8";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
            StringBuilder sb = new StringBuilder();
            
            String line = reader.readLine();
            while(line != null) {
                if(sb.length() > 0 ) sb.append('\n');
                sb.append(line);
                line = reader.readLine();
            }

            reader.close();
            
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read file data with charset '"+charset+"'", e);
        }
    }

    /**
     * Reads text file into string. 
     * @param filename - name of the file to be read
     * @param charset = charset name
     * @return String from file
     */
    public static String read(String filename, String charset) {
        if(Is.empty(filename)) return null;
        if(Is.empty(charset)) charset = "utf-8";
        try {
            return read(new FileInputStream(filename), charset);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read file '"+filename+"' with charset '"+charset+"'", e);
        }
    }
    
    /**
     * Save text to file. Original file will be replaced.
     * @param filename - name of the file to be saved
     * @param charset - charset name
     * @param text  - text to be written to file;
     */
    public static void save(String filename, String charset, String text) {
        if(Is.empty(charset)) charset = "utf-8";
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), charset));
            if(text != null) writer.write(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to save file '"+filename+"' with charset '"+charset+"'", e);
        }
    }

}
