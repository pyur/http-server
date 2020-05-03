package ru.pyur.tst.auth;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.Auth;
import ru.pyur.tst.HttpSession;


public class Api_Auth extends ApiContent {


    public Api_Auth(HttpSession session) { init(session); }



    @Override
    public void makeContent() throws Exception {

        String login = getQuery("login");
        String password = getQuery("password");

        // compare password
        int user_id = 77;  // todo


        if (true) {
            Auth auth = new Auth();
            auth.newAuth(user_id);
            put("result", "ok");
        }

        else {
            put("result", "failed");
            put("error", "wrong password");
            //maybe set response code 401
        }

    }

}