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

import java.io.File;
import java.io.InputStream;
import sk.antons.jaul.util.StringSplitter;
import sk.antons.jaul.util.TextFileSplitter;

/**
 * Splits strings and text files. 
 * 
 * Strings are split by specifying one or more substring delimiters. Exact match 
 * is used (no regexp, ..) and all divided parts are returned (no ignoring 
 * empty parts, ...). 
 * 
 * Text files are split to lines. All exceptions are mapped to runtime exceptions 
 * 
 * @author antons
 */
public class Split {
   
    /**
     * Provides string splitter for splitting given text. 
     * @param text - to be split by a separator 
     * @return string splitter object
     */
    public static StringSplitter string(String text) {
        return new StringSplitter(text);
    }
    
    /**
     * Provides text file splitter for splitting provided file to lines.. 
     * @param filename - name of the file 
     * @param charset - name of the charset used in file
     * @return text file splitter
     */
    public static TextFileSplitter file(String filename, String charset) {
        return new TextFileSplitter(filename, charset);
    }

    /**
     * Provides text file splitter for splitting provided file to lines.. 
     * @param file - the file to be split 
     * @param charset - name of the charset used in file
     * @return text file splitter
     */
    public static TextFileSplitter file(File file, String charset) {
        return new TextFileSplitter(file, charset);
    }

    /**
     * Provides text file splitter for splitting provided file to lines.. 
     * @param is - input stream of provided file (stream will be closed at 
     *         last line.)
     * @param charset - name of the charset used in file
     * @return text file splitter - Can be used only once for line splitting. 
     */
    public static TextFileSplitter file(InputStream is, String charset) {
        return new TextFileSplitter(is, charset);
    }

}
