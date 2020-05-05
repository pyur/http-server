package ru.pyur.tst.battleship;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.Util;

import java.io.File;
import java.io.FileInputStream;


public class Html_Battleship extends HtmlContent {

    @Override
    public void makeHtml() {
        heading("Морской бой");

        text("<script>");
        text("var session = 0;");
        text("var user = 0;");

        try {
            byte[] script = Util.fetchFile("inline_script_battleship.js");
            text(new String(script));
        } catch (Exception e) { e.printStackTrace(); }

        text("</script>");
    }


}