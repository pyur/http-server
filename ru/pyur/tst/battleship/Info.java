package ru.pyur.tst.battleship;

import ru.pyur.tst.*;


public class Info extends ModuleInfo {

    private static final String NAME = "battleship";

    //public static final String WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION = "gsa";
    //public static final String WEBSOCKET_ACTION_GET_SPRITE_ACTION = "sa";

    //public static final String RESOURCES_PARAM_t = "t";


//x    public Info() {}

//x    public Info(HttpSession http_session) {
//x        //System.out.println("Info(Session)");
//x        setHttpSession(http_session);
//x    }


//x    public Info(WebsocketSession websocket_session) {
//x        //System.out.println("Info(Session)");
//x        setWebsocketSession(websocket_session);
//x    }


//    public String ModuleName() { return NAME; }


    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent md = null;

        if (action.isEmpty()) {
            md = new Md_Battleship();
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}




        return md;
    }



    @Override
    public WebsocketDispatcher getWs(String action) {
        WebsocketDispatcher wsm = null;

        if (action.isEmpty()) {
            wsm = new Ws_Battleship();
            //wsm.setStreams();
        }

        return wsm;
    }


}