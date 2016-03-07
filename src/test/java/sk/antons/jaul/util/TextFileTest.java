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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class TextFileTest {
	
    @Test
	public void read() throws Exception {
        String file = "src/test/resources/split-file-test.txt";
        StringBuilder sb = new StringBuilder();
        sb.append("riadok1\u0161\n");
        sb.append("riadok2\u010d\n");
        sb.append("riadok2\u0165\n");
        sb.append("\n");
        sb.append("riadok\u013e\n");
        sb.append("");
        String value = sb.toString();
        String result = TextFile.read(file, "utf-8");
        Assert.assertTrue("check", value.equals(result));
    }
    
    @Test
	public void save() throws Exception {
        String file = "target/write-file-test.txt";
        StringBuilder sb = new StringBuilder();
        sb.append("riadok1\u0161\n");
        sb.append("riadok2\u010d\n");
        sb.append("riadok2\u0165\n");
        sb.append("\n");
        sb.append("riadok\u013e");
        sb.append("");
        String value = sb.toString();
        TextFile.save(file, "utf-8", value);
        String result = TextFile.read(file, "utf-8");
        System.out.println(" '"+result+"'");
        Assert.assertTrue("check", value.equals(result));
    }

}
