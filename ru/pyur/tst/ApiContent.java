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

    protected void init(HttpSession session) {
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
    protected void put(String key, String value) throws Exception {
        answer.add(key, value);
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
            //todo: append to 'answer' e.toString());
        }

        return answer.stringify().getBytes();
    }





}