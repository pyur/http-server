package ru.pyur.tst.resources;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

import static ru.pyur.tst.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public class Api_GetSpriteModules extends ApiContent {

    public Api_GetSpriteModules(HttpSession session) {
        init(session);
    }



    @Override
    public void makeContent() throws Exception {

        String ts = configGet(CONFIG_MODULE_ICON_UPD);

        put("ts", ts);


        // ---------------- 1x size ---------------- //

        File sprite_file = new File("sprite_modules.png");

        FileInputStream fis = new FileInputStream(sprite_file);
        byte[] bytes = new byte[fis.available()];
        int readed = fis.read(bytes);


        StringBuilder url_data = new StringBuilder("data:image/png;base64,");

        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        put("data", url_data.toString());


        // ---------------- 2x size ---------------- //

        sprite_file = new File("sprite_modules_2.png");

        fis = new FileInputStream(sprite_file);
        bytes = new byte[fis.available()];
        readed = fis.read(bytes);


        url_data = new StringBuilder("data:image/png;base64,");

        encoder = Base64.getEncoder();
        encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        put("data2", url_data.toString());
    }


}