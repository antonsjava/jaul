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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates diff log of two POJO instancies.
 * @author antons
 */
public class Differ {

    private boolean compareClasses = false;

    public static Differ instance() { return new Differ(); }
    public Changes diff(Object value1, Object value2) { 
        Changes changes = new Changes();
        diff(changes, "", value1, value2, new ArrayList());
        return changes;
    }

    
    /**
     * List of diff changes.
     */
    public static class Changes {
        private List<Change> changes = new ArrayList<Change>();
        public int size() { return changes.size(); }
        private void add(String path, String v1, String v2) { changes.add(Change.instance(path, v1, v2)); }
        
        /**
         * removes changes with exact path
         * @param path 
         */
        public void remove(String path) {
            Iterator<Change> iter = changes.iterator();
            while(iter.hasNext()) {
                Change next = iter.next();
                if(next.hasPath(path)) iter.remove();
            }
        }
        
        /**
         * removes changes with path patching given path
         * @param path 
         */
        public void removeMatched(String path) {
            Iterator<Change> iter = changes.iterator();
            while(iter.hasNext()) {
                Change next = iter.next();
                if(next.matchPath(path)) iter.remove();
            }
        }

        @Override
        public String toString() {
            if(changes.isEmpty()) return "Changes[]";
            StringBuilder sb = new StringBuilder();
            sb.append("Changes[\n");
            for(Change change : changes ) {
                sb.append(change).append('\n');
            }
            sb.append("]");
            return sb.toString();
        }
        
    }
    
    /**
     * Diff change
     */
    public static class Change {
        private String path;
        private String value1;
        private String value2;

        public static Change instance(String path, String v1, String v2) {
            Change c = new Change();
            c.path = path;
            c.value1 = v1;
            c.value2 = v2;
            return c;
        }

        /**
         * true is path is exact to parameter value
         */
        public boolean hasPath(String path) {
            if(path == null) return false;
            return path.equals(this.path);
        }
        
        /**
         * true is path is starts with parameter value
         */
        public boolean matchPath(String path) {
            if(path == null) return false;
            return this.path.startsWith(path);
        }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getValue1() { return value1; }
        public void setValue1(String value1) { this.value1 = value1; }
        public String getValue2() { return value2; }
        public void setValue2(String value2) { this.value2 = value2; }

        @Override public String toString() { return "change " + path + " -- '" + value1 + "' vs '" + value2 + "'"; }
        
    }

	/**
	 * Compare also classes.
	 */
    public Differ classes(boolean value) {
        this.compareClasses = value;
        return this;
    }

