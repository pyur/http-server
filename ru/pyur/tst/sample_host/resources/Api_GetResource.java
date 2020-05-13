package ru.pyur.tst.sample_host.resources;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.json.Json;
import ru.pyur.tst.util.Util;

import java.util.Base64;


public class Api_GetResource extends ApiContent {

/*
    @Override
    public void makeJson() throws Exception {
        String res_name = getString("name");

        if (res_name.equals("sprite_modules")) {
//            String ts = configGet(CONFIG_MODULE_ICON_UPD);
//            put("ts", ts);

            Json cached = new Json("cached");

            cached.add("sprite_modules", getPng("sample/sprite_modules.png"));
            cached.add("sprite_modules_2", getPng("sample/sprite_modules_2.png"));

//x            putObject(cached);
            put(cached);
        }

        else if (res_name.equals("sprite_actions")) {
            Json cached = new Json("cached");
            cached.add("sprite_actions", getPng("sample/sprite_actions.png"));
//x            putObject(cached);
            put(cached);
        }

    }
*/



    @Override
    public void makeJson() throws Exception {
        //System.out.println("Api_GetResource. makeJson()");
        Json names = getObject("names");

        Json res = new Json("res");

        for (Json name : names.toArray()) {
            //System.out.println("json: " + name.toString());

            if (name.toString().equals("sprite_actions")) {
                res.add("sprite_actions", getPng("sample/sprite_actions.png"));
            }

            else if (name.toString().equals("sprite_modules")) {
                res.add("sprite_modules", getPng("sample/sprite_modules.png"));
            }

            else if (name.toString().equals("sprite_modules_2")) {
                res.add("sprite_modules_2", getPng("sample/sprite_modules_2.png"));
            }

            else if (name.toString().equals("script")) {
                res.add("script", new String(Util.fetchFile("sample/script.js")));
            }

        }

        put(res);

    }




    private String getPng(String file_name) throws Exception {
        byte[] bytes = Util.fetchFile(file_name);

        StringBuilder url_data = new StringBuilder("data:image/png;base64,");

        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        return url_data.toString();
    }


}