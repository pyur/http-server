package ru.pyur.tst.water;

import ru.pyur.tst.HttpModule;
import ru.pyur.tst.HttpSession;

public class Md_Water extends HttpModule {

    public Md_Water(HttpSession session) {
        initHtml(session);
    }



    public void makeContent() {
        b("Hello from Md_Water!");
    }


}