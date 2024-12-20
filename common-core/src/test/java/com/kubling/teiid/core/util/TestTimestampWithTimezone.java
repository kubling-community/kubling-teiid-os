/*
 * Copyright Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the COPYRIGHT.txt file distributed with this work.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kubling.teiid.core.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class TestTimestampWithTimezone {

    @BeforeEach
    public void setUp() {
        TimestampWithTimezone.resetCalendar(TimeZone.getTimeZone("America/Chicago"));
    }

    @AfterEach
    public void tearDown() {
        TimestampWithTimezone.resetCalendar(null);
    }

    /**
     * Ensures that the same calendar fields in different timezones (initially different UTC) can be converted to the same UTC in
     * the local time zone
     *
     * @param startts
     * @param startnanos
     * @param starttz
     * @param endtz
     * @since 4.3
     */
    public void helpTestSame(String startts,
                             int startnanos,
                             String starttz,
                             String endtz) {
        try {
            Timestamp start = getTimestamp(startts, startnanos, starttz);
            Timestamp end = getTimestamp(startts, startnanos, endtz);

            assertNotEquals(start.getTime(), end.getTime(), "Initial timestamps should be different UTC times");

            assertEquals(TimestampWithTimezone.createTimestamp(start, TimeZone.getTimeZone(starttz), Calendar.getInstance())
                    .getTime(), TimestampWithTimezone.createTimestamp(end,
                    TimeZone.getTimeZone(endtz),
                    Calendar.getInstance()).getTime());
        } catch (ParseException e) {
            fail(e.toString());
        }
    }

    /**
     * Assuming local time zone of -06:00, change ts to endtz and compare to expected
     *
     * @param ts
     * @param endtz
     * @since 4.3
     */
    public void helpTestChange(String ts,
                               String endtz,
                               String expected) {
        Timestamp start = Timestamp.valueOf(ts);
        Calendar target = Calendar.getInstance(TimeZone.getTimeZone(endtz));
        assertEquals(expected,
                TimestampWithTimezone.createTimestamp(start, TimeZone.getTimeZone("America/Chicago"), target).toString());
    }

    /**
     * @param startts
     * @param startnanos
     * @param starttz
     * @throws ParseException
     * @since 4.3
     */
    private Timestamp getTimestamp(String startts,
                                   int startnanos,
                                   String starttz) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone(starttz));

        Timestamp ts = new Timestamp(df.parse(startts).getTime());
        ts.setNanos(startnanos);
        return ts;
    }

    @Test
    public void testDST() {
        helpTestSame("2005-10-30 02:39:10", 1, "America/Chicago",
                "GMT-05:00");

        // ambiguous times are defaulted to standard time equivalent
        helpTestSame("2005-10-30 01:39:10", 1, "America/Chicago",
                "GMT");

        // test to ensure a time not representable in DST is converted correctly
        helpTestSame("2005-04-03 02:39:10", 1, "GMT",
                "America/Chicago");

        //expected is in DST
        helpTestChange("2005-10-30 02:39:10.1", "GMT", "2005-10-29 21:39:10.1");

        //expected is in standard time
        helpTestChange("2005-10-30 10:39:10.1", "GMT", "2005-10-30 04:39:10.1");

    }

    @Test
    public void testTimezone() {
        helpTestSame("2004-06-29 15:39:10", 1, "GMT-06:00",
                "GMT-05:00");
    }

    @Test
    public void testTimezone2() {
        helpTestSame("2004-06-29 15:39:10", 1, "GMT-08:00",
                "GMT-06:00");
    }

    @Test
    public void testTimezone3() {
        helpTestSame("2004-08-31 18:25:54", 1, "Europe/London",
                "GMT");
    }

    @Test
    public void testTimezoneOverMidnight() {
        helpTestSame("2004-06-30 23:39:10", 1, "America/Los_Angeles",
                "America/Chicago");
    }

    @Test
    public void testCase2852() {
        helpTestSame("2005-05-17 22:35:33", 508659, "GMT",
                "America/New_York");
    }

    @Test
    public void testCreateDate() {
        Timestamp t = Timestamp.valueOf("2004-06-30 23:39:10.1201");
        Date date = TimestampWithTimezone.createDate(t);

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(date.getTime());

        assertEquals(cal.get(Calendar.HOUR_OF_DAY), 0);
        assertEquals(cal.get(Calendar.MINUTE), 0);
        assertEquals(cal.get(Calendar.SECOND), 0);
        assertEquals(cal.get(Calendar.MILLISECOND), 0);
        assertEquals(cal.get(Calendar.YEAR), 2004);
        assertEquals(cal.get(Calendar.MONTH), Calendar.JUNE);
        assertEquals(cal.get(Calendar.DATE), 30);
    }

    @Test
    public void testCreateTime() {
        Timestamp t = Timestamp.valueOf("2004-06-30 23:39:10.1201");
        Time date = TimestampWithTimezone.createTime(t);

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(date.getTime());

        assertEquals(cal.get(Calendar.HOUR_OF_DAY), 23);
        assertEquals(cal.get(Calendar.MINUTE), 39);
        assertEquals(cal.get(Calendar.SECOND), 10);
        assertEquals(cal.get(Calendar.MILLISECOND), 0);
        assertEquals(cal.get(Calendar.YEAR), 1970);
        assertEquals(cal.get(Calendar.MONTH), Calendar.JANUARY);
        assertEquals(cal.get(Calendar.DATE), 1);
    }

    /**
     * Even though the id of the timezones are different, this should not change the result
     */
    @Test
    public void testDateToDateConversion() {
        Date t = Date.valueOf("2004-06-30");
        Date converted = TimestampWithTimezone.createDate(t, TimeZone.getTimeZone("America/Chicago"), Calendar.getInstance(TimeZone.getTimeZone("US/Central")));

        assertEquals(t.getTime(), converted.getTime());
    }

    @Test
    public void testDateToDateConversion1() {
        Date t = Date.valueOf("2004-06-30");
        Date converted = TimestampWithTimezone.createDate(t, TimeZone.getTimeZone("America/Chicago"), Calendar.getInstance(TimeZone.getTimeZone("GMT")));

        Calendar cal = Calendar.getInstance();
        cal.setTime(converted);

        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

}