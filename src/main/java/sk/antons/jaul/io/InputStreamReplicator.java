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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class for wrapping InputStream instances. It enable
 * to read content more than once. It copy the content to cache
 * and enable to create input stream from that cache.
 *
 * Cache can be in memeory or in temporary file. (default is memory)
 * @author antons
 */
public class InputStreamReplicator {
    private InputStream is;
    private boolean memcache = true;

    public InputStreamReplicator(InputStream is) { this.is = is; }

    public static InputStreamReplicator instance(InputStream is) { return new InputStreamReplicator(is); }
    public InputStreamReplicator useFileCache() { this.memcache = false; return this; }

    private byte[] cache = null;
    private File filecache = null;

    /**
     * cleans  internal caches
     */
    public void close() {
        if(cache != null) cache = null;
        if((filecache != null) && filecache.exists()) filecache.delete();
    }

    /**
     * Returns InputStream with exact content as given InputStream has.
     * @return
     */
    public InputStream getInputStream() {
        if(is == null) return null;
        if((cache == null) && (filecache == null)) {
            try {
                OutputStream os = null;
                ByteArrayOutputStream buffer = null;
                FileOutputStream fos = null;
                File tempfile = null;
                if(memcache) {
                    buffer = new ByteArrayOutputStream();
                    os = buffer;
                } else {
                    tempfile = File.createTempFile("iscache-", ".dat");
                    tempfile.deleteOnExit();
                    fos = new FileOutputStream(tempfile);
                    os = fos;
                }
                int num;
                byte[] data = new byte[1024];
                while ((num = is.read(data, 0, data.length)) != -1) {
                    os.write(data, 0, num);
                }
                os.flush();
                os.close();
                if(memcache) cache = buffer.toByteArray();
                else filecache = tempfile;
            } catch(RuntimeException e) {
                throw e;
            } catch(Exception e) {
                throw new IllegalStateException(e);
            }
        }
        try {
            if(memcache) return new ByteArrayInputStream(cache);
            else return new FileInputStream(filecache);
        } catch(RuntimeException e) {
            throw e;
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
