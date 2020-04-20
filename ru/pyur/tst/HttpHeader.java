package ru.pyur.tst;

import java.util.ArrayList;


public abstract class HttpHeader {

    protected ArrayList<PStr> options = new ArrayList<>();
    protected ArrayList<PStr> options_low = new ArrayList<>();



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
        //maybe trim each value

        return values;
    }



    public abstract void setFirstLine(String first_line) throws Exception;



    public void addOption(String string_line) {  // throws Exception
        //todo: throw some exceptions
        PStr option = Util.split(':', string_line);

        PStr option_trim = new PStr(option.key.trim(), option.value.trim());

        options.add(option_trim);

        PStr option_low = new PStr(option_trim.key.toLowerCase(), option_trim.value.toLowerCase());

        options_low.add(option_low);
    }



    public void addOption(PStr option) {
        options.add(option);
    }



    public void addOptions(ArrayList<PStr> add_options) {
        if (add_options.size() > 0) {
            for (PStr option : add_options) {
                addOption(option);
            }
        }

    }

}