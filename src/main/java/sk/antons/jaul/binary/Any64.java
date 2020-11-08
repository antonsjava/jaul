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

import java.util.Arrays;

/**
 * Simple bytes to text encoder based on base64 functionality with possibility 
 * to provide own char set for mapping.
 * 
 * It is recommended to use this implementation instead of Base64 only if you 
 * have conflict with a char in Base64 charset.
 * 
 * As any character can be in defined charset no formatting of the encoded 
 * text is allowed. 
 * 
 * Generated text is not padded to four characters. It contains only valid chars.
 * 
 * Provided charset is expected to be 64 unique ascii chars. And it is sorted 
 * before usage to get exact order. 
 * 
 * As there is little preprocessing at the beginning it is recommended to use 
 * one Any64 instance whenever it is possible. (Implementation is reentrant.)
 * 
 * @author antons
 */
public class Any64 {

    private char[] charset;
    private Node root;

    /**
     * Creates encoder for defined charset.
     * @param charset provided charset (64 unique ascii chars)
     */
    public Any64(char[] charset) {
        if(charset == null) throw new IllegalArgumentException("Null charset");
        if(charset.length != 64) throw new IllegalArgumentException("Charset has no 64 characters but " + charset.length);
        //System.out.println(" charset: " + new String(charset, 0, 64));
        this.charset = new char[64];
        System.arraycopy(charset, 0, this.charset, 0, 64);
        Arrays.sort(this.charset);
        root = buildtree();
        //root.print("");
        //System.out.println(" charset: " + new String(this.charset, 0, 64));
    }

    /**
     * Creates encoder for defined charset.
     * @param charset provided charset (64 unique ascii chars)
     * @return encoder instance
     */
    public static Any64 instance(char[] charset) {
        return new Any64(charset);
    }
    
    /**
     * Creates encoder for same charset as Base64 except plus and slash char.
     * @param plus character to replace plus char in Base64
     * @param slash character to replace slash char in Base64
     * @return encoder instance
     */
    public static Any64 instance(char plus, char slash) {
        char[] baseChars = new char[] {
          // 0    1    2    3    4    5    6    7    8    9
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',  // 00
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',  // 10
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',  // 20
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',  // 30 
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',  // 40 
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',  // 50
            '8', '9', plus, slash                              // 60
        };
        return new Any64(baseChars);
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
        int linelen = 0;

        StringBuilder sb = new StringBuilder(tripletNum*4 + 4);
        
        int index = 0;
        for (int i=0; i<tripletNum; i++) {
            int byte1 = value[index++] & 0xff;
            int byte2 = value[index++] & 0xff;
            int byte3 = value[index++] & 0xff;
            
            int char1 = byte1 >> 2; // first 6 bytes
            int char2 = (byte1 << 4)&0x3f | (byte2 >> 4); // second 6 bytes
            int char3 = (byte2 << 2)&0x3f | (byte3 >> 6); // third 6 bytes
            int char4 = byte3 & 0x3f; // fourth 6 bytes
            
            sb.append(charset[char1]);
            sb.append(charset[char2]);
            sb.append(charset[char3]);
            sb.append(charset[char4]);
            linelen = linelen + 4;
        }

        if (tripletRest > 0) {
            int byte0 = value[index++] & 0xff;
            sb.append(charset[byte0 >> 2]);
            if (tripletRest == 1) {
                sb.append(charset[(byte0 << 4) & 0x3f]);
            } else {
                int byte1 = value[index++] & 0xff;
                sb.append(charset[(byte0 << 4)&0x3f | (byte1 >> 4)]);
                sb.append(charset[(byte1 << 2)&0x3f]);
            }
        }

        return sb.toString();
    }


