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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import sk.antons.jaul.util.AsRuntimeEx;

/**
 * Simple base64 encoder.
 *
 * @author antons
 */
public class Base64 {

    /**
     * Use form Base64.standard().encode(value)
     * @param value
     * @return
     * @deprecated
     */
    @Deprecated
    public static String encode(byte[] value) {
        return Base64.standard().encode(value);
    }

    /**
     * Use form Base64.standard().encode(value)
     * @param value
     * @param format
     * @return
     * @deprecated
     */
    @Deprecated
    public static String encode(byte[] value, boolean format) {
        if(format) return Base64.standardBased().wrap(76).build().encode(value);
        else return Base64.standard().encode(value);
    }

    /**
     * Use form Base64.standard().encode(value)
     * @param value
     * @param wrapLen
     * @return
     * @deprecated
     */
    @Deprecated
    public static String encode(byte[] value, int wrapLen) {
        if(wrapLen == 0) return Base64.standard().encode(value);
        else return Base64.standardBased().wrap(wrapLen).build().encode(value);
    }

    /**
     * Use form Base64.standard().encode()
     * @param is
     * @param appendable
     * @param wrapLen
     * @deprecated
     */
    @Deprecated
    public static void encode(InputStream is, Appendable appendable, int wrapLen) {
        if(wrapLen == 0) Base64.standard().encode(is, appendable);
        else Base64.standardBased().wrap(wrapLen).build().encode(is, appendable);
    }

    /**
     * Use form Base64.standard().decode()
     * @param value
     * @return
     * @deprecated
     */
    @Deprecated
    public static byte[] decode(String value) {
        return standard().decode(value);
    }

    /**
     * Use Base64.standard().decode()
     * @param reader
     * @param os
     * @deprecated
     */
    @Deprecated
    public static void decode(Reader reader, OutputStream os) {
        standard.decode(reader, os);
    }

    private static Coder standard = null;
    /**
     * Standard base64 encoding with no wrapping and padding with '=';
     * @return
     */
    public static Coder standard() {
        if(standard == null) standard = Base64.standardBased().padding('=').build();
        return standard;
    }

    private static Coder url = null;
    /**
     * Url based base64 encoding with no wrapping and no padding;
     * @return
     */
    public static Coder url() {
        if(url == null) url = Base64.urlBased().nopadding().build();
        return url;
    }

    public static class Coder {

        private final char baseChars[];
        private final char pluss;
        private final char slash;
        private final char padding;
        private final int wrap;

        private Coder(char baseChars[], char padding, int wrap) {
            if(baseChars == null) throw new IllegalArgumentException("no base chars defined");
            if(baseChars.length != 64) throw new IllegalArgumentException("base chars length is not 64 but "+ baseChars.length);
            this.baseChars = baseChars;
            this.padding = padding;
            this.wrap = wrap;
            this.pluss = baseChars[62];
            this.slash = baseChars[63];
        }

        private int char2int(char c) {
            if(c == pluss) return 62;
            if(c == slash) return 63;
            if((c >= 'A') && (c <= 'Z')) return c - 'A' + 0;
            if((c >= 'a') && (c <= 'z')) return c - 'a' + 26;
            if((c >= '0') && (c <= '9')) return c - '0' + 52;
            throw new IllegalArgumentException("The char '"+c+"' is not valid base64 char.");
        }

