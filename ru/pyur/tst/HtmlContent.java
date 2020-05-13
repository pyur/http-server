package ru.pyur.tst;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.DbFetcher;
import ru.pyur.tst.db.FetchArray;
import ru.pyur.tst.json.Json;
import ru.pyur.tst.tags.*;
import ru.pyur.tst.util.PStr;
import ru.pyur.tst.util.Util;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;



public abstract class HtmlContent extends ContentBase {

    private ArrayList<Tag> head = new ArrayList<>();
    private ArrayList<Tag> body = new ArrayList<>();
    private String title;

    private ActionIconManager action_icon_manager;
    private ArrayList<ActionBarItem> actions = new ArrayList<>();



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




    public void init(ModularHost host) {
        initCommon(host);
        setContentType("text/html; charset=utf-8");
        //setContentType("text/plain");
        // https://developer.mozilla.org/ru/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
        action_icon_manager = new ActionIconManager(getConfigDb());
    }



    // ---- append to head: string, int, tags ---- //

    protected void addHead(String text) { head.add(new PlainText(text)); }

    protected void addHead(int number) { head.add(new PlainText("" + number)); }

    protected void addHead(Tag tag) { head.add(tag); }


    //protected void appendTitle(String title) { this.title += title; }
    protected void setTitle(String title) { this.title = title; }



    // -------- actions --------------------------------

    protected void addAction(ActionBarItem action) {
        actions.add(action);
    }


    protected void addActionLink(String text, ModuleUrl url) {
        addActionLink(text, url, null);
    }


    protected void addActionLink(String text, ModuleUrl url, String icon) {
        ActionBarItem action = new ActionBarItem(text, url, icon);
        actions.add(action);
    }



    // ---- append to body: string, int, tags ---- //

    protected void add(String text) { body.add(new PlainText(text)); }

    protected void add(int number) { body.add(new PlainText("" + number)); }

    protected void add(Tag tag) { if (tag != null)  body.add(tag); }

    protected void add(ArrayList<Tag> tags) { for (Tag tag : tags) { body.add(tag); } }



    // -------- predefined elements --------------------------------

    protected void heading(String text) {
        Div div = new Div();
        div.addClass("heading");
        div.add(new PlainText(text));
        body.add(div);
    }




    // ------------------------ compose (compile) page ------------------------ //

    @Override
    public byte[] makeContent() {

        String modules_bar = makeModulesBar();

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

                div_line.add(new PlainText("&nbsp; " + ste.getClassName() + " . "));
                div_line.add(new PlainText(ste.getMethodName() + " "));
                div_line.add(new PlainText("(" + ste.getFileName() + " : "));
                div_line.add(new PlainText(ste.getLineNumber() + ")"));
            }
            add(div);
        }

        makeHtmlHeader();


        StringBuilder html = new StringBuilder();

        for (Tag tag : head) {
            html.append(tag.toString());
        }

        html.append(modules_bar);

        if (actions.size() != 0)  html.append(makeActionsBar());

        for (Tag tag : body) {
            html.append(tag.toString());
        }

        html.append("</body></html>");

        return html.toString().getBytes();
    }




    private void makeHtmlHeader() {
        addHead("<!DOCTYPE html>\r\n<html><head>");
        if (title != null) {
            addHead("<title>");
            addHead(title);
            addHead("</title>");
        }
        addHead("<meta charset=\"UTF-8\">");

        addHead("\r\n<style>\r\n");

        try {
            byte[] style = Util.fetchFile(getHostDir() + "/inline_style.css");
            addHead(new String(style));
        } catch (Exception e) { e.printStackTrace(); }

        addHead("\r\n</style>\r\n");


        DbFetch cache_ts = new DbFetch(getConfigDb());
        cache_ts.table("res_ts");
        cache_ts.col(new String[]{"name", "ts"});

        Json res_ts = new Json();

        try {
            FetchArray fetch_array = cache_ts.fetchArray();

            while (fetch_array.available()) {
                String name = fetch_array.getString("name");
                int ts = fetch_array.getInt("ts");
                res_ts.add(name, ts);
            }

            File file_script = new File(getHostDir() + "/script.js");
            if (file_script.exists()) {
                res_ts.add("script", (int)(file_script.lastModified() / 1000) );
            }

        } catch (Exception e) { e.printStackTrace(); }


        addHead("\r\n<script>\r\n");
        addHead("var cached_ts = ");
        addHead(res_ts.stringify());
        addHead(";\r\n");

        try {
            byte[] script = Util.fetchFile(getHostDir() + "/inline_script.js");
            addHead(new String(script));
        } catch (Exception e) { e.printStackTrace(); }

        addHead("\r\n</script>\r\n");


        addHead("</head><body>");
    }




    // -------------------------------- Modules bar -------------------------------- //

    private String makeModulesBar() {
        Div div_modules_bar = new Div();
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
            div_desc.add(desc);
        }

        return div_modules_bar.toString();
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




    // -------------------------------- Actions bar -------------------------------- //

    private String makeActionsBar() {
        Div div_actions_bar = new Div();
        div_actions_bar.addClass("actions_bar");

        for (ActionBarItem action : actions) {
            div_actions_bar.add(action.make());
        }

        return div_actions_bar.toString();
    }



    protected class ActionBarItem {
        private String text;
        //private String href;
        private ModuleUrl url;
        //javascript onclick
        //? confirmation (for delete)
        //! on new tab (target="_blank")
        //context submenu
        private String icon;


        public ActionBarItem() {}

        public ActionBarItem(String text, ModuleUrl url, String icon) {
            this.text = text;
            this.url = url;
            this.icon = icon;
        }


        public Tag make() {
            Tag root_tag = null;

            if (url != null) {
                A link = new A();
                root_tag = link;
//                link.addClass("action_bar");
                //ModuleUrl url = new ModuleUrl();
                //url.setModule(getModule());
                //url.setAction("from_private_var");
                //add params
                link.setHref(url);

                // ---- icon ---- //
                ActionIcon action_icon = null;
                if (icon != null) {
                    try {
                        action_icon = action_icon_manager.getActionIcon(icon);
                    } catch (Exception e) { e.printStackTrace(); }
                }

                Div div_icon = new Div();
                link.add(div_icon);

                if (action_icon != null) {
                    div_icon.addStyle("background-position", action_icon.getSpriteOffset());
                    //unselectable
                }
                else {
//                    Div div_icon = new Div();
//                    link.add(div_icon);
                    div_icon.addClass("no_icon");
                }

                // ---- text ---- //
                Div div_text = new Div();
                link.add(div_text);
                div_text.add(text);

            }

            return root_tag;
        }


    }

}