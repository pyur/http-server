package ru.pyur.tst;

import ru.pyur.tst.db.DbManager;

public abstract class ModularHost extends Host {

    protected DbManager db_manager;

    protected Auth auth;

    protected String prefix;  // a - api, i - image, e - embed, etc
    protected String module;
    protected String action;
    protected String last_argument;

    protected final String doc_root = "files";





    // --------------------------------------------------------------------------------

    public DbManager getDbManager() { return db_manager; }


    public String getModule() { return module; }

    public String getAction() { return action; }




    // --------------------------------------------------------------------------------

    protected void dispatchPath() throws Exception {
        prefix = "";
        module = "";
        action = "";
        last_argument = "";

        int path_length = request_header.lsPath.length;

        if (!request_header.lsPath[path_length - 1].isEmpty())  last_argument = request_header.lsPath[path_length - 1];


        if (path_length < 2) {
            // response 400
            throw new Exception("path length less than 2");
        }

//        else if (path_length == 2) {
        // only '/'. root
//            if (request_header.lsPath[1].isEmpty()) {
//                //processHtml();
//            }
//
//            // '/file'. root
//            else {
//                last_empty = false;
//            }
//        }


        // '/module/'
        // '/a/' api
        // '/e/' embed
        // '/i/' image
        // '/k/' kiosk
        else {  // request_header.lsPath.length > 2
            if (request_header.lsPath[1].length() == 1) {
                prefix = request_header.lsPath[1];

                // '/?/module/'
                if (path_length == 4) {
                    module = request_header.lsPath[2];
                }

                // '/?/module/action/'
                else if (path_length == 5) {
                    module = request_header.lsPath[2];
                    action = request_header.lsPath[3];
                }
            }


            else {
                // '/module/'
                if (path_length == 3) {
                    module = request_header.lsPath[1];
                }

                // '/module/action/'
                else if (path_length == 4) {
                    module = request_header.lsPath[1];
                    action = request_header.lsPath[2];
                }
            }

        }

    }

}