        /**
         * Decode from reader to output stream
         * @param reader input
         * @param os output
         */
        public void decode(Reader reader, OutputStream os) {
            if(reader == null) return;
            int index = 1;
            try {

                int position = 1;
                int buff = 0;
                int c = reader.read(); while( ignorable(c)) c = reader.read();
                while((c > -1) && (c != padding)) {
                    int cint = char2int((char)c);
                    switch(position) {
                        case 1:
                            buff = (cint << 2);
                            break;
                        case 2:
                            buff = buff | (cint >> 4);
                            os.write(buff);
                            buff = (cint << 4);
                            break;
                        case 3:
                            buff = buff | (cint >> 2);
                            os.write(buff);
                            buff = (cint << 6);
                            break;
                        case 4:
                            buff = buff | cint;
                            os.write(buff);
                            buff = 0;
                            break;
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

        /**
         * Decodes base64 string into bytes.
         * @param value - base64 encoded data
         * @return resulting bytes
         */
        public byte[] decode(String value) {
            if(value == null) return null;
            if("".equals(value)) return new byte[0];

            int length = value.length();
            int quaternionNum = length / 4;
            StringReader reader = new StringReader(value);
            ByteArrayOutputStream os = new ByteArrayOutputStream((quaternionNum+10)*3);
            decode(reader, os);
            return os.toByteArray();
        }

        /**
         * Encodes bytes to single line base64 string.
         * @param is   bytes to be encoded
         * @param appendable target
         */
        public void encode(InputStream is, Appendable appendable) {
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
                        position = append(appendable, baseChars[char1], position, wrap);
                        position = append(appendable, baseChars[char2], position, wrap);
                        position = append(appendable, baseChars[char3], position, wrap);
                        position = append(appendable, baseChars[char4], position, wrap);
                    } else if(len == 2) {
                        int char1 = byte1 >> 2; // first 6 bytes
                        int char2 = (byte1 << 4)&0x3f | (byte2 >> 4); // second 6 bytes
                        int char3 = (byte2 << 2)&0x3f | (byte3 >> 6); // third 6 bytes
                        position = append(appendable, baseChars[char1], position, wrap);
                        position = append(appendable, baseChars[char2], position, wrap);
                        position = append(appendable, baseChars[char3], position, wrap);
                        if(padding != 0) position = append(appendable, padding, position, wrap);
                    } else if(len == 1) {
                        int char1 = byte1 >> 2; // first 6 bytes
                        int char2 = (byte1 << 4)&0x3f | (byte2 >> 4); // second 6 bytes
                        position = append(appendable, baseChars[char1], position, wrap);
                        position = append(appendable, baseChars[char2], position, wrap);
                        if(padding != 0) position = append(appendable, padding, position, wrap);
                        if(padding != 0) position = append(appendable, padding, position, wrap);
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

        /**
         * Encodes bytes to single line base64 string.
         * @param value   bytes to be encoded
         * @return encoded data.
         */
        public String encode(byte[] value) {
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


                sb.append(baseChars[char1]); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                sb.append(baseChars[char2]); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                sb.append(baseChars[char3]); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                sb.append(baseChars[char4]); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
            }

            if (tripletRest > 0) {
                int byte0 = value[index++] & 0xff;
                sb.append(baseChars[byte0 >> 2]);
                if (tripletRest == 1) {
                    sb.append(baseChars[(byte0 << 4) & 0x3f]); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                    if(padding != 0) sb.append(padding); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                    if(padding != 0) sb.append(padding); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                } else {
                    int byte1 = value[index++] & 0xff;
                    sb.append(baseChars[(byte0 << 4)&0x3f | (byte1 >> 4)]); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                    sb.append(baseChars[(byte1 << 2)&0x3f]); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                    if(padding != 0) sb.append(padding); if((wrap > 0) && (linelen++ >= wrap)) { linelen = 1; sb.append('\n'); }
                }
            }

            return sb.toString();
        }


    }

    private static boolean ignorable(int c) {
        switch(c) {
            case '\n':
            case '\r':
            case '\t':
            case ' ':
                return true;
            default:
                return false;
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


    private static final char baseCharsStandard[] = {
      // 0    1    2    3    4    5    6    7    8    9
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',  // 00
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',  // 10
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',  // 20
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',  // 30
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',  // 40
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',  // 50
        '8', '9', '+', '/'                                 // 60
    };

    private static final char baseCharsUrl[] = {
      // 0    1    2    3    4    5    6    7    8    9
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',  // 00
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',  // 10
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',  // 20
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',  // 30
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',  // 40
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',  // 50
        '8', '9', '-', '_'                                 // 60
    };

    public static Builder standardBased() { return new Builder(baseCharsStandard); }
    public static Builder urlBased() { return new Builder(baseCharsUrl); }
    public static Builder otherBased(char pluss, char slash) {
        char[] array = new char[64];
        System.arraycopy(baseCharsStandard, 0, array, 0, 64);
        array[62] = pluss;
        array[63] = slash;
        return new Builder(array);
    }

    public static class Builder {
        private char[] baseChars = baseCharsStandard;
        private char padding = '=';
        private int wrap = 0;

        private Builder(char[] baseChars) {
            this.baseChars = baseChars;
        }

        public Builder padding(char value) { this.padding = value; return this; }
        public Builder nopadding() { this.padding = 0; return this; }
        public Builder wrap(int value) { this.wrap = value; return this; }

        public Coder build() {
            return new Coder(baseChars, padding, wrap);
        }

    }

}
