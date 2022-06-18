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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sk.antons.jaul.Get;

/**
 * Generates java code for copying one instance to other instance.
 * It is designed for classes which has public get/set methods for accessing 
 * fields. (It is possible to use them also for xsd generated classes - no 
 * setters for list fields)
 * 
 * @author antons
 */
public class CopyGen {

    private boolean jaxbStyle = false;

    public static CopyGen instance() { return new CopyGen(); }
	
    /**
	 * Compare classes as generated for jaxb. (lists has no setters)
	 */
    public CopyGen jaxbStyle(boolean value) {
        this.jaxbStyle = value;
        return this;
    }

    public String generate(Class value1, Class value2) { 
        StringBuilder sb = new StringBuilder();
        sb.append("package xxxx;\n");
        sb.append("\n");
        sb.append("public class Copy {\n");
        List<ClassPair> stack = new ArrayList<ClassPair>();
        stack.add(ClassPair.instance(value1, value2));
        while(!stack.isEmpty()) {
            ClassPair pair = stack.remove(0);
            generate(sb, pair.c1, pair.c2, ClassPairDb.instance(), stack);
        }
        sb.append("}\n");
        return sb.toString();
    }


    private void generate(StringBuilder sb, Class c1, Class c2, ClassPairDb db, List<ClassPair> stack) {
        db.add(c1, c2);
        sb.append("    public static ").append(c1.getName()).append(" copy(").append(c2.getName()).append(" value) {\n");
        sb.append("        if(value == null) return null;\n");
        sb.append("        ").append(c1.getName()).append(" rv = new ").append(c1.getName()).append("();\n");
        ClassInfo ci1 = ClassInfo.instance(c1);
        ClassInfo ci2 = ClassInfo.instance(c2);
        for(String field : ci1.fields) {
            Method m1 = ci1.setters.get(field);
            if(m1 == null) {
                sb.append("        // "+field+" no setter in target\n");
                continue;
            }
            Method m2 = ci2.getters.get(field);
            if(m2 == null) {
                sb.append("        //rv.set").append(field).append("(value.get").append(field).append("()); no getter in source\n");
                continue;
            }
            Class clazz1 = Get.first(m1.getParameterTypes());
            Class clazz2 = m2.getReturnType();
            if(clazz1.isAssignableFrom(clazz2)) {
                sb.append("        rv.set").append(field).append("(value.get").append(field).append("());\n");
            } else {
                sb.append("        rv.set").append(field).append("(copy(value.get").append(field).append("()));\n");
                if(!db.match(clazz1, clazz2)) stack.add(ClassPair.instance(clazz1, clazz2));
            }

        }
        
        for(String field : ci2.getters.keySet()) {
            Method m1 = ci1.setters.get(field);
            if(m1 == null) {
                sb.append("        // "+field+" no setter in target\n");
            }
        }


        

        sb.append("        return rv;\n");
        sb.append("    }\n");
    }
    

    private static class ClassPair {
        
        Class c1;
        Class c2;
        
        public ClassPair(Class c1, Class c2) {
            this.c1 = c1;
            this.c2 = c2;
        }
        public static ClassPair instance(Class c1, Class c2) { return new ClassPair(c1, c2); }

        public boolean match(Class c1, Class c2) { return this.c1.isAssignableFrom(c1) && this.c2.isAssignableFrom(c2); }
        
    }

    private static class ClassPairDb {
        
        List<ClassPair> db = new ArrayList<ClassPair>();
        
        public ClassPairDb() {}
        public static ClassPairDb instance() { return new ClassPairDb(); }

        public void add(Class c1, Class c2) { db.add(ClassPair.instance(c1, c2)); }
        public boolean match(Class c1, Class c2) { 
            for(ClassPair cp : db) { if(cp.match(c1, c2)) return true; }
            return false;
        }
        
    }
    
    private static class ClassInfo {
        Map<String, Method> getters = new HashMap<String, Method>();
        Map<String, Method> lgetters = new HashMap<String, Method>();
        Map<String, Method> setters = new HashMap<String, Method>();
        List<String> fields = new ArrayList<String>();
        List<String> lfields = new ArrayList<String>();
        
        public ClassInfo(Class c) {
            try {
                Method[] methods = c.getMethods();
                if(methods != null) {
                    for(Method method : methods) {
                        String name = method.getName();
                        if("getClass".equals(name)) continue;
                        String field = null;
                        if(name.startsWith("get")) {
                            if(method.getParameterCount() == 0) {
                                field = name.substring(3);
                                getters.put(field, method);
                                Class cl = method.getReturnType();
                                if(cl != null) {
                                    if(cl.isAssignableFrom(List.class)) { if(!lfields.contains(field)) lfields.add(field); }
                                }
                                
                            }
                        } else if(name.startsWith("is")) {
                            if(method.getParameterCount() == 0) {
                                field = name.substring(2);
                                getters.put(field, method);
                            }
                        } else if(name.startsWith("set")) {
                            if(method.getParameterCount() == 1) {
                                field = name.substring(3);
                                setters.put(field, method);
                                if(!fields.contains(field)) fields.add(field);
                            }
                        }
                    }
                }
            } catch(Exception e) {
            }
            Collections.sort(fields);
        }
        public static ClassInfo instance(Class c) { return new ClassInfo(c); }

        
    }

    public static class Person {
        private String name;
        private int age;
        private Address addr;
        private Address niecovperson;

        public Address getNiecovperson() { return niecovperson; }
        public void setNiecovperson(Address niecovperson) { this.niecovperson = niecovperson; }
        public List<String> getTitles() { return null; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public Address getAddr() { return addr; }
        public void setAddr(Address addr) { this.addr = addr; }
    }
    public static class Employee extends Person {
        private int sallary;
        private Employer employer;

        public int getSallary() { return sallary; }
        public void setSallary(int sallary) { this.sallary = sallary; }

        public Employer getEmployer() { return employer; }
        public void setEmployer(Employer employer) { this.employer = employer; }
        
    }
    public static class Address {
        private String street;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
    }
    public static class Person2 {
        private String name;
        private int age;
        private Address2 addr;
        private int niecovperson2;

        public int getNiecovperson2() { return niecovperson2; }
        public void setNiecovperson2(int niecovperson2) { this.niecovperson2 = niecovperson2; }
        public List<String> getTitles() { return null; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public Address2 getAddr() { return addr; }
        public void setAddr(Address2 addr) { this.addr = addr; }
    }

    public static class Employee2 extends Person2 {
        private int sallary;
        private Employer employer;

        public int getSallary() { return sallary; }
        public void setSallary(int sallary) { this.sallary = sallary; }

        public Employer getEmployer() { return employer; }
        public void setEmployer(Employer employer) { this.employer = employer; }
        
    }
    public static class Address2 {
        private String street;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
    }
    
    public static class Employer {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static void main(String[] argv) {
        CopyGen gen = new CopyGen();
        System.out.println(gen.generate(Employee2.class, Employee.class));
    }
}
