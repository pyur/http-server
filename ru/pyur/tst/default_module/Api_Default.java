package ru.pyur.tst.default_module;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HttpSession;


public class Api_Default extends ApiContent {


    public Api_Default(HttpSession session) { init(session); }



    @Override
    public void makeContent() throws Exception {

        add("heading", "Reference Http Java Server");

        add("time", "" + (System.currentTimeMillis() / 1000) );

    }


}