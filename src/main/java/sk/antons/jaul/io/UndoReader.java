/*
 * Copyright 2022 Anton Straka
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
package sk.antons.jaul.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

/**
 * Reader wrapper whic enable to return characters to be reade for another reading.
 * @author antons
 */
public class UndoReader extends Reader {

    private Reader reader;
    private Stack<Character> undo = new Stack<Character>();

    public UndoReader(Reader reader) {
        this.reader = reader;
    }
    public static UndoReader instance(Reader reader) { return new UndoReader(reader); }

    @Override
    public int read() throws IOException {
        if(!undo.isEmpty()) return undo.pop();
        return reader.read();
    }

    public void undo(char c) {
        undo.push(c);
    }

    public void undo(char[] carr) {
        if(carr == null) return;
        for(int i = carr.length-1; i >=0; i--) {
            undo.push(carr[i]);
        }
    }

    public void undo(String str) {
        if(str == null) return;
        undo(str.toCharArray());
    }

    @Override
    public int read(char[] chars, int offset, int len) throws IOException {
        int i = 0;
        for(; i < len;) {
            int c = read();
            if(c < 0) {
                if(i == 0) i = -1;
                break;
            }
            i++;
            chars[offset++] = (char)c;
        }
        return i;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
