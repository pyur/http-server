package ru.pyur.tst.websocket;

import ru.pyur.tst.*;


public class Info extends ModuleInfo {

    private static final String NAME = "ws";

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


    public HttpModule dispatch() {
        HttpModule md = null;

        if (action.isEmpty()) {
            md = new Md_Chat(session);
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}




        return md;
    }



    public WebsocketModule getWs() {
        WebsocketModule wsm = null;

        if (action.isEmpty()) {
            wsm = new Ws_Chat();
            //wsm.setStreams();
        }

        return wsm;
    }


}