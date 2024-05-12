/*
 *
 */
package sk.antons.jaul.xml;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.TextFile;

/**
 * Removes unused namespace declarations. Used for jaxb generated xmls.
 * @author antons
 */
public class UnusedNsRemover {

    public static void process(Document doc, String... ignoreNs) {
        if(doc == null) return;
        process(EW.elem(doc.getDocumentElement()), ignoreNs);
    }

    public static void process(EW ew, String... ignoreNs) {
        if(ew == null) return;

        List<String> attrnames = ew.attrNames();
        if(!Is.empty(attrnames)) {
            List<org.w3c.dom.Attr> okattrs = new ArrayList<>();
            for(String attrname : attrnames) {
                if(attrname.startsWith("xmlns:")) {
                    if(!match(ew.attr(attrname), ignoreNs)) {
                        String prefixwithdots = attrname.substring(6)+':';
                        boolean result = find(ew, true, attrname, prefixwithdots);
                        if(!result) ew.elem().removeAttribute(attrname);
                    }
                }
            }
        }

        List<EW> children = ew.children();
        for(EW ew1 : children) {
            process(ew1);
        }

    }

    private static boolean find(EW ew, boolean root, String fullname, String prefixwithdots) {
        if(ew == null) return false;

        List<String> attrnames = ew.attrNames();
        if(!Is.empty(attrnames)) {
            if(!root) {
                for(String attrname : attrnames) {
                    if(fullname.equals(attrname)) return false;
                }
            }
        }

        String name = ew.elem().getNodeName();
        if((name != null) && name.startsWith(prefixwithdots)) return true;

        if(!Is.empty(attrnames)) {
            for(String attrname : attrnames) {
                if(attrname.startsWith(prefixwithdots)) return true;
            }
        }

        List<EW> children = ew.children();
        for(EW ew1 : children) {
            boolean result = find(ew1, false, fullname, prefixwithdots);
            if(result) return true;
        }
        return false;
    }

    public static void process(Elem elem, String... ignoreNs) {
        if(elem == null) return;

        int asize = elem.attributeSize();
        if(asize>0) {
            for(int i = asize-1; i >=0; i--) {
                Elem.Attr attr = elem.attribute(i);
                if("xmlns".equals(attr.name().prefix())) {
                    if(!match(attr.value(), ignoreNs)) {
                        boolean result = find(elem, true, attr.name().prefixedName(), attr.name().name());
                        if(!result) attr.delete();
                    }
                }
            }
        }

        int size = elem.childrenSize();
        for(int i = size-1; i >= 0; i--) {
            process(elem.child(i));
        }

    }

    private static boolean find(Elem elem, boolean root, String fullname, String ns) {
        if(elem == null) return false;

        int asize = elem.attributeSize();
        if(asize>0) {
            if(!root) {
                for(int i = asize-1; i >=0; i--) {
                    Elem.Attr attr = elem.attribute(i);
                    if(fullname.equals(attr.name().prefixedName())) return false;
                }
            }
        }

        if(ns.equals(elem.name().prefix())) return true;

        if(asize>0) {
            for(int i = asize-1; i >=0; i--) {
                Elem.Attr attr = elem.attribute(i);
                if(ns.equals(attr.name().prefix())) return true;
            }
        }

        int size = elem.childrenSize();
        for(int i = size-1; i >= 0; i--) {
            boolean result = find(elem.child(i), false, fullname, ns);
            if(result) return true;
        }
        return false;
    }

    private static boolean match(String ns, String[] ignoreNs) {
        if(ignoreNs == null) return false;
        for(String ignoreN : ignoreNs) {
            if(ignoreN.equals(ns)) return true;
        }
        return false;
    }

    public static void main(String[] argv) throws Exception {
        Document doc = Xml.documentFromFile("/home/antons/Downloads/a.xml");
        UnusedNsRemover.process(doc);
        TextFile.save("/home/antons/Downloads/b.xml", "utf-8", Xml.documentToString(doc, "utf08", true, false));
        Elem elem = Elem.parse(new FileInputStream("/home/antons/Downloads/a.xml"));
        UnusedNsRemover.process(elem);
        TextFile.save("/home/antons/Downloads/c.xml", "utf-8", elem.export().indent("  ").toString());
    }
}
