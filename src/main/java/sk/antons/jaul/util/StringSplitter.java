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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * String splitting helper class. Provides functionality to split strings by substring 
 * delimiter.
 * 
 * <br/>
 * It uses exact equals for identifying delimiter inside string.
 * 
 * <br/>
 * It returns all substring delimited by delimiter including empty one and also 
 * first and last id delimiter is on begin or end of provided string.
 * 
 * <br/>
 * for example String ", the text,, is separated, by comma," is separated by "," 
 * substring to "", " the text", "", " is separated", " by comma", "" 
 * 
 * 
 * @author antons
 */
public class StringSplitter {
    private final String text;
    
    /**
     * Constructs StringSplitter instance.
     * @param text - to be split.
     */
    public StringSplitter(String text) {
        this.text = text;
    }

    /**
     * Splits string by provided delimiter. 
     * @param substring - delimiter used to split text.
     * @return Iterator providing individual parts of separated text.
     */
    public Iterator<String> bySubstring(String substring) {
        if(text == null) return new EmptyIterator();
        if(substring == null) return new EmptyIterator();
        if("".equals(substring)) return new EmptyIterator();
        return new SubstringIterator(substring);
    }
    
    /**
     * Splits string by provided delimiter. 
     * @param substring - delimiter used to split text.
     * @return List with individual parts of separated text.
     */
    public List<String> bySubstringToList(String substring) {
        return toList(bySubstring(substring));
    }

    /**
     * Splits string by provided delimiters. You can specify more different 
     * delimiters. They all are used to separate provided text.
     * @param substring - delimiters used to split text.
     * @return Iterator providing individual parts of separated text.
     */
    public Iterator<String> bySubstrings(String... substring) {
        if(text == null) return new EmptyIterator();
        if(substring == null) return new EmptyIterator();
        if("".equals(substring)) return new EmptyIterator();
        return new SubstringsIterator(substring);
    }
    
    /**
     * Splits string by provided delimiters. You can specify more different 
     * delimiters. They all are used to separate provided text.
     * @param substring - delimiters used to split text.
     * @return List with individual parts of separated text.
     */
    public List<String> bySubstringsToList(String... substring) {
        return toList(bySubstrings(substring));
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
        public void remove() { throw new UnsupportedOperationException(); }
        
    }

    private class SubstringIterator implements Iterator<String> {
        private String substring = null;
        private int pos = 0;        

        public SubstringIterator(String substring) {
            this.substring = substring;
        }
        
        public boolean hasNext() {
            return pos <= StringSplitter.this.text.length();
        }
        
        public String next() {
            String text = StringSplitter.this.text;
            if(pos > text.length()) throw new NoSuchElementException("No moere elements");
            if(pos == text.length()) {
                pos = text.length() + 1;
                return "";
            }
            int p = text.indexOf(substring, pos);
            if(p < 0) {
                String rv = text.substring(pos);
                pos = text.length() + 1;
                return rv;
            } else {
                String rv = text.substring(pos, p);
                pos = p + substring.length();
                return rv;
            }
        }

        public void remove() { throw new UnsupportedOperationException(); }
    }

    private class SubstringsIterator implements Iterator<String> {
        private String[] substrings = null;
        private int pos = 0;   

        private int nearestPos = -1;
        private int nearestIndex = -1;

        public SubstringsIterator(String[] substrings) {
            this.substrings = substrings;
        }
        
        public boolean hasNext() {
            return pos <= StringSplitter.this.text.length();
        }

        private void findNext() {
            int minpos = -1;
            int minindex = -1;
            
            String text = StringSplitter.this.text;
            for(int i = 0; i < substrings.length; i++) {
                int p = text.indexOf(substrings[i], pos);
                if(p < 0) continue;
                if((minpos < 0) || (minpos > p)) {
                    minpos = p;
                    minindex = i;
                }
            }
             
            nearestPos = minpos;
            nearestIndex = minindex;

        }
        
        public String next() {
            String text = StringSplitter.this.text;
            if(pos > text.length()) throw new NoSuchElementException("No moere elements");
            if(pos == text.length()) {
                pos = text.length() + 1;
                return "";
            }
            findNext();
            if(nearestPos < 0) {
                String rv = text.substring(pos);
                pos = text.length() + 1;
                return rv;
            } else {
                String rv = text.substring(pos, nearestPos);
                pos = nearestPos + substrings[nearestIndex].length();
                return rv;
            }
        }

        public void remove() { throw new UnsupportedOperationException(); }
    }
}
