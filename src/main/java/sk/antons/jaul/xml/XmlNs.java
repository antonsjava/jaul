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

package sk.antons.jaul.xml;

import java.util.List;
import sk.antons.jaul.Is;

/**
 * Helper class form manipulating xml namespaces
 * 
 * @author antons
 */
public class XmlNs {

    public static void removeUnused(EW ew) {
        List<String> list = ew.attrNames();
        for(String attr : list) {
            if(attr.startsWith("xmlns:")) {
                String name = attr.substring(6);
                if(!Is.empty(name)) {
                    if(!isNsPrefixUsed(ew, name+":", attr, true)) {
                        ew.elem().removeAttribute(attr);
                    }
                }
            }
        }
        for(EW child : ew.children()) {
            removeUnused(child);
        }
    }

    private static boolean isNsPrefixUsed(EW ew, String prefix, String definition, boolean root) {
        List<String> list = ew.attrNames();
        if(!root) if(list.contains(definition)) return false;
        if(ew.elem().getNodeName().startsWith(prefix)) return true;
        for(String attr : list) {
            if(attr.startsWith(prefix)) return true;
        }

        for(EW child : ew.children()) {
            if(isNsPrefixUsed(child, prefix, definition, false)) return true;
        }
        return false;
    }

//    public static void main(String[] argv) {
//        String xml = TextFile.read("/home/antons/Downloads/stupid2.xml", "utf-8");
//        Document doc = Xml.document(xml);
//        XmlNs.removeUnused(EW.elem(doc.getDocumentElement()));
//        TextFile.save("/home/antons/Downloads/stupid3.xml", "utf-8", Xml.documentToString(doc, "utf-8", true, false));
//    }

}
