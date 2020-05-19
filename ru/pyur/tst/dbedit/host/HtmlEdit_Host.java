package ru.pyur.tst.dbedit.host;

import ru.pyur.tst.HtmlEditContent;
import ru.pyur.tst.db.Var;

public class HtmlEdit_Host extends HtmlEditContent {


    @Override
    protected void initEdit() throws Exception {
//x        editDb(getHostDb());
        edit_conn = getConfigDb();
        if (edit_conn == null)  throw new Exception("get db failed.");

//x        editTable("db");  // rename to 'host'
        edit_table = "db";

//x        editIdParam("host");
        id_param = "host";

//x        editString("хост", "хоста");
        name_a = "хост";
        name_b = "хоста";


//        addEditColumn("host", "");
//        addEditColumn("port", 0);
//        addEditColumn("login", "");
//        addEditColumn("password", "");

        EditColumn ec = new EditColumn();
        ec.column = "host";
        ec.type = EDIT_COLUMN_TYPE_STRING;
        ec.value = new Var("");



        addEditColumn(ec);

    }


}