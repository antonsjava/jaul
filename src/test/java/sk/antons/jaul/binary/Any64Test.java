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

import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class Any64Test {
    private static Any64 encoder = Any64.instance('?', '!');

    private static String printArray(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < data.length; i++) {
            sb.append(" ").append(data[i]);
        }
        return sb.toString();
    }
    
    @Test
	public void encode() throws Exception {
        try {
            Random random = new Random(System.currentTimeMillis());
            for(int i = 0; i < 100; i++) {
                byte[] data = new byte[i];
                for(int j = 0; j < i; j++) data[j] = (byte)random.nextInt(255);
                String encoded = encoder.encode(data);
                //System.out.println(" " + encoded);
                Assert.assertNotNull("null text", encoded);
                byte[] data2 = encoder.decode(encoded);
                Assert.assertNotNull("null", data2);
                Assert.assertTrue("length", data.length == data2.length);
                for(int j = 0; j < i; j++) {
                    Assert.assertTrue("length "+i+" char "+j, data[j] == data2[j]);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        
    }
    
}
