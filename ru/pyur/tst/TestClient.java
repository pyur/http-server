package ru.pyur.tst;

public class TestClient {

    private String host;


    public TestClient() {}



    public void run() {
        TransportTcp transp = new TransportTcp();
        //host = "loga.pyur.ru";
        host = "anglesharp.azurewebsites.net";
        transp.createClient(host, cb_transport, cb_client, cb_payload);
    }



    private Transport.TransportCallback cb_transport = new Transport.TransportCallback() {
        @Override
        public byte[] onConnected() {
            System.out.println("onConnected()");
            HttpRequest rq = new HttpRequest();

            //rq.defaultGet("/", host);
            rq.defaultGet("/Chunked", host);

            return rq.stringify();
        }

        @Override
        public void onFailed() {}
    };


    private ProtocolDispatcher.CallbackHttpClient cb_client = new ProtocolDispatcher.CallbackHttpClient() {
        @Override
        public int responseReceived(HttpResponse http_response) {
            System.out.println("----------- Response -----------");
            System.out.println(http_response.code + " " + http_response.szDesc);

            for (PStr option : http_response.options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
            System.out.println("--------------------------------");

            return 0;
        }
    };



    private ProtocolDispatcher.CallbackHttpPayload cb_payload = new ProtocolDispatcher.CallbackHttpPayload() {
        @Override
        public int payloadReceived(byte[] bytes) {
            System.out.println("----------- Payload (" + bytes.length + ") ------------");
            System.out.println(new String(bytes));
            System.out.println("--------------------------------");

            return 0;
        }
    };



}