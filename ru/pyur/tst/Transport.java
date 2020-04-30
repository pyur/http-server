package ru.pyur.tst;

import java.io.OutputStream;

public class Transport {


    CallbackTransportEvents callback_transport_events;

    public interface CallbackTransportEvents {
        byte[] onConnected();
        void onFailed();
    }


    public interface CallbackTransportControl {
        int send(byte[] bytes);
        //OutputStream getOutputStream();
    }


}