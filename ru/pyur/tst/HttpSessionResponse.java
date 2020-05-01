package ru.pyur.tst;

public class HttpSessionResponse extends HttpResponse {


    public HttpSessionResponse() {
        this(200);
    }


    public HttpSessionResponse(int code) {
        //this.code = code;
        setVersion(HTTP_VERSION_1_0);
        setCode(code);

        addOption("Server", "ReferenceHttpServer/1.0 (Java)");
        setConnectionClose();
    }



}