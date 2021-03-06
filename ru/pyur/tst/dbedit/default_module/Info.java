package ru.pyur.tst.dbedit.default_module;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;


public class Info extends ModuleInfo {

    private static final String NAME = "default";


    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Html_Default();
        }

        return html_content;
    }



    @Override
    public ApiContent getApi(String action) {
        ApiContent api_content = null;

        if (action.isEmpty()) {
            api_content = new Api_Default();
        }

        return api_content;
    }


}