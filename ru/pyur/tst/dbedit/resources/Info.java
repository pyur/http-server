package ru.pyur.tst.dbedit.resources;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;


public class Info extends ModuleInfo {

    private static final String NAME = "res";

    public static final String RESOURCES_ACTION_GENERATE_SPRITE_ACTION = "gsa";
    public static final String RESOURCES_ACTION_GENERATE_SPRITE_MODULE = "gsm";

    public static final String RESOURCES_PARAM_t = "t";


//    public String ModuleName() { return NAME; }



    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Md_ResList();
        }

        else if (action.equals(RESOURCES_ACTION_GENERATE_SPRITE_ACTION)) {
            html_content = new Md_MakeSpriteActions();
        }

        else if (action.equals(RESOURCES_ACTION_GENERATE_SPRITE_MODULE)) {
            html_content = new Md_MakeSpriteModules();
        }

        return html_content;
    }



    @Override
    public ApiContent getApi(String action) {
        ApiContent api_content = null;

        if (action.isEmpty()) {
            api_content = new Api_GetResource();
        }


        return api_content;
    }


}