package ru.pyur.tst;

import ru.pyur.tst.json.Json;
import ru.pyur.tst.tags.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;

import static ru.pyur.tst.resources.Md_MakeSpriteActions.CONFIG_ACTION_ICON_UPD;
import static ru.pyur.tst.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public abstract class ApiContent extends ContentBase {

    private Json request;
    private Json answer;  // response




    protected abstract void makeContent() throws Exception;




    public byte[] getContent() {
        request = new Json();
        try {
            request.parse(new String(session.getPayload()));
        } catch (Exception e) { e.printStackTrace(); }

        answer = new Json();

        byte[] content = makeJson();  // todo: move function here, inline

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

    public byte[] makeJson() {
        byte[] in_payload = session.getPayload();

        try {
            request.parse(new String(in_payload));
        } catch (Exception e) {
            e.printStackTrace();
            return ("{\"error\":\"request parse failed\"}").getBytes();
        }

        try {
            makeContent();
        } catch (Exception e) {
            e.printStackTrace();
            //todo: append to 'answer' e.toString());
        }

        return answer.stringify().getBytes();
    }





}