    private void diff(Changes changes, String path, Object o1, Object o2, List stack) {
        boolean block = nullCompare(changes, path, o1, o2);
        if(block) return;
		
		if(stack.contains(o1)) return;
		stack.add(o1);

        Class clazz1 = o1.getClass();
        Class clazz2 = o2.getClass();
        
        if(compareClasses) {
            if(clazz1 != clazz2) {
                changes.add(path, clazz1.toString(), clazz2.toString());
				stack.remove(stack.size()-1);
                return;
            } 
        }
        
        if(isSimpleClass(clazz1)) {
            String s1 = o1.toString();
            String s2 = o2.toString();
            if(!s1.equals(s2)) {
                changes.add(path, s1, s2);
            } 
			stack.remove(stack.size()-1);
            return;
        }
        
        if(clazz1.isArray() || clazz2.isArray()) {
            diffArray(changes, path, clazz1, o1, clazz2, o2, stack);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(List.class.isAssignableFrom(clazz1) || List.class.isAssignableFrom(clazz2)) {
            diffList(changes, path, clazz1, o1, clazz2, o2, stack);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(Set.class.isAssignableFrom(clazz1) || Set.class.isAssignableFrom(clazz2)) {
            diffSet(changes, path, clazz1, o1, clazz2, o2, stack);
			stack.remove(stack.size()-1);
            return;
        }
        
        if(Map.class.isAssignableFrom(clazz1) || Map.class.isAssignableFrom(clazz2)) {
            diffMap(changes, path, clazz1, o1, clazz2, o2, stack);
			stack.remove(stack.size()-1);
            return;
        }

        diffObject(changes, path, clazz1, o1, clazz2, o2, stack);
		stack.remove(stack.size()-1);
    }
    
    private boolean nullCompare(Changes changes, String path, Object o1, Object o2) {
        if((o1 == null) && (o2 == null)) return true;
        if(o1 == null) {
            changes.add(path, "NULL", "notNULL");
            return true;
        }
        if(o2 == null) {
            changes.add(path, "notNULL", "NULL");
            return true;
        }
        return false;
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
        if(clazz.equals(BigDecimal.class)) return true;
        if(clazz.equals(BigInteger.class)) return true;
        
        return false;
    }
    
    private void diffArray(Changes changes, String path, Class clazz1, Object o1, Class clazz2, Object o2, List stack) {
        if(clazz1.isArray() && clazz2.isArray()) {
            int size1 = arraySize(clazz1, o1);
            int size2 = arraySize(clazz2, o2);
            if(size1 != size2) {
                changes.add(path, "array.size:" + size1, "array.size:"+size2);
            } else {
                for(int i = 0; i < size1; i++) {
                    Object oo1 = arrayItem(clazz1, o1, i);
                    Object oo2 = arrayItem(clazz2, o2, i);
                    diff(changes, path+'/'+i, oo1, oo2, stack);
                }
            }
        } else {
            if(clazz1.isArray()) changes.add(path, "ARRAY", "notARRAY");
            if(clazz2.isArray()) changes.add(path, "notARRAY", "ARRAY");
        }
    }
    
    private void diffList(Changes changes, String path, Class clazz1, Object o1, Class clazz2, Object o2, List stack) {
        if(List.class.isAssignableFrom(clazz1) && List.class.isAssignableFrom(clazz2)) {
            List list1 = (List)o1;
            List list2 = (List)o2;
            int size1 = list1.size();
            int size2 = list2.size();
            if(size1 != size2) {
                changes.add(path, "list.size:" + size1, "list.size:"+size2);
            } else {
                for(int i = 0; i < size1; i++) {
                    Object oo1 = list1.get(i);
                    Object oo2 = list2.get(i);
                    diff(changes, path+'/'+i, oo1, oo2, stack);
                }
            }
        } else {
            if(List.class.isAssignableFrom(clazz1)) changes.add(path, "List", "notList");
            if(List.class.isAssignableFrom(clazz2)) changes.add(path, "notList", "List");
        }
    }
    
    private void diffSet(Changes changes, String path, Class clazz1, Object o1, Class clazz2, Object o2, List stack) {
        if(Set.class.isAssignableFrom(clazz1) && Set.class.isAssignableFrom(clazz2)) {
            Set list1 = (Set)o1;
            Set list2 = (Set)o2;
            int size1 = list1.size();
            int size2 = list2.size();
            if(size1 != size2) {
                changes.add(path, "set.size:" + size1, "set.size:"+size2);
            } else {
                List unpaired = new ArrayList(list2);
                int i = 0;
                int diffcount = 0;
                for(Object oo1 : list1) {
                    Object oo2 = null;
                    for(Object object : unpaired) {
                        if(object.equals(oo1)) {
                            oo2 = object;
                            break;
                        }
                    }
                    if(oo2 != null) {
                        diff(changes, path+'/'+i, oo1, oo2, stack);
                        unpaired.remove(oo2);
                    } else {
                        diffcount++;
                    }
                    i++;
                }
                if(diffcount > 0) changes.add(path, "set.contains."+diffcount+".more", "set.notcontains");
                if(unpaired.size() > 0) changes.add(path, "set.notcontains", "set.contains"+unpaired.size()+".more");
            }
        } else {
            if(Set.class.isAssignableFrom(clazz1)) changes.add(path, "Set", "notSet");
            if(Set.class.isAssignableFrom(clazz2)) changes.add(path, "notSet", "Set");
        }
    }
    
    private void diffMap(Changes changes, String path, Class clazz1, Object o1, Class clazz2, Object o2, List stack) {
        if(Map.class.isAssignableFrom(clazz1) && Map.class.isAssignableFrom(clazz2)) {
            Map list1 = (Map)o1;
            Map list2 = (Map)o2;
            int size1 = list1.size();
            int size2 = list2.size();
            if(size1 != size2) {
                changes.add(path, "map.size:" + size1, "map.size:"+size2);
            } else {
                List unpaired = new ArrayList(list2.keySet());
                int i = 0;
                int diffcount = 0;
                for(Object key1 : list1.keySet()) {
                    Object key2 = null;
                    for(Object object : unpaired) {
                        if(object.equals(key1)) {
                            key2 = object;
                            break;
                        }
                    }
                    if(key2 != null) {
                        diff(changes, path+"["+key2+"]", list1.get(key1), list2.get(key2), stack);
                        unpaired.remove(key2);
                    } else {
                        diffcount++;
                    }
                    i++;
                }
                if(diffcount > 0) changes.add(path, "map.contains."+diffcount+".more", "set.notcontains");
                if(unpaired.size() > 0) changes.add(path, "map.notcontains", "set.contains"+unpaired.size()+".more");
            }
        } else {
            if(Map.class.isAssignableFrom(clazz1)) changes.add(path, "Map", "notMap");
            if(Map.class.isAssignableFrom(clazz2)) changes.add(path, "notMap", "Map");
        }
    }

    private int arraySize(Class clazz, Object o) {
        return ((Object[])o).length;
    }

    private Object arrayItem(Class clazz, Object o, int index) {
        return Array.get(o, index);
    }

    private void diffObject(Changes changes, String path, Class clazz1, Object o1, Class clazz2, Object o2, List stack) {
        List<Field> fields = allFields(clazz1);
        if(fields == null) return;
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) continue;
            Field field2 = null;
            try {
                field2 = field(clazz2, field.getName());
            } catch(Exception e) {
            }
            if(field2 == null) {
                changes.add(path+'/' + field.getName(), "field.exists", "field.notexists");
                continue;
            }
            Object oo1 = value(clazz1, o1, field);
            Object oo2 = value(clazz2, o2, field2);
            diff(changes, path+'/'+field.getName(), oo1, oo2, stack);
            
        }
        
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

//    public static void main(String[] argv) {
//        Person p = Messer.instance().map(Address.class, CompleteAddress.class).junk(Person.class);
//        System.out.println(" -- " + p);
//    }
}
