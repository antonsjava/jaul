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
public class ResourceTest {

    @Test
	public void merge() throws Exception {

        Assert.assertEquals("/path", Resource.mergeUrls("/path", null));
        Assert.assertEquals("/child", Resource.mergeUrls("/path", "/child"));
        Assert.assertEquals("classpath:child", Resource.mergeUrls("/path", "classpath:child"));
        Assert.assertEquals("/path/p1/p2/child", Resource.mergeUrls("/path/p1/p2", "child"));
        Assert.assertEquals("/path/p1/p2/child", Resource.mergeUrls("/path/p1/p2", "./child"));
        Assert.assertEquals("/path/p1/child", Resource.mergeUrls("/path/p1/p2/", "../child"));
        Assert.assertEquals("/path/p1/child", Resource.mergeUrls("/path/p1/p2", "../child"));
        Assert.assertEquals("/path/child", Resource.mergeUrls("/path/p1/p2", "../../child"));
        Assert.assertEquals("/child", Resource.mergeUrls("/path/p1/p2", "../../../child"));
        Assert.assertEquals("../child", Resource.mergeUrls("/path/p1/p2", "../../../../child"));
        Assert.assertEquals("../../child", Resource.mergeUrls("/path/p1/p2", "../../../../../child"));
        Assert.assertEquals("classpath:/path/p1/child", Resource.mergeUrls("classpath:/path/p1/p2/", "../child"));
        Assert.assertEquals("classpath:/path/p1/child", Resource.mergeUrls("classpath:/path/p1/p2", "../child"));
        Assert.assertEquals("classpath:/path/child", Resource.mergeUrls("classpath:/path/p1/p2", "../../child"));

    }

    @Test
	public void normalize() throws Exception {

        Assert.assertEquals(null, Resource.normalizeUrl(null));
        Assert.assertEquals("", Resource.normalizeUrl(""));
        Assert.assertEquals("/", Resource.normalizeUrl("/"));
        Assert.assertEquals("/path", Resource.normalizeUrl("/path"));
        Assert.assertEquals("/path/", Resource.normalizeUrl("/path/"));
        Assert.assertEquals("/path/children", Resource.normalizeUrl("/path/children"));
        Assert.assertEquals("/path/children/p1", Resource.normalizeUrl("/path/children/./p1"));
        Assert.assertEquals("/p1", Resource.normalizeUrl("/path/children/p1/..////./../../p1"));
        Assert.assertEquals("/path/p1", Resource.normalizeUrl("/path/children/../p1"));
        Assert.assertEquals("/path", Resource.normalizeUrl("/path/children/.."));
        Assert.assertEquals("/p1", Resource.normalizeUrl("/path/../p1"));
        Assert.assertEquals("../../p1", Resource.normalizeUrl("../../p1"));
        Assert.assertEquals("../../p1", Resource.normalizeUrl("/../../p1"));
        Assert.assertEquals("../p1", Resource.normalizeUrl("/path/../../p1"));

    }

    @Test
	public void parentUrl() throws Exception {

        Assert.assertEquals(null, Resource.parentUrl(null));
        Assert.assertEquals("", Resource.parentUrl(""));
        Assert.assertEquals("", Resource.parentUrl("/"));
        Assert.assertEquals("", Resource.parentUrl(".path"));
        Assert.assertEquals("", Resource.parentUrl("/path/"));
        Assert.assertEquals("/path", Resource.parentUrl("/path/children"));
        Assert.assertEquals("/path", Resource.parentUrl("/path/p"));

    }

}
