package ru.pyur.tst;

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



    protected void b(String s) {
        body.append(s);
    }

    protected void b(int i) {
        body.append(i);
    }


    public String render() {
        //System.out.println(body.toString());
        return body.toString();
    }



}