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
 *    StopWatch sw = StopWatch.instance().finalRound(100);
 *    for(int i = 0; i &lt; 100; i++) {
 *        Thread.sleep(1000);
 *        sw.round();
 *        System.out.println(" processed " + i + " remaining time: " + sw.remainingTime().readable());
 *    }
 * &lt;/pre&gt;
 * @author antons
 */
public class StopWatch {
    private static Logger log = Logger.getLogger(StopWatch.class.getName());

    long creationtime;
    long starttime;

    int finalRound;
    int rounds = 0;

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
    public StopWatch finalRound(int value) { this.finalRound = value; return this; }

    /**
     * start new time measurement.
     */
    public StopWatch reset() { this.starttime = System.currentTimeMillis(); this.rounds = 0; return this; }

    /**
     * add n to rounds.
     */
    public StopWatch round(int num) { this.rounds = this.rounds + num; return this; }

    /**
     * add 1 to rounds.
     */
    public StopWatch round() { this.rounds++; return this; }

    /**
     * returns number of rounds.
     */
    public int rounds() { return this.rounds; }

    /**
     * time from last reset of creation or StopWatch.
     */
    public ReadableTime time() { return ReadableTime.of(System.currentTimeMillis() - this.starttime); }

    /**
     * time from creation or StopWatch.
     */
    public ReadableTime wholeTime() { return ReadableTime.of(System.currentTimeMillis() - this.creationtime); }

    /**
     * Estimated time to last iteration based on counter. (finalRound must be specified)
     */
    public ReadableTime remainingTime() {
        long time = -1;
        if(finalRound > 0) {
            if(rounds > 0) {
                time = System.currentTimeMillis() - this.starttime;
                time = time * (finalRound - rounds) / rounds;
            }
        }
        return ReadableTime.of(time);
    }

    /**
     * Average time of round. Calculated as time() / rounds().
     */
    public ReadableTime averageRoundTime() {
        long time = -1;
        if(rounds > 0) {
            time = System.currentTimeMillis() - this.starttime;
            time = time / rounds;
        }
        return ReadableTime.of(time);
    }


    /**
     * Helper for displaying time interval in readable form
     */
    public static class ReadableTime {

        long time;
        boolean displayMS = false;

        public ReadableTime(long time) { this.time = time; }
        public static ReadableTime of(long time) { return new ReadableTime(time); }
        public ReadableTime displayMsInReadable(boolean value) { this.displayMS = value; return this; }

        public long ms() { return time; }

        public String readable()  {
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

        public String toString() {
            return String.valueOf(time);
        }

    }

    public static void main(String[] argv) throws Exception {
        StopWatch sw = StopWatch.instance().finalRound(100);
        for(int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            System.out.println(" processed " + i + " remaining time: " + sw.round().remainingTime().readable());
        }

    }
}