    /**
     * Decodes string into bytes.
     * @param value - encoded data
     * @return resulting bytes 
     */
    public byte[] decode(String value) {
        if(value == null) return null;
        if("".equals(value)) return new byte[0];
        
        int length = value.length();
        int quaternionNum = length / 4;
        int quaternionRest = length % 4;
        
        int lastQuaternionsize = 0;
        int lastQuaternionbytesize = 0;    ;
        if (quaternionRest == 1) {
            throw new IllegalArgumentException("Illegal length of encoded string");
        } else if (quaternionRest == 2) {
            lastQuaternionsize = 2;
            lastQuaternionbytesize = 1;
        } else if (quaternionRest == 3) {
            lastQuaternionsize = 3;
            lastQuaternionbytesize = 2;
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

    private int char2int(char c) {
        return char2int(c, root);
        //return char2int(c, 0, 65);
    }
    
    private int char2int(char c, int lindex, int uindex) {
        //System.out.println("char: "+c+" ["+lindex+", "+uindex+"]");
        int isize = uindex - lindex;
        if(isize < 2) {
            if(c == charset[lindex]) return lindex;
            else throw new IllegalArgumentException("The char '"+c+"' is not valid base64 char.");
        } else {
            int pivot = lindex + isize / 2;
            //System.out.println(" pivot " + pivot);
            int cc = charset[pivot];
            if(c == cc) {
                return pivot;
            } else if(c < cc) {
                return char2int(c, lindex, pivot);
            } else {
                return char2int(c, pivot+1, uindex);
            }
        }
    }
    
    private int char2int(char c, Node node) {
        if(node == null) throw new IllegalArgumentException("The char '"+c+"' is not valid base64 char.");
        if(c == node.c) return node.index;
        else if(c < node.c) return char2int(c, node.left);
        else return char2int(c, node.right);
    }

    private Node buildtree() {
        Node node0 = new Node(charset[0], 0, null, null);
        Node node1 = new Node(charset[1], 1, node0, null);
        Node node3 = new Node(charset[3], 3, null, null);
        Node node2 = new Node(charset[2], 2, node1, node3);
        Node node5 = new Node(charset[5], 5, null, null);
        Node node7 = new Node(charset[7], 7, null, null);
        Node node6 = new Node(charset[6], 6, node5, node7);
        Node node4 = new Node(charset[4], 4, node2, node6);
        Node node9 = new Node(charset[9], 9, null, null);
        Node node11 = new Node(charset[11], 11, null, null);
        Node node10 = new Node(charset[10], 10, node9, node11);
        Node node13 = new Node(charset[13], 13, null, null);
        Node node15 = new Node(charset[15], 15, null, null);
        Node node14 = new Node(charset[14], 14, node13, node15);
        Node node12 = new Node(charset[12], 12, node10, node14);
        Node node8 = new Node(charset[8], 8, node4, node12);
        Node node17 = new Node(charset[17], 17, null, null);
        Node node19 = new Node(charset[19], 19, null, null);
        Node node18 = new Node(charset[18], 18, node17, node19);
        Node node21 = new Node(charset[21], 21, null, null);
        Node node23 = new Node(charset[23], 23, null, null);
        Node node22 = new Node(charset[22], 22, node21, node23);
        Node node20 = new Node(charset[20], 20, node18, node22);
        Node node25 = new Node(charset[25], 25, null, null);
        Node node27 = new Node(charset[27], 27, null, null);
        Node node26 = new Node(charset[26], 26, node25, node27);
        Node node29 = new Node(charset[29], 29, null, null);
        Node node31 = new Node(charset[31], 31, null, null);
        Node node30 = new Node(charset[30], 30, node29, node31);
        Node node28 = new Node(charset[28], 28, node26, node30);
        Node node24 = new Node(charset[24], 24, node20, node28);
        Node node16 = new Node(charset[16], 16, node8, node24);
        Node node33 = new Node(charset[33], 33, null, null);
        Node node35 = new Node(charset[35], 35, null, null);
        Node node34 = new Node(charset[34], 34, node33, node35);
        Node node37 = new Node(charset[37], 37, null, null);
        Node node39 = new Node(charset[39], 39, null, null);
        Node node38 = new Node(charset[38], 38, node37, node39);
        Node node36 = new Node(charset[36], 36, node34, node38);
        Node node41 = new Node(charset[41], 41, null, null);
        Node node43 = new Node(charset[43], 43, null, null);
        Node node42 = new Node(charset[42], 42, node41, node43);
        Node node45 = new Node(charset[45], 45, null, null);
        Node node47 = new Node(charset[47], 47, null, null);
        Node node46 = new Node(charset[46], 46, node45, node47);
        Node node44 = new Node(charset[44], 44, node42, node46);
        Node node40 = new Node(charset[40], 40, node36, node44);
        Node node49 = new Node(charset[49], 49, null, null);
        Node node51 = new Node(charset[51], 51, null, null);
        Node node50 = new Node(charset[50], 50, node49, node51);
        Node node53 = new Node(charset[53], 53, null, null);
        Node node55 = new Node(charset[55], 55, null, null);
        Node node54 = new Node(charset[54], 54, node53, node55);
        Node node52 = new Node(charset[52], 52, node50, node54);
        Node node57 = new Node(charset[57], 57, null, null);
        Node node59 = new Node(charset[59], 59, null, null);
        Node node58 = new Node(charset[58], 58, node57, node59);
        Node node61 = new Node(charset[61], 61, null, null);
        Node node63 = new Node(charset[63], 63, null, null);
        Node node62 = new Node(charset[62], 62, node61, node63);
        Node node60 = new Node(charset[60], 60, node58, node62);
        Node node56 = new Node(charset[56], 56, node52, node60);
        Node node48 = new Node(charset[48], 48, node40, node56);
        Node node32 = new Node(charset[32], 32, node16, node48);
        return node32;
    }

    private static class Node {
        private char c;
        private int index;
        private Node left;
        private Node right;
        private Node(char c, int index, Node left, Node right) {
            this.c = c;
            this.index = index;
            this.left = left;
            this.right = right;
        }
        private void print(String prefix) {
            if(left != null) left.print(prefix + "  ");
            System.out.println(prefix + " " + c + " " + index);
            if(right != null) right.print(prefix + "  ");
        }
    }

//    private static int printcode(int lindex, int uindex) {
//        int isize = uindex - lindex;
//        if(isize < 1) return -1;
//        int pivot = lindex + isize / 2;
//        int left = -1;
//        int right = -1;
//        if(isize > 1) {
//            left = printcode(lindex, pivot);
//            right = printcode(pivot+1, uindex);
//        }
//        StringBuilder sb = new StringBuilder();
//        sb.append("        Node node").append(pivot);
//        sb.append(" = new Node(charset[").append(pivot);
//        sb.append("], ").append(pivot);
//        sb.append(", ");
//        if(left < 0) sb.append("null");
//        else sb.append("node").append(left);
//        sb.append(", ");
//        if(right < 0) sb.append("null");
//        else sb.append("node").append(right);
//        sb.append(");");
//        System.out.println(sb.toString());
//        return pivot;
//    }
//
//    public static void main(String[] argv) {
//        printcode(0, 64);
//        Any64 encoder = Any64.instance('-', '.');
//        
//    }


}
