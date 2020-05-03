package ru.pyur.tst;


// https://en.wikipedia.org/wiki/HTTP_cookie
// https://tools.ietf.org/html/rfc6265

public class Cookie {

    private String name;
    private String value;

    // ---- set-cookie ---- //

    private String path;
    private String domain;
    private int expires;
    //max_age - not required

    private boolean secure;
    private boolean http_only;

    // Set-Cookie: SID=31d4d96e407aad42; Path=/; Domain=example.com
    // Set-Cookie: SID=31d4d96e407aad42; Path=/; Secure; HttpOnly
    // Set-Cookie: t=NjU1MDUsMSwxNTg4NTA3MDY5.UR5RTRA6PTeUgzqXdqKsmjyq6bfGMepGaFJAuhXEcws%3D; expires=Mon, 07-Apr-2025 11:57:49 GMT; Max-Age=155520000; path=/
    // Set-Cookie: lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT

    // remove (expiration date in the past)
    // Set-Cookie: lang=; Expires=Sun, 06 Nov 1994 08:49:37 GMT


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