package ru.pyur.tst.util;

public class SqlTime extends DateTime {

    public SqlTime() {}


    public SqlTime(String time) {
        parseTime(time);
    }

}