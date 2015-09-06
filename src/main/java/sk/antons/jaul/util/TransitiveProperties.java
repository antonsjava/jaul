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

package sk.antons.jaul.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import sk.antons.jaul.Replace;

/**
 * Helper class for making java Properties transitive. You can use use ${prop name}
 * in property values. This placeholdes will be replaced to property name. 
 * <pre>
 * property1=value1
 * property2=value2 ${property1}
 * </pre>
 * Then property2 will have value 'value2 value1'
 * 
 * Implementation expects that all keys and values are strings. 
 * 
 * @author antons
 */
public class TransitiveProperties {
    
    /**
     * Replace ${} place holders in provided properties. It is useful for 
     * properties, which is not modified any more. So after properties 
     * initialization just call this method to fulfill transitive values. 
     * @param props properties to be modified for transitive values
     */
    public static void makeClosure(Properties props) {
        if(props == null) return;
        for(Map.Entry<Object, Object> entry : props.entrySet()) {
            String property = (String) entry.getKey();
            String value = (String) entry.getValue();
            String newvalue = makeClosureForProperty(props, property, new ArrayList<String>());
            props.put(property, newvalue);
        }
    
    }
    
    /**
     * Create instance of Properties, which encapsulate provided properties 
     * and dynamically compute transitive values. 
     * 
     * This implementation is slower than makeClosure() method, but allows to 
     * dynamically change values.
     * 
     * @param props - properties to be wrapped for transitive values
     * @return wrapped properties. 
     */
    public static Properties wrap(Properties props) {
        if(props == null) return null;
        return new TransitivePropertiesWrapper(props);
    }

    private static String makeClosureForProperty(Properties props, String propertyName, List<String> stack) {
        if(propertyName == null) return null;
        if(stack.contains(propertyName))
            throw new IllegalStateException("Loop detected on property " + propertyName + " " + stack);
        else stack.add(propertyName);
        String propertyValue = null;
        if(props instanceof TransitivePropertiesWrapper) {
            propertyValue = ((TransitivePropertiesWrapper)props).getRealProperty(propertyName);
        } else propertyValue = props.getProperty(propertyName);
        if(propertyValue == null) return null;
        String toBeReplaced = findPropertyToBeReplaced(propertyValue);
        while(toBeReplaced != null) {
            List<String> newStack = new ArrayList<String>(stack);
            String replaceValue = makeClosureForProperty(props, toBeReplaced, newStack);
            if(replaceValue != null) {
                propertyValue = Replace.all(propertyValue, "${" + toBeReplaced + "}", replaceValue);
            } else {
                propertyValue = Replace.all(propertyValue, "${" + toBeReplaced + "}", "$(" + toBeReplaced + ")");
            }
            toBeReplaced = findPropertyToBeReplaced(propertyValue);
        }
        return propertyValue;

    }

    private static String findPropertyToBeReplaced(String text) {
        if(text == null) return null;
        int pos = text.lastIndexOf("${");
        if(pos < 0) return null;
        int pos2 = text.indexOf("}", pos);
        if(pos2 < 0) return null;
        return text.substring(pos + 2, pos2);
    }
    
    private static class TransitivePropertiesWrapper extends Properties {

        public TransitivePropertiesWrapper(Properties props) {
            putAll(props);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            String value = getProperty(key);
            if(value == null) return defaultValue;
            return value;
        }

        @Override
        public String getProperty(String key) {
            String value = makeClosureForProperty(this, key, new ArrayList<String>());
            return value;
        }

        public String getRealProperty(String key) {
            return super.getProperty(key);
        }
        
    }

}
