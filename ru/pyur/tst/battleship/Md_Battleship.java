package ru.pyur.tst.battleship;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;

import java.io.File;
import java.io.FileInputStream;


public class Md_Battleship extends HtmlContent {

    //public static final String CONFIG_MODULE_ICON_UPD = "module_icon_upd";


    public Md_Battleship(HttpSession session) { initHtml(session); }


    private class ModuleDesc {
        public int id;
        public String name;

        public ModuleDesc(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    @Override
    public void makeHtml() {

        text("Морской бой");


        text("<script>");
        text("var session = 0;");
        text("var user = 0;");

        File script_file = new File("inline_script_battleship.js");
        try {
            FileInputStream fis = new FileInputStream(script_file);
            byte[] script = new byte[fis.available()];
            int readed = fis.read(script);
            text(new String(script));
        } catch (Exception e) { e.printStackTrace(); }
        text("</script>");

    }


}