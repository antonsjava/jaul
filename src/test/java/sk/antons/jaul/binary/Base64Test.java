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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;
import sk.antons.jaul.pojo.Pojo;

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
        
        value = "any carnal pleasure".getBytes();
        result = "YW55IGNhcm\n5hbCBwbGVh\nc3VyZQ==";
        Assert.assertTrue("xx" + Base64.encode(value, 10), result.equals(Base64.encode(value, 10)));
        Assert.assertTrue("xxss" + new String(Base64.decode(result)), result.equals(Base64.encode(Base64.decode(result), 10)));
        Assert.assertTrue("xx" +  new String(Base64.decode(result)), "any carnal pleasure".equals(new String(Base64.decode(result))));
    }
    
    @Test
	public void encodestream() throws Exception {
        byte[] value = new byte[0];
        String result = "";
        ByteArrayInputStream is = new ByteArrayInputStream(value);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        Base64.encode(is, sb, 0);
        Base64.decode(new StringReader(result), os);
        Assert.assertEquals("null encode", "", sb.toString());
        Assert.assertNotNull("null decode", os.toByteArray());
        
        value = new byte[]{};
        result = "";
        is = new ByteArrayInputStream(value);
        os = new ByteArrayOutputStream();
        sb = new StringBuilder();
        Base64.encode(is, sb, 0);
        Base64.decode(new StringReader(result), os);
        Assert.assertTrue("empty", sb.isEmpty());
        Assert.assertTrue("empty", os.toByteArray().length == 0);

        value = "any carnal pleas".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhcw==";
        is = new ByteArrayInputStream(value);
        os = new ByteArrayOutputStream();
        sb = new StringBuilder();
        Base64.encode(is, sb, 0);
        Base64.decode(new StringReader(result), os);
        //{ byte[] list = value; for(int i = 0; i < list.length; i++) { System.out.print(", "+ list[i]); } System.out.println(" ");}
        //{ byte[] list = os.toByteArray(); for(int i = 0; i < list.length; i++) { System.out.print(", "+ list[i]); } System.out.println(" ");}
        //{ byte[] list = Base64.decode(result); for(int i = 0; i < list.length; i++) { System.out.print(", "+ list[i]); } System.out.println(" ");}
        //System.out.println(" ---1- " + Pojo.dumper().dump(value));
        //System.out.println(" ---2- " + Pojo.dumper().dump(os.toByteArray()));
        Assert.assertEquals(result, sb.toString());
        Assert.assertEquals(new String(value), new String(os.toByteArray()));
        
        value = "any carnal pleasu".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhc3U=";
        is = new ByteArrayInputStream(value);
        os = new ByteArrayOutputStream();
        sb = new StringBuilder();
        Base64.encode(is, sb, 0);
        Base64.decode(new StringReader(result), os);
        Assert.assertEquals(result, sb.toString());
        Assert.assertEquals(new String(value), new String(os.toByteArray()));
        
        value = "any carnal pleasur".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhc3Vy";
        is = new ByteArrayInputStream(value);
        os = new ByteArrayOutputStream();
        sb = new StringBuilder();
        Base64.encode(is, sb, 0);
        Base64.decode(new StringReader(result), os);
        Assert.assertEquals(result, sb.toString());
        Assert.assertEquals(new String(value), new String(os.toByteArray()));
        
        value = "any carnal pleasure".getBytes();
        result = "YW55IGNhcm5hbCBwbGVhc3VyZQ==";
        is = new ByteArrayInputStream(value);
        os = new ByteArrayOutputStream();
        sb = new StringBuilder();
        Base64.encode(is, sb, 0);
        Base64.decode(new StringReader(result), os);
        Assert.assertEquals(result, sb.toString());
        Assert.assertEquals(new String(value), new String(os.toByteArray()));
        
        value = "any carnal pleasure".getBytes();
        result = "YW55IGNhcm\n5hbCBwbGVh\nc3VyZQ==";
        is = new ByteArrayInputStream(value);
        os = new ByteArrayOutputStream();
        sb = new StringBuilder();
        Base64.encode(is, sb, 10);
        Base64.decode(new StringReader(result), os);
        Assert.assertEquals(result, sb.toString());
        Assert.assertEquals(new String(value), new String(os.toByteArray()));
    }

    @Test
	public void full() throws Exception {
        
        for(int i = 0; i < 257; i++) {
            for(int j = 0; j < 257; j++) {
                for(int k = 0; k < 257; k++) {
                        byte[] data = new byte[]{(byte)i, (byte)j, (byte)k};
                        String my = Base64.encode(data);
                        String jav = java.util.Base64.getEncoder().encodeToString(data);
                        Assert.assertEquals(jav, my);
                        byte[] data2 = Base64.decode(my);
                        //byte[] data2 = java.util.Base64.getDecoder().decode(my);
                        Assert.assertEquals(3, data2.length);
                        Assert.assertEquals((byte)i, data2[0]);
                        Assert.assertEquals((byte)j, data2[1]);
                        Assert.assertEquals((byte)k, data2[2]);

                }
            }
        }

    }
    
    @Test
	public void fullstream() throws Exception {
        
        for(int i = 0; i < 257; i++) {
            for(int j = 0; j < 257; j++) {
                for(int k = 0; k < 257; k++) {
                        byte[] data = new byte[]{(byte)i, (byte)j, (byte)k};
                        ByteArrayInputStream is = new ByteArrayInputStream(data);
                        StringBuilder sb = new StringBuilder();
                        Base64.encode(is, sb, 0);
                        String my = sb.toString();
                        String jav = java.util.Base64.getEncoder().encodeToString(data);
                        Assert.assertEquals(jav, my);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        Base64.decode(new StringReader(my), os);
                        byte[] data2 = os.toByteArray();
                        Assert.assertEquals(3, data2.length);
                        Assert.assertEquals((byte)i, data2[0]);
                        Assert.assertEquals((byte)j, data2[1]);
                        Assert.assertEquals((byte)k, data2[2]);
                }
            }
        }

    }

}
