package ru.pyur.tst;


public class Transport {


    CallbackTransportEvents callback_transport_events;

    public interface CallbackTransportEvents {
        byte[] onConnected();
        void onFailed();
    }


//    public interface CallbackTransportControl {
//        int send(byte[] bytes);
//        //OutputStream getOutputStream();
//    }


}