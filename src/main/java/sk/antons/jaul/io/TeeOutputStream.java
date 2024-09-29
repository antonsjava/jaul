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
import java.io.OutputStream;

/**
 * Helper class for wrapping OutputStream instances. It enable
 * to write to other stream too.
 * @author antons
 */
public class TeeOutputStream extends OutputStream {
    private OutputStream os;
    private OutputStream nextos;

    public TeeOutputStream(OutputStream os, OutputStream nextos) {
        this.os = os;
        this.nextos = nextos;
    }

    public static TeeOutputStream instance(OutputStream os, OutputStream nextos) { return new TeeOutputStream(os, nextos); }

    @Override
    public void write(int b) throws IOException {
        os.write(b);
        nextos.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        os.write(b);
        nextos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        os.write(b, off, len);
        nextos.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        os.flush();
        nextos.flush();
    }

    @Override
    public void close() throws IOException {
        os.close();
        nextos.close();
    }

}
