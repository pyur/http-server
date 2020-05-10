package ru.pyur.tst.tags;

import ru.pyur.tst.db.DbFetcher;

import java.sql.ResultSet;


public abstract class TableFetcher extends DbFetcher {

    protected Table table;
    protected Tr tr;

    //private ArrayList<Tag> tags = new ArrayList<>();
    protected Tag tag;

    private TableCallback table_callback;

    protected interface TableCallback {
        //void onFetch();
        void onRow();
        //void onRowWithId(int row_id);
        String onColumnString(int column_num, String value);
        Tag onColumnTag(int column_num, String value);
        //boolean onColumn(int column_num, String value);
    }



//    public TableFetcher() {}
//
//
//    public TableFetcher(Connection conn) {
//        setTableCallback(cb_table);
//        setConnection(conn);
//    }



    //abstract public Table make();
    abstract public Tag make();



    // ---- setters, getters ----------------------------------------------------------------

//        protected void add(String str) { tags.add(new PlainText(str)); }

//        protected void add(int number) { tags.add(new PlainText(number)); }

//        protected void add(Tag tag) { tags.add(tag); }


    public void addColumn(String description, int width) {
        table.addColumn(description, width);
    }

    public void addColumn(String description, int width, int align) {
        table.addColumn(description, width, align);
    }


    //protected void setTableCallback(TableCallback cb_table) {
    protected void initTable(TableCallback cb_table) {
        table_callback = cb_table;
        setFetcherCallback(cb_fetcher);

        table = new Table();
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



    private FetcherCallback cb_fetcher = new FetcherCallback() {
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
            //table = new Table();
            tag = table;

            //table_callback.onFetch();
            //todo: instead of call child, add columns (?and actions)
        }

        @Override
        public void onRow() {
            tr = new Tr();
            table.add(tr);

            table_callback.onRow();
        }

        @Override
        public void onColumn(ResultSet rs, int column_num) {
        //public void onColumnString(int column_num, String value) {
            Td cell = new Td();
            tr.add(cell);

            String value;
            try {
                value = rs.getString(column_num);
            } catch (Exception e) { e.printStackTrace(); return; }


            String inner_str = table_callback.onColumnString(column_num, value);
            if (inner_str != null)  cell.add(inner_str);

            Tag inner_tag = table_callback.onColumnTag(column_num, value);
            if (inner_tag != null)  cell.add(inner_tag);

            if (inner_str == null && inner_tag == null) { cell.add(value); }

            //boolean isInner = table_callback.onColumn(column_num, value);
            //if (!isInner) { cell.add(value); }


            //cell.add(inner);
        }
    };


}