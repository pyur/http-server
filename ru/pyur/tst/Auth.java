package ru.pyur.tst;

import ru.pyur.tst.db.*;
import ru.pyur.tst.util.Util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;


public class Auth {

    private byte[] secret = "yVDL6Jm68LBA5hMpnIIGMTVQpvF1fgyh5DzIITNNDWhuccvq".getBytes();

    // token format:
    // user_is,session_id,claim_iat . sign

    private final int AUTH_STATE_NONE = 0;
    private final int AUTH_STATE_ONE = 1;
    private final int AUTH_STATE_TWO = 2;
    private final int AUTH_STATE_THREE = 3;
    // 1 - being checked
    // ? - `sess` found, perform search `user`
    // 2 - `user` got
    // 4 - cookie (token) absent
    // 8 - `sess` for cookie (token) not exists, or not valid

    public int state = AUTH_STATE_NONE;

    private ArrayList<String> permissions = new ArrayList<>();

    private String user_desc;
    private int session_id;
    private int user_id;
    private int user_category;

    private boolean super_user = false;

    private ArrayList<String> modules = new ArrayList<>();

    private int main_days = 1;
    private int sync_days = 1;


    private final int AUTH_TOKEN_LIFE_TIME = 60;  // seconds

//    ControlSession co_session;
//    HttpSession session;

    private HttpRequest request_header;  // redundant. todo: fold
    private HttpResponse response_header;

    private DbManager db_manager;



//    public Auth() {}


//    public Auth(DbManager dbm, ControlSession co_session) {
//        db_manager = dbm;
//        this.co_session = co_session;
//    }

//    public Auth(HttpSession session) {
//        this.session = session;
//        db_manager = session.getDbManager();
//    }

    public Auth(DbManager dbm, HttpResponse response_header) {
        db_manager = dbm;
        this.response_header = response_header;
    }




    // ---- setters, getters ---- //

    public int getSessionId() { return session_id; }

    public int getUserId() { return user_id; }

    public ArrayList<String> getModules() { return modules; }




    // ------------------------------------ auth by cookie ------------------------------------- //
    //https://softwareengineering.stackexchange.com/questions/405038/result-object-vs-throwing-exceptions

    public void authByCookie(HttpRequest request_header) throws Exception {
        System.out.println("authByCookie()");
        this.request_header = request_header;

        try {
            getAuthCookie();
        } catch (Exception e) {
            e.printStackTrace();
            //resetCookie();
        }


        checkConfigUser();  // if (user_id >= 65504)

//todo        if (state != 2)  checkDbUser();

//        if (state != 2)  fetchNoauthModules();

//        if (state == 2)  apache_note('sid', sid);  // TODO

//        if (!Array.has(modules[mod]))  mod = "";

//        makePermissons();
    }




    // ------------------------------------ read COOKIE ------------------------------------- //

    public void getAuthCookie() throws Exception {
        System.out.println("getAuthCookie()");
        //if (no_cookie) {
        //    throw new Exception("token cookie absent");
        //}

        String token_raw = request_header.getCookie("t").getValue();
        //System.out.println("token_raw: " + token_raw);
        //String token = Cookie.replacePlus(token_raw);  // adhoc
        String token = token_raw;  // todo: replace

        String token_claim;
        try {
            token_claim = validateSign(token);
        } catch (Exception e) {
            e.printStackTrace();
            resetCookie();
            throw new Exception("token validation failed");
        }


        String[] claim = Util.explode(',', token_claim);

        if (claim.length != 3) {
            resetCookie();
            throw new Exception("token claim not 3");
        }


        System.out.println("token claim: " + claim[0] + ", " + claim[1] + ", " + claim[2]);
        int claim_time = Integer.parseInt(claim[2]);

        // check for token issued longer than life time
        if ((claim_time + AUTH_TOKEN_LIFE_TIME) < (System.currentTimeMillis() / 1000) ) {
            // alternatively may be redirect to some refresh url
            try {
                refreshToken(claim);
            } catch (Exception e) {
                e.printStackTrace();
                resetCookie();
                throw new Exception("token refresh failed.");
            }
        }

        state = AUTH_STATE_ONE;
        session_id = Integer.parseInt(claim[1]);
        user_id =  Integer.parseInt(claim[0]);
    }




