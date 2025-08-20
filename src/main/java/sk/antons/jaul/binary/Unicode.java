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

package sk.antons.jaul.binary;

import sk.antons.jaul.Is;

/**
 * Converts String chars to escaped (\u0000) form string and vice versa.
 * Only visible ASCII chars leave unchanged.
 *
 * @author antons
 */
public class Unicode {

	/**
	 * Translates the given String into ASCII chars and others are escaped.
	 *
	 * @param input - string to be encoded
	 * @param ignoreInvisible - chars up to 31 will be not escaped (useful if
     *                          you want to keep new lines and tabs)
	 * @return new String with escaped non visible ASII chars
	 */
	public static String escape(String input, boolean ignoreInvisible) {
		if(Is.empty(input)) return input;
		StringBuilder sb = new StringBuilder(input.length() + 30);
		for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if((ignoreInvisible || (c > 31)) && (c < 127)) {
                sb.append(c);
            } else {
            	sb.append("\\u");
            	String hex = Integer.toHexString(c);
            	for(int j = hex.length(); j < 4; j++) sb.append( '0' );
            	sb.append( hex );
            }
        }
		return sb.toString();
	}


	/**
	 * Decodes unicode escaped literal back to real String.
	 *
	 * @param input text with escape sequences
	 * @return String with translated escape sequences
	 */
	public static String unescape(String input) {
		if(Is.empty(input)) return input;
		StringBuilder sb = new StringBuilder( input.length() );
		boolean isbs = false;
		for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (isbs) {
            	isbs = false;
                if(c == 'f')  c = '\f';
                else if(c == 'n') c = '\n';
                else if(c == 'r') c = '\r';
                else if(c == 't') c = '\t';
                else if(c == '\\') c = '\\';
                else if(c == 'u') {
                    String hex = input.substring( i + 1, i + 5 );
                    c = (char) Integer.parseInt(hex, 16 );
                    i += 4;
                } else {
                    sb.append('\\');
                }
            } else {
            	isbs = (c == '\\');
            }
            if (!isbs) sb.append(c);
        }
		return sb.toString();
	}

    /**
	 * Translates the given String into ASCII chars and others are escaped.
     * excape also \r, \n. ... and other Java special characters
     * @param input string to be escaped
     * @return escaped string
     */
	public static String escapeJava(String input) {
		if(Is.empty(input)) return input;
		StringBuilder sb = new StringBuilder(input.length() + 30);
		for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if(c == '\b') sb.append("\\b");
            else if(c == '\n') sb.append("\\n");
            else if(c == '\r') sb.append("\\r");
            else if(c == '\t') sb.append("\\t");
            else if(c == '\f') sb.append("\\f");
            else if(c == '\'') sb.append("\\\'");
            else if(c == '\\') sb.append("\\\\");
            else if(c == '"') sb.append("\\\"");
            else if((c > 31) && (c < 127)) sb.append(c);
            else {
            	sb.append("\\u");
            	String hex = Integer.toHexString(c);
            	for(int j = hex.length(); j < 4; j++) sb.append( '0' );
            	sb.append( hex );
            }
        }
		return sb.toString();
	}

	/**
	 * Decodes unicode escaped literal back to real String.
     * unexape also Java special characters like \n \r, ...
	 *
	 * @param input text with escape sequences
	 * @return String with translated escape sequences
	 */
	public static String unescapeJava(String input) {
		if(Is.empty(input)) return input;
		StringBuilder sb = new StringBuilder( input.length() );
		boolean isbs = false;
		for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (isbs) {
            	isbs = false;
                if(c == 'f')  c = '\f';
                else if(c == 'n') c = '\n';
                else if(c == 'r') c = '\r';
                else if(c == 't') c = '\t';
                else if(c == 'b') c = '\b';
                else if(c == '\\') c = '\\';
                else if(c == '"') c = '"';
                else if(c == '\'') c = '\'';
                else if(c == 'u') {
                    String hex = input.substring( i + 1, i + 5 );
                    c = (char) Integer.parseInt(hex, 16 );
                    i += 4;
                } else {
                    sb.append('\\');
                }
            } else {
            	isbs = (c == '\\');
            }
            if (!isbs) sb.append(c);
        }
		return sb.toString();
	}


	/**
	 * Translates the given String into ASCII chars and others are escaped.
     * Uses HTML &amp;#XXX; escape form;
	 *
	 * @param input - string to be encoded
	 * @param ignoreInvisible - chars up to 31 will be not escaped (useful if
     *                          you want to keep new lines and tabs)
	 * @return new String with escaped non visible ASII chars
	 */
	public static String escapeHtml( String input, boolean ignoreInvisible) {
		if(Is.empty(input)) return input;
        StringBuilder buff = new StringBuilder(input.length()*2);
        for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if((ignoreInvisible || (c > 31)) && (c < 127)) buff.append(c);
            else buff.append("&#").append((int)c).append(';');
        }
        return buff.toString();
	}

	/**
	 * Decodes unicode escaped literal back to real String.
     * Uses HTML &amp;#XXX; escape form;
	 *
	 * @param input text with escape sequences
	 * @return String with translated escape sequences
	 */
	public static String unescapeHtml(String input) {
		if(Is.empty(input)) return input;
        StringBuilder buff = new StringBuilder(input.length()*2);
/*
0 - none
1 - and
2 - cross
3 - num
4 - ;
 */
        int state = 0;
        int sbuff = 0;
        for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if(state == 0) {
                if(c == '&') {
                    state = 1;
                    continue;
                }
            } else if(state == 1) {
                if(c == '#') {
                    state = 2;
                    sbuff = 0;;
                    continue;
                } else {
                    buff.append('&');
                    state = 0;
                }
            } else if(state == 2) {
                if(('0' <= c) && (c <= '9')) {
                    state = 3;
                    sbuff = 10*sbuff + (c - '0');
                    continue;
                } else {
                    buff.append('&');
                    buff.append('#');
                    state = 0;
                }
            } else if(state == 3) {
                if(('0' <= c) && (c <= '9')) {
                    state = 3;
                    sbuff = 10*sbuff + (c - '0');
                    continue;
                } else if((c == ';')) {
                    buff.append((char)sbuff);
                    state = 0;
                    continue;
                } else {
                    buff.append('&');
                    buff.append('#');
                    buff.append(sbuff);
                    state = 0;
                }
            }
            buff.append(c);
        }
        return buff.toString();
	}

}
