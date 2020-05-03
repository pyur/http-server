package ru.pyur.tst.auth;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.ModuleInfo;
import ru.pyur.tst.resources.*;


public class Info extends ModuleInfo {

    private static final String NAME = "auth";

    //public static final String RESOURCES_ACTION_GENERATE_SPRITE_ACTION = "gsa";

    //public static final String RESOURCES_PARAM_t = "t";


    public Info(HttpSession session) {
        //System.out.println("Info(Session)");
        setHttpSession(session);
    }


    public String ModuleName() { return NAME; }



    @Override
    public HtmlContent getHtml() {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Html_Auth(session);
        }

//        else if (action.equals(RESOURCES_ACTION_GENERATE_SPRITE_ACTION)) {
//            hc = new Md_MakeSpriteActions(session);
//        }


        return html_content;
    }




    @Override
    public ApiContent getApi() {
        ApiContent api_content = null;

        if (action.isEmpty()) {
            api_content = new Api_Auth(session);
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}


        return api_content;
    }


}