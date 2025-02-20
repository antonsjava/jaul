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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sk.antons.jaul.Is;

/**
 * Eanble to define time interval in format 'HH:mm-HH:mm' and than check if
 * current time is in this interval.
 *
 * @author antons
 */
public class WorkHours {
    String from;
    String to;
    boolean allopen = false;
    boolean reverse = false;

    /**
     * Creates WorkHours interval.
     * @param conf interval in format 'HH:mm-HH:mm' or empty string for whole day interval.
     */
    public WorkHours(String conf) {
        if(Is.empty(conf)) {
            allopen = true;
        } else {
            if(conf.length() != 11) throw new IllegalArgumentException("work hours must be configured in format 'HH:mm-HH:mm' but was '"+conf+"'");
            if(conf.charAt(2) != ':') throw new IllegalArgumentException("work hours must be configured in format 'HH:mm-HH:mm' but was '"+conf+"'");
            if(conf.charAt(5) != '-') throw new IllegalArgumentException("work hours must be configured in format 'HH:mm-HH:mm' but was '"+conf+"'");
            if(conf.charAt(8) != ':') throw new IllegalArgumentException("work hours must be configured in format 'HH:mm-HH:mm' but was '"+conf+"'");
            this.from = conf.substring(0, 5);
            this.to = conf.substring(6);
            if(from.compareTo(to) > 0) this.reverse = true;
        }
    }
    public static WorkHours instance(String conf) { return new WorkHours(conf); }

    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");

    public boolean isWorkTime() {
        if(allopen) return true;
        return isWorkTime(LocalDateTime.now());
    }

    public boolean isWorkTime(LocalDateTime time) {
        if(time == null) return false;
        if(allopen) return true;
        String times = time.format(format);
        if(reverse) {
            return !((to.compareTo(times) < 0) && (times.compareTo(from) < 0));
        } else {
            return (from.compareTo(times) <= 0) && (times.compareTo(to) <= 0);
        }
    }


    public static void main(String[] argv) {
        WorkHours wh = WorkHours.instance("18:00-01:00");
        System.out.println(" ----- " + wh.isWorkTime());
        wh = WorkHours.instance("10:00-01:00");
        System.out.println(" ----- " + wh.isWorkTime());
        wh = WorkHours.instance("10:00-18:00");
        System.out.println(" ----- " + wh.isWorkTime());
        wh = WorkHours.instance("14:25-18:00");
        System.out.println(" ----- " + wh.isWorkTime());
    }
}
