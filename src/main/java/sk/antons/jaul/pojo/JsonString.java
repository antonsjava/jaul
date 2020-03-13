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

package sk.antons.jaul.pojo;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import sk.antons.jaul.binary.Base64;

/**
 * Simple StringBuilder wrapper with helper functionality to produce json string
 * Class provides methods for constructing json string. It check validity 
 * of resulted json. 
 * @author antons
 */
public class JsonString {

    private boolean ignoreNulls = true;
    private String indent = null;
    private StringBuilder sb = new StringBuilder();

    Deque<State> stack = new LinkedList<State>();
    
    /**
     * New instance of JsonString class.
     * @return 
     */
    public static JsonString instance() { return new JsonString(); }
    
    /**
     * If true it ignores null values in attr() method.
     * @param value new setting (default true)
     * @return this instance
     */
    public JsonString ignoreNulls(boolean value) { ignoreNulls = value; return this; }
    /**
     * If not null resulted string is indented using provided value.
     * @param value string used for indentation
     * @return this instance
     */
    public JsonString indent(String indent) { this.indent = indent; return this; }

    @Override
    public String toString() {
        return sb.toString();
    }
    
    /**
     * Adds '{' to buffer.
     * @return this instance
     */
    public JsonString objectStart() {
        if(indent ==  null) sb.append('{');
        else sb.append("{");
        something();
        stack.push(State.instance(Type.OBJECT));
        return this;
    }

    /**
     * Adds '}' to buffer.
     * @return this instance
     */
    public JsonString objectEnd() {
        if(indent != null) {
            if(isSomething()) {
                sb.append('\n');
                indent(-1);
            }
        }
        sb.append('}');
        stack.pop();
        return this;
    }
    
    /**
     * Adds '[' to buffer.
     * @return this instance
     */
    public JsonString arrayStart() {
        sb.append('[');
        something();
        stack.push(State.instance(Type.ARRAY));
        return this;
    }
    
    /**
     * Adds ']' to buffer.
     * @return this instance
     */
    public JsonString arrayEnd() {
        sb.append(']');
        stack.pop();
        return this;
    }

    /**
     * Adds value to buffer as array item or parameter value.
     * @return this instance
     */
    public JsonString value(Object value) { 
        if(isArray()) {
            if(isSomething()) {
                sb.append(',');
                if(indent != null) sb.append(' ');
            }
        }
        appendValue(value); 
        something();
        return this;
    }
    
    /**
     * Adds attribute name to buffer.
     * @return this instance
     */
    public JsonString attrName(String name) { 
        if(isSomething()) sb.append(',');
        if(indent != null) {
            sb.append('\n');
            indent();
        }
        sb.append('"').append(name).append('\"');
        if(indent != null) sb.append(" : ");
        else sb.append(":");
        something();
        return this;
    }

    /**
     * Adds attribute name and its value to buffer.
     * @return this instance
     */
    public JsonString attr(String name, Object value) { 
        if(ignoreNulls && (value == null)) return this; 
        attrName(name);
        value(value);
        return this;
    }


    private void appendValue(Object o) {
        
        if(o == null) {
            sb.append("null");
            return;
        }
        
        Class clazz = o.getClass();
        if(isNonStringClass(clazz)) {
            sb.append(o);
            return;
        }

        if(clazz.isArray()) {
            Class clazzin = clazz.getComponentType();
            if(clazzin.equals(byte.class)) {
                byte[] bytes = (byte[])o;
                String ss = Base64.encode(bytes);
                sb.append('"').append(ss).append('"');
                return;
            }
            arrayStart();
            int size = ((Object[])o).length;
            for(int i = 0; i < size; i++) {
                value(Array.get(o, i));
            }
            arrayEnd();
            return;
        }
        
        if(Collection.class.isAssignableFrom(clazz)) {
            Collection coll = (Collection)o;
            arrayStart();
            Iterator iter = coll.iterator();
            while(iter.hasNext()) {
                Object next = iter.next();
                value(next);
            }
            arrayEnd();
            return;
        }
        
        if(ToJsonString.class.isAssignableFrom(clazz)) {
            ToJsonString jsso = (ToJsonString)o;
            jsso.toJsonString(this, true);
            return;
        }

        sb.append('"').append(escape(o.toString())).append('"');
    }

    private boolean isNonStringClass(Class clazz) {
        
        if(clazz.equals(int.class)) return true;
        if(clazz.equals(long.class)) return true;
        if(clazz.equals(byte.class)) return true;
        if(clazz.equals(short.class)) return true;
        if(clazz.equals(double.class)) return true;
        if(clazz.equals(float.class)) return true;
        if(clazz.equals(boolean.class)) return true;
        if(clazz.equals(char.class)) return true;
        
        if(clazz.equals(Integer.class)) return true;
        if(clazz.equals(Long.class)) return true;
        if(clazz.equals(Byte.class)) return true;
        if(clazz.equals(Short.class)) return true;
        if(clazz.equals(Double.class)) return true;
        if(clazz.equals(Float.class)) return true;
        if(clazz.equals(Boolean.class)) return true;
        if(clazz.equals(Character.class)) return true;
        
        return false;
    }

    private boolean isArray() {
        if(stack.isEmpty()) return false;
        return stack.peek().type == Type.ARRAY;
    }
    private boolean isObject() {
        if(stack.isEmpty()) return false;
        return stack.peek().type == Type.OBJECT;
    }
    private void something() {
        if(stack.isEmpty()) return;
        stack.peek().something = true;
    }
    private boolean isSomething() {
        if(stack.isEmpty()) return false;
        return stack.peek().something;
    }
    private void indent() { indent(0); }
    private void indent(int correction) {
        int sum = stack.size() + correction;
        for(int i = 0; i < sum; i++) {
            sb.append(indent);
        }
    }
    

    private static enum Type { ARRAY, OBJECT; }
    private static class State {
        public Type type;
        public boolean something = false;
        public static State instance(Type type) {
            State s = new State();
            s.type = type;
            return s;
        }
    }

    
    private static String escape(String value) {
        if(value == null) return null;
        int offset = 0;
        int length = value.length();
        StringBuilder sb = new StringBuilder();
        int len = offset + length;
        for(int i = offset; i < len; i++) {
            char c = value.charAt(i);
            if(c == '"') sb.append("\\\"");
            else if(c == '\\') sb.append("\\\\");
            else if(c == '\n') sb.append("\\n");
            else if(c == '\t') sb.append("\\t");
            else if(c == '\r') sb.append("\\r");
            else if(c == '/') sb.append("\\/");
            else if(c == '\b') sb.append("\\b");
            else if(c == '\f') sb.append("\\f");
            else sb.append(c);
        }
        return sb.toString();
    }

    
}
