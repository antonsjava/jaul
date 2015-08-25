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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Text file splitting helper class. It splits file to individual lines. 
 * 
 * <br/>
 * Implementation hides all io exceptions to runtime exception. Implementation 
 * is not reentrant - use it in one thread only. 
 * 
 * @author antons
 */
public class TextFileSplitter {
    private final BufferedReader reader;

    /**
     * Creates instance of TextFileSplitter.
     * @param filename - file name of given text file.
     * @param charset - name of the charset used in file
     */
    public TextFileSplitter(String filename, String charset) {
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charset));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read file '"+filename+"' with charset '"+charset+"'", e);
        }
    }
    
    /**
     * Creates instance of TextFileSplitter.
     * @param file - given text file.
     * @param charset - name of the charset used in file
     */
    public TextFileSplitter(File file, String charset) {
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read file '"+file+"' with charset '"+charset+"'", e);
        }
    }
    
    /**
     * Creates instance of TextFileSplitter.
     * The instance can be used for splitting only once. The input stream is 
     * closed on last line. 
     * @param is - file input stream.
     * @param charset - name of the charset used in file
     */
    public TextFileSplitter(InputStream is, String charset) {
        try {
            reader = new BufferedReader(new InputStreamReader(is, charset));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read is with charset '"+charset+"'", e);
        }
    }

    /**
     * Separates file to lines. 
     * @return Iterator for individual lines
     */
    public Iterator<String> byLines() {
        if(reader == null) return new EmptyIterator();
        return new LineIterator();
    }
    
    /**
     * Separates file to lines. 
     * @return List with all lines of provided file.
     */
    public List<String> byLinesToList() {
        return toList(byLines());
    }


    private static List<String> toList(Iterator<String> iterator) {
        List<String> rv = new ArrayList<String>();
        while(iterator.hasNext()) {
            String next = iterator.next();
            rv.add(next);
        }
        return rv;
    }

        
    private static class EmptyIterator implements Iterator<String> {
        public boolean hasNext() {return false; }
        public String next() { throw new NoSuchElementException("Empty iterator"); }
    }

    private class LineIterator implements Iterator<String> {
        private String line = null;    
        
        public LineIterator() {
            nextLine(true);
        }
        
        private void nextLine(boolean firstLine) {
            if(!firstLine && line == null) return;
            line = null;
            try {
                line = TextFileSplitter.this.reader.readLine();
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to read next line", e);
            }
            if(line == null) {
                try {
                    TextFileSplitter.this.reader.close();
                } catch (Exception e) {
                }
            }
        }

        
        public boolean hasNext() {
            return line != null;
        }
        
        public String next() {
            if(line == null) throw new NoSuchElementException("No moere elements");
            String rv = line;
            nextLine(false);
            return rv;
        }
    }

}
