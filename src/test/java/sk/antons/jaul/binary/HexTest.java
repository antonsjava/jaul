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
public class HexTest {
	
    @Test
	public void encode() throws Exception {
        byte[] value = null;
        String result = null;
        Assert.assertTrue("null encode", Hex.encode(value).length() == 0);
        Assert.assertNull("null decode", Hex.decode(result));
        
        value = new byte[]{};
        result = "";
        Assert.assertTrue("empty", Hex.encode(value).length() == 0);
        Assert.assertTrue("empty", result.equals(new String(Hex.decode(result))));

        value = "qwertyuiop".getBytes();
        result = "71776572747975696F70";
        Assert.assertTrue("xx1", result.equals(Hex.encode(value)));
        Assert.assertTrue("xx2", "qwertyuiop".equals(new String(Hex.decode(result))));
    }
    
}
