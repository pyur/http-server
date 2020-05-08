package ru.pyur.tst;

public class HttpSessionResponse extends HttpResponse {


    public HttpSessionResponse() {
        this(200);
    }


    public HttpSessionResponse(int code) {
        setVersion(HTTP_VERSION_1_1);
        setCode(code);

        addOption("Server", "ReferenceHttpServer/1.0 (Java)");
        //setConnectionClose();  // not sure, because websocket answers
    }



}