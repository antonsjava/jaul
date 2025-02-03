/*
 *
 */
package sk.antons.jaul.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import sk.antons.jaul.Is;

/**
 *
 * @author antons
 */
public class HierarchicalProperties {
    Properties props;
    String pathDelimiter = ".";
    String delimiter = "..";


    public Properties props() { return props; }
    public HierarchicalProperties props(Properties value) { this.props = value; return this; }
    public String pathDelimiter() { return pathDelimiter; }
    public HierarchicalProperties pathDelimiter(String value) { this.pathDelimiter = value; return this; }
    public String delimiter() { return delimiter; }
    public HierarchicalProperties delimiter(String value) { this.delimiter = value; return this; }

    public HierarchicalProperties(Properties props) {
        this.props = props;
    }
    public static HierarchicalProperties instance(Properties props) { return new HierarchicalProperties(props); }

    public String property(String name, String defaultValue) {
        String value = property(name);
        return value == null ? defaultValue : value;
    }

    public String property(String name) {
        if(Is.empty(name)) return null;
        return hierarchicalProperty(path(name), name(name));
    }

    public String hierarchicalProperty(String path, String name, String defaultValue) {
        String value = hierarchicalProperty(path, name);
        return value == null ? defaultValue : value;
    }

    public String hierarchicalProperty(String path, String name) {
        if(Is.empty(name)) return null;
        if(Is.empty(path)) return props.getProperty(name);
        String value = props.getProperty(path + delimiter + name);
        if(value != null) return value;
        return hierarchicalProperty(parent(path), name);
    }

    private Map<String, String> parents = new HashMap<>();
    private Map<String, String> paths = new HashMap<>();
    private Map<String, String> properties = new HashMap<>();

    private String parent(String path) {
        if(Is.empty(path)) return path;
        String parent = parents.get(path);
        if(parent != null) return parent;
        int pos = path.lastIndexOf(pathDelimiter);
        parent = (pos < 0) ? "" : path.substring(0, pos);
        parents .put(path, parent);
        return parent;
    }

    private String path(String property) {
        if(Is.empty(property)) return property;
        String path = paths.get(property);
        if(path != null) return path;
        int pos = property.lastIndexOf(delimiter);
        path = (pos < 0) ? "" : property.substring(0, pos);
        paths .put(property, path);
        return path;
    }

    private String name(String property) {
        if(Is.empty(property)) return property;
        String name = properties.get(property);
        if(name != null) return name;
        int pos = property.lastIndexOf(delimiter);
        name = (pos < 0) ? property : property.substring(pos+delimiter.length());
        paths .put(property, name);
        return name;
    }


}
