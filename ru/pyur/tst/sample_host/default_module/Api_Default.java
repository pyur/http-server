package ru.pyur.tst.sample_host.default_module;

import ru.pyur.tst.ApiContent;


public class Api_Default extends ApiContent {

    @Override
    public void makeJson() throws Exception {

        put("heading", "Reference Http Java Server");

        put("time", "" + (System.currentTimeMillis() / 1000) );

    }

}