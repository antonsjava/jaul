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

package sk.antons.jaul;

import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class SplitTest {
	
    @Test
	public void stringSubstring() throws Exception {
        String text = null;
        String separator = null;
        Iterator<String> iter = null;
        List<String> list = null;
        iter = Split.string(text).bySubstring(separator);
        list = Split.string(text).bySubstringToList(separator);
        Assert.assertFalse("null null'" + list + "'", iter.hasNext());
        Assert.assertTrue("null null'" + list + "'", list.isEmpty());
        
        text = "abcba";
        iter = Split.string(text).bySubstring(separator);
        list = Split.string(text).bySubstringToList(separator);
        Assert.assertFalse("x null'" + list + "'", iter.hasNext());
        Assert.assertTrue("x null'" + list + "'", list.isEmpty());
        
        text = "abcba";
        separator = "";
        iter = Split.string(text).bySubstring(separator);
        list = Split.string(text).bySubstringToList(separator);
        Assert.assertFalse("x empty'" + list + "'", iter.hasNext());
        Assert.assertTrue("x empty'" + list + "'", list.isEmpty());

        text = "abcba";
        separator = "a";
        iter = Split.string(text).bySubstring(separator);
        list = Split.string(text).bySubstringToList(separator);
        Assert.assertTrue("x a'" + list + "'", iter.hasNext());
        Assert.assertTrue("x a'" + list + "'", list.size() == 3);
        Assert.assertTrue("x a'" + list + "'", "".equals(list.get(0)));
        Assert.assertTrue("x a'" + list + "'", "bcb".equals(list.get(1)));
        Assert.assertTrue("x a'" + list + "'", "".equals(list.get(2)));

        text = "abcba";
        separator = "b";
        iter = Split.string(text).bySubstring(separator);
        list = Split.string(text).bySubstringToList(separator);
        Assert.assertTrue("x b'" + list + "'", iter.hasNext());
        Assert.assertTrue("x b'" + list + "'", list.size() == 3);
        Assert.assertTrue("x b'" + list + "'", "a".equals(list.get(0)));
        Assert.assertTrue("x b'" + list + "'", "c".equals(list.get(1)));
        Assert.assertTrue("x b'" + list + "'", "a".equals(list.get(2)));
        
        text = "abcba";
        separator = "c";
        iter = Split.string(text).bySubstring(separator);
        list = Split.string(text).bySubstringToList(separator);
        Assert.assertTrue("x c'" + list + "'", iter.hasNext());
        Assert.assertTrue("x c'" + list + "'", list.size() == 2);
        Assert.assertTrue("x c'" + list + "'", "ab".equals(list.get(0)));
        Assert.assertTrue("x c'" + list + "'", "ba".equals(list.get(1)));
        
        text = "abcba";
        separator = "d";
        iter = Split.string(text).bySubstring(separator);
        list = Split.string(text).bySubstringToList(separator);
        Assert.assertTrue("x d'" + list + "'", iter.hasNext());
        Assert.assertTrue("x d'" + list + "'", list.size() == 1);
        Assert.assertTrue("x d'" + list + "'", "abcba".equals(list.get(0)));
    }
    
    @Test
	public void stringSubstrings() throws Exception {
        String text = null;
        String separator1 = null;
        String separator2 = null;
        Iterator<String> iter = null;
        List<String> list = null;
        iter = Split.string(text).bySubstrings(separator1, separator2);
        list = Split.string(text).bySubstringsToList(separator1, separator2);
        Assert.assertFalse("null null null'" + list + "'", iter.hasNext());
        Assert.assertTrue("null null null'" + list + "'", list.isEmpty());
        
        text = "abcba";
        iter = Split.string(text).bySubstrings(separator1, separator2);
        list = Split.string(text).bySubstringsToList(separator1, separator2);
        Assert.assertFalse("x null null'" + list + "'", iter.hasNext());
        Assert.assertTrue("x null null'" + list + "'", list.isEmpty());
        
        text = "abcba";
        separator1 = "";
        separator2 = "";
        iter = Split.string(text).bySubstrings(separator1, separator2);
        list = Split.string(text).bySubstringsToList(separator1, separator2);
        Assert.assertFalse("x empty empty'" + list + "'", iter.hasNext());
        Assert.assertTrue("x empty empty'" + list + "'", list.isEmpty());

        text = "abcba";
        separator1 = "a";
        separator2 = "b";
        iter = Split.string(text).bySubstrings(separator1, separator2);
        list = Split.string(text).bySubstringsToList(separator1, separator2);
        Assert.assertTrue("x a b'" + list + "'", iter.hasNext());
        Assert.assertTrue("x a b'" + list + "'", list.size() == 5);
        Assert.assertTrue("x a b'" + list + "'", "".equals(list.get(0)));
        Assert.assertTrue("x a b'" + list + "'", "".equals(list.get(1)));
        Assert.assertTrue("x a b'" + list + "'", "c".equals(list.get(2)));
        Assert.assertTrue("x a b'" + list + "'", "".equals(list.get(3)));
        Assert.assertTrue("x a b'" + list + "'", "".equals(list.get(4)));

        text = "abcba";
        separator1 = "a";
        separator2 = "c";
        iter = Split.string(text).bySubstrings(separator1, separator2);
        list = Split.string(text).bySubstringsToList(separator1, separator2);
        Assert.assertTrue("x a c'" + list + "'", iter.hasNext());
        Assert.assertTrue("x a c'" + list + "'", list.size() == 4);
        Assert.assertTrue("x a c'" + list + "'", "".equals(list.get(0)));
        Assert.assertTrue("x b c'" + list + "'", "b".equals(list.get(1)));
        Assert.assertTrue("x a c'" + list + "'", "b".equals(list.get(2)));
        Assert.assertTrue("x a c'" + list + "'", "".equals(list.get(3)));
        
        text = "abcba";
        separator1 = "c";
        separator2 = "d";
        iter = Split.string(text).bySubstrings(separator1, separator2);
        list = Split.string(text).bySubstringsToList(separator1, separator2);
        Assert.assertTrue("x c d'" + list + "'", iter.hasNext());
        Assert.assertTrue("x c d'" + list + "'", list.size() == 2);
        Assert.assertTrue("x c d'" + list + "'", "ab".equals(list.get(0)));
        Assert.assertTrue("x c d'" + list + "'", "ba".equals(list.get(1)));
        
        text = "abcba";
        separator1 = "d";
        separator1 = "e";
        iter = Split.string(text).bySubstrings(separator1, separator2);
        list = Split.string(text).bySubstringsToList(separator1, separator2);
        Assert.assertTrue("x d e'" + list + "'", iter.hasNext());
        Assert.assertTrue("x d e'" + list + "'", list.size() == 1);
        Assert.assertTrue("x d e'" + list + "'", "abcba".equals(list.get(0)));

    }
    
    @Test
	public void fileLines() throws Exception {
        String file = "src/test/resources/split-file-test.txt";
        Iterator<String> iter = null;
        List<String> list = null;
        iter = Split.file(file, "utf-8").byLines();
        list = Split.file(file, "utf-8").byLinesToList();
        Assert.assertTrue("iter" + list + "'", iter.hasNext());
        Assert.assertTrue("size '" + list + "'", list.size() == 6);
        Assert.assertTrue("line1'" + list + "'", "riadok1\u0161".equals(list.get(0)));
        Assert.assertTrue("line2'" + list + "'", "riadok2\u010d".equals(list.get(1)));
        Assert.assertTrue("line3'" + list + "'", "riadok2\u0165".equals(list.get(2)));
        Assert.assertTrue("line4'" + list + "'", "".equals(list.get(3)));
        Assert.assertTrue("line5'" + list + "'", "riadok\u013e".equals(list.get(4)));
        Assert.assertTrue("line6'" + list + "'", "".equals(list.get(5)));

    }
}