package ru.pyur.tst;

import ru.pyur.tst.tags.*;
import ru.pyur.tst.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;

import static ru.pyur.tst.sample_host.resources.Md_MakeSpriteActions.CONFIG_ACTION_ICON_UPD;
import static ru.pyur.tst.sample_host.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public abstract class HtmlContent extends ContentBase {

    private ArrayList<Tag> head = new ArrayList<>();
    private ArrayList<Tag> body = new ArrayList<>();
    private String title;



    // -------- Sprite -------- //

    public static final int SPRITE_ACTION_WIDTH = 1024;
    public static final int SPRITE_ACTION_COUNT = 64;
    public static final int SPRITE_ACTION_ICON_SIZE = 16;

    public static final int SPRITE_MODULE_WIDTH = 1024;
    public static final int SPRITE_MODULE_COUNT = 32;
    public static final int SPRITE_MODULE_ICON_SIZE = 32;

    public static final int SPRITE_MODULE2_WIDTH = 1024;
    public static final int SPRITE_MODULE2_COUNT = 16;
    public static final int SPRITE_MODULE2_ICON_SIZE = 64;




    protected abstract void makeHtml() throws Exception;




    public void init(ModularHost session) {
        initCommon(session);
        setContentType("text/html; charset=utf-8");
        //setContentType("text/plain");
        // https://developer.mozilla.org/ru/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    }



    // ---- append to head: string, int, tags ---- //

    protected void headText(String text) {
        head.add(new PlainText(text));
    }

    protected void headText(int number) {
        head.add(new PlainText("" + number));
    }

    protected void headTag(Tag tag) {
        head.add(tag);
    }


    protected void title(String title) { this.title = title; }



    // ---- append to body: string, int, tags ---- //

    protected void text(String text) {
        body.add(new PlainText(text));
    }

    protected void text(int number) {
        body.add(new PlainText("" + number));
    }

    protected void tag(Tag tag) {
        body.add(tag);
    }


    protected void heading(String text) {
        Div div = new Div();
        div.addClass("heading");
        div.add(new PlainText(text));
        body.add(div);
    }




    // ------------------------ compose (compile) page ------------------------ //

    @Override
    public byte[] makeContent() {

        makeModulesBar();

        try {
            makeHtml();
        } catch (Exception e) {
            e.printStackTrace();
            Div div = new Div();
            div.addClass("exception");
            //div.add(new PlainText(e.toString()));
            div.add(new PlainText(e.getClass().getName()));
            div.add(new PlainText(": "));
            div.add(new PlainText("<span style=\"font-weight: bold;\">" + e.getMessage() + "</span>"));

            StackTraceElement[] stack_trace = e.getStackTrace();
            for (StackTraceElement ste : stack_trace) {
                //div.add(new PlainText(ste.toString() + "<br>\n"));
                Div div_line = new Div();
                div.add(div_line);
                //div_line.add(new PlainText("toString: " + ste.toString() + "<br>\n"));

                //div_line.add(new PlainText(", getClassName: " + ste.getClassName() + "<br>\n"));
                //div_line.add(new PlainText(", getMethodName: " + ste.getMethodName() + "<br>\n"));
                //div_line.add(new PlainText(", getFileName: " + ste.getFileName() + "<br>\n"));
                //div_line.add(new PlainText(", getLineNumber: " + ste.getLineNumber() + "<br>\n"));
                //div_line.add(new PlainText(", isNativeMethod: " + ste.isNativeMethod() + "<br>\n"));

                div_line.add(new PlainText("&nbsp; " + ste.getClassName() + " . "));
                div_line.add(new PlainText(ste.getMethodName() + " "));
                div_line.add(new PlainText("(" + ste.getFileName() + " : "));
                div_line.add(new PlainText(ste.getLineNumber() + ")"));
            }
            tag(div);
        }

        makeHtmlHeader();


        StringBuilder html = new StringBuilder();

        for (Tag tag : head) {
            html.append(tag.toString());
        }

        for (Tag tag : body) {
            html.append(tag.toString());
        }

        html.append("</body></html>");

        return html.toString().getBytes();
    }




    private void makeHtmlHeader() {
        headText("<!DOCTYPE html>\r\n<html><head>");
        if (title != null) {
            headText("<title>");
            headText(title);
            headText("</title>");
        }
        headText("<meta charset=\"UTF-8\">");

        headText("\r\n<style>\r\n");

//        File style_file = new File("inline_style.css");
//        try {
//            FileInputStream fis = new FileInputStream(style_file);
//            byte[] style = new byte[fis.available()];
//            int readed = fis.read(style);
//            headText(new String(style));
//        } catch (Exception e) { e.printStackTrace(); }
        try {
            byte[] style = Util.fetchFile("inline_style.css");
            headText(new String(style));
        } catch (Exception e) { e.printStackTrace(); }

        headText("\r\n</style>\r\n");


        int tsSpriteActions = configGeti(CONFIG_ACTION_ICON_UPD);
        int tsSpriteModules = configGeti(CONFIG_MODULE_ICON_UPD);


        headText("\r\n<script>\r\n");
        headText("var tsSpriteActions = ");
        headText(tsSpriteActions);
        headText(";\r\n");
        headText("var tsSpriteModules = ");
        headText(tsSpriteModules);
        headText(";\r\n");

//        File script_file = new File("inline_script.js");
//        try {
//            FileInputStream fis = new FileInputStream(script_file);
//            byte[] script = new byte[fis.available()];
//            int readed = fis.read(script);
//            headText(new String(script));
//        } catch (Exception e) { e.printStackTrace(); }
        try {
            byte[] script = Util.fetchFile("inline_script.js");
            headText(new String(script));
        } catch (Exception e) { e.printStackTrace(); }

        headText("\r\n</script>\r\n");


        headText("</head><body>");
    }




    private void makeModulesBar() {
        Div div_modules_bar = new Div();
        tag(div_modules_bar);
        div_modules_bar.addClass("modules_bar");

        // ---- todo: menu = auth->get_menu()
        ModuleBar module_bar = new ModuleBar();

        for (ModuleBarItem mbi : module_bar.getModules()) {
            A mod = new A();
            div_modules_bar.add(mod);
            ModuleUrl link = new ModuleUrl();
            link.setModule(mbi.name);
            mod.setHref(link);

            Div div_icon = new Div();
            mod.add(div_icon);
            int x = ((mbi.id - 1) % SPRITE_MODULE_COUNT) * SPRITE_MODULE_ICON_SIZE;
            int y = ((mbi.id - 1) / SPRITE_MODULE_COUNT) * SPRITE_MODULE_ICON_SIZE;

            div_icon.addStyle("background-position", ((x == 0) ? "0" : "-" + x + "px") + " " + ((y == 0) ? "0" : "-" + y + "px") );
            div_icon.setUnselectable();


            Div div_desc = new Div();
            mod.add(div_desc);
            String desc = mbi.descb.isEmpty() ? mbi.desc : mbi.descb;
            div_desc.text(desc);
        }

    }


    private class ModuleBarItem {
        public int id;
        public String name;
        public String perm;
        public String desc;
        public String descb;
        public int pos;
        public int noauth;
        public int auth;

        public ModuleBarItem(int id, String name, String perm, String desc, String descb, int pos, int noauth, int auth) {
            this.id = id;
            this.name = name;
            this.perm = perm;
            this.desc = desc;
            this.descb = descb;
            this.pos = pos;
            this.noauth = noauth;
            this.auth = auth;
        }
    }


    private class ModuleBar {
        public ArrayList<ModuleBarItem> modules = new ArrayList<>();

        public ModuleBar() {
            Statement stmt = getConfigStatement();

            String query = "SELECT `id`, `name`, `perm`, `desc`, `descb`, `pos`, `noauth`, `auth` FROM `module` ORDER BY `pos`, `desc`";

            try {
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    String perm = rs.getString(3);
                    String desc = rs.getString(4);
                    String descb = rs.getString(5);
                    int pos = rs.getInt(6);
                    int noauth = rs.getInt(7);
                    int auth = rs.getInt(8);

                    modules.add(new ModuleBarItem(id, name, perm, desc, descb, pos, noauth, auth));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        public ArrayList<ModuleBarItem> getModules() { return modules; }
    }


}