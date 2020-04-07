package ru.pyur.tst;

import ru.pyur.tst.tags.Div;

public class Module {

    protected Session session;

    private StringBuilder body;


    public Module() {
        body = new StringBuilder();
    }


    //public Module(Session session) {
    //    this.session = session;
    //}


    protected void prepare() {}


    // ---- string, int ---- //
    protected void b(String s) {
        body.append(s);
    }

    protected void b(int i) {
        body.append(i);
    }


    // ---- tags ---- //
    protected void b(Div div) {
        body.append(div.render());
    }


    public String render() {
        //System.out.println(body.toString());
        return body.toString();
    }



}