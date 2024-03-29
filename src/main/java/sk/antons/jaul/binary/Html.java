/*
 *
 */
package sk.antons.jaul.binary;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple html escaper utility.
 *
 * @author antons
 */
public class Html {

    /**
     * Only characters &apos;&lt;&apos;, &apos;&gt;&apos;, &apos;&quot;&apos;, &apos;\&apos;&apos;, &apos;&amp;&apos; are escaped to their names.
     * @param value text to be escaped
     * @return escaped text
     */
    public static String escapeSimple(String value) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder(value.length()*2);
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch(c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case '&': sb.append("&amp;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Characters &apos;&lt;&apos;, &apos;&gt;&apos;, &apos;&quot;&apos;, &apos;\&apos;&apos;, &apos;&amp;&apos; are escaped to their names.
     * And non ascii characters are escaped to generale escape form (&amp;#NNNN;)
     * @param value text to be escaped
     * @return escaped text
     */
    public static String escapeSimpleAndNonAscii(String value) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder(value.length()*2);
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch(c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case '&': sb.append("&amp;"); break;
                default:
                    if(c < 127) sb.append(c);
                    else sb.append("&#").append((int)c).append(';');
            }
        }
        return sb.toString();
    }

    /**
     * Characters &apos;&lt;&apos;, &apos;&gt;&apos;, &apos;&quot;&apos;, &apos;\&apos;&apos;, &apos;&amp;&apos; are escaped to their names.
     * And non ascii characters are escaped to generale escape form (&amp;#NNNN;)
     * @param value text to be escaped
     * @return escaped text
     */
    public static String escapeSimpleAndNonAsciiAndNonPrintable(String value) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder(value.length()*2);
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch(c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case '&': sb.append("&amp;"); break;
                default:
                    if((c < 127) && (c > 31)) sb.append(c);
                    else sb.append("&#").append((int)c).append(';');
            }
        }
        return sb.toString();
    }

    /**
     * All known named characters are escaped to their names.
     * @param value text to be escaped
     * @return escaped text
     */
    public static String escapeNames(String value) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder(value.length()*2);
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch(c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case '&': sb.append("&amp;"); break;
                default:
                    String name = names().get(c);
                    if(name == null) sb.append(c);
                    else sb.append(name);
            }
        }
        return sb.toString();
    }

    /**
     * All known named characters are escaped to their names.
     * And other non ascii characters are escaped to generale escape form (&amp;#NNNN;)
     * @param value text to be escaped
     * @return escaped text
     */
    public static String escapeNamesAndNonAscii(String value) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder(value.length()*2);
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch(c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case '&': sb.append("&amp;"); break;
                default:
                    String name = names().get(c);
                    if(name != null) sb.append(name);
                    else if(c < 127) sb.append(c);
                    else sb.append("&#").append((int)c).append(';');
            }
        }
        return sb.toString();
    }

    /**
     * All known named characters are escaped to their names.
     * And other non ascii characters are escaped to generale escape form (&amp;#NNNN;)
     * @param value text to be escaped
     * @return escaped text
     */
    public static String escapeNamesAndNonAsciiAndNonPrintable(String value) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder(value.length()*2);
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            switch(c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                case '&': sb.append("&amp;"); break;
                default:
                    String name = names().get(c);
                    if(name != null) sb.append(name);
                    else if((c < 127) && (c > 31)) sb.append(c);
                    else sb.append("&#").append((int)c).append(';');
            }
        }
        return sb.toString();
    }

    /**
     * All numeric codes (&amp;#NNNN; and &amp;#xHHHH;) and mamed entities (&amp;xxxx;) are converted to
     * represented chars.
     * @param value escaped text
     * @return unescaped text
     */
    public static String unescape(String value) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder(value.length());
        int len = value.length();
        for(int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if(c == '&') {
                char nc = charAt(value, len, i+1);
                if(nc == '#') {
                    char nnc = charAt(value, len, i+2);
                    if(nnc == 'x') {
                        int numlen = hexLen(value, len, i+3);
                        if(numlen > 0) {
                            int num = hexNum(value, len, i+3, numlen);
                            sb.append((char)num);
                            i = i + numlen + 3;
                        } else {
                            sb.append(c);
                        }
                    } else {
                        int numlen = decimalLen(value, len, i+2);
                        if(numlen > 0) {
                            int num = decimalNum(value, len, i+2, numlen);
                            sb.append((char)num);
                            i = i + numlen + 2;
                        } else {
                            sb.append(c);
                        }
                    }
                } else {
                    Node node = root().find(value, len, i);
                    if(node == null) {
                        sb.append(c);
                    } else {
                        sb.append(node.character);
                        i = i + node.name.length() - 1;
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static char charAt(String value, int len, int index) {
        if(index < len) {
            return value.charAt(index);
        } else {
            return 0;
        }
    }

    private static int decimalLen(String value, int len, int index) {
        for(int i = 0; i < 5; i++) {
            char c = charAt(value, len, index + i);
            if(c == ';') return i;
            if((c < '0') || (c > '9')) break;
        }
        return -1;
    }

    private static int decimalNum(String value, int len, int index, int numlen) {
        int num = 0;
        for(int i = 0; i < numlen; i++) {
            char c = charAt(value, len, index + i);
            num = num*10 + c - '0';
        }
        return num;
    }

    private static int hexLen(String value, int len, int index) {
        for(int i = 0; i < 5; i++) {
            char c = charAt(value, len, index + i);
            if(c == ';') return i;
            if(!(
                ((c >= '0') && (c <= '9'))
                ||
                ((c >= 'a') && (c <= 'f'))
                ||
                ((c >= 'A') && (c <= 'F'))
                )) break;
        }
        return -1;
    }

    private static int hexNum(String value, int len, int index, int numlen) {
        int num = 0;
        for(int i = 0; i < numlen; i++) {
            char c = charAt(value, len, index + i);
            if((c >= '0') && (c <= '9')) {
                num = num*16 + c - '0';
            } else if((c >= 'a') && (c <= 'f')) {
                num = num*16 + c - 'a';
            } else {
                num = num*16 + c - 'A';
            }
        }
        return num;
    }

//    private static class Node {
//        String name;
//        char character;
//        Map<Character, Node> children = new HashMap<Character, Node>();
//        void add(String name, char character, int index) {
//            if(name.length() == index) {
//                this.name = name;
//                this.character = character;
//            } else {
//                char c = name.charAt(index);
//                Node n = children.get(c);
//                if(n == null) {
//                    n = new Node();
//                    children.put(c, n);
//                }
//                n.add(name, character, index+1);
//            }
//        }
//
//        Node find(String value, int len, int index) {
//            char c = charAt(value, len, index);
//            Node n = children.get(c);
//            if(n == null) return null;
//            if(c == ';') return n;
//            return n.find(value, len, index+1);
//        }
//    }

    private static class Node {
        String name;
        char character;
        Node[] children = new Node[2 + 10 + 26 + 26];
        private int index(char c) {
            if(c == '&') return 0;
            else if(c == ';') return 1;
            else if(('0'<= c) && (c <= '9')) return c - '0' + 2;
            else if(('a'<= c) && (c <= 'z')) return c - 'a' + 10 + 2;
            else if(('A'<= c) && (c <= 'Z')) return c - 'A' + 26 + 10 + 2;
            else return -1;
        }

        void add(String name, char character, int index) {
            if(name.length() == index) {
                this.name = name;
                this.character = character;
            } else {
                char c = name.charAt(index);
                Node n = children[index(c)];
                if(n == null) {
                    n = new Node();
                    children[index(c)] = n;
                }
                n.add(name, character, index+1);
            }
        }

        Node find(String value, int len, int index) {
            char c = charAt(value, len, index);
            int i = index(c);
            if(i < 0) return null;
            Node n = children[i];
            if(n == null) return null;
            if(c == ';') return n;
            return n.find(value, len, index+1);
        }
    }

    private static Node root = null;
    private static Node root() {
        if(root == null) {
            init();
        }
        return root;
    }
    private static Map<Character, String> names = null;
    private static Map<Character, String> names() {
        if(names == null) {
            init();
        }
        return names;
    }

    private static void addName(String name, char c, Node root, Map<Character, String> names) {
        root.add(name, c, 0);
        names.put(c, name);
    }

    private static void init() {
        Node node = new Node();
        Map<Character, String> map = new HashMap<Character, String>();
		addName("&amp;", '&', node, map);
		addName("&apos;", '\'', node, map);
		addName("&quot;", '"', node, map);
		addName("&lt;", '<', node, map);
		addName("&gt;", '>', node, map);

		addName("&Aacute;", '\u00C1', node, map);
		addName("&aacute;", '\u00E1', node, map);
		addName("&Acirc;", '\u00C2', node, map);
		addName("&acirc;", '\u00E2', node, map);
		addName("&acute;", '\u00B4', node, map);
		addName("&AElig;", '\u00C6', node, map);
		addName("&aelig;", '\u00E6', node, map);
		addName("&Agrave;", '\u00C0', node, map);
		addName("&agrave;", '\u00E0', node, map);
		addName("&alefsym;", '\u2135', node, map);
		addName("&Alpha;", '\u0391', node, map);
		addName("&alpha;", '\u03B1', node, map);
		addName("&and;", '\u2227', node, map);
		addName("&ang;", '\u2220', node, map);
		addName("&Aring;", '\u00C5', node, map);
		addName("&aring;", '\u00E5', node, map);
		addName("&asymp;", '\u2248', node, map);
		addName("&Atilde;", '\u00C3', node, map);
		addName("&atilde;", '\u00E3', node, map);
		addName("&Auml;", '\u00C4', node, map);
		addName("&auml;", '\u00E4', node, map);
		addName("&bdquo;", '\u201E', node, map);
		addName("&Beta;", '\u0392', node, map);
		addName("&beta;", '\u03B2', node, map);
		addName("&brvbar;", '\u00A6', node, map);
		addName("&bull;", '\u2022', node, map);
		addName("&cap;", '\u2229', node, map);
		addName("&Ccedil;", '\u00C7', node, map);
		addName("&ccedil;", '\u00E7', node, map);
		addName("&cedil;", '\u00B8', node, map);
		addName("&cent;", '\u00A2', node, map);
		addName("&Chi;", '\u03A7', node, map);
		addName("&chi;", '\u03C7', node, map);
		addName("&circ;", '\u02C6', node, map);
		addName("&clubs;", '\u2663', node, map);
		addName("&cong;", '\u2245', node, map);
		addName("&copy;", '\u00A9', node, map);
		addName("&crarr;", '\u21B5', node, map);
		addName("&cup;", '\u222A', node, map);
		addName("&curren;", '\u00A4', node, map);
		addName("&dagger;", '\u2020', node, map);
		addName("&Dagger;", '\u2021', node, map);
		addName("&darr;", '\u2193', node, map);
		addName("&dArr;", '\u21D3', node, map);
		addName("&deg;", '\u00B0', node, map);
		addName("&Delta;", '\u0394', node, map);
		addName("&delta;", '\u03B4', node, map);
		addName("&diams;", '\u2666', node, map);
		addName("&divide;", '\u00F7', node, map);
		addName("&Eacute;", '\u00C9', node, map);
		addName("&eacute;", '\u00E9', node, map);
		addName("&Ecirc;", '\u00CA', node, map);
		addName("&ecirc;", '\u00EA', node, map);
		addName("&Egrave;", '\u00C8', node, map);
		addName("&egrave;", '\u00E8', node, map);
		addName("&empty;", '\u2205', node, map);
		addName("&emsp;", '\u2003', node, map);
		addName("&ensp;", '\u2002', node, map);
		addName("&Epsilon;", '\u0395', node, map);
		addName("&epsilon;", '\u03B5', node, map);
		addName("&equiv;", '\u2261', node, map);
		addName("&Eta;", '\u0397', node, map);
		addName("&eta;", '\u03B7', node, map);
		addName("&ETH;", '\u00D0', node, map);
		addName("&eth;", '\u00F0', node, map);
		addName("&Euml;", '\u00CB', node, map);
		addName("&euml;", '\u00EB', node, map);
		addName("&euro;", '\u20AC', node, map);
		addName("&exist;", '\u2203', node, map);
		addName("&fnof;", '\u0192', node, map);
		addName("&forall;", '\u2200', node, map);
		addName("&frac12;", '\u00BD', node, map);
		addName("&frac14;", '\u00BC', node, map);
		addName("&frac34;", '\u00BE', node, map);
		addName("&frasl;", '\u2044', node, map);
		addName("&Gamma;", '\u0393', node, map);
		addName("&gamma;", '\u03B3', node, map);
		addName("&ge;", '\u2265', node, map);
		addName("&harr;", '\u2194', node, map);
		addName("&hArr;", '\u21D4', node, map);
		addName("&hearts;", '\u2665', node, map);
		addName("&hellip;", '\u2026', node, map);
		addName("&Iacute;", '\u00CD', node, map);
		addName("&iacute;", '\u00ED', node, map);
		addName("&Icirc;", '\u00CE', node, map);
		addName("&icirc;", '\u00EE', node, map);
		addName("&iexcl;", '\u00A1', node, map);
		addName("&Igrave;", '\u00CC', node, map);
		addName("&igrave;", '\u00EC', node, map);
		addName("&image;", '\u2111', node, map);
		addName("&infin;", '\u221E', node, map);
		addName("&int;", '\u222B', node, map);
		addName("&Iota;", '\u0399', node, map);
		addName("&iota;", '\u03B9', node, map);
		addName("&iquest;", '\u00BF', node, map);
		addName("&isin;", '\u2208', node, map);
		addName("&Iuml;", '\u00CF', node, map);
		addName("&iuml;", '\u00EF', node, map);
		addName("&Kappa;", '\u039A', node, map);
		addName("&kappa;", '\u03BA', node, map);
		addName("&Lambda;", '\u039B', node, map);
		addName("&lambda;", '\u03BB', node, map);
		addName("&lang;", '\u2329', node, map);
		addName("&laquo;", '\u00AB', node, map);
		addName("&larr;", '\u2190', node, map);
		addName("&lArr;", '\u21D0', node, map);
		addName("&lceil;", '\u2308', node, map);
		addName("&ldquo;", '\u201C', node, map);
		addName("&le;", '\u2264', node, map);
		addName("&lfloor;", '\u230A', node, map);
		addName("&lowast;", '\u2217', node, map);
		addName("&loz;", '\u25CA', node, map);
		addName("&lrm;", '\u200E', node, map);
		addName("&lsaquo;", '\u2039', node, map);
		addName("&lsquo;", '\u2018', node, map);
		addName("&macr;", '\u00AF', node, map);
		addName("&mdash;", '\u2014', node, map);
		addName("&micro;", '\u00B5', node, map);
		addName("&middot;", '\u00B7', node, map);
		addName("&minus;", '\u2212', node, map);
		addName("&Mu;", '\u039C', node, map);
		addName("&mu;", '\u03BC', node, map);
		addName("&nabla;", '\u2207', node, map);
		addName("&nbsp;", '\u00A0', node, map);
		addName("&ndash;", '\u2013', node, map);
		addName("&ne;", '\u2260', node, map);
		addName("&ni;", '\u220B', node, map);
		addName("&notin;", '\u2209', node, map);
		addName("&not;", '\u00AC', node, map);
		addName("&nsub;", '\u2284', node, map);
		addName("&Ntilde;", '\u00D1', node, map);
		addName("&ntilde;", '\u00F1', node, map);
		addName("&Nu;", '\u039D', node, map);
		addName("&nu;", '\u03BD', node, map);
		addName("&Oacute;", '\u00D3', node, map);
		addName("&oacute;", '\u00F3', node, map);
		addName("&Ocirc;", '\u00D4', node, map);
		addName("&ocirc;", '\u00F4', node, map);
		addName("&OElig;", '\u0152', node, map);
		addName("&oelig;", '\u0153', node, map);
		addName("&Ograve;", '\u00D2', node, map);
		addName("&ograve;", '\u00F2', node, map);
		addName("&oline;", '\u203E', node, map);
		addName("&Omega;", '\u03A9', node, map);
		addName("&omega;", '\u03C9', node, map);
		addName("&Omicron;", '\u039F', node, map);
		addName("&omicron;", '\u03BF', node, map);
		addName("&oplus;", '\u2295', node, map);
		addName("&ordf;", '\u00AA', node, map);
		addName("&ordm;", '\u00BA', node, map);
		addName("&or;", '\u2228', node, map);
		addName("&Oslash;", '\u00D8', node, map);
		addName("&oslash;", '\u00F8', node, map);
		addName("&Otilde;", '\u00D5', node, map);
		addName("&otilde;", '\u00F5', node, map);
		addName("&otimes;", '\u2297', node, map);
		addName("&Ouml;", '\u00D6', node, map);
		addName("&ouml;", '\u00F6', node, map);
		addName("&para;", '\u00B6', node, map);
		addName("&part;", '\u2202', node, map);
		addName("&permil;", '\u2030', node, map);
		addName("&perp;", '\u22A5', node, map);
		addName("&Phi;", '\u03A6', node, map);
		addName("&phi;", '\u03C6', node, map);
		addName("&Pi;", '\u03A0', node, map);
		addName("&pi;", '\u03C0', node, map);
		addName("&piv;", '\u03D6', node, map);
		addName("&plusmn;", '\u00B1', node, map);
		addName("&pound;", '\u00A3', node, map);
		addName("&prime;", '\u2032', node, map);
		addName("&Prime;", '\u2033', node, map);
		addName("&prod;", '\u220F', node, map);
		addName("&prop;", '\u221D', node, map);
		addName("&Psi;", '\u03A8', node, map);
		addName("&psi;", '\u03C8', node, map);
		addName("&radic;", '\u221A', node, map);
		addName("&rang;", '\u232A', node, map);
		addName("&raquo;", '\u00BB', node, map);
		addName("&rarr;", '\u2192', node, map);
		addName("&rArr;", '\u21D2', node, map);
		addName("&rceil;", '\u2309', node, map);
		addName("&rdquo;", '\u201D', node, map);
		addName("&real;", '\u211C', node, map);
		addName("&reg;", '\u00AE', node, map);
		addName("&rfloor;", '\u230B', node, map);
		addName("&Rho;", '\u03A1', node, map);
		addName("&rho;", '\u03C1', node, map);
		addName("&rlm;", '\u200F', node, map);
		addName("&rsaquo;", '\u203A', node, map);
		addName("&rsquo;", '\u2019', node, map);
		addName("&sbquo;", '\u201A', node, map);
		addName("&Scaron;", '\u0160', node, map);
		addName("&scaron;", '\u0161', node, map);
		addName("&sdot;", '\u22C5', node, map);
		addName("&sect;", '\u00A7', node, map);
		addName("&shy;", '\u00AD', node, map);
		addName("&sigmaf;", '\u03C2', node, map);
		addName("&Sigma;", '\u03A3', node, map);
		addName("&sigma;", '\u03C3', node, map);
		addName("&sim;", '\u223C', node, map);
		addName("&spades;", '\u2660', node, map);
		addName("&sube;", '\u2286', node, map);
		addName("&sub;", '\u2282', node, map);
		addName("&sum;", '\u2211', node, map);
		addName("&sup1;", '\u00B9', node, map);
		addName("&sup2;", '\u00B2', node, map);
		addName("&sup3;", '\u00B3', node, map);
		addName("&supe;", '\u2287', node, map);
		addName("&sup;", '\u2283', node, map);
		addName("&szlig;", '\u00DF', node, map);
		addName("&Tau;", '\u03A4', node, map);
		addName("&tau;", '\u03C4', node, map);
		addName("&there4;", '\u2234', node, map);
		addName("&thetasym;", '\u03D1', node, map);
		addName("&Theta;", '\u0398', node, map);
		addName("&theta;", '\u03B8', node, map);
		addName("&thinsp;", '\u2009', node, map);
		addName("&THORN;", '\u00DE', node, map);
		addName("&thorn;", '\u00FE', node, map);
		addName("&tilde;", '\u02DC', node, map);
		addName("&times;", '\u00D7', node, map);
		addName("&trade;", '\u2122', node, map);
		addName("&Uacute;", '\u00DA', node, map);
		addName("&uacute;", '\u00FA', node, map);
		addName("&uarr;", '\u2191', node, map);
		addName("&uArr;", '\u21D1', node, map);
		addName("&Ucirc;", '\u00DB', node, map);
		addName("&ucirc;", '\u00FB', node, map);
		addName("&Ugrave;", '\u00D9', node, map);
		addName("&ugrave;", '\u00F9', node, map);
		addName("&uml;", '\u00A8', node, map);
		addName("&upsih;", '\u03D2', node, map);
		addName("&Upsilon;", '\u03A5', node, map);
		addName("&upsilon;", '\u03C5', node, map);
		addName("&Uuml;", '\u00DC', node, map);
		addName("&uuml;", '\u00FC', node, map);
		addName("&weierp;", '\u2118', node, map);
		addName("&Xi;", '\u039E', node, map);
		addName("&xi;", '\u03BE', node, map);
		addName("&Yacute;", '\u00DD', node, map);
		addName("&yacute;", '\u00FD', node, map);
		addName("&yen;", '\u00A5', node, map);
		addName("&yuml;", '\u00FF', node, map);
		addName("&Yuml;", '\u0178', node, map);
		addName("&Zeta;", '\u0396', node, map);
		addName("&zeta;", '\u03B6', node, map);
		addName("&zwj;", '\u200D', node, map);
		addName("&zwnj;", '\u200C', node, map);
        root = node;
        names = map;
    }

//    public static void main(String[] params) {
//        List<String> list = Split.file("/tmp/empty.txt", "utf-8").byLinesToList();
//        for(String string : list) {
//            List<String> data = Split.string(string).bySubstringToList("\t");
//            String num = data.get(0);
//            int ii = Get.from(num, true, true).intValue();
//            String hex = Integer.toHexString(ii).toUpperCase();
//            while(hex.length() < 4) hex = "0" + hex;
//            String name = data.get(1);
//
//            System.out.println("        addName(\""+name+"\", '\\u"+hex+"', node, map);");
//        }
//    }
}
