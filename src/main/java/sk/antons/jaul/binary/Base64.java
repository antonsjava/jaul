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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import sk.antons.jaul.util.AsRuntimeEx;

/**
 * Simple base64 encoder. 
 * 
 * @author antons
 */
public class Base64 {

    /**
     * Encodes bytes to single line base64 string. 
     * @param value - bytes to be encoded
     * @return encoded data.
     */
    public static String encode(byte[] value) {
        return encode(value, false);
    }

    /**
     * Encodes bytes to single line base64 string. 
     * @param value   bytes to be encoded
     * @param format - true if resulting string should be cut to 76 chars lines.
     * @return encoded data.
     */
    public static String encode(byte[] value, boolean format) {
        return encode(value, format ? 76 : 0);
    }

    /**
     * Encodes bytes to single line base64 string. 
     * @param value   bytes to be encoded
     * @param wrapLen - if greater than zero result is wrapped to this len
     * @return encoded data.
     */
    public static String encode(byte[] value, int wrapLen) {
        if(value == null) return null;

        int length = value.length;
        int tripletNum = length / 3;
        int tripletRest = length % 3;
        int linelen = 1;
        StringBuilder sb = new StringBuilder(tripletNum*4 + 4);
        
        
        int index = 0;
        for (int i=0; i<tripletNum; i++) {
            int byte1 = value[index++] & 0xff;
            int byte2 = value[index++] & 0xff;
            int byte3 = value[index++] & 0xff;
            
            int char1 = byte1 >> 2; // first 6 bytes
            int char2 = (byte1 << 4)&0x3f | (byte2 >> 4); // second 6 bytes
            int char3= (byte2 << 2)&0x3f | (byte3 >> 6); // third 6 bytes
            int char4 = byte3 & 0x3f; // fourth 6 bytes

            
            sb.append(baseChars[char1]); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
            sb.append(baseChars[char2]); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
            sb.append(baseChars[char3]); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
            sb.append(baseChars[char4]); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
        }

        if (tripletRest > 0) {
            int byte0 = value[index++] & 0xff;
            sb.append(baseChars[byte0 >> 2]);
            if (tripletRest == 1) {
                sb.append(baseChars[(byte0 << 4) & 0x3f]); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
                sb.append('='); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
                sb.append('='); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
            } else {
                int byte1 = value[index++] & 0xff;
                sb.append(baseChars[(byte0 << 4)&0x3f | (byte1 >> 4)]); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
                sb.append(baseChars[(byte1 << 2)&0x3f]); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
                sb.append('='); if((wrapLen > 0) && (linelen++ >= wrapLen)) { linelen = 1; sb.append('\n'); }
            }
        }

        return sb.toString();
    }

    /**
     * Encodes bytes to single line base64 string. 
     * @param is   bytes to be encoded
     * @param appendable target
     * @param wrapLen - if greater than zero result is wrapped to this len
     */
    public static void encode(InputStream is, Appendable appendable, int wrapLen) {
        if(is == null) return;
        int index = 1;
        try {
            int position = 0;
            
            int byte1 = is.read();
            int byte2 = is.read();
            int byte3 = is.read();
            int len = 3;
            if(byte1 < 0) len = 0;    
            else if(byte2 < 0) len = 1;    
            else if(byte3 < 0) len = 2;    
            else len = 3;
            byte1 = byte1 < 0 ? 0 : byte1 & 0xff;
            byte2 = byte2 < 0 ? 0 : byte2 & 0xff;
            byte3 = byte3 < 0 ? 0 : byte3 & 0xff;

            while(len > 0) {

                if(len == 3) {
                    int char1 = byte1 >> 2; // first 6 bytes
                    int char2 = (byte1 << 4)&0x3f | (byte2 >> 4); // second 6 bytes
                    int char3 = (byte2 << 2)&0x3f | (byte3 >> 6); // third 6 bytes
                    int char4 = byte3 & 0x3f; // fourth 6 bytes
                    position = append(appendable, baseChars[char1], position, wrapLen);
                    position = append(appendable, baseChars[char2], position, wrapLen);
                    position = append(appendable, baseChars[char3], position, wrapLen);
                    position = append(appendable, baseChars[char4], position, wrapLen);
                } else if(len == 2) {
                    int char1 = byte1 >> 2; // first 6 bytes
                    int char2 = (byte1 << 4)&0x3f | (byte2 >> 4); // second 6 bytes
                    int char3 = (byte2 << 2)&0x3f | (byte3 >> 6); // third 6 bytes
                    position = append(appendable, baseChars[char1], position, wrapLen);
                    position = append(appendable, baseChars[char2], position, wrapLen);
                    position = append(appendable, baseChars[char3], position, wrapLen);
                    position = append(appendable, '=', position, wrapLen);
                } else if(len == 1) {
                    int char1 = byte1 >> 2; // first 6 bytes
                    int char2 = (byte1 << 4)&0x3f | (byte2 >> 4); // second 6 bytes
                    position = append(appendable, baseChars[char1], position, wrapLen);
                    position = append(appendable, baseChars[char2], position, wrapLen);
                    position = append(appendable, '=', position, wrapLen);
                    position = append(appendable, '=', position, wrapLen);
                }
                
            
                byte1 = is.read();
                byte2 = is.read();
                byte3 = is.read();
                if(byte1 < 0) len = 0;    
                else if(byte2 < 0) len = 1;    
                else if(byte3 < 0) len = 2;    
                else len = 3;
                byte1 = byte1 < 0 ? 0 : byte1 & 0xff;
                byte2 = byte2 < 0 ? 0 : byte2 & 0xff;
                byte3 = byte3 < 0 ? 0 : byte3 & 0xff;

                index++;
            }
        } catch(Exception e) {
            throw AsRuntimeEx.argument(e, "conversion failed at index " + index);
        }
        
    }

