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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import sk.antons.jaul.util.FromDate;
import sk.antons.jaul.util.FromString;

/**
 * Helper class for manipulating nulls in case collections, arrays and primitives.
 *
 * @author antons
 */
public class Get {
    
    /**
     * First element of provided collection. 
     * @param value - provided collection 
     * @return first element of collection of null if provided collection is empty.
     */
    public static <T> T first(List<T> value) {
        if(value == null) return null;
        if(value.isEmpty()) return null;
        return (T) value.get(0);
    }
    
    /**
     * First element of provided array. 
     * @param value - provided array 
     * @return first element of array of null if provided collection is empty.
     */
    public static <T> T first(T[] value) {
        if(value == null) return null;
        return (T) value[0];
    }

    /**
     * First element of provided collection. 
     * @param value - provided collection 
     * @return first element of collection of null if provided collection is empty.
     */
    public static <T> T first(Collection<T> value) {
        if(value == null) return null;
        if(value.isEmpty()) return null;
        return (T) value.iterator().next();
    }


    /**
     * last element of provided collection. 
     * @param value - provided collection 
     * @return last element of collection or null if provided collection is empty.
     */
    public static <T> T last(List<T> value) {
        if(value == null) return null;
        if(value.isEmpty()) return null;
        return (T) value.get(value.size()-1);
    }
    
    /**
     * last element of provided array. 
     * @param value - provided array 
     * @return last element of array or null if provided collection is empty.
     */
    public static <T> T last(T[] value) {
        if(value == null) return null;
        return (T) value[value.length-1];
    }
    
    /**
     * Nth element of provided collection. 
     * @param value - provided collection 
     * @param index - index of element 
     * @return Nth element of collection or null if provided collection index not exists.
     */
    public static <T> T nth(List<T> value, int index) {
        if(value == null) return null;
        if(value.isEmpty()) return null;
        if(index < 0) return null;
        if(index >= value.size()) return null;
        return (T) value.get(index);
    }

    /**
     * Nth element of provided array. 
     * @param value - provided array 
     * @param index - index of element 
     * @return Nth element of array or null if provided array index not exists.
     */
    public static <T> T nth(T[] value, int index) {
        if(value == null) return null;
        if(index < 0) return null;
        if(index >= value.length) return null;
        return (T) value[index];
    }



    /**
     * Safe string value .
     * @param value - value to be returned
     * @return provided value or "" if provided value is null.
     */
    public static String safeString(String value) {
        if(value == null) return "";
        return value;
    }

    /**
     * Safe string value .
     * @param value - value to be returned
     * @param def - value to be returned if first parameter is null
     * @return provided value or def if provided value is null.
     */
    public static String safeString(String value, String def) {
        if(value == null) return def;
        return value;
    }

    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or 0 if provided value is null.
     */
    public static int safeInt(Integer value) {
        if(value == null) return 0;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static int safeInt(Integer value, int defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or 0 if provided value is null.
     */
    public static byte safeByte(Byte value) {
        if(value == null) return 0;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static byte safeByte(Byte value, byte defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or 0 if provided value is null.
     */
    public static short safeShort(Short value) {
        if(value == null) return 0;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static short safeShort(Short value, short defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or 0 if provided value is null.
     */
    public static long safeLong(Long value) {
        if(value == null) return 0;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static long safeLong(Long value, long defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or 0 if provided value is null.
     */
    public static float safeFloat(Float value) {
        if(value == null) return 0;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static float safeFloat(Float value, float defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or 0 if provided value is null.
     */
    public static double safeDouble(Double value) {
        if(value == null) return 0;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static double safeDouble(Double value, double defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or false if provided value is null.
     */
    public static boolean safeBool(Boolean value) {
        if(value == null) return false;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static boolean safeBool(Boolean value, boolean defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @return unboxed value or 0 if provided value is null.
     */
    public static char safeChar(Character value) {
        if(value == null) return 0;
        return value;
    }
    
    /**
     * Safe unboxing.
     * @param value - value to be unboxed
     * @param defaultValue default value if value is null
     * @return unboxed value or defaultValue if provided value is null.
     */
    public static char safeChar(Character value, char defaultValue) {
        if(value == null) return defaultValue;
        return value;
    }

    /**
     * Size of the provided string.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(String value) {
        if(value == null) return 0;
        return value.length();
    }

    /**
     * Size of the provided collection.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(Collection value) {
        if(value == null) return 0;
        return value.size();
    }
    
    /**
     * Size of the provided map.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(Map value) {
        if(value == null) return 0;
        return value.size();
    }
    
    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(Object[] value) {
        if(value == null) return 0;
        return value.length;
    }
    
    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(byte[] value) {
        if(value == null) return 0;
        return value.length;
    }
    
    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(char[] value) {
        if(value == null) return 0;
        return value.length;
    }

    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(short[] value) {
        if(value == null) return 0;
        return value.length;
    }

    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(int[] value) {
        if(value == null) return 0;
        return value.length;
    }

    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(long[] value) {
        if(value == null) return 0;
        return value.length;
    }

    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(float[] value) {
        if(value == null) return 0;
        return value.length;
    }

    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(double[] value) {
        if(value == null) return 0;
        return value.length;
    }

    /**
     * Size of the provided array.
     * @param value - provided data
     * @return size of the provided data or 0 is data is null
     */ 
    public static int size(boolean[] value) {
        if(value == null) return 0;
        return value.length;
    }

    /**
     * Trims provided value.
     * @param value - value to be trimmed
     * @return trimmed value of null if null was provided
     */
    public static String trim(String value) {
        if(value == null) return null;
        return value.trim();
    }
    
    /**
     * returns date converter
     * @param value - value to be converted
     * @return 
     */
    public static FromDate from(Date value) {
        return new FromDate(value, null);
    }
    
    /**
     * returns date converter
     * @param value - value to be converted
     * @param nullResult  - value to be used if value is null
     * @return 
     */
    public static FromDate from(Date value, String nullResult) {
        return new FromDate(value, nullResult);
    }

    /**
     * returns StringParser instance
     * @param value - value to be parsed
     * @return 
     */
    public static FromString from(String value) {
        return new FromString(value);
    }

    /**
     * returns StringParser instance
     * @param value - value to be parsed
     * @param defaultEmpty - return default value in case of empty string
     * @param defaultBad - return default value in case of bad string
     * @return 
     */
    public static FromString from(String value, boolean defaultEmpty, boolean defaultBad) {
        return new FromString(value, defaultEmpty, defaultBad);
    }
}
