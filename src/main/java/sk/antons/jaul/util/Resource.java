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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sk.antons.jaul.Is;

/**
 * Helper class for resolving resources from classpath, filesystem of generic urls.
 * Reqource is identified by url property ans possible values are
 * &lt;ul&gt;
 * &lt;li&gt;'classpath:...' - in this case resource ... is loaded from classpath.
 * &lt;li&gt;'...' - in this case resource ... is loaded from file system (name of the resource not contains ':' character)
 * &lt;li&gt;otherwise is url taken as parameter for URL class and resolved in this way
 * &lt;/ul&gt;
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
	 * Instantiate Resource class.
	 * @param url - url for the new instance
	 * @return new instance
	 */
	public static Resource url(String url) {
		return new Resource(url);
	}

    /**
     * Returns input stream for given url.
     * @param cl - class loader used in case of url in format classpath:...
     * @return input stream of resource
     */
    public InputStream inputStream(ClassLoader cl) {
        try {
            if(url.startsWith("classpath:")) {
                return cl.getResourceAsStream(url.substring(10));
            } else if(url.indexOf(':') < 0) {
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
    public InputStream inputStream() {
        return inputStream(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Merges two urls. If childPath starts with '/' or contains ':' it is expected to be
     * absolute and it is returned directly. Otherwise combination of paths is returned.
     *
     * Input "/parent" + "/child" returns "/child" (child is absolute).
     * Input "/parent" + "child" returns "/parent/child".
     * Input "/parent" + "./child" returns "/parent/child".
     * Input "/parent" + "../child" returns "/child".
     *
     * Invalid path is returned for invalid combination. ("/parent" combination
     * with "../../../child" returns invalid path)
     * @param parentPath parent path - expected to be absolute
     * @param childPath possible relative child path (starts with ./ or ../)
     * @return combination of parent and child paths.
     */
    public static String mergeUrls(String parentPath, String childPath) {
        if(Is.empty(parentPath)) return childPath;
        if(Is.empty(childPath)) return parentPath;
        if(childPath.startsWith("/")) return childPath;
        if(childPath.contains(":")) return childPath;
        String path = parentPath + (parentPath.endsWith("/")?"":"/") + childPath;

        return normalizeUrl(path);
    }


    /**
     * Normalize relative parts in url. Ignores all parts before ':' char.
     * removes slash reperation "//". Removes "/./" substrings. tries to
     * resolve relative paths "/../";
     * @param url input url
     * @return normalized url
     */
    public static String normalizeUrl(String url) {
        if(Is.empty(url)) return url;
        String path = url;
        String prefix = "";
        boolean isprefix = false;
        int pos = path.lastIndexOf(":");
        if(pos > -1) {
            prefix = path.substring(0, pos);
            path = path.substring(pos+1);
            isprefix = true;
        }

        // split path to elements
        int len = path.length();
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(300);
        char c = 0;
        for(int i = 0; i < len; i++) {
            c = path.charAt(i);
            if(c == '/') {
                list.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        list.add(sb.toString());


        boolean something = true;
        while(something) {

            something = false;
            len = list.size();
            int lastindex = len - 1;
            for(int i = 0; i < len; i++) {
                String next = list.get(i);

                if("".equals(next) && (i > 0) && (i < lastindex)) {
                    something = true;
                    list.remove(i);
                    i--; len--;
                    continue;
                }

                if(".".equals(next) && (i > 0)) {
                    something = true;
                    list.remove(i);
                    i--; len--;
                    continue;
                }
                if("..".equals(next) && (i == 1)
                    && ("".equals(list.get(i-1)))
                    ) {
                    something = true;
                    list.remove(0);
                    i--; len--;
                    continue;
                }

                if("..".equals(next) && (i > 0)
                    && (!"".equals(list.get(i-1)))
                    && (!".".equals(list.get(i-1)))
                    && (!"..".equals(list.get(i-1)))
                    ) {
                    something = true;
                    list.remove(i);
                    list.remove(i-1);
                    i = i - 2;
                    len = len - 2;
                    continue;
                }

            }
        }

        // join path
        path = list.stream().collect(Collectors.joining("/"));

        return isprefix ? prefix + ":" + path : path;
    }

    /**
     * returns parent url of given url.
     * @param url input url
     * @return parent url if it is possible (root has no parent)
     */
    public static String parentUrl(String url) {
        if(Is.empty(url)) return url;
        String path = url;
        String prefix = "";
        boolean isprefix = false;
        int pos = path.lastIndexOf(":");
        if(pos > -1) {
            prefix = path.substring(0, pos);
            path = path.substring(pos+1);
            isprefix = true;
        }
        pos = path.lastIndexOf('/', path.length()-2);
        if(pos> -1) path = path.substring(0, pos);
        else path = "";
        return isprefix ? prefix + ":" + path : path;
    }
}
