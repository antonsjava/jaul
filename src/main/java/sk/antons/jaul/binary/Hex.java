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

/**
 * Converts bytes to hex encoded string and vice versa.
 * 
 * @author antons
 */
public class Hex {
    
    
    private static char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7'
                                         , '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static int fromHex(char c) {
        if((c >= '0') && (c <= '9')) return c - '0';
        if((c >= 'A') && (c <= 'F')) return c - 'A' + 10;
        if((c >= 'a') && (c <= 'f')) return c - 'a' + 10;
        throw new IllegalArgumentException("Can't convert char '" + c + "' to hex number");
    }
                                               


    /**
     * Converts the bytes to the string of the hexadecimal numbers.
     * 
     * @param bytes - input bytes 
     * @return bytes converted to hez string
     */
    public static String encode(byte[] bytes) {
        if(bytes == null) return "";
        StringBuilder buff = new StringBuilder();
        for(int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            int hb = (b >> 4) & 0xF;
            int lb = b & 0xF;
            buff.append(hex[hb]);
            buff.append(hex[lb]);
        }

        return buff.toString();
    }
    
    /**
     * Converts string of the hexadecimal numbers to the array of bytes. The 
     * string must consists from pairs of the hexadecimal numbers each pair 
     * is than converted to the byte.
     * 
     * @param str - hex encoded string 
     * @return bytes decoded from 
     */
    public static byte[] decode(String str) {
        if(str == null) return null;
        str = str.trim();
        int len = str.length()/2;
        if((str.length() % 2) != 0) throw new IllegalArgumentException("Length of hex encoded string must be divided by 2");
        byte[] bytes = new byte[len];
        for(int i = 0; i < len; i++) {
            int hb = fromHex(str.charAt(2*i));
            int lb = fromHex(str.charAt(2*i + 1));
            bytes[i] = (byte)(((hb << 4) | lb ) & 0xFF);
        }
        return bytes;
    }

    
}
