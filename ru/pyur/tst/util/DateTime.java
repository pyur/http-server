package ru.pyur.tst.util;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class DateTime {

    private static final int[] month_len = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };
    private static final int[] month_len_leap = { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };

    public int year;
    public int month;  // 1 ... 12
    public int day;    // 1 ... 28,29,30,31
    public int hour;
    public int minute;
    public int second;
    public int millisecond;



    public DateTime() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        second = cal.get(Calendar.SECOND);
        millisecond = cal.get(Calendar.MILLISECOND);
    }


    public DateTime(int timestamp) {
        parseTimestamp(timestamp);
    }


    public DateTime(String sql) {
        if (sql.length() == 10)  parseDate(sql);
        else if (sql.length() == 19)  parseDatetime(sql);
    }


    public DateTime(String sql_date, String sql_time) {
        parseDate(sql_date);
        parseTime(sql_time);
    }


    public DateTime(int year, int month, int day) {
        assign(year, month, day);
    }


    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        assign(year, month, day, hour, minute, second);
    }




    // -------------------------------- Getters -------------------------------- //

//x    public int getYear() { return year; }
//x    public int getMonth() { return month; }
//x    public int getDay() { return day; }
//x    public int getHour() { return hour; }
//x    public int getMinute() { return minute; }
//x    public int getSecond() { return second; }



    // -------------------------------- Readers -------------------------------- //

    // ---------------- parse SQL-formatted date string ---------------- //

    // ---- YYYY-MM-DD ---- //

    public void parseDate(String sql_date) {
        if (sql_date.length() != 10) return;

        year = Integer.parseInt(sql_date.substring(0, 4));
        month = Integer.parseInt(sql_date.substring(5, 7));
        day = Integer.parseInt(sql_date.substring(8, 10));
        hour = 0;
        minute = 0;
        second = 0;
        millisecond = 0;
    }



    // ---- YYYY-MM-DD HH:MM:SS ---- //

    public void parseDatetime(String sql_datetime) {
        if (sql_datetime.length() != 19) return;

        parseDate(sql_datetime.substring(0, 10));
        parseTime(sql_datetime.substring(11, 19));
    }



    // ---- HH:MM:SS ---- //

    public void parseTime(String sql_time) {
        if (sql_time.length() != 8) return;

        hour = Integer.parseInt(sql_time.substring(0, 2));
        minute = Integer.parseInt(sql_time.substring(3, 5));
        second = Integer.parseInt(sql_time.substring(6, 8));
        millisecond = 0;
    }



    // ---- unix timestamp ---- //

    public void parseTimestamp(int unix_timestamp) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(unix_timestamp * 1000);
        assign(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    }



    // ---- fine unix timestamp ---- //

    public void parseUtimestamp(int unix_timestamp_milliseconds) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(unix_timestamp_milliseconds);
        assign(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
        millisecond = cal.get(Calendar.MILLISECOND);
    }



    // ---- components date ---- //

    public void assign(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }



    // ---- components datetime ---- //

    public void assign(int year, int month, int day, int hour, int minute, int second) {
        assign(year, month, day);

        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }




    // ---- padders ----------------------------------------------------------------
    // speed is an issue

    private String pad2(int number) {
        if (month < 10)  return "0" + number;
        return "" + number;
    }


    private String pad3(int number) {
        if (number < 10)       return "00" + number;
        else if (number < 100)  return "0" + number;
        return "" + number;
    }


    private String pad4(int number) {
        if (number < 10)       return "000" + number;
        else if (number < 100)  return "00" + number;
        else if (number < 1000)  return "0" + number;
        return "" + number;
    }




    // -------------------------------- Formatters -------------------------------- //

    public int toTimestamp() {
        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        //Timestamp timestamp = (Timestamp)cal.getTime();
        //return (int)(timestamp.getTime() / 1000);
        return (int)(cal.getTimeInMillis() / 1000);
    }



    public long toUtimestamp() {
        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        //Calendar cal = GregorianCalendar.getInstance();
        //Timestamp timestamp = (Timestamp)cal.getTime();//(year, month - 1, day, hour, minute, second, 1)
        //return (int)(timestamp.getTime() / 1000);
        //cal.setTimeInMillis();
        return cal.getTimeInMillis();
    }



    // ---------------- SQL format ---------------- //

    // ---- SQL date YYYY-MM-DD ---- //

    public String toSqlDate() {
        StringBuilder sb = new StringBuilder();

        sb.append(pad4(year));

        sb.append("-");

        sb.append(pad2(month));

        sb.append("-");

        sb.append(pad2(day));

        return sb.toString();
    }



    // ---- SQL time HH:MM:SS ---- //

    public String toSqlTime() {
        StringBuilder sb = new StringBuilder();

        sb.append(pad2(hour));

        sb.append(":");

//s        if (minute < 10)  sb.append("0");
//s        sb.append(minute);
        sb.append(pad2(minute));

        sb.append(":");

        sb.append(pad2(second));

        return sb.toString();
    }



    // ---- SQL datetime YYYY-MM-DD HH:MM:SS ---- //

    public String toSqlDateTime() {
        return toSqlDate() + " " + toSqlTime();
    }



    // ---- SQL fine datetime YYYY-MM-DD HH:MM:SS.UUU ---- //

    public String toSqlDateUtime() {
        StringBuilder sb = new StringBuilder();

        sb.append(toSqlDateTime());

        sb.append(".");

        sb.append(pad3(millisecond));

        return sb.toString();
    }




    // ---------------- human readable format ---------------- //

    // ---- date DD.MM.YYYY ---- //

    public String toStringDate() {
        StringBuilder sb = new StringBuilder();

        sb.append(pad2(day));

        sb.append(".");

        sb.append(pad2(month));

        sb.append(".");

        sb.append(pad4(year));

        return sb.toString();
    }



    // ---- date DD.MM.YYYY г ---- //

    public String toStringDateG() {
        return toStringDate() + " г";
    }



    // ---- adaptive date DD.MM.YYYY ---- //
    // or MM.YYYY
    // or YYYY
    // or "-"

    public String toStringDateAdaptive(boolean need_year_letter) {
        StringBuilder sb = new StringBuilder();

        if (day != 0) {
            sb.append(pad2(day));

            sb.append(".");
        }

        if (month != 0) {
            sb.append(pad2(month));

            sb.append(".");
        }

        if (year != 0) {
            sb.append(pad4(year));

            if (need_year_letter)  sb.append(" г");
        }


        if (sb.length() == 0)  sb.append("–");

        return sb.toString();
    }



    // ---- datetime DD.MM.YYYY HH:MM:SS ---- //

    public String toStringDateTime() {
        return toStringDate() + " " + toStringTime();
    }



    // ---- datetime DD.MM.YYYY г. HH:MM:SS ---- //

    public String toStringDateTimeG() {
        return toStringDate() + " г " + toStringTime();
    }



    // ---- date YYYY.MM ---- //

    public String toStringYearMonth() {
        StringBuilder sb = new StringBuilder();

        sb.append(pad4(year));

        sb.append(".");

        sb.append(pad2(month));

        return sb.toString();
    }



    // ---- date MM.YYYY ---- //

    public String toStringMonthYear() {
        StringBuilder sb = new StringBuilder();

        sb.append(pad2(month));

        sb.append(".");

        sb.append(pad4(year));

        return sb.toString();
    }



    // ---- time HH:MM:SS ---- //

    public String toStringTime() {
        return toSqlTime();
    }



    // ---- time without seconds HH:MM ---- //

    public String toStringHourMinute() {
        String r = "";

        if (hour < 10)  r += "0";
        r += Integer.toString(hour);

        r += ":";

        if (minute < 10)  r += "0";
        r += Integer.toString(minute);

        return r;
    }



    // ---------------- numeric ---------------- //
