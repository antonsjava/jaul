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
public class Base64Test {
	
    private static String printArray(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < data.length; i++) {
            sb.append(" ").append(data[i]);
        }
        return sb.toString();
    }
    
    @Test
	public void encode() throws Exception {
        byte[] value = null;
        String result = null;
        Assert.assertNull("null encode", Base64.encode(value));
        Assert.assertNull("null decode", Base64.decode(result));
        
        value = new byte[]{};
        result = "";
        Assert.assertTrue("empty", Base64.encode(value).length() == 0);
        Assert.assertTrue("empty", result.equals(new String(Base64.decode(result))));

        value = "qwertyuiop".getBytes();
        result = "cXdlcnR5dWlvcA==";
        
        //System.out.println(" inp --- " + printArray(value)); 
        //System.out.println(" inp --- " + printArray(Base64.decode(result))); 
        value = "any carnal pleas".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhcw==";
        Assert.assertTrue("xx" + Base64.encode(value), result.equals(Base64.encode(value)));
        Assert.assertTrue("xxss" + new String(Base64.decode(result)), result.equals(Base64.encode(Base64.decode(result))));
        Assert.assertTrue("xx" +  new String(Base64.decode(result)), "any carnal pleas".equals(new String(Base64.decode(result))));
        
        value = "any carnal pleasu".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhc3U=";
        Assert.assertTrue("xx" + Base64.encode(value), result.equals(Base64.encode(value)));
        Assert.assertTrue("xxss" + new String(Base64.decode(result)), result.equals(Base64.encode(Base64.decode(result))));
        Assert.assertTrue("xx" +  new String(Base64.decode(result)), "any carnal pleasu".equals(new String(Base64.decode(result))));
        
        value = "any carnal pleasur".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhc3Vy";
        Assert.assertTrue("xx" + Base64.encode(value), result.equals(Base64.encode(value)));
        Assert.assertTrue("xxss" + new String(Base64.decode(result)), result.equals(Base64.encode(Base64.decode(result))));
        Assert.assertTrue("xx" +  new String(Base64.decode(result)), "any carnal pleasur".equals(new String(Base64.decode(result))));
        
        value = "any carnal pleasure".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhc3VyZQ==";
        Assert.assertTrue("xx" + Base64.encode(value), result.equals(Base64.encode(value)));
        Assert.assertTrue("xxss" + new String(Base64.decode(result)), result.equals(Base64.encode(Base64.decode(result))));
        Assert.assertTrue("xx" +  new String(Base64.decode(result)), "any carnal pleasure".equals(new String(Base64.decode(result))));
        
    }
    
}
