package ru.pyur.tst.elec;

import ru.pyur.tst.Module;


public class Info {

    private static final String NAME = "elec";
    //private static final int ICON = R.mipmap.elec;
    //private static final Fragment UI = "Fm_Elec";


    public String ModuleName() { return NAME; }
    //public int ModuleIcon() { return ICON; }


    public Module module() {
        return new Md_Elec(null);
    }

}