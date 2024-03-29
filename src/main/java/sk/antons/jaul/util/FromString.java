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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import sk.antons.jaul.Is;

/**
 * Converts string value to primitives.
 * @author antons
 */
public class FromString {
    private String value;
    private boolean defaultEmptyValue = true;
    private boolean ignoreBadValue = false;

    /**
     *
     * @param value - value to be parsed.
     */
    public FromString(String value) {
        this.value = value;
    }


    /**
     *
     * @param value - value to be parsed
     * @param defaultEmptyValue - true if empty value should be defaulted (default is true)
     * @param ignoreBadValue - true if also bad formated value should be
     *                         defaulted (default is false - exception is thrown)
     */
    public FromString(String value, boolean defaultEmptyValue, boolean ignoreBadValue) {
        this.value = value;
        this.defaultEmptyValue = defaultEmptyValue;
        this.ignoreBadValue = ignoreBadValue;
    }

    /**
     * Converts string value to int.
     * @param defaultValue - default value
     * @return converted value
     */
    public int intValue(int defaultValue) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new NumberFormatException("Unable to tarse int from empty value");
        }
        int rv = defaultValue;
        try {
            rv = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            if(!ignoreBadValue) throw e;
        }
        return rv;
    }

    /**
     * Converts string value to int.
     * @return converted value or 0 in case empty input
     */
    public int intValue() { return intValue(0); }

    /**
     * Converts string value to long.
     * @param defaultValue - default value
     * @return converted value
     */
    public long longValue(long defaultValue) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new NumberFormatException("Unable to tarse long from empty value");
        }
        long rv = defaultValue;
        try {
            rv = Long.parseLong(value);
        } catch (NumberFormatException e) {
            if(!ignoreBadValue) throw e;
        }
        return rv;
    }

    /**
     * Converts string value to long.
     * @return converted value or 0 in case empty input
     */
    public long longValue() { return longValue(0); }


    /**
     * Converts string value to float.
     * @param defaultValue - default value
     * @return converted value
     */
    public float floatValue(float defaultValue) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new NumberFormatException("Unable to tarse float from empty value");
        }
        float rv = defaultValue;
        value = value.replace(',', '.');
        try {
            rv = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            if(!ignoreBadValue) throw e;
        }
        return rv;
    }

    /**
     * Converts string value to float.
     * @return converted value or 0 in case empty input
     */
    public float floatValue() { return floatValue(0); }

    /**
     * Converts string value to double.
     * @param defaultValue - default value
     * @return converted value
     */
    public double doubleValue(double defaultValue) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new NumberFormatException("Unable to tarse double from empty value");
        }
        double rv = defaultValue;
        value = value.replace(',', '.');
        try {
            rv = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            if(!ignoreBadValue) throw e;
        }
        return rv;
    }

    /**
     * Converts string value to double.
     * @return converted value or 0 in case empty input
     */
    public double doubleValue() { return doubleValue(0); }


    /**
     * Converts string value to boolean.
     * @param defaultValue - default value
     * @return converted value
     */
    public boolean booleanValue(boolean defaultValue) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new IllegalArgumentException("Unable to tarse boolean from empty value");
        }
        boolean rv = defaultValue;
        value = value.toLowerCase();
        if("true".equals(value)) return true;
        if("yes".equals(value)) return true;
        if("1".equals(value)) return true;
        return false;
    }

    /**
     * Converts string value to boolean.
     * @return converted value or false in case empty input
     */
    public boolean booleanValue() { return booleanValue(false); }

    /**
     * Converts string value to BigDecimal.
     * @param defaultValue - default value
     * @return converted value
     */
    public BigDecimal bd(BigDecimal defaultValue) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new IllegalArgumentException("Unable to tarse boolean from empty value");
        }
        BigDecimal rv = defaultValue;
        value = value.replace(',', '.');
        try {
            rv = new BigDecimal(value);
        } catch (Exception e) {
            if(!ignoreBadValue) throw e;
        }
        return rv;
    }

    /**
     * Converts string value to BigDecimal.
     * @return converted value or false in case empty input
     */
    public BigDecimal bd() { return bd(null); }


    /**
     * Converts string value to date.
     * @param defaultValue - default value
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value
     */
    public Date dateValue(Date defaultValue, String format) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new IllegalArgumentException("Unable to tarse date from empty value");
        }
        if(Is.empty(format)) {
            throw new IllegalArgumentException("Unable to tarse date using empty format");
        }

        Date rv = defaultValue;
        value = value.replace(',', '.');
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            rv = df.parse(value);
        } catch (ParseException e) {
            if(!ignoreBadValue) throw new IllegalArgumentException(e);
        }
        return rv;
    }

    /**
     * Converts string value to date.
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value or null in case empty input
     */
    public Date dateValue(String format) { return dateValue(null, format); }

    /**
     * Converts string value to date.
     * @param defaultValue - default value
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value
     */
    public LocalDate localDateValue(LocalDate defaultValue, DateTimeFormatter format) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new IllegalArgumentException("Unable to parse date from empty value");
        }
        if(Is.empty(format)) {
            throw new IllegalArgumentException("Unable to parse date using empty format");
        }

        LocalDate rv = null;
        value = value.replace(',', '.');
        try {
            rv = LocalDate.parse(value, format);
        } catch (Exception e) {
            if(!ignoreBadValue) throw new IllegalArgumentException(e);
            rv = defaultValue;
        }
        return rv;
    }

    /**
     * Converts string value to date.
     * @param defaultValue - default value
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value
     */
    public LocalDate localDateValue(LocalDate defaultValue, String format) { return localDateValue(defaultValue, DateTimeFormatter.ofPattern(format)); }

    /**
     * Converts string value to date.
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value or null in case empty input
     */
    public LocalDate localDateValue(String format) { return localDateValue(null, DateTimeFormatter.ofPattern(format)); }

    /**
     * Converts string value to date.
     * @param defaultValue - default value
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value
     */
    public LocalDateTime localDateTimeValue(LocalDateTime defaultValue, DateTimeFormatter format) {
        if(Is.empty(value)) {
            if(defaultEmptyValue) return defaultValue;
            throw new IllegalArgumentException("Unable to parse date from empty value");
        }
        if(Is.empty(format)) {
            throw new IllegalArgumentException("Unable to parse date using empty format");
        }

        LocalDateTime rv = null;
        value = value.replace(',', '.');
        try {
            rv = LocalDateTime.parse(value, format);
        } catch (Exception e) {
            if(!ignoreBadValue) throw new IllegalArgumentException(e);
            rv = defaultValue;
        }
        return rv;
    }

    /**
     * Converts string value to date.
     * @param defaultValue - default value
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value
     */
    public LocalDateTime localDateTimeValue(LocalDateTime defaultValue, String format) { return localDateTimeValue(defaultValue, DateTimeFormatter.ofPattern(format)); }

    /**
     * Converts string value to date.
     * @param format - format of the parsed date (SimpleDateFpormat)
     * @return converted value or null in case empty input
     */
    public LocalDateTime localDateTimeValue(String format) { return localDateTimeValue(null, DateTimeFormatter.ofPattern(format)); }

}
