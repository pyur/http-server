package ru.pyur.tst;

import ru.pyur.tst.util.DateTime;
import ru.pyur.tst.util.SqlDate;
import ru.pyur.tst.util.SqlTime;

import java.sql.ResultSet;


public class EditColumn {

    public String column;
    public String desc;


    public int type;

    protected static final int EDIT_COLUMN_TYPE_UNDEFINED = 0;
    protected static final int EDIT_COLUMN_TYPE_NUMERIC = 1;
    protected static final int EDIT_COLUMN_TYPE_TEXT = 2;
    protected static final int EDIT_COLUMN_TYPE_LARGE_TEXT = 3;
    protected static final int EDIT_COLUMN_TYPE_DATE = 4;
    protected static final int EDIT_COLUMN_TYPE_TIME = 5;
    protected static final int EDIT_COLUMN_TYPE_DATETIME = 6;
    //protected static final int EDIT_COLUMN_TYPE_PASSWORD = 7;

    private long value_number;
    private String value_text;
    //private byte[] value_bytes;
    private SqlDate value_date;
    private SqlTime value_time;
    private DateTime value_dt;


//        public EditColumn() {}


    public EditColumn(String column, String desc) {
        this.column = column;
        this.desc = desc;
    }


    public void setNumber(long number) {
        type = EDIT_COLUMN_TYPE_NUMERIC;
        value_number = number;
    }


    public void setText(String text) {
        type = EDIT_COLUMN_TYPE_TEXT;
        value_text = text;
    }


    public void setDate(SqlDate date) {
        type = EDIT_COLUMN_TYPE_DATE;
        value_date = date;
    }


    public void setTime(SqlTime time) {
        type = EDIT_COLUMN_TYPE_TIME;
        value_time = time;
    }


    public void setDateTime(DateTime dt) {
        type = EDIT_COLUMN_TYPE_DATETIME;
        value_dt = dt;
    }



    public void setFromResultSet(ResultSet rs, int idx) throws Exception {
        switch (type) {
            case EDIT_COLUMN_TYPE_NUMERIC:
                value_number = rs.getLong(idx);
                break;

            case EDIT_COLUMN_TYPE_TEXT:
                value_text = rs.getString(idx);
                break;

            case EDIT_COLUMN_TYPE_LARGE_TEXT:
                value_text = rs.getString(idx);
                break;

            case EDIT_COLUMN_TYPE_DATE:
                value_date = new SqlDate(rs.getString(idx));
                break;

            case EDIT_COLUMN_TYPE_TIME:
                value_time = new SqlTime(rs.getString(idx));
                break;

            case EDIT_COLUMN_TYPE_DATETIME:
                value_dt = new DateTime(rs.getString(idx));
                break;

        }
    }



    @Override
    public String toString() {
        switch (type) {
            case EDIT_COLUMN_TYPE_NUMERIC:
                return "" + value_number;

            case EDIT_COLUMN_TYPE_TEXT:
                return value_text;

            case EDIT_COLUMN_TYPE_LARGE_TEXT:
                return value_text;

            case EDIT_COLUMN_TYPE_DATE:
                return value_date.toSqlDate();

            case EDIT_COLUMN_TYPE_TIME:
                return value_time.toSqlTime();

            case EDIT_COLUMN_TYPE_DATETIME:
                return value_dt.toSqlDateTime();

            default:
                return "unknown";
        }
    }

}