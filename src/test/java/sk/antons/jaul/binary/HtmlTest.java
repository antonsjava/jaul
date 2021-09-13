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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class HtmlTest {

    private static String data = "<text>Covid automat: Situácia v okresoch sa zhoršuje, červených je už desať && a nieco ine</text>";
	
    @Test
	public void simple() throws Exception {
        String encoded = Html.escapeSimple(data);
        System.out.println(" simple: " + encoded);
        String decoded = Html.unescape(encoded);
        Assert.assertEquals(data, decoded);
    }
    
    @Test
	public void simpleNonAscii() throws Exception {
        String encoded = Html.escapeSimpleAndNonAscii(data);
        System.out.println(" simple non ascii: " + encoded);
        String decoded = Html.unescape(encoded);
        Assert.assertEquals(data, decoded);
    }
    
    @Test
	public void names() throws Exception {
        String encoded = Html.escapeNames(data);
        System.out.println(" names: " + encoded);
        String decoded = Html.unescape(encoded);
        Assert.assertEquals(data, decoded);
    }
    
    @Test
	public void namesNonascii() throws Exception {
        String encoded = Html.escapeNamesAndNonAscii(data);
        System.out.println(" names: " + encoded);
        String decoded = Html.unescape(encoded);
        Assert.assertEquals(data, decoded);
    }
}
