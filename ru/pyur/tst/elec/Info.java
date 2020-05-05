package ru.pyur.tst.elec;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;


public class Info extends ModuleInfo {

    private static final String NAME = "elec";


//    public String ModuleName() { return NAME; }
    //public int ModuleIcon() { return ICON; }


    @Override
    public HtmlContent getHtml(String action) {
        return new Html_Elec();
    }

}