package ru.pyur.tst;

import java.util.ArrayList;

public abstract class ModuleInfo {
    public abstract String ModuleName();
    //public abstract int ModuleIcon();

    protected Session session;

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

    protected void parseSession() {
        action = session.action;
        lsQuery = session.getQuery();
    }

//    public abstract Module module();

    public Module dispatch() { return null; }

}