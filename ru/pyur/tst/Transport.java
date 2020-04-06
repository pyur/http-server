package ru.pyur.tst;

public class Transport {


    TransportCallback tr_callback;

    public interface TransportCallback {
        byte[] onConnected();
        void onFailed();
    }


    public interface Callback {
        int send(byte[] bytes);
    }


}