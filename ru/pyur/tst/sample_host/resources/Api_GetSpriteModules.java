package ru.pyur.tst.sample_host.resources;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.Util;

import java.util.Base64;

import static ru.pyur.tst.sample_host.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public class Api_GetSpriteModules extends ApiContent {

//    public Api_GetSpriteModules(HttpSession session) {
//        init(session);
//    }



    @Override
    public void makeJson() throws Exception {

        String ts = configGet(CONFIG_MODULE_ICON_UPD);

        put("ts", ts);


        // ---------------- 1x size ---------------- //

        byte[] bytes = Util.fetchFile("sprite_modules.png");


        StringBuilder url_data = new StringBuilder("data:image/png;base64,");

        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        put("data", url_data.toString());


        // ---------------- 2x size ---------------- //

        bytes = Util.fetchFile("sprite_modules_2.png");

        url_data = new StringBuilder("data:image/png;base64,");

        encoder = Base64.getEncoder();
        encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        put("data2", url_data.toString());
    }


}