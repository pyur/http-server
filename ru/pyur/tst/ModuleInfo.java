package ru.pyur.tst;


public abstract class ModuleInfo {

    private static final String NAME = "";



    public String ModuleName() { return NAME; }




    // -------------------------------- Http -------------------------------- //

    public HtmlContent getHtml(String action) { return null; }

    public ApiContent getApi(String action) { return null; }




    // -------------------------------- Websocket -------------------------------- //

    public WebsocketDispatcher getWs(String action) { return null; }


}