    private String validateSign(String token) throws Exception {
        System.out.println("validateSign()");

        System.out.println("explode: " + token);
        String[] token_e = Util.explode('.', token);
        if (token_e.length != 2)  throw new Exception("token not 2 parts (" + token_e.length +")");

        String token_claim64 = token_e[0];
        String token_sign64 = token_e[1];


        // todo: is there signature check function?
        //byte[] verify_signature = hash_hmac('sha256', token_claim64, secret, true);
        byte[] verify_signature = makeSignature(token_claim64.getBytes());

//        byte[] verify_signature = null;
//        try {
//            Mac mac = Mac.getInstance("HmacSHA256");
//            SecretKeySpec key_spec = new SecretKeySpec(secret, "HmacSHA256");
//            mac.init(key_spec);
//            verify_signature = mac.doFinal(token_claim64.getBytes());
//        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }


        byte[] token_sign = Base64.getDecoder().decode(token_sign64);

        if (!Arrays.equals(verify_signature, token_sign)) {
            throw new Exception("token signature fake.");
        }

        byte[] token_claim_decoded = Base64.getDecoder().decode(token_claim64);
        return new String(token_claim_decoded);
    }




    private void refreshToken(String[] claim) throws Exception {
        System.out.println("refreshToken()");
        int user_id = Integer.parseInt(claim[0]);
        int session_id = Integer.parseInt(claim[1]);
        int claim_iat = Integer.parseInt(claim[2]);


        DbFetch db_sess = new DbFetch(db_manager.getDb());
        db_sess.table("sesst");  // todo: `sessta` - archived
        db_sess.col(new String[]{"user", "tp", "tm"});
        db_sess.where("`id` = ?");  // `stat` = 0 . todo: make second table for archived sessions
        db_sess.wa(session_id);

        FetchSingle fetch = db_sess.fetchSingle();

        if (fetch.isEmpty())  throw new Exception("refresh token. session not found.");

        if (fetch.getInt("user") != user_id)  throw new Exception("refresh token. session wrong user.");


        //                  V v v
        // real time: . . . . . . . . . .
        // sess ts:   _ _ _ _ _ _ _ _ _ _
        //                  ^

        int current_time = (int)(System.currentTimeMillis() / 1000);

        // -- adhoc: ignore refresh, when token issued just within 2 seconds -- //
        if ((current_time - 2) <= fetch.getInt("tm")) {
//            fwrite (fopen ('r/auth/collision', 'ab'), $curr['datetime']." token just issued. session: ".$session_id.", user: ".$user_id.", sess tm: ".$sess['tm']." (".timestamp_to_sql($sess['tm'])."), sess tp: ".$sess['tp']." (".timestamp_to_sql($sess['tp'])."), claim iat: ".$claim_iat." (".timestamp_to_sql($claim_iat).")\r\n");
//            clearstatcache();
            return;
        }


        int prev_time = fetch.getInt("tm");

        if (fetch.getInt("tm") != claim_iat) {
//            fwrite (fopen ('r/auth/not_match', 'ab'), $curr['datetime']." 'iat' not match. session: ".$session_id.", user: ".$user_id.", sess tm: ".$sess['tm']." (".timestamp_to_sql($sess['tm'])."), sess tp: ".$sess['tp']." (".timestamp_to_sql($sess['tp'])."), claim iat: ".$claim_iat." (".timestamp_to_sql($claim_iat).")\r\n");
//            clearstatcache();


            prev_time = fetch.getInt("tp");  // redefine

            if (prev_time != claim_iat) {
//                fwrite (fopen ('r/auth/not_prev', 'ab'), $curr['datetime']." 'iat' not match prev. session: ".$session_id.", user: ".$user_id.", sess tm: ".$sess['tm']." (".timestamp_to_sql($sess['tm'])."), sess tp: ".$sess['tp']." (".timestamp_to_sql($sess['tp'])."), claim iat: ".$claim_iat." (".timestamp_to_sql($claim_iat).")\r\n");
//                clearstatcache();

//strict                failSession(session_id);

//strict                throw new Exception("'iat' not match prev");
            }
        }


        updateSession(session_id, prev_time);
        String token = makeToken(user_id, session_id);

        // -------- update COOKIE -------- //
        setCookie(token);
    }




