package ru.pyur.tst.default_module;

import ru.pyur.tst.ApiContent;


public class Api_Default extends ApiContent {

    @Override
    public void makeJson() throws Exception {

        put("heading", "Reference Http Java Server");

        put("time", "" + (System.currentTimeMillis() / 1000) );

    }

}