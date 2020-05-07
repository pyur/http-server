package ru.pyur.tst.sample_host.auth;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;


public class Info extends ModuleInfo {

    private static final String NAME = "auth";

    //public static final String RESOURCES_ACTION_GENERATE_SPRITE_ACTION = "gsa";

    //public static final String RESOURCES_PARAM_t = "t";


//    public String ModuleName() { return NAME; }



    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Html_Auth();
        }

        return html_content;
    }




    @Override
    public ApiContent getApi(String action) {
        ApiContent api_content = null;

        if (action.isEmpty()) {
            api_content = new Api_Auth();
        }

        return api_content;
    }


}