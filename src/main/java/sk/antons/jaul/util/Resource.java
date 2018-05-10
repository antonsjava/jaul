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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Helper class for resolving resources from classpath, filesystem of generic urls.
 * Reqource is identified by url property ans possible values are 
 * <ul>
 * <li>'classpath:...' - in this case resource ... is loaded from classpath</li>
 * <li>'...' - in this case resource ... is loaded from file system (name of the resource not contains ':' character)</li>
 * <li>otherwise is url taken as parameter for URL class and resolved in this way</li>
 * </ul>
 * @author antons
 */
public class Resource {
    private String url;

    /**
     * Creates new instace for given url;
     * @param url 
     */
    public Resource(String url) {
        this.url = url;
    }

    /**
     * Returns input stream for given url.
     * @param cl - class loader used in case of url in format classpath:...
     * @return input stream of resource 
     */
    public InputStream inputStreeam(ClassLoader cl) {
        try {
            if(url.startsWith("classpath:")) {
                return cl.getResourceAsStream(url.substring(10));
            } else if(url.indexOf(':') > -1) {
                File f = new File(url);
                return new FileInputStream(f);
            } else {
                URL u = new URL(url);
                return u.openStream();
            }
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to obtain input stream from '"+url+"'", e);
        }
    }
    
    /**
     * Returns input stream for given url. (for classpath:... urls Context classloader is used);
     * @return input stream of resource 
     */
    public InputStream inputStreeam() {
        return inputStreeam(Thread.currentThread().getContextClassLoader());
    }
}
