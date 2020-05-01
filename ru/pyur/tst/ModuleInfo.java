package ru.pyur.tst;

import java.util.ArrayList;


public abstract class ModuleInfo {
    public abstract String ModuleName();
    //public abstract int ModuleIcon();

    protected HttpSession session;  // todo: passing whole session seems redundant, passing request header seems enough
    protected WebsocketSession websocket_session;

    protected String action;
    private ArrayList<PStr> lsQuery;


//    private String permissions = "";
//
//    public void setPermissions(String permissions) { this.permissions = permissions; }
//    public String getPermissions() { return permissions; }

    //public ModuleInfo() {
    //    System.out.println("ModuleInfo()");
    //}

    //public ModuleInfo(Session session) {
    //    System.out.println("ModuleInfo(Session)");
    //    //this.session = session;
    //}



    // -------------------------------- Http -------------------------------- //

    public void setHttpSession(HttpSession http_session) {
        this.session = http_session;
        action = http_session.getAction();
        lsQuery = http_session.getQuery();
    }


    public HtmlContent getHtml() { return null; }

    public ApiContent getApi() { return null; }




    // -------------------------------- Websocket -------------------------------- //

    public void setWebsocketSession(WebsocketSession websocket_session) {
        this.websocket_session = websocket_session;
        action = websocket_session.action;
        lsQuery = websocket_session.getQuery();
    }


    public WebsocketModule getWs() { return null; }



    DummyProtCallback mod_callback;

    public DummyModCallback setGetWebsocketCallback(DummyProtCallback dummy_callback) {
        mod_callback = dummy_callback;

        return websocketDispatcher;
    }



    private DummyModCallback websocketDispatcher = new DummyModCallback() {
        @Override
        public void receivedString(String str) {
            receivedString2(str);
        }

        @Override
        public void receivedBinary(byte[] data) {
            receivedBinary2(data);
        }
    };


    // ---- for Override ---- //
    protected void receivedString2(String str) {}

    protected void receivedBinary2(byte[] data) {}


    // ---- for back ---- //
    protected void sendString(String str) {
        if (mod_callback != null)  mod_callback.sendString(str);
    }

}