package ru.pyur.tst.sample_host.battleship;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.util.Util;


public class Html_Battleship extends HtmlContent {

    @Override
    public void makeHtml() {
        heading("Морской бой");

        add("<script>");
        add("var session = 0;");
        add("var user = 0;");

        try {
            byte[] script = Util.fetchFile("inline_script_battleship.js");
            add(new String(script));
        } catch (Exception e) { e.printStackTrace(); }

        add("</script>");
    }


}