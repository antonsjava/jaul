/*
 * 
 */
package sk.antons.jaul.pojo;

/**
 * Factory class for some utilities processing java POJO classes. 
 * (mostly data classes.)
 * @author antons
 */
public class Pojo {
   
    public static Messer messer() { return Messer.instance(); }
    public static Differ differ() { return Differ.instance(); }
    public static Dumper dumper() { return Dumper.instance(); }

}
