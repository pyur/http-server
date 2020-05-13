package ru.pyur.tst.sample_host.auth;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.Auth;


public class Api_Auth extends ApiContent {

    @Override
    public void makeJson() throws Exception {

        String login = getString("login");
        String password = getString("password");
        System.out.println("login: " + login + ", password: " + password);

        // ---- compare password ---- //
        int user_id = -1;

        if (login.equals("user") && password.equals("1")) {
            user_id = 65505;
        }


        if (user_id != -1) {
//todo            Auth auth = new Auth(host_session.getConfigDb(), host_session.getDb(), host_session.getResponseHeader());
//todo            auth.newAuth(user_id);
            put("result", "ok");
        }

        else {
            put("result", "failed");
            put("error", "wrong password");
            //maybe set response code 401
        }

//        throw new Exception("some mess.");
    }

}