    // --------------------------------------------------------------------------------------

//    public static void newAuth(int user_id) throws Exception {
    public void newAuth(int user_id) throws Exception {
        int session_id = newSession(user_id);
        String token = makeToken(user_id, session_id);

        setCookie(token);
    }



    public int newSession(int user_id) throws Exception {
        int current_time = (int)(System.currentTimeMillis() / 1000);

        DbInsert db_sess = new DbInsert(db_manager.getDb());
        db_sess.table("sesst");

        //skip "stat"
        db_sess.set("user", user_id);
        db_sess.set("ip", 0);  // inet_aton($_SERVER['REMOTE_ADDR']
        db_sess.set("ua", 0);  // substr($_SERVER['HTTP_USER_AGENT'],0,512);
        db_sess.set("tc", current_time);
        db_sess.set("tp", current_time);
        db_sess.set("tm", current_time);

        int session_id = db_sess.insert();

        return session_id;
    }



    private void updateSession(int session_id, int prev_time) throws Exception {
        int current_time = (int)(System.currentTimeMillis() / 1000);

        DbUpdate db_sess = new DbUpdate(db_manager.getDb());
        db_sess.table("sesst");
        db_sess.where("`id` = ?");
        db_sess.wa(session_id);

        db_sess.set("ip", 0);  // inet_aton($_SERVER['REMOTE_ADDR']
        db_sess.set("ua", 0);  // substr($_SERVER['HTTP_USER_AGENT'],0,512);
        db_sess.set("tp", prev_time);
        db_sess.set("tm", current_time);

        db_sess.update();
    }



    private void failSession(int session_id) {
        // todo: move session to archive
    }



    public String makeToken(int user_id, int session_id) {
        int curr_time = (int)(System.currentTimeMillis() / 1000);

        //String claim = user_id + "," + session_id + "," + curr_time;
        StringBuilder sb = new StringBuilder();
        sb.append(user_id);
        sb.append(",");
        sb.append(session_id);
        sb.append(",");
        sb.append(curr_time);

        byte[] token_claim64 = Base64.getEncoder().encode(sb.toString().getBytes());

        byte[] signature = makeSignature(token_claim64);
        byte[] signature64 = Base64.getEncoder().encode(signature);

        String token = new String(token_claim64) + "." + new String(signature64);

        return token;
    }




    private byte[] makeSignature(byte[] data) {
        byte[] verify_signature = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key_spec = new SecretKeySpec(secret, "HmacSHA256");
            mac.init(key_spec);
            verify_signature = mac.doFinal(data);
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch (InvalidKeyException e) { e.printStackTrace(); }

        return verify_signature;
    }




    // ---------------------------------------------------------------- //

    private void setCookie(String value) {
        int current_time = (int)(System.currentTimeMillis() / 1000);
        int lifetime = 60 * 60 * 24 * 30 * 12 * 5;  // ~5 years
        response_header.setCookie("t", value, current_time + lifetime, "/");
    }


    private void resetCookie() {
//        header ("Cache-Control: no-cache, must-revalidate");  ??
//        header ("Expires: Thu, 17 Apr 1991 12:00:00 GMT");    ??
//        setcookie('t', '', time()-60*60, '/');

        int current_time = (int)(System.currentTimeMillis() / 1000);
        response_header.setCookie("t", "", current_time - 3600, "/");
        state = 4;
    }




    // ---------------------------------------------------------------- //

    private void checkConfigUser() {
        //todo
    }

}