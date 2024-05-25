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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
     * @param createDir  - if true create parent directory if not exists
     * @return number of saved data
     */
    public static int save(String filename, InputStream is, boolean createDir) {
        if(Is.empty(filename)) return 0;
        try {
            if(createDir) {
                File f = new File(filename);
                File p = f.getParentFile();
                if(!p.exists()) p.mkdirs();
            }
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
     * Saves bytes from stream to binary file.
     * @param filename - name of the file
     * @param is - data to be saved
     * @return number of saved data
     */
    public static int save(String filename, InputStream is) {
        return save(filename, is, false);
    }

    /**
     * Saves bytes to binary file.
     * @param filename - name of the file
     * @param bytes - data to be saved
     * @param createDir  - if true create parent directory if not exists
     * @return number of saved data
     */
    public static int save(String filename, byte[] bytes, boolean createDir ) {
        if(Is.empty(filename)) return 0;
        try {
            if(createDir) {
                File f = new File(filename);
                File p = f.getParentFile();
                if(!p.exists()) p.mkdirs();
            }
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

    /**
     * Saves bytes to binary file.
     * @param filename - name of the file
     * @param bytes - data to be saved
     * @return number of saved data
     */
    public static int save(String filename, byte[] bytes ) {
        return save(filename, bytes, false);
    }

    /**
     * Copy one file to another;
     * @param fromFile - name of the file to be copied
     * @param toFile - name of new created copy
     * @param createDir  - if true create parent directory if not exists
     * @return number of saved data
     */
    public static int copy(String fromFile, String toFile, boolean createDir ) {
        if(Is.empty(fromFile)) return 0;
        if(Is.empty(toFile)) return 0;
        if(fromFile.equals(toFile)) return 0;
        try (InputStream is = new FileInputStream(fromFile)) {
            return save(toFile, is, createDir);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to copy file '"+fromFile+"' -> '"+toFile+"'", e);
        }
    }

    /**
     * Copy one file to another;
     * @param fromFile - name of the file to be copied
     * @param toFile - name of new created copy
     * @return number of saved data
     */
    public static int copy(String fromFile, String toFile ) {
        return copy(fromFile, toFile, false);
    }

}