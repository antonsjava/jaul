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

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import sk.antons.jaul.Is;
import sk.antons.jaul.binary.Bytes;

/**
 * Read and write binary file.
 * 
 * @author antons
 */
public class BinFile {
    
    /**
     * Reads binary file into InputStream. 
     * @param filename - name of the file
     * @return stream from file
     */
    public static InputStream read(String filename) {
        if(Is.empty(filename)) return null;
        try {
            return new FileInputStream(filename);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read file '"+filename+"'", e);
        }
    }
    
    /**
     * Reads binary file into bytes. 
     * @param filename - name of the file
     * @return bytes from file
     */
    public static byte[] readBytes(String filename) {
        if(Is.empty(filename)) return new byte[0];
        return Bytes.fromStream(read(filename));
    }

    /**
     * Saves bytes from stream to binary file. 
     * @param filename - name of the file
     * @param is - data to be saved
     * @return number of saved data
     */
    public static int save(String filename, InputStream is) {
        if(Is.empty(filename)) return 0;
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            int rv = 0;
            if(!Is.empty(is)) rv = Bytes.transfer(is, fos);
            fos.flush();
            fos.close();
            return rv;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to save file '"+filename+"'", e);
        }
    }
    
    /**
     * Saves bytes to binary file. 
     * @param filename - name of the file
     * @param bytes - data to be saved
     * @return number of saved data
     */
    public static int save(String filename, byte[] bytes ) {
        if(Is.empty(filename)) return 0;
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            int rv = 0;
            if(!Is.empty(bytes)) {
                rv = bytes.length;
                fos.write(bytes);
            }
            fos.flush();
            fos.close();
            return rv;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to save file '"+filename+"'", e);
        }
    }

}
