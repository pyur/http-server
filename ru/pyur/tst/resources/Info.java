package ru.pyur.tst.resources;

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


    public HtmlContent getHtml() {
        HtmlContent md = null;

        if (action.isEmpty()) {
            md = new Md_ResList(session);
        }

        else if (action.equals(RESOURCES_ACTION_GENERATE_SPRITE_ACTION)) {
            md = new Md_MakeSpriteActions(session);
        }

        else if (action.equals(RESOURCES_ACTION_GET_SPRITE_ACTION)) {
            md = new Md_GetSpriteActions(session);
        }

        else if (action.equals(RESOURCES_ACTION_GENERATE_SPRITE_MODULE)) {
            md = new Md_MakeSpriteModules(session);
        }

        else if (action.equals(RESOURCES_ACTION_GET_SPRITE_MODULE)) {
            md = new Md_GetSpriteModules(session);
        }



        return md;
    }


}