    private static int append(Appendable appendable, char c, int position, int wrapLen) throws IOException {
        appendable.append(c);
        if(wrapLen > 0) {
            position++;
            if(position >= wrapLen) {
                appendable.append('\n');
                position = 0;
            }
        }
        return position;
    } 


    /**
     * Decodes base64 string into bytes.
     * @param value - base64 encoded data
     * @return resulting bytes 
     */
    public static byte[] decode(String value) {
        if(value == null) return null;
        if("".equals(value)) return new byte[0];

        int pos = value.indexOf('\n');
        if(pos > -1) {
            int lastpos = 0;
            StringBuilder sb = new StringBuilder(value.length());
            while(pos > -1) {
                sb.append(value.substring(lastpos, pos).trim());
                lastpos = pos + 1;
                pos = value.indexOf('\n', lastpos);
            }
            sb.append(value.substring(lastpos).trim());
            value = sb.toString();
        }

        
        value = value.trim();
        
        int length = value.length();
        int quaternionNum = length / 4;
        if (length % 4 != 0) throw new IllegalArgumentException("The input string is not valid base64 string. Length must be devided by four");
        
        int lastQuaternionsize = 0;
        int lastQuaternionbytesize = 0;    ;
        if (value.charAt(length-2) == '=') {
            lastQuaternionsize = 2;
            lastQuaternionbytesize = 1;
            quaternionNum--;
        } else if (value.charAt(length-1) == '=') {
            lastQuaternionsize = 3;
            lastQuaternionbytesize = 2;
            quaternionNum--;
        }
        
        byte[] buff = new byte[3*quaternionNum + lastQuaternionbytesize];

        int sIndex = 0;
        int bIndex = 0;
        for (int i=0; i<quaternionNum; i++) {
            int char1 = char2int(value.charAt(sIndex++));
            int char2 = char2int(value.charAt(sIndex++));
            int char3 = char2int(value.charAt(sIndex++));
            int char4 = char2int(value.charAt(sIndex++));
            buff[bIndex++] = (byte) ((char1 << 2) | (char2 >> 4));
            buff[bIndex++] = (byte) ((char2 << 4) | (char3 >> 2));
            buff[bIndex++] = (byte) ((char3 << 6) | char4);
        }

        if (lastQuaternionsize > 0) {
            int char1 = char2int(value.charAt(sIndex++));
            int char2 = char2int(value.charAt(sIndex++));
            buff[bIndex++] = (byte) ((char1 << 2) | (char2 >> 4));
            if (lastQuaternionsize > 2) {
                int char3 = char2int(value.charAt(sIndex++));
                buff[bIndex++] = (byte) ((char2 << 4) | (char3 >> 2));
            }
        }

        return buff;
    }

    /**
     * Decode from reader to output stream
     * @param reader input
     * @param os output
     */    
    public static void decode(Reader reader, OutputStream os) {
        if(reader == null) return;
        int index = 1;
        try {

            int position = 1;
            int buff = 0;
            int c = reader.read(); while( ignorable(c)) c = reader.read();
            while((c > -1) && (c != '=')) {
                int cint = char2int((char)c);
                if(position == 1) {
                    buff = (cint << 2);
                } else if(position == 2) {
                    buff = buff | (cint >> 4);
                    os.write(buff);
                    buff = (cint << 4);
                } else if(position == 3) {
                    buff = buff | (cint >> 2);
                    os.write(buff);
                    buff = (cint << 6);
                } else if(position == 4) {
                    buff = buff | cint;
                    os.write(buff);
                    buff = 0;
                }
                
                c = reader.read(); while( ignorable(c)) c = reader.read();
                index++;
                position++;
                if(position >= 5) position = 1;
            }

            //if(position > 1) os.write(buff);
            

        } catch(Exception e) {
            throw AsRuntimeEx.argument(e, "conversion failed at index " + index);
        }
    }

    private static boolean ignorable(int c) {
        if(c == '\n') return true;
        if(c == '\r') return true;
        if(c == '\t') return true;
        if(c == ' ') return true;
        return false;
    }

    private static int char2int(char c) {
        if(c == '+') return 62;
        if(c == '/') return 63;
        if((c >= 'A') && (c <= 'Z')) return c - 'A' + 0;
        if((c >= 'a') && (c <= 'z')) return c - 'a' + 26;
        if((c >= '0') && (c <= '9')) return c - '0' + 52;
        throw new IllegalArgumentException("The char '"+c+"' is not valid base64 char.");
    }


    private static final char baseChars[] = {
      // 0    1    2    3    4    5    6    7    8    9
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',  // 00
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',  // 10
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',  // 20
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',  // 30 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',  // 40 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',  // 50
        '8', '9', '+', '/'                                 // 60
    };


}
