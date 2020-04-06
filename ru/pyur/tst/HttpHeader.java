package ru.pyur.tst;

import java.util.ArrayList;


public abstract class HttpHeader {

    protected ArrayList<PStr> options;



    protected void parseOptions(String[] list) {

        for (int i = 1; i < list.length; i++) {
            PStr option = Util.split(':', list[i]);
            option.key = option.key.trim();
            option.value = option.value.trim();

            //todo: lower-case keys. maybe not pair, but special struct

            options.add(option);
        }
    }


}