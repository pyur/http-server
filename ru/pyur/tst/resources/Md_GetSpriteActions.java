package ru.pyur.tst.resources;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;


public class Md_GetSpriteActions extends Module {

    public Md_GetSpriteActions(Session session) {
        setSession(session);
        //setType(MODULE_TYPE_BINARY);
        setType(MODULE_TYPE_JSON);
    }



    @Override
    //public void prepareBin() {
    public void prepareJsonData() {

        appendJson("ts", "999");

        File sprite_file = new File("sprite_actions.png");

        byte[] bytes;
        try {
            FileInputStream fis = new FileInputStream(sprite_file);
            bytes = new byte[fis.available()];
            int readed = fis.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            //b("Failed. read image " + sprite_file.getAbsoluteFile());
            return;
        }


        StringBuilder url_data = new StringBuilder("data:image/png;base64,");

        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        appendJson("data", url_data.toString());
    }


}