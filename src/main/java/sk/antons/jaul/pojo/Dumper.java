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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Generates dump of POJO instance.
 * @author antons
 */
public class Dumper {

    public static Dumper instance() { return new Dumper(); }
    public String dump(Object value) { 
		if(value == null) return null;
        StringBuilder sb = new StringBuilder("instance of ").append(value.getClass()).append(" hash: ").append(value.hashCode()).append('\n');
        dump(sb, "", value, new ArrayList());
        return sb.toString();
    }

    public String jsonPretty(Object value, String indent) {
        JsonString json = JsonString.instance().indent(indent);
        json(json, null, value, new ArrayList());
        return json.toString();
    }
    public String json(Object value) {
        JsonString json = JsonString.instance();
        json(json, null, value, new ArrayList());
        return json.toString();
    }

    private void dumpMedssage(StringBuilder sb, String path, String message) {
        sb.append(path).append(": ").append(message).append('\n');
    }
    
    private void dump(StringBuilder sb, String path, Object o, List stack) {
        if(o == null) {
            sb.append("null");
            return;
        }
		
		if(stack.contains(o)) return;
		stack.add(o);

        Class clazz = o.getClass();
        
        if(isSimpleClass(clazz)) {
            String s = o.toString();
            if(s != null) s = s.replace('\n', ' ');
            dumpMedssage(sb, path, s);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(clazz.isArray()) {
            dumpArray(sb, path, clazz, o, stack);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(Collection.class.isAssignableFrom(clazz)) {
            dumpCollection(sb, path, clazz, o, stack);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(Map.class.isAssignableFrom(clazz)) {
            dumpMap(sb, path, clazz, o, stack);
			stack.remove(stack.size()-1);
            return;
        }

        dumpObject(sb, path, clazz, o, stack);
		stack.remove(stack.size()-1);
    }

    private boolean isSimpleClass(Class clazz) {
        if(clazz.equals(String.class)) return true;
        
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
       
        if(clazz.isEnum()) return true;
        
        if(clazz.equals(LocalDateTime.class)) return true;
        if(clazz.equals(LocalDate.class)) return true;
        if(clazz.equals(Date.class)) return true;
        
        return false;
    }
    
    private void dumpArray(StringBuilder sb, String path, Class clazz, Object o, List stack) {
        if(clazz.isArray()) {
            Class cl = clazz.getComponentType();
            if(byte.class.equals(cl)) {
                byte[] arr = (byte[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else if(int.class.equals(cl)) {
                int[] arr = (int[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else if(long.class.equals(cl)) {
                long[] arr = (long[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else if(char.class.equals(cl)) {
                char[] arr = (char[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else if(short.class.equals(cl)) {
                short[] arr = (short[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else if(float.class.equals(cl)) {
                float[] arr = (float[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else if(double.class.equals(cl)) {
                double[] arr = (double[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else if(boolean.class.equals(cl)) {
                boolean[] arr = (boolean[])o;
                StringBuilder tmbbuff = new StringBuilder();
                tmbbuff.append("[");
                for(int i = 0; i < 5; i++) {
                    if(arr.length <= i) break;
                    if(i > 0) tmbbuff.append(", ");
                    tmbbuff.append(arr[i]);
                }
                if(arr.length > 5) tmbbuff.append(", ..." + arr.length);
                tmbbuff.append("]");
                dumpMedssage(sb, path, tmbbuff.toString());
            } else {
                int size = arraySize(clazz, o);
                dumpMedssage(sb, path+".size", ""+size);
                for(int i = 0; i < size; i++) {
                    Object oo = arrayItem(clazz, o, i);
                    dump(sb, path+"["+i+"]", oo, stack);
                }
            }

        }
    }
    
    private void dumpCollection(StringBuilder sb, String path, Class clazz, Object o, List stack) {
        if(Collection.class.isAssignableFrom(clazz)) {
            Collection list = (Collection)o;
            int size = list.size();
            dumpMedssage(sb, path+".size", ""+size);
            int i = 0;
            Iterator iter = list.iterator();
            while(iter.hasNext()) {
                Object oo = iter.next();
                dump(sb, path+'['+i+']', oo, stack);
                i++;
            }
        }
    }

    
    
    private void dumpMap(StringBuilder sb, String path, Class clazz, Object o, List stack) {
        if(Map.class.isAssignableFrom(clazz)) {
            Map map = (Map)o;
            int size = map.size();
            dumpMedssage(sb, path+".size", ""+size);
            for(Object key : map.keySet()) {
                dump(sb, path+'['+key+']', map.get(key), stack);
            }
        }
    }
    private FieldCopmarator comp = FieldCopmarator.instance(); 
    private void dumpObject(StringBuilder sb, String path, Class clazz, Object o, List stack) {
        List<Field> fields = allFields(clazz);
        if(fields == null) return;
        Collections.sort(fields, comp);
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) continue;
            Object oo = value(clazz, o, field);
            if(oo == null) continue;
            dump(sb, path+'.'+field.getName(), oo, stack);
        }
        
    }

    private int arraySize(Class clazz, Object o) {
        Class cl = clazz.getComponentType();
        if(byte.class.equals(cl)) return ((byte[])o).length;
        if(char.class.equals(cl)) return ((char[])o).length;
        if(int.class.equals(cl)) return ((int[])o).length;
        if(short.class.equals(cl)) return ((short[])o).length;
        if(long.class.equals(cl)) return ((long[])o).length;
        if(float.class.equals(cl)) return ((float[])o).length;
        if(double.class.equals(cl)) return ((double[])o).length;
        if(boolean.class.equals(cl)) return ((boolean[])o).length;
        return ((Object[])o).length;
    }

    private Object arrayItem(Class clazz, Object o, int index) {
        return Array.get(o, index);
    }


    private List<Field> allFields(Class clazz) {
        List<Field> list = new ArrayList<Field>();
        allFields(clazz, list);
        return list;
    }
    private void allFields(Class clazz, List<Field> list) {
        if(clazz == null) return;
        Field[] fields = clazz.getDeclaredFields();
        if(fields == null) return;
        for(Field field : fields) {
            list.add(field);
        }
        allFields(clazz.getSuperclass(), list);
    }
    private Field field(Class clazz, String name) {
        if(clazz == null) return null;
        Field field = null;
        try {
            field = clazz.getDeclaredField(name);
        } catch(Exception e) {
        }
        if(field != null) return field;
        return field(clazz.getSuperclass(), name);
    }
    private Object valueOld(Class clazz, Object object, Field field) {
        field.setAccessible(true);
        Object o = null;
        try {
            o = field.get(object);
        } catch(Exception e) {
        }
        return o;
    }
    private Object value(Class clazz, Object object, Field field) {
        Object o = null;
        boolean ok = false;
        try {
            String name = field.getName();
            String accessor = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Method m = clazz.getMethod(accessor, new Class[]{});
            if(m != null) {
                o = m.invoke(object, new Object[]{});
                ok = true;
            }
        } catch(Exception e) {
        }
        if(ok) return o;
        try {
            String name = field.getName();
            String accessor = "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Method m = clazz.getMethod(accessor, new Class[]{});
            if(m != null) {
                o = m.invoke(object, new Object[]{});
                ok = true;
            }
        } catch(Exception e) {
        }
        return o;
    }

    private static class FieldCopmarator implements Comparator<Field> {

        @Override
        public int compare(Field o1, Field o2) {
            String name1 = null;
            String name2 = null;
            name1 = o1.getName();
            name2 = o2.getName();
            if(name1 == null) name1 = "";
            if(name2 == null) name2 = "";
            return name1.compareTo(name2);
        }

        public static FieldCopmarator instance() { return new FieldCopmarator(); }
    
    }


    private void jsonMessage(JsonString json, String name, String value) {
        if(name == null) json.value(value);
        else json.attr(name, value);
    }
    private void json(JsonString json, String name, Object o, List stack) {
        if(o == null) return;
		
		if(stack.contains(o)) {
            json.objectStart().objectEnd();
            return;
        }
		stack.add(o);

        Class clazz = o.getClass();
        
        if(isSimpleClass(clazz)) {
            String s = o.toString();
            jsonMessage(json, name, s);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(name != null) json.attrName(name);

        if(clazz.isArray()) {
            jsonArray(json, name, clazz, o, stack);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(Collection.class.isAssignableFrom(clazz)) {
            jsonCollection(json, name, clazz, o, stack);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(Map.class.isAssignableFrom(clazz)) {
            jsonMap(json, name, clazz, o, stack);
			stack.remove(stack.size()-1);
            return;
        }

        jsonObject(json, name, clazz, o, stack);
		stack.remove(stack.size()-1);
    }

    private void jsonArray(JsonString json, String name, Class clazz, Object o, List stack) {
        if(clazz.isArray()) {
            json.arrayStart();
            int size = arraySize(clazz, o);
            for(int i = 0; i < size; i++) {
                Object oo = arrayItem(clazz, o, i);
                json(json, null, oo, stack);
            }
            json.arrayEnd();
        }
    }
    
    private void jsonCollection(JsonString json, String name, Class clazz, Object o, List stack) {
        if(Collection.class.isAssignableFrom(clazz)) {
            Collection list = (Collection)o;
            int size = list.size();
            int i = 0;
            json.arrayStart();
            Iterator iter = list.iterator();
            while(iter.hasNext()) {
                Object oo = iter.next();
                json(json, null, oo, stack);
                i++;
            }
            json.arrayEnd();
        }
    }
    
    
    private void jsonMap(JsonString json, String name, Class clazz, Object o, List stack) {
        if(Map.class.isAssignableFrom(clazz)) {
            Map map = (Map)o;
            int size = map.size();
            json.objectStart();
            for(Object key : map.keySet()) {
                json(json, ""+key, map.get(key), stack);
            }
            json.objectEnd();
        }
    }
    private void jsonObject(JsonString json, String name, Class clazz, Object o, List stack) {
        List<Field> fields = allFields(clazz);
        if(fields == null) return;
        Collections.sort(fields, comp);
        json.objectStart();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) continue;
            Object oo = value(clazz, o, field);
            if(oo == null) continue;
            json(json, field.getName(), oo, stack);
        }
        json.objectEnd();
    }

//    public static void main(String[] argv) {
//        Person p = Messer.instance().map(Address.class, CompleteAddress.class).junk(Person.class);
//        System.out.println(" -- " + p);
//    }
}
