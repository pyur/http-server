package ru.pyur.tst;


// https://en.wikipedia.org/wiki/HTTP_cookie

public class Cookie {

    private String name;
    private String value;

    // ---- set-cookie ---- //

    //domain
    //etc


    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getName() { return name; }

    public String getValue() { return value; }





    // ---- utilities ---- //
    // or do it automatically on parse/stringify

    public static String replacePlus(String src) {
        //todo: replace '+' > ' '
        return src;
    }
}