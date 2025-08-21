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

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class MapCacheTest {

    @Test
	public void size() throws Exception {
        Map<Integer, Integer> cache = MapCache.instance(Integer.class, Integer.class)
            .limit(10)
            .synchronize();
        for(int i = 0; i < 100; i++) {
            cache.put(i, i);
        }
        System.out.println(" -- " + cache.keySet());
        for(int i = 0; i < 100; i++) {
            if(i < 90) Assert.assertEquals("check " + i, null, cache.get(i));
            else Assert.assertEquals("check " + i, (Integer)i, cache.get(i));
        }
    }

    @Test
	public void expiration() throws Exception {
        Map<Integer, Integer> cache = MapCache.instance(Integer.class, Integer.class)
            .expiration(1000)
            .synchronize();
        for(int i = 0; i < 90; i++) {
            cache.put(i, i);
        }
        Thread.sleep(1001);
        for(int i = 90; i < 100; i++) {
            cache.put(i, i);
        }
        System.out.println(" -- " + cache.keySet());
        for(int i = 0; i < 100; i++) {
            if(i < 90) Assert.assertEquals("check " + i, null, cache.get(i));
            else Assert.assertEquals("check " + i, (Integer)i, cache.get(i));
        }
    }

    @Test
	public void expirationsize() throws Exception {
        Map<Integer, Integer> cache = MapCache.instance(Integer.class, Integer.class)
            .limit(10)
            .expiration(1000)
            .synchronize();
        for(int i = 0; i < 100; i++) {
            cache.put(i, i);
        }
        Thread.sleep(1000);
        for(int i = 0; i < 100; i++) {
            cache.put(i, i);
        }
        System.out.println(" -- " + cache.keySet());
        for(int i = 0; i < 100; i++) {
            if(i < 90) Assert.assertEquals("check " + i, null, cache.get(i));
            else Assert.assertEquals("check " + i, (Integer)i, cache.get(i));
        }
    }

    @Test
	public void all() throws Exception {
        MapCache<Integer, Integer> cache = MapCache.instance(Integer.class, Integer.class)
            ;
        System.out.println(" -- " + cache.dunp());
        for(int i = 0; i < 10; i++) {
            cache.put(i, i);
            System.out.println(" -- " + cache.dunp());
        }
        Assert.assertEquals("size 10", 10, cache.size());
        cache.remove(0);
        System.out.println(" remove tail " + cache.dunp());
        Assert.assertEquals("size 9", 9, cache.size());
        cache.remove(9);
        System.out.println(" remove head " + cache.dunp());
        Assert.assertEquals("size 8", 8, cache.size());
        cache.remove(5);
        System.out.println(" remove middle " + cache.dunp());
        Assert.assertEquals("size 7", 7, cache.size());
        for(int i = 0; i < 10; i++) {
            cache.remove(i);
        }
        System.out.println(" remove all " + cache.dunp());
        Assert.assertEquals("size 0", 0, cache.size());
    }


}
