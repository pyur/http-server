package ru.pyur.tst.tags;


public class ActionButton {

    public String icon;
    public String description;
    public String module;
    public String action;

    private int mode;
    private final int MODE_LOCATION = 0;
    private final int MODE_AJAX = 1;
    private final int MODE_CODE = 2;



    public ActionButton(String icon) {
        this.icon = icon;
    }


    public ActionButton(String icon, String description) {
        this.icon = icon;
        this.description = description;
//x        this.action = action;
    }


    public void setLocation(String module, String action) {
        this.module = module;
        this.action = action;
        mode = MODE_LOCATION;
    }


    public String getFunction() {
        String function = "";

        switch (mode) {
            case MODE_LOCATION:
                function = "window.location = '/" + module + "/";
                if (action != null)  function += action + "/";
                function += "?id=' + row_id";
                break;

            case MODE_AJAX:
                break;

            case MODE_CODE:
                break;
        }

        return function;
    }


}
