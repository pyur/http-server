package ru.pyur.tst.dbedit.host;

import ru.pyur.tst.HtmlEditContent;
import ru.pyur.tst.db.Var;
import ru.pyur.tst.util.SqlDate;
import ru.pyur.tst.util.SqlTime;

public class HtmlEdit_Host extends HtmlEditContent {


    @Override
    protected void initEdit() throws Exception {
        edit_conn = getConfigDb();
        if (edit_conn == null)  throw new Exception("get db failed.");

        edit_table = "db";

        id_param = "host";

        name_a = "хост";
        name_b = "хоста";


//        EditColumn ec = new EditColumn();
//        ec.column = "host";
//        ec.type = EDIT_COLUMN_TYPE_STRING;
//        ec.value = new Var("");
//
//        addEditColumn(ec);

        addEditColumn("host", "123", "Хост");
        addEditColumn("port", 3306, "Порт");
        addEditColumn("login", "", "Логин");
        addEditColumn("password", "", "Пароль");

//        addEditColumn("date", new SqlDate(), "Дата");
//        addEditColumn("time", new SqlTime(), "Время");

    }


}