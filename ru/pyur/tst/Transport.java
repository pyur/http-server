package ru.pyur.tst;


import java.io.OutputStream;

public class Transport {


    CallbackTransportEvents callback_transport_events;

    public interface CallbackTransportEvents {
//        byte[] onConnected();
        void onConnected(OutputStream output_stream);
        void onFailed();
    }


//    public interface CallbackTransportControl {
//        int send(byte[] bytes);
//        //OutputStream getOutputStream();
//    }


}