package ru.pyur.tst.resources;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

import static ru.pyur.tst.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public class Md_GetSpriteModules extends Module {

    public Md_GetSpriteModules(Session session) {
        initJson(session);
    }



    @Override
    public void makeContent() {
        int ts = 0;

        getConfigDb();
        Statement stmt = getConfigStatement();

        String query = "SELECT `value` FROM `config` WHERE `key` = '" + CONFIG_MODULE_ICON_UPD + "'";

        try {
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                ts = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }


        appendJson("ts", "" + ts);


        // ---------------- 1x size ---------------- //

        File sprite_file = new File("sprite_modules.png");

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


        // ---------------- 2x size ---------------- //

        sprite_file = new File("sprite_modules_2.png");

        //byte[] bytes;
        try {
            FileInputStream fis = new FileInputStream(sprite_file);
            bytes = new byte[fis.available()];
            int readed = fis.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            //b("Failed. read image " + sprite_file.getAbsoluteFile());
            return;
        }


        url_data = new StringBuilder("data:image/png;base64,");

        encoder = Base64.getEncoder();
        encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        appendJson("data2", url_data.toString());

    }


}