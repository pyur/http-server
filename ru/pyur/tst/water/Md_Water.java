package ru.pyur.tst.water;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;

public class Md_Water extends Module {

    public Md_Water(Session session) {
        this.session = session;
    }


    public void prepare() {
        b("Hello from Md_Water!");
    }


}