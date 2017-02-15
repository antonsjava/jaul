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

import java.text.SimpleDateFormat;
import java.util.Date;
import sk.antons.jaul.Is;

/**
 * Converts string value to primitives. 
 * @author antons
 */
public class FromDate {
    private Date value;
    private String nullResult;

    /**
     * 
     * @param value - value to be converted.
     * @param nullResult - default value for null date.
     */
    public FromDate(Date value, String nullResult) {
        this.value = value;
        this.nullResult = nullResult;
    }
    

    /**
     * Converts Date to String value to int.
     * @param format - format of the date in SimpleDateFormatter
     * @return converted value or nullResult if input date is null;
     */
    public String string(String format) {
        if(Is.empty(value)) return null;
        String rv = nullResult;
        try {
            if(format == null) format = "yyyy.MM.dd";
            if(value != null) {
                SimpleDateFormat df = new SimpleDateFormat(format);
                rv = df.format(value);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return rv;
    }
    
}
