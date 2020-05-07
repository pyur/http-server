package ru.pyur.tst.test;

import ru.pyur.tst.Cookie;
import ru.pyur.tst.HttpResponse;
import ru.pyur.tst.Url;
import ru.pyur.tst.client.HtmlClient;
import ru.pyur.tst.client.HttpClient;


public class SampleHtmlClient extends HttpClient {


    public SampleHtmlClient() {
        init(new Url("https://anglesharp.azurewebsites.net/Chunked"));
    }



    @Override
    public boolean onHeader(HttpResponse response_header) {
        System.out.println("onHeader()");

        return false;
    }



    @Override
    public void onSetCookie(Cookie cookie) {
        System.out.println("onSetCookie()");
        System.out.println(cookie.toString());
    }



    @Override
    public void onPayload(byte[] payload) {
        System.out.println("onPayload()");

        System.out.println(new String(payload));
    }





}