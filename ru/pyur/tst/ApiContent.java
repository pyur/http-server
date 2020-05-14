package ru.pyur.tst;

import ru.pyur.tst.json.Json;


public abstract class ApiContent extends ContentBase {

    private Json request;
    private Json answer;  // response




    protected abstract void makeJson() throws Exception;




    public void init(ModularHost host) {
        initCommon(host);
        //setContentType("application/json; charset=utf-8");  // utf-8 redundant
        setContentType("application/json");
        //setContentType("application/javascript");  // for JSON-P
    }




    // ---------------- get from json request ---------------- //

    protected Json getObject(String key) throws Exception {
        return request.getNode(key);
    }


    protected String getString(String key) throws Exception {
        return request.getString(key);
    }


    protected int getInt(String key) throws Exception {
        return request.getInt(key);
    }




    // ---- append to json answer ---- //
//    protected void putArray(Json array) throws Exception {
//        if (!array.isArray()) {
//            //return;
//            throw new Exception("type not array");
//        }
//
//        try {
//            answer.addToObject(array);
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//
//
//
//    protected void putObject(Json object) throws Exception {
//        if (!object.isObject()) {
//            //return;
//            throw new Exception("type not object");
//        }
//
//        try {
//            answer.addToObject(object);
//        } catch (Exception e) { e.printStackTrace(); }
//    }



    protected void put(String key, String value) {
        try {
            answer.add(key, value);
        } catch (Exception e) { e.printStackTrace(); }
    }



    protected void put(Json object) {
        try {
            answer.addToObject(object);
        } catch (Exception e) { e.printStackTrace(); }
    }




    // ------------------------ compose (compile) answer json ------------------------ //

    @Override
    public byte[] makeContent() {
        request = new Json();
        answer = new Json();

        byte[] payload = host_session.getPayload();
        if (payload == null) {
            put("result", "error");
            put("error", "request is empty");
            setCode(406);
            return answer.stringify().getBytes();
        }

        // -------- parse request -------- //
        //System.out.println("host_session: " + host_session);
        System.out.println(new String(payload));
        //System.out.println("request: " + request);

        try {
            request.parse(new String(payload));
        } catch (Exception e) {
            System.err.println("request parse failed.");
            e.printStackTrace();
            put("result", "error");
            put("error", "request parse failed");
            setCode(406);
            return answer.stringify().getBytes();
        }
        System.out.println("request parse ok.");


        // -------- make answer -------- //

        try {
            makeJson();
        } catch (Exception e) {
            e.printStackTrace();
            setCode(406);

            // ---- append to 'answer' e.toString()); ---- //
            // maybe reset answer before adding

            try {
                Json exc = new Json("exception");

                //exc.add("result", "error");
                //exc.add("error", "makeJson failed");
                put("error", "makeJson failed");

                exc.add("class", e.getClass().getName());
                exc.add("desc", e.getMessage());

                StackTraceElement[] stack_trace = e.getStackTrace();
                Json jst = new Json("stack_trace").array();
                for (StackTraceElement ste : stack_trace) {
                    Json stl = new Json();  // anonymous object
                    stl.add("class", ste.getClassName());
                    stl.add("method", ste.getMethodName());
                    stl.add("file", ste.getFileName());
                    stl.add("line", ste.getLineNumber());
                    jst.addToArray(stl);
                }
                //putArray(jst);
                exc.addToObject(jst);
//x                putObject(exc);
                put(exc);
            } catch (Exception e2) { e2.printStackTrace(); put("exception", "failed"); }
        }

        return answer.stringify().getBytes();
    }


}