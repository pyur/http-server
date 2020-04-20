package ru.pyur.tst;

import java.io.InputStream;


public class WebsocketInputStream extends InputStream {

    InputStream is;


    public WebsocketInputStream(InputStream is) {
        this.is = is;
    }


    @Override
    public int read() {

        return 0;
    }


    @Override
    public int read(byte b[], int off, int len) {


        return 0;
    }



    //@Override
    //public long skip(long n) {
    //}



    @Override
    public int available() {
        return 0;
    }


    @Override
    public void close() {}


}