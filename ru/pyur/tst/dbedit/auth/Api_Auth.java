package ru.pyur.tst.dbedit.auth;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.Auth;
import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.DbUpdate;
import ru.pyur.tst.db.FetchedSingle;
import ru.pyur.tst.util.Util;

import java.util.Arrays;


public class Api_Auth extends ApiContent {

    @Override
    public void makeJson() throws Exception {

        String login = getString("login");
        String password = getString("password");
        System.out.println("login: " + login + ", password: " + password);

        //debug
        //updatePassword(login, password);

        if (login.isEmpty()) {
            put("result", "failed");
            put("error", "login is empty.");
            setCode(401);
            return;
        }


        if (password.isEmpty()) {
            put("result", "failed");
            put("error", "password is empty.");
            setCode(401);
            return;
        }

        // ---- compare password ---- //
        int user_id = -1;

        //if (login.equals("user") && password.equals("1")) {
        //    user_id = 65505;
        //}

        DbFetch db_user = new DbFetch(getUserDb());
        db_user.table("user");
        db_user.col(new String[]{"id", "password"});
        db_user.where("`login` = ?");
        db_user.wa(login);

        FetchedSingle fs = db_user.fetchSingle();

        if (fs.isEmpty()) {
            // -- user not found -- //
            put("result", "failed");
            put("error", "user not found");
            setCode(401);
            return;
            // or fetch another table
        }

        byte[] fetched_password_hash = fs.getBytes("password");
        user_id = fs.getInt("id");
        db_user.finish();  // because locking


        byte[] password_hash = Util.SHA512(password);

        if (!Arrays.equals(password_hash, fetched_password_hash)) {
            put("result", "failed");
            put("error", "wrong password");
            setCode(401);
            return;
        }


        if (user_id != -1) {
            Auth auth = new Auth(getSessDb(), getUserDb(), getAdmUserDb(), host_session.getResponseHeader());
            auth.newAuth(user_id);
            put("result", "ok");
            put("location", "/");
        }

        else {
            put("result", "failed");
            put("error", "user not found");
            setCode(401);
        }

    }




    private void updatePassword(String login, String password) {
        byte[] password_hash = Util.SHA512(password);

        DbUpdate db_user = new DbUpdate(getUserDb());
        db_user.table("user");
        db_user.set("password", password_hash);
        db_user.where("`login` = ?");
        db_user.wa(login);

        try {
            db_user.update();
        } catch (Exception e) { e.printStackTrace(); }
    }

}