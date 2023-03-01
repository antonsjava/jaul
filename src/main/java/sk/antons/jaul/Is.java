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
import java.util.Map;

/**
 * Helper class for manipulating nulls in case collections, arrays and 
 * primitives in condition expressions.
 *
 * @author antons
 */
public class Is {
    
    
    /**
     * Checks if string is empty.
     * @param value - checked text
     * @return true if text is not null and text length > 0
     */
    public static boolean empty(String value) {
        if(value == null) return true;
        return value.length() == 0;
    }
    
    /**
     * Checks if collection is empty.
     * @param value - checked value
     * @return true if collection is not null and collection length > 0
     */
    public static boolean empty(Collection value) {
        if(value == null) return true;
        return value.isEmpty();
    }

    /**
     * Checks if map is empty.
     * @param value - checked value
     * @return true if map is not null and map size > 0
     */
    public static boolean empty(Map value) {
        if(value == null) return true;
        return value.isEmpty();
    }
    
    /**
     * Checks if array is empty.
     * @param value - checked value
     * @return true if array is not null and array length > 0
     */
    public static boolean empty(Object[] value) {
        if(value == null) return true;
        return value.length == 0;
    }

    /**
     * Checks if object is null. The method also handles primitive arrays.
     * @param value - checked value
     * @return true if object is not null
     */
    public static boolean empty(Object value) {
        if(value == null) return true;
        if(value.getClass().isArray()) {
            if(value instanceof byte[]) return ((byte[])value).length == 0;
            if(value instanceof int[]) return ((int[])value).length == 0;
            if(value instanceof char[]) return ((char[])value).length == 0;
            if(value instanceof short[]) return ((short[])value).length == 0;
            if(value instanceof long[]) return ((long[])value).length == 0;
            if(value instanceof float[]) return ((float[])value).length == 0;
            if(value instanceof double[]) return ((double[])value).length == 0;
            if(value instanceof boolean[]) return ((boolean[])value).length == 0;
        }
        return false;
    }


    /**
     * Checks if provided value is zero. Used for checking valid object ids mostly.
     * @param value - provided value
     * @return true if value is zero
     */
    public static boolean zero(int value) {
        return value == 0;
    }
    
    /**
     * Checks if provided value is zero. Used for checking valid object ids mostly.
     * @param value - provided value
     * @return true if value is zero or null
     */
    public static boolean zero(Integer value) {
        if(value == null) return true;
        return value == 0;
    }
    

    /**
     * Checks if provided value is zero. Used for checking valid object ids mostly.
     * @param value - provided value
     * @return true if value is zero
     */
    public static boolean zero(long value) {
        return value == 0;
    }
    
    /**
     * Checks if provided value is zero. Used for checking valid object ids mostly.
     * @param value - provided value
     * @return true if value is zero or null
     */
    public static boolean zero(Long value) {
        if(value == null) return true;
        return value == 0;
    }


    /**
     * Safely inboxing provided boolean value
     * @param value boolean value 
     * @return true is unvoxed value is true
     */
    public static boolean truth(Boolean value) {
        if(value == null) return false;
        return value.booleanValue();
    }
    
}
