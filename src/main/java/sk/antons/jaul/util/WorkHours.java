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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.antons.jaul.Get;
import sk.antons.jaul.Is;

/**
 * Allow to define time intervals and check if given time is in that intervals.
 *
 * It is configured using one string which is parsed in following way.
 *
 * <ul>
 * <li>spring is separated by space and each part is combined using OR</li>
 * <li>each part is separated by '|' and each part is combined using AND</li>
 * <li>each part is separated by ',' and each part is combined using OR</li>
 * <li>if part is number than it is considered to be day of month (negative number is taken from month end - -1 is last day of month)</li>
 * <li>if part is three letter abbreviation of week day than it is considered to be day of week</li>
 * <li>if part is three letter abbreviation of month than it is considered to be month</li>
 * <li>otherwise it shoul be time interval in gorm HH:mm-HH:mm</li>
 * </ul>
 *
 * <ul>
 * <li>'08:00-16:00' - simple interval</li>
 * <li>'22:00-06:00' - simple interval</li>
 * <li>'22:00-06:00,16:00-20:00' - combine two intervals</li>
 * <li>'SUN|22:00-06:00 1|16:00-20:00' - combine two intervals one for sunday one for first day in month</li>
 * <li>'1,15,28|16:00-20:00,22:00-04:00' - intervals for three days in month</li>
 * <li>'MON,TUE,WED,THU,FRI|08:00-16:00 SAT,SUN|08:00-12:00' - intervals for three days in month</li>
 * </ul>
 *
 * @author antons
 */
public class WorkHours {
    TmCond condition;

    /**
     * Creates WorkHours interval.
     * @param conf interval in format 'HH:mm-HH:mm' or empty string for whole day interval.
     */
    public WorkHours(String conf) {
        this.condition = orCond(conf, ' ', true);
    }
    public static WorkHours instance(String conf) { return new WorkHours(conf); }

    public String toString() { return condition == null ? "null" : condition.toString(); }

    private static TmCond orCond(String conf, char c, boolean top) {
        if(Is.empty(conf)) {
            return TrueCond.instance();
        } else {
            conf = conf.trim();
            List<String> list = splitby(conf, c);
            if(list.size() == 0) {
                return TrueCond.instance();
            } else if(list.size() == 1) {
                return top ? andCond(conf, '|') : simpleCond(conf);
            } else {
                OrCond rv = OrCond.instance();
                for(String string : list) {
                    rv.add(top ? andCond(string, '|') : simpleCond(string));
                }
                return rv;
            }
        }
    }

    private static TmCond andCond(String conf, char c) {
        if(Is.empty(conf)) {
            return TrueCond.instance();
        } else {
            conf = conf.trim();
            List<String> list = splitby(conf, c);
            if(list.size() == 0) {
                return TrueCond.instance();
            } else if(list.size() == 1) {
                return orCond(conf, ',', false);
            } else {
                AndCond rv = AndCond.instance();
                for(String string : list) {
                    rv.add(orCond(string, ',', false));
                }
                return rv;
            }
        }
    }

    private static TmCond simpleCond(String conf) {
        if(Is.empty(conf)) return TrueCond.instance();

        conf = conf.toLowerCase();

        int day = Get.from(conf, true, true).intValue(-1);
        if(day != -1) return DayOfMonthCond.instance(day);

        if("mon".equals(conf)) return DayOfWeekCond.instance(DayOfWeek.MONDAY);
        if("tue".equals(conf)) return DayOfWeekCond.instance(DayOfWeek.TUESDAY);
        if("wed".equals(conf)) return DayOfWeekCond.instance(DayOfWeek.WEDNESDAY);
        if("thu".equals(conf)) return DayOfWeekCond.instance(DayOfWeek.THURSDAY);
        if("fri".equals(conf)) return DayOfWeekCond.instance(DayOfWeek.FRIDAY);
        if("sat".equals(conf)) return DayOfWeekCond.instance(DayOfWeek.SATURDAY);
        if("sun".equals(conf)) return DayOfWeekCond.instance(DayOfWeek.SUNDAY);

        if("jan".equals(conf)) return MonthCond.instance(Month.JANUARY);
        if("feb".equals(conf)) return MonthCond.instance(Month.FEBRUARY);
        if("mar".equals(conf)) return MonthCond.instance(Month.MARCH);
        if("apr".equals(conf)) return MonthCond.instance(Month.APRIL);
        if("may".equals(conf)) return MonthCond.instance(Month.MAY);
        if("jun".equals(conf)) return MonthCond.instance(Month.JUNE);
        if("jul".equals(conf)) return MonthCond.instance(Month.JULY);
        if("aug".equals(conf)) return MonthCond.instance(Month.AUGUST);
        if("sep".equals(conf)) return MonthCond.instance(Month.SEPTEMBER);
        if("oct".equals(conf)) return MonthCond.instance(Month.OCTOBER);
        if("nov".equals(conf)) return MonthCond.instance(Month.NOVEMBER);
        if("dec".equals(conf)) return MonthCond.instance(Month.DECEMBER);

        return TimeCond.instance(conf);

    }

