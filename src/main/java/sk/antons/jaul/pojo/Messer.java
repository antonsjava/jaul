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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javafx.scene.chart.PieChart;

/**
 * Generates dummy instances of data objects. It is usable for 
 * generating dummy examples for json documentation.
 * 
 * It recognize Arrays and List and Set attributes. Others expects 
 * public empty constructor
 * @author antons
 */
public class Messer {

    private Map<Class, Class> classmap = new HashMap<Class, Class>();

    public static Messer instance() {
        return new Messer();
    }

    public <T> T junk(Class<T> clazz) {
        return (T) instance(clazz, new ArrayList<Class>(), null);
    }

    public Messer map(Class from, Class to) {
        if(from == null) return this;
        if(to == null) return this;
        if(!(from.isAssignableFrom(to))) throw new IllegalArgumentException(from +" is not assignable from " + to);
        classmap.put(from, to);
        return this;
    }
    
    private static Class fromParameterizedTypeFirst(Type type) {
        Class cl = null;
        if(type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Type[] types = ptype.getActualTypeArguments();
            if(types != null) {
                if(types.length>0) {
                    cl = (Class)types[0];
                }
            }
        }
        return cl;
    }
    private static Class fromTypeFirst(Type type) {
        Class cl = null;
        if(type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Type[] types = ptype.getActualTypeArguments();
            if(types != null) {
                if(types.length>0) {
                    cl = (Class)types[0];
                }
            }
        } else {
            cl = (Class)type;
        }
        return cl;
    }
    private static Class fromTypeRaw(Type type) {
        Class cl = null;
        if(type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            cl = (Class)ptype.getRawType();
        } else {
            cl = (Class)type;
        }
        return cl;
    }
    
    private Class change(Class cl) {
        if(cl == null) return cl;
        if(classmap.containsKey(cl)) return classmap.get(cl);
        return cl;
    }
    
    private Object instance(Type tp, List<Class> path, String name) {
        Object rv = null;
        //Class clazz = fromTypeFirst(tp);
        Class clazz = fromTypeRaw(tp);
        clazz = change(clazz);
        if(clazz.isArray()) {
            Class cl = clazz.getComponentType();
			if(byte.class.equals(cl)) {
				rv = new byte[]{1};
			} else if(char.class.equals(cl)) {
				rv = new char[]{1};
			} else if(int.class.equals(cl)) {
				rv = new int[]{1};
			} else if(long.class.equals(cl)) {
				rv = new long[]{1};
			} else if(double.class.equals(cl)) {
				rv = new double[]{1.0};
			} else if(short.class.equals(cl)) {
				rv = new short[]{1};
			} else if(float.class.equals(cl)) {
				rv = new float[]{(float)1.0};
			} else if(short.class.equals(cl)) {
				rv = new short[]{1};
			} else if(boolean.class.equals(cl)) {
				rv = new boolean[]{true};
			} else {
            	Object[] arr = (Object[])Array.newInstance(cl, 1);
            	Array.set(arr, 0, instance(cl, path, name));
          		rv = arr;
			}
        } else if(clazz.isAssignableFrom(ArrayList.class)) {
            ArrayList al = new ArrayList();
            Class cl = fromParameterizedTypeFirst(tp); 
            if(cl != null) {
                Object o = instance(cl, path, name);
                al.add(o);
            }
            rv = al;
        } else if(clazz.isAssignableFrom(HashSet.class)) {
            HashSet hs = new HashSet();
            Class cl = fromParameterizedTypeFirst(tp); 
            if(cl != null) {
                Object o = instance(cl, path, name);
                hs.add(o);
            }
            rv = hs;
        } else {
            rv = singleInstance(clazz, path, name);
        }
        return rv;
    }

    private <T> T singleInstance(Class<T> clazz, List<Class> path, String name) {
        T rv = (T) simpleValue(clazz, name);
        if(rv != null) return rv;
        rv = emptyInstance(clazz);
        if(path.contains(clazz)) return rv;
        path.add(clazz);
        fill(rv, path);
        path.remove(path.size()-1);
        return rv;
    }
    
    private void fill(Object o, List<Class> path) {
        try {
            Class c = o.getClass();
            Method[] methods = c.getMethods();
            if(methods == null) return;
            for(Method method : methods) {
                String name = method.getName();
                if(name.length() < 4) continue;
                if(!name.startsWith("set")) continue;
                Type[] params = method.getGenericParameterTypes();
                if(params == null) continue;
                if(params.length != 1) continue;
                try {
                    Object pvalue = instance(params[0], path, name);
                    method.invoke(o, new Object[]{pvalue});
                } catch(Exception e) {
                }
                
            }
        } catch(Exception e) {
        }
    }
    
    private <T> T emptyInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch(Exception e) {
        }
        return null;
    }

    private static String formatName(String name) {
        if(name == null) return "";
        if(name.startsWith("set")) return name.substring(3);
        return name;
    }

    private Object simpleValue(Class clazz, String name) {
        if(clazz.equals(String.class)) return formatName(name);
        
        if(clazz.equals(int.class)) return 1;
        if(clazz.equals(long.class)) return 1;
        if(clazz.equals(byte.class)) return 1;
        if(clazz.equals(short.class)) return 1;
        if(clazz.equals(double.class)) return 1.0;
        if(clazz.equals(float.class)) return 1.0;
        if(clazz.equals(boolean.class)) return true;
        if(clazz.equals(char.class)) return 'c';
        
        if(clazz.equals(Integer.class)) return 1;
        if(clazz.equals(Long.class)) return 1;
        if(clazz.equals(Byte.class)) return 1;
        if(clazz.equals(Short.class)) return 1;
        if(clazz.equals(Double.class)) return 1.0;
        if(clazz.equals(Float.class)) return 1.0;
        if(clazz.equals(Boolean.class)) return true;
        if(clazz.equals(Character.class)) return 'c';
       
        if(clazz.isEnum()) return enumValue(clazz);
        
        if(clazz.equals(LocalDateTime.class)) return LocalDateTime.now();
        if(clazz.equals(LocalDate.class)) return LocalDate.now();
        if(clazz.equals(Date.class)) return new Date();
        if(clazz.equals(BigDecimal.class)) return BigDecimal.ONE;
        if(clazz.equals(BigInteger.class)) return BigInteger.ONE;
        
        return null;
    } 

    private <T> T enumValue(Class<T> clazz) {
        try {
            Method m = clazz.getDeclaredMethod("values", new Class[]{});
            if(m == null) return null;
            Object[] values = (Object[])m.invoke(clazz, new Object[]{});
            if(values == null) return null;
            if(values.length < 1) return null;
            return (T) values[0];
        } catch(Exception e) {
            return null;
        }
    }
    

//    public static void main(String[] argv) {
//        Person p = Messer.instance().map(Address.class, CompleteAddress.class).junk(Person.class);
//        System.out.println(" -- " + p);
//    }
}
