package ru.pyur.tst;

import java.util.ArrayList;


public abstract class HttpHeader {

    protected ArrayList<PStr> options = new ArrayList<>();
    protected ArrayList<PStr> options_low_key = new ArrayList<>();
    protected ArrayList<PStr> options_low = new ArrayList<>();



    public static final int HTTP_VERSION_Unknown = 0;
    public static final int HTTP_VERSION_0_9 = 1;
    public static final int HTTP_VERSION_1_0 = 2;
    public static final int HTTP_VERSION_1_1 = 3;


    protected String szHttpVersion[] = {
            "Unknown",
            "HTTP/0.9",
            "HTTP/1.0",
            "HTTP/1.1"
    };





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
        String name_low = name.toLowerCase();
        //System.out.println("searching for \"" + name_low + "\"...");

        for (PStr opt : options_low) {
            //System.out.println("...\"" + opt.key + "\"");
            if (opt.key.equals(name_low)) {
                //System.out.println("......found!");
                return true;
            }
        }

        //System.out.println("......not found.");
        //throw("option missing.");
        return false;
    }



    public String getOption(String name) {  // throw Exception
        String name_low = name.toLowerCase();
        //System.out.println("searching for \"" + name_low + "\"...");

        for (PStr opt : options_low_key) {
            //System.out.println("...\"" + opt.key + "\"");
            if (opt.key.equals(name_low)) {
                //System.out.println("......found!");
                return opt.value;
            }
        }

        //System.out.println("......not found.");
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

        PStr option_low_key = new PStr(option_trim.key.toLowerCase(), option_trim.value);

        options_low_key.add(option_low_key);

        PStr option_low = new PStr(option_low_key.key, option_low_key.value.toLowerCase());

        options_low.add(option_low);
    }



    public void addOption(PStr option) {
        options.add(option);
    }


    public void addOption(String name, String value) {
        options.add(new PStr(name, value));
    }



    public void addOptions(ArrayList<PStr> add_options) {
        if (add_options.size() > 0) {
            for (PStr option : add_options) {
                addOption(option);
            }
        }

    }

}