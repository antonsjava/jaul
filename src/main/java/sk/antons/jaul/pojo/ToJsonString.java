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

package sk.antons.jaul.pojo;

/**
 * Helper utility which marks class as class with ability to produce 
 * its json representation.
 * 
 * Implementation can look like 
 * <pre>
 * public claxx XXX implements ToJsonString {
 *   private String value1;
 *   private String value2;
 * 
 *   public void toJsonString(JsonString json, boolean makeObject) {
 *     if(makeObject) json.objectStart();
 *     json.attr("value1", value1);
 *     json.attr("value2", value2);
 *     if(makeObject) json.objectEnd();
 *   }
 * 
 *   public String toString() {
 *     JsonString json = JsonString.instance().indent("  ");
 *     toJsonString(json, true);
 *     return json.toString();
 *   }
 * } 
 * </pre>
 * 
 * @author antons
 */
public interface ToJsonString {

    void toJsonString(JsonString json, boolean makeObject);

}
