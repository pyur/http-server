package ru.pyur.tst.tags;

import ru.pyur.tst.util.PStr;

import java.util.ArrayList;

public class ModuleUrl {

    //http://example.com:8080/module/action/?opt1=val1&opt2=val2

    private String protocol;  // http, https
    private String domain;
    private String port;

    private String module;
    private String action;

    private ArrayList<PStr> options = new ArrayList<>();



    public ModuleUrl() {}



    public void setModule(String module) {
        this.module = module;
    }


    public void setAction(String action) {
        this.action = action;
    }


    public void addParameter(String option, String value) {
        options.add(new PStr(option, value));
    }


    public void addParameter(String option, int value) {
        options.add(new PStr(option, value));
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();


        if (domain != null) {
            if (protocol != null) {
                sb.append(protocol);
                sb.append("://");
            }

            sb.append(domain);

            if (port != null) {
                sb.append(":");
                sb.append(port);
            }
        }


        sb.append("/");


        if (module != null) {
            sb.append(module);
            sb.append("/");

            if (action != null) {
                sb.append(action);
                sb.append("/");
            }
        }


        if (options.size() != 0) {
            sb.append("?");

            boolean first = true;
            for (PStr opt : options) {
                if (!first)  sb.append("&");
                sb.append(opt.key);
                sb.append("=");
                sb.append(opt.value);
                if (first) first = false;
            }
        }


        return sb.toString();
    }

}