    private static List<String> splitby(String text, char c) {
        List<String> list = new ArrayList<>();
        int len = text.length();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++) {
            char cc = text.charAt(i);
            if(cc == c) {
                if(!sb.isEmpty()) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(cc);
            }
        }
        if(!sb.isEmpty()) {
            list.add(sb.toString());
            sb.setLength(0);
        }
        return list;
    }


    public boolean isWorkTime() {
        return isWorkTime(LocalDateTime.now());
    }

    public boolean isWorkTime(LocalDateTime time) {
        if(time == null) return false;
        return condition.apply(Tm.instance(time));
    }


    private static class Tm {
        int minute;
        int hour;
        java.time.DayOfWeek dayOfWeek;
        java.time.Month month;
        int dayOfMonth;
        int monthDays;

        public static Tm instance(LocalDateTime time) {
            if(time == null) return null;
            Tm tm = new Tm();
            tm.minute = time.getMinute();
            tm.hour = time.getHour();
            tm.month = time.getMonth();
            tm.dayOfWeek = time.getDayOfWeek();
            tm.dayOfMonth = time.getDayOfMonth();
            switch(tm.month) {
                case JANUARY:
                case MARCH:
                case MAY:
                case JULY:
                case AUGUST:
                case OCTOBER:
                case DECEMBER:
                    tm.monthDays = 32;
                    break;
                case APRIL:
                case JUNE:
                case SEPTEMBER:
                case NOVEMBER:
                    tm.monthDays = 31;
                    break;
                default:
                    int year = time.getYear();
                    if(year % 4 == 0) {
                        if(year % 100 == 0) {
                            if(year % 400 == 0) {
                                tm.monthDays = 30;
                            } else {
                                tm.monthDays = 29;
                            }
                        } else {
                            tm.monthDays = 30;
                        }
                    } else {
                        tm.monthDays = 29;
                    }
                    break;

            }
            return tm;
        }
    }

    private static interface TmCond {
        boolean apply(Tm time);
    }

    private static abstract class ContainerCond {
        List<TmCond> conditions = new ArrayList<>();
        public void add(TmCond cond) { this.conditions.add(cond); }
    }

    private static class OrCond extends ContainerCond implements TmCond {

        @Override
        public boolean apply(Tm time) {
            for(TmCond condition : conditions) {
                if(condition.apply(time)) return true;
            }
            return false;
        }
        public static OrCond instance() { return new OrCond(); }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for(TmCond condition1 : conditions) {
                if(sb.length() > 1) sb.append(" or ");
                sb.append(condition1);
            }
            sb.append(")");
            return sb.toString();
        }
    }

    private static class AndCond extends ContainerCond implements TmCond {

        @Override
        public boolean apply(Tm time) {
            if(Is.empty(conditions)) return false;
            for(TmCond condition : conditions) {
                if(!condition.apply(time)) return false;
            }
            return true;
        }
        public static AndCond instance() { return new AndCond(); }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for(TmCond condition1 : conditions) {
                if(sb.length() > 1) sb.append(" and ");
                sb.append(condition1);
            }
            sb.append(")");
            return sb.toString();
        }

    }

    private static class TrueCond implements TmCond {

        @Override
        public boolean apply(Tm time) {
            return true;
        }
        public static TrueCond instance() { return new TrueCond(); }

        public String toString() {
            return "true";
        }

    }

    private static class TimeCond implements TmCond {
        private int hourFrom;
        private int minuteFrom;
        private int hourTo;
        private int minuteTo;
        boolean reverse = false;

        public String toString() {
            return hourFrom + ":" + minuteFrom+ "-" + hourTo + ":" + minuteTo;
        }

        @Override
        public boolean apply(Tm time) {
            if(time == null) return false;
            if(reverse) {
                if(hourFrom < time.hour) return true;
                if((hourFrom == time.hour) && (minuteFrom <= time.minute)) return true;
                if(hourTo > time.hour) return true;
                if((hourTo == time.hour) && (minuteTo >= time.minute)) return true;
                return false;
            } else {
                if(hourFrom > time.hour) return false;
                if((hourFrom == time.hour) && (minuteFrom > time.minute)) return false;
                if(hourTo < time.hour) return false;
                if((hourTo == time.hour) && (minuteTo < time.minute)) return false;
                return true;
            }
        }


        private static Pattern timepattern = Pattern.compile("^([0-9][0-9]):([0-9][0-9])-([0-9][0-9]):([0-9][0-9])$");
        public static TimeCond instance(String conf) {
            try {
                TimeCond cond = new TimeCond();
                Matcher matcher = timepattern.matcher(conf);
                if(!matcher.find()) throw new IllegalArgumentException();
                cond.hourFrom = Get.from(matcher.group(1)).intValue();
                cond.minuteFrom = Get.from(matcher.group(2)).intValue();
                cond.hourTo = Get.from(matcher.group(3)).intValue();
                cond.minuteTo = Get.from(matcher.group(4)).intValue();
                cond.reverse = (cond.hourFrom > cond.hourTo) ||
                    ((cond.hourFrom == cond.hourTo) && (cond.minuteFrom > cond.minuteTo)) ;
                return cond;
            } catch(Exception e) {
                throw new IllegalArgumentException("unable to parse time interval '"+conf+"'", e);
            }
        }

    }

    private static class DayOfWeekCond implements TmCond {
        private java.time.DayOfWeek dayOfWeek;

        @Override
        public boolean apply(Tm time) {
            if(time == null) return false;
            return dayOfWeek == time.dayOfWeek;
        }

        public static DayOfWeekCond instance(DayOfWeek conf) {
            try {
                DayOfWeekCond cond = new DayOfWeekCond();
                cond.dayOfWeek = conf;
                return cond;
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e, "unable to parse dayOfWeek '{}'", conf);
            }
        }

        public String toString() {
            return dayOfWeek == null ? "null" : dayOfWeek.name();
        }

    }

    private static class MonthCond implements TmCond {
        private java.time.Month month;

        @Override
        public boolean apply(Tm time) {
            if(time == null) return false;
            return month == time.month;
        }

        public static MonthCond instance(java.time.Month conf) {
            try {
                MonthCond cond = new MonthCond();
                cond.month = conf;
                return cond;
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e, "unable to parse month '{}'", conf);
            }
        }

        public String toString() {
            return month == null ? "null" : month.name();
        }

    }

    private static class DayOfMonthCond implements TmCond {
        private int dayOfMonth;

        @Override
        public boolean apply(Tm time) {
            if(time == null) return false;
            int day = (dayOfMonth < 0) ? (time.monthDays + dayOfMonth) : dayOfMonth;
            return day == time.dayOfMonth;
        }

        public static DayOfMonthCond instance(int conf) {
            try {
                DayOfMonthCond cond = new DayOfMonthCond();
                cond.dayOfMonth = conf;
                return cond;
            } catch(Exception e) {
                throw AsRuntimeEx.argument(e, "unable to parse dayOfMonth '{}'", conf);
            }
        }

        public String toString() {
            return dayOfMonth + "";
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
        wh = WorkHours.instance("14:43-10:00");
        System.out.println(" ----- " + wh.isWorkTime());
    }
}
