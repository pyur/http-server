package ru.pyur.tst.sample_host.resources;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.util.Util;

import java.util.Base64;

import static ru.pyur.tst.sample_host.resources.Md_MakeSpriteActions.CONFIG_ACTION_ICON_UPD;


public class Api_GetSpriteActions extends ApiContent {

    @Override
    public void makeJson() throws Exception {

        String ts = configGet(CONFIG_ACTION_ICON_UPD);

        put("ts", ts);

        byte[] bytes = Util.fetchFile("sample/sprite_actions.png");

        StringBuilder url_data = new StringBuilder("data:image/png;base64,");

        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString(bytes);

        url_data.append(encoded);

        put("data", url_data.toString());
    }


}