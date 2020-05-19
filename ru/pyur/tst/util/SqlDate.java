package ru.pyur.tst.util;

public class SqlDate extends DateTime {

    public SqlDate() {}


    public SqlDate(String date) {
        parseDate(date);
    }

}