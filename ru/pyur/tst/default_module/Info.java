package ru.pyur.tst.default_module;

import ru.pyur.tst.*;


public class Info extends ModuleInfo {

    private static final String NAME = "default";

    //public static final String WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION = "gsa";
    //public static final String WEBSOCKET_ACTION_GET_SPRITE_ACTION = "sa";

    //public static final String RESOURCES_PARAM_t = "t";


//x    public Info() {}

//x    public Info(HttpSession http_session) {
        //System.out.println("Info(Session)");
//x        setHttpSession(http_session);
//x    }


//x    public Info(WebsocketSession websocket_session) {
        //System.out.println("Info(Session)");
//x        setWebsocketSession(websocket_session);
//x    }


//x    public String ModuleName() { return NAME; }




    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Html_Default();
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}


        return html_content;
    }



    @Override
    public ApiContent getApi(String action) {
        ApiContent api_content = null;

        if (action.isEmpty()) {
            api_content = new Api_Default();
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}


        return api_content;
    }



}