/*
    public int numeric_time_seconds() {
        return  hour + (minute * 60) + (second * 3600);
    }

    public int numeric_time_minutes() {
        return  minute + (second * 60);
    }

    // -------- numeric year_mon, day_hour -------- //

    public int numeric_year_month() {
        return  ((year - 2000) * 12) + (month - 1);
    }

    public int numeric_day_hour() {
        return  ((day - 1) * 24) + (second );
    }
*/



    // -------------------------------- Manipulation -------------------------------- //

    // -------- increment/decrement days -------- //

    public DateTime ModifyDays(int days) {
        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        cal.add(Calendar.DATE, days);  // synonym DAY_OF_MONTH
        //todo: without calendar
        assign(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        return this;
    }


    // -------- first day of month -------- //
    public DateTime AdjustBeginOfMonth() {
        day = 1;
        hour = 0;
        second = 0;
        minute = 0;
        millisecond = 0;

        return this;
    }


    // -------- last day of month -------- //
    public DateTime AdjustEndOfMonth() {
        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        hour = 23;
        second = 59;
        minute = 59;
        millisecond = 999;

        return this;
    }




    // -------- first day of week -------- //

    public DateTime AdjustBeginOfWeek() {
        return AdjustWeekday(1);
    }


    // -------- last day of week -------- //

    public DateTime AdjustEndOfWeek() {
        return AdjustWeekday(7);
    }



    // -------- X day of week (1-monday ... 7-sunday) -------- //
    public DateTime AdjustWeekday(int weekday) {

        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        //cal.setFirstDayOfWeek(Calendar.MONDAY);
        // Calendar.DAY_OF_WEEK - Sun(1), Mon(2), Tue(3), Wen(4), Thu(5), Fri(6), Sat(7)
        int curr_wkd = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (curr_wkd < 0)  curr_wkd += 7;
        //cal.add(Calendar.DATE, 0 - wkd);      // set to monday
        //cal.add(Calendar.DATE, weekday - 1);  // set to desired
        cal.add(Calendar.DATE, (0 - curr_wkd) + (weekday - 1));

        assign(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        return this;
    }



    // -------- increment/decrement months -------- //

    public DateTime ModifyMonths(int months) {
        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        cal.add(Calendar.MONTH, months);
        //todo: without calendar
        assign(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        return this;
    }



    // -------- increment/decrement hours -------- //

    public DateTime ModifyHours(int hours) {
        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        cal.add(Calendar.HOUR, hours);
        //todo: without calendar
        assign(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        return this;
    }




    // -------------------------------- packed Month -------------------------------- //

    // 0 - jan 0000
    // 2147483647 - xxx 9999  todo

    public int packMonth() {
        return ((year - 1970) * 12) + (month - 1);
    }


    public void unpackMonth(int packed) {
        this.year = (packed / 12) + 1970;
        this.month = (packed % 12) +  1;
        this.day = 1;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
    }




    // -------------------------------- packed Week -------------------------------- //

    // 0 - mon jan 0000
    // 2147483647 - www xxx 9999  todo

    public int packWeek() {
        return (packDay() + 5) / 7;
    }


    public DateTime unpackWeek(int packed) {
        unpackDay((packed * 7) - 5);
        return this;
    }




    // -------------------------------- packed Day -------------------------------- //

    // 0 - 00 jan 0000
    // 2147483647 - dd mmm 9999  todo

    private boolean isYearLeap() {
        boolean leap = false;

        if ((year % 4) != 0)  leap = false;
        else if ((year % 100) != 0)  leap = true;
        else if ((year % 400) != 0)  leap = false;
        else  leap = true;

        return leap;
    }



    public int packDay() {
        int packed = year * 365;
        packed += ((year - 1) / 4) + 1;    // +1 for every 4 years
        packed -= ((year - 1) / 100) + 1;  // -1 for every 100 years
        packed += ((year - 1) / 400) + 1;  // +1 for every 400 years

        packed += isYearLeap() ? month_len_leap[month-1] : month_len[month-1];

        packed += day - 1;

        return packed;
    }



    public DateTime unpackDay(int packed) {
        int pack_add;

        int y400 = packed / 146097;
        packed -= y400 * 146097;

        pack_add = 0;
        if (packed == 0)  pack_add = 1;
        int y100 = (packed + pack_add - 1) / 36524;
        packed -= y100 * 36524;

        pack_add = 0;
        if (packed == 1)  pack_add = 1;
        int y4 =  (packed - pack_add) / 1461;
        packed -= y4 * 1461;

        pack_add = 0;
        if (packed == 0)  pack_add = 1;
        int y1 = (packed + pack_add - 1) / 365;
        packed -= y1 * 365;
        //packed--;

        if (y1 != 0)  packed--;
        if (y1 == 0 && y4 == 0 && y100 != 0)  packed--;

        //System.out.println("" + packed);

        this.year = y400 * 400 + y100 * 100 + y4 * 4 + y1;

        boolean leap = isYearLeap();
        int monlen;

        for (int i = 0; i < 12; i++) {
            monlen = leap ? month_len_leap[i+1] : month_len[i+1];
            if (packed < monlen) {
                this.month = i + 1;
                packed -= leap ? month_len_leap[i] : month_len[i];
                break;
            }
        }

        this.day = packed + 1;

        this.hour = 0;
        this.minute = 0;
        this.second = 0;

        return this;
    }



//    public void TestPackedDay() {
//        DateTime dt = new DateTime();
//
//        for (int i = 0; i < 2000; i++) {
//            dt.UnpackDMY(i);
//            System.out.println("" + i + " - " + dt.StrDate());
//        }
//    }




    // -------------------------------- Other -------------------------------- //
/*
    // -------- compare -------- //
    public function cmp($date2) {
    }



    // -------- difference -------- //
    public function dif($date2) {
    }
*/


    // -------- number of days in month -------- //

    public int daysInMonth() {
        Calendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }




    // ---- Equality -------------------------------------------------------------------------------

    @Override
    public boolean equals(Object cmp_date) {
        if (getClass() != cmp_date.getClass())  return false;

        DateTime d = (DateTime) cmp_date;

        if (year != d.year)  return false;
        if (month != d.month)  return false;
        if (day != d.day)  return false;
        if (hour != d.hour)  return false;
        if (minute != d.minute)  return false;
        if (second != d.second)  return false;
        if (millisecond != d.millisecond)  return false;

        return true;
    }


}
