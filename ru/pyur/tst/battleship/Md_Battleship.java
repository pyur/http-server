package ru.pyur.tst.battleship;

import ru.pyur.tst.HttpModule;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.tags.Div;

import java.io.File;
import java.io.FileInputStream;


public class Md_Battleship extends HttpModule {

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
    public void makeContent() {

        b("Морской бой");


        b("<script>");
        b("var session = 0;");
        b("var user = 0;");

        File script_file = new File("inline_script_battleship.js");
        try {
            FileInputStream fis = new FileInputStream(script_file);
            byte[] script = new byte[fis.available()];
            int readed = fis.read(script);
            b(new String(script));
        } catch (Exception e) { e.printStackTrace(); }
        b("</script>");

    }


}