/*
 *
 */
package sk.antons.jaul.xml;

import java.io.FileInputStream;
import java.util.List;
import org.w3c.dom.Document;
import sk.antons.jaul.Is;
import sk.antons.jaul.util.TextFile;

/**
 * Removes elements with xsi:nil="true" attribute. Is doesn't check namespace
 * of attribute. It simple expects that prefix is xsi. Used for jaxb generated xmls
 * which use xsi as prefix.
 * @author antons
 */
public class NilRemover {

    public static void process(Document doc) {
        if(doc == null) return;
        process(EW.elem(doc.getDocumentElement()));
    }

    public static void process(EW ew) {
        if(ew == null) return;
        List<EW> children = ew.children();
        String nil = ew.attr("xsi:nil");
        if("true".equals(nil)) {
            if(Is.empty(children)) {
                ew.elem().getParentNode().removeChild(ew.elem());
                return;
            }
        }
        for(EW ew1 : children) {
            process(ew1);
        }

    }

    public static void process(Elem elem) {
        if(elem == null) return;
        int size = elem.childrenSize();
        String nil = elem.attr("xsi:nil");
        if("true".equals(nil)) {
            if(size == 0) {
                elem.delete();
                return;
            }
        }
        for(int i = size-1; i >= 0; i--) {
            process(elem.child(i));
        }

    }

    public static void main(String[] argv) throws Exception {
        Document doc = Xml.documentFromFile("/home/antons/Downloads/a.xml");
        NilRemover.process(doc);
        TextFile.save("/home/antons/Downloads/b.xml", "utf-8", Xml.documentToString(doc, "utf08", true, false));
        Elem elem = Elem.parse(new FileInputStream("/home/antons/Downloads/a.xml"));
        NilRemover.process(elem);
        TextFile.save("/home/antons/Downloads/c.xml", "utf-8", elem.export().indent("  ").toString());
    }
}
