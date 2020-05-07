package ru.pyur.tst.sample_host.battleship;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.util.Util;


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