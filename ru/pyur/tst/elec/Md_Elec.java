package ru.pyur.tst.elec;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;

public class Md_Elec extends Module {


    public Md_Elec(Session session) {
        this.session = session;
    }


    public void prepare() {
        b("Hello from Md_Elec!");
    }


}