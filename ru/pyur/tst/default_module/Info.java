package ru.pyur.tst.default_module;

import ru.pyur.tst.*;


public class Info extends ModuleInfo {

    private static final String NAME = "default";

    //public static final String WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION = "gsa";
    //public static final String WEBSOCKET_ACTION_GET_SPRITE_ACTION = "sa";

    //public static final String RESOURCES_PARAM_t = "t";


    public Info() {}

    public Info(HttpSession http_session) {
        //System.out.println("Info(Session)");
        setHttpSession(http_session);
    }


    public Info(WebsocketSession websocket_session) {
        //System.out.println("Info(Session)");
        setWebsocketSession(websocket_session);
    }


    public String ModuleName() { return NAME; }




    @Override
    public HtmlContent getHtml() {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Html_Default(session);
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}


        return html_content;
    }



    @Override
    public ApiContent getApi() {
        ApiContent api_content = null;

        if (action.isEmpty()) {
            api_content = new Api_Default(session);
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}


        return api_content;
    }



//    @Override
//    public WebsocketModule getWs() {
//        WebsocketModule websocket_module = null;
//
//        if (action.isEmpty()) {
//            websocket_module = new Ws_Default();
//            //wsm.setStreams();
//        }
//
//        return websocket_module;
//    }


}