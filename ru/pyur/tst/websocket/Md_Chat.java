package ru.pyur.tst.websocket;

import ru.pyur.tst.HttpSession;
import ru.pyur.tst.HttpModule;
import ru.pyur.tst.tags.Div;

import java.io.File;
import java.io.FileInputStream;


public class Md_Chat extends HttpModule {

    //public static final String CONFIG_MODULE_ICON_UPD = "module_icon_upd";


    public Md_Chat(HttpSession session) { initHtml(session); }


    private class ModuleDesc {
        public int id;
        public String name;

        public ModuleDesc(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    @Override
    public void makeContent() {

        b("Тестовый websocket чат");


        Div div_msg_container = new Div();
        div_msg_container.setId("msg_container");
        div_msg_container.width(300);
        div_msg_container.height(400);
        div_msg_container.border("1px solid #000");
        div_msg_container.addStyle("margin", "0 auto");
        div_msg_container.addStyle("overflow-y", "scroll");
        div_msg_container.addStyle("background-color", "#eee");

        b(div_msg_container);

        b("<div style=\"width: 300px; height: 50px; margin: 0 auto; border: 1px solid #000;\">");
        b("<textarea id=\"msg_input\" style=\"width: 250px; height: 44px; margin: 0 auto; border: 1px solid #000;\" autofocus></textarea>");
        b("<input id=\"msg_send\" type=\"button\" style=\"width: 44px; height: 50px; vertical-align: top;\" value=\"&gt;\">");
        b("</div>");


        b("<script>");
        File script_file = new File("inline_script_websocket.js");
        try {
            FileInputStream fis = new FileInputStream(script_file);
            byte[] script = new byte[fis.available()];
            int readed = fis.read(script);
            b(new String(script));
        } catch (Exception e) { e.printStackTrace(); }
        b("</script>");

    }


}