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

/**
 * Simple substring replacer. It provides methods to substring by another
 * substring. (No regext replacement - simple text by text)
 *
 * @author antons
 */
public class Replace {

    /**
     * Replaces all occurrences of given substring by another substring.
     * @param where Text whee the substrings must be replaced
     * @param what old Substring, which must be replaced
     * @param byWhat new substring, which must replace old one
     * @return Replaced text
     */
    public static String all(String where, String what, String byWhat) {
        if(where == null) return where;
        if(what == null) return where;
        if(byWhat == null) byWhat = "";
        int last = 0;
        int pos = where.indexOf(what, last);
        if(pos < 0) return where;
        StringBuilder buff = new StringBuilder(where.length()*2);
        while(pos > -1) {
            buff.append(where.substring(last, pos));
            buff.append(byWhat);
            last = pos + what.length();
            pos = where.indexOf(what, last);
        }
        buff.append(where.substring(last));
        return buff.toString();
    }


    /**
     * Replaces first occurrence of given substring by another substring.
     * @param where Text whee the substring must be replaced
     * @param what old Substring, which must be replaced
     * @param byWhat new substring, which must replace old one
     * @return Replaced text
     */
    public static String first(String where, String what, String byWhat) {
        if(where == null) return where;
        if(what == null) return where;
        if(byWhat == null) return byWhat = "";
        int pos = where.indexOf(what);
        if(pos < 0) return where;
        else {
            StringBuilder buff = new StringBuilder(where.length() + 100);
            buff.append(where.substring(0, pos));
            buff.append(byWhat);
            int last = pos + what.length();
            buff.append(where.substring(last));
            return buff.toString();
        }
    }

    /**
     * Replaces last occurrence of given substring by another substring.
     * @param where Text whee the substring must be replaced
     * @param what old Substring, which must be replaced
     * @param byWhat new substring, which must replace old one
     * @return Replaced text
     */
    public static String last(String where, String what, String byWhat) {
        if(where == null) return where;
        if(what == null) return where;
        if(byWhat == null) return byWhat = "";
        int pos = where.lastIndexOf(what);
        if(pos < 0) return where;
        else {
            StringBuilder buff = new StringBuilder(where.length() + 100);
            buff.append(where.substring(0, pos));
            buff.append(byWhat);
            int last = pos + what.length();
            buff.append(where.substring(last));
            return buff.toString();
        }
    }

}
