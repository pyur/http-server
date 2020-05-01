package ru.pyur.tst.resources;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

import static ru.pyur.tst.resources.Md_MakeSpriteActions.CONFIG_ACTION_ICON_UPD;


public class Api_GetSpriteActions extends ApiContent {

    public Api_GetSpriteActions(HttpSession session) {
        init(session);
    }



    @Override
    public void makeContent() throws Exception {

        String ts = configGet(CONFIG_ACTION_ICON_UPD);

        add("ts", ts);

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

        add("data", url_data.toString());
    }


}