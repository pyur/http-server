package ru.pyur.tst.tags;

import ru.pyur.tst.db.DbFetcher;

import java.sql.ResultSet;


public abstract class TableFetcher extends DbFetcher {

    protected Table table;
    protected Tr tr;

    //private ArrayList<Tag> tags = new ArrayList<>();
    protected Tag tag;

//x    private TableCallback table_callback;
//x
//x    protected interface TableCallback {
//x        //void onFetch();
//x        void onRow();
//x        //void onRowWithId(int row_id);
//x        String onColumnString(int column_num, String value);
//x        Tag onColumnTag(int column_num, String value);
//x        //boolean onColumn(int column_num, String value);
//x    }



//    public TableFetcher() {}
//
//
//    public TableFetcher(Connection conn) {
//        setTableCallback(cb_table);
//        setConnection(conn);
//    }



    // ---- prototypes ----------------

    //abstract public Table make();
    abstract public Tag make();


    protected void onTableFetch() {}

    protected void onTableRow() {}

    protected String onTableColumnString(int column_num, String value) { return null; }

    protected Tag onTableColumnTag(int column_num, String value) { return null; }

    protected void onTableFooter() {}



    // ---- setters, getters ----------------------------------------------------------------

    public void addColumn(String description, int width) {
        table.addColumn(description, width);
    }

    public void addColumn(String description, int width, int align) {
        table.addColumn(description, width, align);
    }



    protected void fetchTable() {
        try {
            fetchResults();
        } catch (Exception e) {
            e.printStackTrace();

            Div div = new Div();
            div.addStyle("font-size", "12pt");
            //div.addStyle("border", "1px solid black");
            div.addStyle("color", "red");

            div.add("В процессе запроса данных произошла ошибка.<br>");
            div.add(e.getMessage());

            tag = div;
        }
    }



    @Override
    public void onEmpty() {
        Div div = new Div();
        div.add("Данные отсутствуют.");
        div.addStyle("font-size", "12pt");
        //div.addStyle("border", "1px solid black");
        tag = div;
    }



    @Override
    public void onFetch() {
        table = new Table();
        tag = table;

        onTableFetch();
    }



    @Override
    public void onRow() {
        tr = new Tr();
        table.add(tr);

        onTableRow();
    }


    @Override
    public void onColumn(ResultSet rs, int column_num) {
        Td cell = new Td();
        tr.add(cell);

        String value;
        try {
            value = rs.getString(column_num);
        } catch (Exception e) { e.printStackTrace(); return; }


        String inner_str = onTableColumnString(column_num, value);
        if (inner_str != null)  cell.add(inner_str);

        Tag inner_tag = onTableColumnTag(column_num, value);
        if (inner_tag != null)  cell.add(inner_tag);

        if (inner_str == null && inner_tag == null) { cell.add(value); }
    }



    @Override
    public void onDone() {
        onTableFooter();
    }

}