package ru.pyur.tst.websocket;

import ru.pyur.tst.*;


public class Info extends ModuleInfo {

    private static final String NAME = "ws";

    //public static final String WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION = "gsa";
    //public static final String WEBSOCKET_ACTION_GET_SPRITE_ACTION = "sa";

    //public static final String RESOURCES_PARAM_t = "t";


//    public String ModuleName() { return NAME; }


    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Html_Chat();
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}

        return html_content;
    }



    @Override
    public WebsocketDispatcher getWs(String action) {
        WebsocketDispatcher wsd = null;

        if (action.isEmpty()) {
            wsd = new Ws_Chat();
        }

        return wsd;
    }


}