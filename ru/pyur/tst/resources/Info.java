package ru.pyur.tst.resources;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.ModuleInfo;


public class Info extends ModuleInfo {

    private static final String NAME = "res";

    public static final String RESOURCES_ACTION_GENERATE_SPRITE_ACTION = "gsa";
    public static final String RESOURCES_ACTION_GET_SPRITE_ACTION = "sa";
    public static final String RESOURCES_ACTION_GENERATE_SPRITE_MODULE = "gsm";
    public static final String RESOURCES_ACTION_GET_SPRITE_MODULE = "sm";

    public static final String RESOURCES_PARAM_t = "t";


    public Info(HttpSession session) {
        //System.out.println("Info(Session)");
        setHttpSession(session);
    }


    public String ModuleName() { return NAME; }



    @Override
    public HtmlContent getHtml() {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Md_ResList(session);
        }

        else if (action.equals(RESOURCES_ACTION_GENERATE_SPRITE_ACTION)) {
            html_content = new Md_MakeSpriteActions(session);
        }


        else if (action.equals(RESOURCES_ACTION_GENERATE_SPRITE_MODULE)) {
            html_content = new Md_MakeSpriteModules(session);
        }


        return html_content;
    }



    @Override
    public ApiContent getApi() {
        ApiContent api_content = null;

        if (action.isEmpty()) {
            //api_content = new Api_Default(session);
        }

        else if (action.equals(RESOURCES_ACTION_GET_SPRITE_ACTION)) {
            api_content = new Api_GetSpriteActions(session);
        }

        else if (action.equals(RESOURCES_ACTION_GET_SPRITE_MODULE)) {
            api_content = new Api_GetSpriteModules(session);
        }


        return api_content;
    }


}