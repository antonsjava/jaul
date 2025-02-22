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
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class WorkDaysTest {
    private static LocalDateTime time = LocalDateTime.of(2025, Month.FEBRUARY, 22, 13, 22);

    @Test
	public void empty() throws Exception {
        WorkHours wh = WorkHours.instance("");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void timeOK() throws Exception {
        WorkHours wh = WorkHours.instance("10:12-22:00");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("13:22-13:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("13:22-22:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("10:22-13:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("22:00-14:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("22:00-13:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("13:22-06:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void timeNOK() throws Exception {
        WorkHours wh = WorkHours.instance("14:12-22:00");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("12:22-12:22");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("13:23-22:22");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("10:22-13:21");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("22:00-12:22");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("22:00-13:21");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("13:23-06:22");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void dayOfWeek() throws Exception {
        WorkHours wh = WorkHours.instance("SUN");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("SAT");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void dayOfMonth() throws Exception {
        WorkHours wh = WorkHours.instance("12");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void month() throws Exception {
        WorkHours wh = WorkHours.instance("MAR");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("FEB");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void simpleOr() throws Exception {
        WorkHours wh = WorkHours.instance("12:00-13:00,13:12-15:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("12:00-13:00,13:23-15:22");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void simpleAnd() throws Exception {
        WorkHours wh = WorkHours.instance("SAT|13:12-15:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("12|13:23-15:22");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
    }

    @Test
	public void combined() throws Exception {
        WorkHours wh = WorkHours.instance("12,MON|13:23-15:22 TUE,SAT|13:12-15:22");
        System.out.println(" test " + wh);
        Assert.assertTrue(wh.toString(), wh.isWorkTime(time));
        wh = WorkHours.instance("12,MON|13:23-15:22 TUE,SAT|13:25-15:22");
        System.out.println(" test " + wh);
        Assert.assertFalse(wh.toString(), wh.isWorkTime(time));
    }
}
