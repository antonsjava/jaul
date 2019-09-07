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

package sk.antons.jaul.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import sk.antons.jaul.Is;

/**
 * Transfers bytes from and to stream.
 * 
 * @author antons
 */
public class Bytes {

    /**
     * Transfers bytes to stream
     * @param bytes - bytes to be transfered
     * @param os - stream to be filled
     */
    public static void toStream(byte[] bytes, OutputStream os) {
        if(Is.empty(bytes)) return;
        try {
            os.write(bytes);
            os.flush();
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to transfer bytes", e);
        }
    }
    
    /**
     * Transfers bytes from stream
     * @param is - stream to be read
     * @return - stream to be read
     */
    public static byte[] fromStream(InputStream is) {
        if(Is.empty(is)) return new byte[0];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int num;
            byte[] buff = new byte[1024];
            while ((num = is.read(buff, 0, buff.length)) != -1) {
                baos.write(buff, 0, num);
            }
            baos.flush();
            byte[] rv = baos.toByteArray();
            return rv;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to transfer bytes", e);
        }
    }
    
    /**
     * Transfers bytes from stream to stream
     * @param is - stream to be read
     * @param os - stream to write
     * @return  - number of transfered bytes
     */
    public static int transfer(InputStream is, OutputStream os) {
        if(Is.empty(is)) return 0;
        try {
            int rv = 0;
            int num = 0;
            byte[] buff = new byte[1024];
            while ((num = is.read(buff, 0, buff.length)) != -1) {
                os.write(buff, 0, num);
                rv = rv + num;
            }
            os.flush();
            return rv;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to transfer bytes", e);
        }
    }
    
    /**
     * Converts bytes to stream
     * @param bytes - bytes to be transfered
     */
    public static InputStream asStream(byte[] bytes) {
        try {
            if(bytes == null) bytes = new byte[0]; ;
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            return bais;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to converts bytes to stream", e);
        }
    }

}
