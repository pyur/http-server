package ru.pyur.tst;

import java.io.OutputStream;


public class WebsocketOutputStream extends OutputStream {

    OutputStream os;


    public WebsocketOutputStream(OutputStream os) {
        this.os = os;
    }


    @Override
    public void write(int a) {

    }


    @Override
    public void write(byte b[], int off, int len) {

    }


    @Override
    public void flush() {

    }



    @Override
    public void close() {

    }


}