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
import java.util.logging.Logger;

/**
 * Helper class for time measurment in ms.
 * &lt;pre&gt;
 *    StopWatch sw = StopWatch.instance().iterationCount(100);
 *    for(int i = 0; i &lt; 100; i++) {
 *        Thread.sleep(1000);
 *        System.out.println(" processed " + i + " remaining time: " + sw.remainingTimeReadable(i));
 *    }
 * &lt;/pre&gt;
 * @author antons
 */
public class StopWatch {
    private static Logger log = Logger.getLogger(StopWatch.class.getName());

    long creationtime;
    long starttime;

    int iterationCount;
    int counter = 0;

    private StopWatch() {
        creationtime = System.currentTimeMillis();
        starttime = creationtime;
    }

    /**
     * New instance created. with initiated start time.
     * @return
     */
    public static StopWatch instance() { return new StopWatch(); }

    /**
     * Sets number of iterations used for remainting time counting;
     */
    public StopWatch iterationCount(int count) { this.iterationCount = count; return this; }

    /**
     * start new time measurement.
     */
    public void reset() { this.starttime = System.currentTimeMillis(); this.counter = 0; }

    /**
     * time from last reset of creation or StopWatch.
     */
    public long time() { return System.currentTimeMillis() - this.starttime; }

    /**
     * time from last reset of creation or StopWatch.
     */
    public String timeReadable() { return readableTime(time(), false); }

    /**
     * time from creation or StopWatch.
     */
    public long wholeTime() { return System.currentTimeMillis() - this.creationtime; }

    /**
     * add n to counter.
     */
    public void counter(int num) { this.counter = this.counter + num; }

    /**
     * returns counter value.
     */
    public int counter() { return this.counter; }

    /**
     * Estimated time to last iteration based on counter. (iterationCount must be specified)
     */
    public long remainingTime() {
        return remainingTime(counter);
    }

    /**
     * Estimated time to last iteration. (iterationCount must be specified)
     */
    public long remainingTime(int iteration) {
        if(iterationCount < 1) return -1;
        if(iteration < 1) return -1;
        long time = time();
        long rtime = time * (iterationCount - iteration) / iteration;
        if(rtime < 0 ) iteration = 0;
        return rtime;
    }

    /**
     * Estimated time to last iteration based on counter. (iterationCount must be specified)
     */
    public String remainingTimeReadable() {
        return remainingTimeReadable(counter);
    }

    /**
     * Estimated time to last iteration. (iterationCount must be specified)
     */
    public String remainingTimeReadable(int iteration) {
        return readableTime(remainingTime(iteration), false);
    }

    public static String readableTime(long time, boolean displayMS)  {
        if(time < 0) return "undefined";
        long ms = time % 1000; time = time / 1000;
        long sec = time % 60; time = time / 60;
        long min = time % 60; time = time / 60;
        long hour = time % 24; time = time / 24;
        long day = time;

        boolean something = false;
        StringBuilder sb = new StringBuilder();
        if((day > 0) || something) {
            sb.append(something?" ":"").append(day).append('d');
            something = true;
        }
        if((hour > 0) || something) {
            sb.append(something?" ":"").append(hour).append('h');
            something = true;
        }
        if((min > 0) || something) {
            sb.append(something?" ":"").append(min).append('m');
            something = true;
        }
        if((!displayMS) || (sec > 0) || something) {
            sb.append(something?" ":"").append(sec).append('s');
            something = true;
        }
        if(displayMS) {
            sb.append(something?" ":"").append(ms).append("ms");
        }
        return sb.toString();
    }

    public static void main(String[] argv) throws Exception {
        long time = 23;
        System.out.println(" -- " + readableTime(time, true) + "  " + readableTime(time, false));
        time = time + 12*1000;
        System.out.println(" -- " + readableTime(time, true) + "  " + readableTime(time, false));
        time = time + 4*60*1000;
        System.out.println(" -- " + readableTime(time, true) + "  " + readableTime(time, false));
        time = time + 22*60*60*1000;
        System.out.println(" -- " + readableTime(time, true) + "  " + readableTime(time, false));
        time = time + 123l*24*60*60*1000;
        System.out.println(" -- " + readableTime(time, true) + "  " + readableTime(time, false));

        StopWatch sw = StopWatch.instance().iterationCount(100);
        for(int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            System.out.println(" processed " + i + " remaining time: " + sw.remainingTimeReadable(i));
        }

    }
}
