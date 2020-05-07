package ru.pyur.tst;

import ru.pyur.tst.util.PStr;
import ru.pyur.tst.util.Util;

public class Url {

    // URI = scheme:[//authority]path[?query][#fragment]
    // authority = [userinfo@]host[:port]

    private String scheme;  // http, https
    //authority
    //userinfo user:password
    private String user;
    private String password;
    private String host;
    private int port;
    //private String location;  // path?query
    private String path;
    private String query;


    public Url(String url) {
        parse(url);
    }


    private void parse(String url) {
        PStr split = Util.split(':', url);
        scheme = split.key;
        String authority_path = split.value;

        String path_query;
        if (authority_path.charAt(0) == '/' && authority_path.charAt(1) == '/') {
            split = Util.split('/', authority_path.substring(2));
            String authority = split.key;
            path_query = "/" + split.value;

            split = Util.split('@', authority);
            String host_port;
            if (split.value.isEmpty()) {
                host_port = split.key;
            }
            else {
                host_port = split.value;
                String user_password = split.key;

                split = Util.split(':', user_password);
                user = split.key;
                password = split.value;
            }

            split = Util.split(':', host_port);
            host = split.key;
            if (!split.value.isEmpty()) {
                port = Integer.parseInt(split.value);
            }
            else {
                if (scheme.equals("http"))  port = 80;
                else if (scheme.equals("https"))  port = 443;
            }

        }
        else {
            path_query = authority_path;
        }

        split = Util.split('?', path_query);
        path = split.key;
        query = split.value;

        System.out.println("url: " + url);
        System.out.println("scheme: " + scheme);
        System.out.println("user: " + user);
        System.out.println("password: " + password);
        System.out.println("host: " + host);
        System.out.println("port: " + port);
        System.out.println("path: " + path);
        System.out.println("query: " + query);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(scheme);  // maybe default to 'http'
        sb.append(":");

        if (host != null) {
            sb.append("//");

            if (user != null) {
                sb.append(user);

                if (password != null) {
                    sb.append(":");
                    sb.append(password);
                }

                sb.append("@");
            }

            sb.append(host);

            if (port != 0) {
                sb.append(":");
                sb.append(port);
            }

            //sb.append("/");
        }
        else {
            sb.append("/");
        }

        sb.append(path);
        sb.append("?");
        sb.append(query);

        return sb.toString();
    }



    // ---- setters, getters ------------------------------------------------

    public String getHost() { return host; }

    public int getPort() { return port; }

    public String getLocation() {
        StringBuilder sb = new StringBuilder();
        sb.append(path);

        if (query != null && !query.isEmpty()) {
            sb.append("?");
            sb.append(query);
        }

        return sb.toString();
    }


}