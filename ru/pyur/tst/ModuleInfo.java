package ru.pyur.tst;


import java.sql.Connection;

public abstract class ModuleInfo {

    private static final String NAME = "";



    public String ModuleName() { return NAME; }


    // abstract void modulePre();
    // abstract void moduleAfter();

    //protected Connection getModuleDb() { return null; }





    // -------------------------------- Http -------------------------------- //

    public HtmlContent getHtml(String action) { return null; }

    public ApiContent getApi(String action) { return null; }




    // -------------------------------- Websocket -------------------------------- //

    public WebsocketDispatcher getWs(String action) { return null; }


}