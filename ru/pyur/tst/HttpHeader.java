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



    public ArrayList<PStr> getOptions() {
        return options;
    }



    public boolean hasOption(String name) {
        return getOption(name) != null;
    }



    public String getOption(String name) {  // throw Exception
        String name_low = name.toLowerCase();

        for (PStr opt : options) {
            if (opt.key.toLowerCase().equals(name_low)) {
                return opt.value;
            }
        }

        //throw("option missing.");
        return null;
    }



    public String[] getOptionSplit(String name) {
        String value = getOption(name);
        if (value == null)  return null;

        String[] values = Util.explode(',', value);

        return values;
    }


}