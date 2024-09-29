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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

/**
 * InputStream wrapper whic enable to return bytes to be ready for another reading.
 *
 * @author antons
 */
public class UndoInputStream extends InputStream {

    private InputStream is;
    private Stack<Byte> undo = new Stack<Byte>();

    public UndoInputStream(InputStream is) {
        this.is = is;
    }
    public static UndoInputStream instance(InputStream is) { return new UndoInputStream(is); }

    @Override
    public int read() throws IOException {
        if(!undo.isEmpty()) return undo.pop() & 0xFF;
        return is.read();
    }

    /**
     * Returns byte back to stream.
     * @param b
     */
    public void undo(byte b) {
        undo.push(b);
    }

    /**
     * Returns bytes back to stream.
     * @param barr
     */
    public void undo(byte[] barr) {
        if(barr == null) return;
        for(int i = barr.length-1; i >=0; i--) {
            undo.push(barr[i]);
        }
    }

    /**
     * Returns bytes from string back to stream.
     * @param str - string
     * @param encoding - string encoding
     */
    public void undo(String str, String encoding) {
        if(str == null) return;
        try {
            undo(str.getBytes(encoding));
        } catch(UnsupportedEncodingException e) {
            throw new IllegalArgumentException("unknown encoding " + encoding, e);
        }
    }

    /**
     * Reads n bytes from stream and return them back. So stream is unchanged.
     * @param n number of bytes to read
     * @return previewed bytes.
     */
    public byte[] peview(int n) {
        try {
            if(n < 1) return new byte[]{};
            ByteArrayOutputStream bos = new ByteArrayOutputStream(n);
            int b = read();
            while(b != -1) {
                bos.write(b);
                if(--n < 1) break;
                b = read();
            }
            byte[] rv = bos.toByteArray();
            undo(rv);
            return rv;
        } catch(Exception e) {
            throw new IllegalArgumentException("unable to preview stream", e);
        }
    }



}
