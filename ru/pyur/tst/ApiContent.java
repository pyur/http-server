package ru.pyur.tst;

import ru.pyur.tst.json.Json;


public abstract class ApiContent extends ContentBase {

    private Json request;
    private Json answer;  // response




    protected abstract void makeJson() throws Exception;




    @Override
    public byte[] makeContent() {
        request = new Json();
        try {
            request.parse(new String(session.getPayload()));
        } catch (Exception e) { e.printStackTrace(); }

        answer = new Json();

        byte[] content = makeCont();  // todo: move function here, inline

        return content;
    }





    // -------------------------------- Api -------------------------------- //

//    protected void init(HttpSession session) {
    protected void setSession(HttpSession session) {
        initCommon(session);
        //setContentType("application/json; charset=utf-8");  // utf-8 redundant
        setContentType("application/json");
        //setContentType("application/javascript");  // for JSON-P
    }




    // ---- get from json request ---- //
    protected String getString(String key) throws Exception {
        return request.getString(key);
    }


    protected int getInt(String key) throws Exception {
        return request.getInt(key);
    }




    // ---- append to json answer ---- //
    protected void putArray(Json array) {
        if (!array.isArray())  return;  // throw new Exception("type not array");

        try {
            answer.addToObject(array);
        } catch (Exception e) { e.printStackTrace(); }
    }



    protected void putObject(Json object) {
        if (!object.isObject())  return;  // throw new Exception("type not object");

        try {
            answer.addToObject(object);
        } catch (Exception e) { e.printStackTrace(); }
    }



    protected void put(String key, String value) {
        try {
            answer.add(key, value);
        } catch (Exception e) { e.printStackTrace(); }
    }




    // -------------------------------- Api -------------------------------- //

    public byte[] makeCont() {
        byte[] in_payload = session.getPayload();

        try {
            request.parse(new String(in_payload));
        } catch (Exception e) {
            e.printStackTrace();
            return ("{\"error\":\"request parse failed\"}").getBytes();
        }

        try {
            makeJson();
        } catch (Exception e) {
            e.printStackTrace();

            // ---- append to 'answer' e.toString()); ---- //
            // maybe reset answer before adding

            try {
                Json exc = new Json("exception");

                //exc.add("result", "error");
                exc.add("error", "makeJson failed");

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
                putObject(exc);
            } catch (Exception e2) { e2.printStackTrace(); put("exception", "failed"); }
        }

        return answer.stringify().getBytes();
    }


}