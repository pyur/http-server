package ru.pyur.tst;

public class TestClient {

    String host;


    public TestClient() {}



    public void run() {
        TransportTcp transp = new TransportTcp();
        host = "loga.pyur.ru";
        transp.createClient(host, test_t, test_1, test_2);
    }



    private Transport.TransportCallback test_t = new Transport.TransportCallback() {
        @Override
        public byte[] onConnected() {
            System.out.println("onConnected()");
            HttpRequest rq = new HttpRequest();

            rq.defaultGet("/", host);

            return rq.stringify();
        }

        @Override
        public void onFailed() {}
    };


    private ProtocolDispatcher.CallbackHttpClient test_1 = new ProtocolDispatcher.CallbackHttpClient() {
        @Override
        public int responseReceived(HttpResponse http_response) {
            System.out.println("----------- Response -----------");
            System.out.println(http_response.code + " " + http_response.szDesc);

            for (PStr option : http_response.options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
            System.out.println("--------------------------------");

            return 0;
        }
    };



    private ProtocolDispatcher.CallbackHttpPayload test_2 = new ProtocolDispatcher.CallbackHttpPayload() {
        @Override
        public int payloadReceived(byte[] bytes) {
            System.out.println("----------- Payload ------------");
            System.out.println(new String(bytes));
            System.out.println("--------------------------------");

            return 0;
        }
    };



}