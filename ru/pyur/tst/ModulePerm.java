package ru.pyur.tst;

import ru.pyur.tst.util.Util;

import java.util.ArrayList;

public class ModulePerm {
    public int id;
    public String name;
    //public String perm;
    public String[] perms;
    public String desc;
    public String descb;
    public int pos;
    public int noauth;
    public int auth;

    public ModulePerm(int id, String name, String perm, String desc, String descb, int pos, int noauth, int auth) {
        this.id = id;
        this.name = name;
        //this.perm = perm;
        perms = Util.explode(",", perm);
        this.desc = desc;
        this.descb = (descb == null) ? "" : descb;
        this.pos = pos;
        this.noauth = noauth;
        this.auth = auth;
    }


}