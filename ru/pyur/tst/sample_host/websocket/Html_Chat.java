package ru.pyur.tst.sample_host.websocket;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.util.Util;
import ru.pyur.tst.tags.Div;


public class Html_Chat extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {
        byte[] script = Util.fetchFile("inline_script_websocket.js");

        heading("Тестовый websocket чат");


        Div div_msg_container = new Div();
        div_msg_container.setId("msg_container");
        div_msg_container.width(300);
        div_msg_container.height(400);
        div_msg_container.border("1px solid #000");
        div_msg_container.addStyle("margin", "0 auto");
        div_msg_container.addStyle("overflow-y", "scroll");
        div_msg_container.addStyle("background-color", "#eee");

        add(div_msg_container);

        add("<div style=\"width: 300px; height: 50px; margin: 0 auto; border: 1px solid #000;\">");
        add("<textarea id=\"msg_input\" style=\"width: 250px; height: 44px; margin: 0 auto; border: 1px solid #000;\" autofocus></textarea>");
        add("<input id=\"msg_send\" type=\"button\" style=\"width: 44px; height: 50px; vertical-align: top;\" value=\"&gt;\">");
        add("</div>");


        add("<script>");

        add(new String(script));

        add("</script>");

    }


}