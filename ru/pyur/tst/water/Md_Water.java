package ru.pyur.tst.water;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;

public class Md_Water extends HtmlContent {

    public Md_Water(HttpSession session) {
        initHtml(session);
    }



    public void makeContent() {
        b("Hello from Md_Water!");
    }


}