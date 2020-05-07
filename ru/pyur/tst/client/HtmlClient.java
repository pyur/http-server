package ru.pyur.tst.client;

import ru.pyur.tst.HttpResponse;
import ru.pyur.tst.Url;

public class HtmlClient extends HttpClient {


    public HtmlClient(Url url) {
        init(url);
    }


    @Override
    public boolean onHeader(HttpResponse response_header) {
        System.out.println("onHeader()");

        return false;
    }



    @Override
    public void onPayload(byte[] payload) {
        System.out.println("onPayload()");

        System.out.println(new String(payload));
    }






}