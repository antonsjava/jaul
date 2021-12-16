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
public class HtmlEraserTest {

    private static String data = "<p>Covid &jablko; automat: <!--Situácia--> v <script>toto ti nie je</script>okresoch &amp; &</p>";
	
    @Test
	public void simple() throws Exception {
        String erased = HtmlEraser.of(data).erase();
        Assert.assertEquals("Covid &jablko; automat: v okresoch & &", erased);
    }
    
    @Test
	public void simplelen() throws Exception {
        String erased = HtmlEraser.of(data).maxlen(10).erase();
        Assert.assertEquals("Covid &jab...", erased);
    }
    
    @Test
	public void simplelen2() throws Exception {
        String erased = HtmlEraser.of("tototototo").maxlen(1).erase();
        Assert.assertEquals("t...", erased);
    }
    
    @Test
	public void simplelen3() throws Exception {
        String erased = HtmlEraser.of(null).maxlen(1).erase();
        Assert.assertNull(erased);
    }
}
