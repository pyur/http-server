package ru.pyur.tst.sample_host.water;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;
import ru.pyur.tst.sample_host.elec.Html_Elec;


public class Info extends ModuleInfo {

    private static final String NAME = "water";


//    public String ModuleName() { return NAME; }


    @Override
    public HtmlContent getHtml(String action) {
        return new Html_Elec();
    }

}