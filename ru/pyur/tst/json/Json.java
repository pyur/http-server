package ru.pyur.tst.json;


import java.util.ArrayList;

import static ru.pyur.tst.json.JsonParser.JSON_IN_ARRAY;
import static ru.pyur.tst.json.JsonParser.JSON_IN_OBJECT;

public class Json {

    public static final int JSON_TYPE_OBJECT = 1;
    public static final int JSON_TYPE_ARRAY = 2;
    public static final int JSON_TYPE_INTEGER = 3;
    public static final int JSON_TYPE_DOUBLE = 4;
    public static final int JSON_TYPE_STRING = 5;
    public static final int JSON_TYPE_BOOLEAN = 6;
    public static final int JSON_TYPE_NULL = 7;

    //private static final int JSON_TYPE_LONG = 9;

    public int type;
    public String key;

    public String string;              // type 5
    public ArrayList<Json> lsJson;     // type 1, 2

    //private int integer;                // type 3
    public long integer;              // type 3b
    //private float fnum;               // type 4a
    public double fract_num;           // type 4b



    public Json() {}


    // -------- without key -------- //

    public Json(ArrayList<Json> arr) {
        type = JSON_TYPE_OBJECT;
        lsJson = arr;
    }


    public void array() {
        type = JSON_TYPE_ARRAY;
    }


    public Json(int num) {
        type = JSON_TYPE_INTEGER;
        integer = num;
    }


    public Json(double num) {
        type = JSON_TYPE_DOUBLE;
        fract_num = num;
    }


    public Json(String string) {
        type = JSON_TYPE_STRING;
        this.string = string;
    }


    public Json(boolean b) {
        type = JSON_TYPE_BOOLEAN;
        integer = b ? 1 : 0;
    }


    // -------- with key -------- //

    public Json(String key, ArrayList<Json> arr) {
        type = JSON_TYPE_OBJECT;
        this.key = key;
        lsJson = arr;
    }


    public Json(String key, int num) {
        type = JSON_TYPE_INTEGER;
        this.key = key;
        integer = num;
    }


    public Json(String key, double num) {
        type = JSON_TYPE_DOUBLE;
        this.key = key;
        fract_num = num;
    }


    public Json(String key, String string) {
        type = JSON_TYPE_STRING;
        this.key = key;
        this.string = string;
    }


    public Json(String key, boolean b) {
        type = JSON_TYPE_BOOLEAN;
        this.key = key;
        integer = b ? 1 : 0;
    }



    // -------------------------------- Getters -------------------------------- //

    public String getKey() {
        return key;
    }


    public ArrayList<Json> getObject() {
        if (type == JSON_TYPE_OBJECT) {
            return lsJson;
        }

        return null;
    }


    public ArrayList<Json> getArray() {
    //todo: public JsonArray getArray() {
        if (type == JSON_TYPE_ARRAY) {
            return lsJson;
        }

        return null;
    }


    public String getString() {
        switch (type) {
            case JSON_TYPE_OBJECT:
                return "[Object]";

            case JSON_TYPE_ARRAY:
                return "[Array]";

            case JSON_TYPE_INTEGER:
                return "" + integer;

            case JSON_TYPE_DOUBLE:
                return "" + fract_num;

            case JSON_TYPE_STRING:
                return string;

            case JSON_TYPE_BOOLEAN:
                return (integer == 0) ? "false" : "true";

            case JSON_TYPE_NULL:
                return "null";

            //default:
                //throw new Exception("unknown type");
            //    break;
        }

        return "[unknown type]";
    }



    public int getInt() {
        switch (type) {
            case JSON_TYPE_OBJECT:
                //throw new Exception("not convertible");
                return 0;

            case JSON_TYPE_ARRAY:
                return 0;

            case JSON_TYPE_INTEGER:
                return (int)integer;

            case JSON_TYPE_DOUBLE:
                return (int)fract_num;

            case JSON_TYPE_STRING:
                return Integer.parseInt(string);

            case JSON_TYPE_BOOLEAN:
                return (integer == 0) ? 0 : 1;

            case JSON_TYPE_NULL:
                return 0;
        }

        return 0;  // unknown type
    }



    public long getLong() {
        switch (type) {
            case JSON_TYPE_OBJECT:
                //throw new Exception("not convertible");
                return 0L;

            case JSON_TYPE_ARRAY:
                return 0L;

            case JSON_TYPE_INTEGER:
                return integer;

            case JSON_TYPE_DOUBLE:
                return (long)fract_num;

            case JSON_TYPE_STRING:
                return Long.parseLong(string);

            case JSON_TYPE_BOOLEAN:
                return (integer == 0) ? 0 : 1;

            case JSON_TYPE_NULL:
                return 0;
        }

        return 0;  // unknown type
    }


    //public float getFloat() {}
    //public double getDouble() {}
    //public boolean getBoolean() {}




    // -------------------------------- Object, Array adder -------------------------------- //

    public void add(Json json) throws Exception {
        if (type != JSON_TYPE_OBJECT && type != JSON_TYPE_ARRAY)  throw new Exception("type not object or array");
        lsJson.add(json);
    }




    // -------------------------------- Utilities -------------------------------- //

    public boolean has(String key) {
        if (type != JSON_TYPE_OBJECT)  return false;

        for (Json json : lsJson) {
            if (json.key.equals(key))  return true;
        }

        return false;
    }



    public Json getNode(String key) throws Exception {
        if (type != JSON_TYPE_OBJECT)  throw new Exception("json type not 'object'");

        for (Json json : lsJson) {
            if (json.key.equals(key))  return json;
        }

        throw new Exception("node miss");
    }



    public Json getNode(int pos) throws Exception {
        if (type != JSON_TYPE_ARRAY)  throw new Exception("json type not 'array'");

        if (pos > lsJson.size())  throw new Exception("out of boundary");

        return lsJson.get(pos);
    }




    // -------------------------------- Stringifier -------------------------------- //

//    public String stringify() {
//        return null;
//    }

    StringBuilder sb;

    String stringifyWithRoot(Json json) {
//x        Expandable sb = Expandable_Create(4096);
        sb = new StringBuilder(4096);
        stringifyNode(json);

//x        return Expandable_ConvertToString(&sb);
        return sb.toString();
    }



    // ---------------- StringifyAsObject ---------------- //

//x    String stringify(ArrayList<Json> list) {
    public String stringify() {
//x        Expandable sb = Expandable_Create(4096);
        sb = new StringBuilder(4096);
        if (type == JSON_TYPE_OBJECT) {
            stringifyNested(lsJson, JSON_IN_OBJECT);
        }
        else if (type == JSON_TYPE_ARRAY) {
            stringifyNested(lsJson, JSON_IN_ARRAY);
        }
        else {
            stringifyNode(this);
        }

//x        return Expandable_ConvertToString(&sb);
        return sb.toString();
    }



    // ---------------- StringifyAsArray ---------------- //

/*
    public String stringifyAsArray(ArrayList<Json> list) {
//x        Expandable sb = Expandable_Create(4096);
        sb = new StringBuilder(4096);
        stringifyNested(list, JSON_IN_ARRAY);

//x        return Expandable_ConvertToString(&sb);
        return sb.toString();
    }
*/



    public void stringifyNode(Json json) {
//x        String conv;

        switch (json.type) {
            case JSON_TYPE_OBJECT:
                stringifyNested(json.lsJson, JSON_IN_OBJECT);
                break;

            case JSON_TYPE_ARRAY:
                stringifyNested(json.lsJson, JSON_IN_ARRAY);
                break;

            case JSON_TYPE_INTEGER:
//x                conv = String_FromInt(json.integer);
//x                Expandable_AppendString(sb, conv);
//x                String_Destroy(conv);
                sb.append(json.integer);
                break;

//x            case JSON_TYPE_UINT:
//x                conv = String_FromULL(json->uint);
//x                Expandable_AppendString(sb, conv);
//x                String_Destroy(conv);
//x                break;

//x            case JSON_TYPE_LL:
//x                conv = String_FromLL(json->lint);
//x                Expandable_AppendString(sb, conv);
//x                String_Destroy(conv);
//x                break;

//x            case JSON_TYPE_ULL:
//x                conv = String_FromULL(json->ulint);
//x                //Expandable_AppendString(sb, json->string);  // todo: convert num to str
//x                Expandable_AppendString(sb, conv);
//x                String_Destroy(conv);
//x                break;

            case JSON_TYPE_DOUBLE:
                //Expandable_AppendString(sb, json->string);  // todo
                sb.append(json.fract_num);
                break;

            case JSON_TYPE_STRING:
//x                Expandable_AppendString(sb, "\"");
//x                Str escaped = String_JsonEscape(json->string);
//x                Expandable_AppendString(sb, escaped);
//x                String_Destroy(escaped);
//x                Expandable_AppendString(sb, "\"");
                sb.append("\"");
                sb.append(json.string);  // JsonEscape
                sb.append("\"");
                break;

            case JSON_TYPE_BOOLEAN:
//x                Expandable_AppendString(sb, json->integer ? "true" : "false");
                sb.append((json.integer == 0) ? "false" : "true");
                break;

            case JSON_TYPE_NULL:
//x                Expandable_AppendString(sb, "null");
                sb.append("null");
                break;


            default:
                System.out.println("json stringify. error: unknown type.");
                break;
        }

    }



    public void stringifyNested(ArrayList<Json> list, int type) {
        //char num_key[32];  // max 20 +1 zero terminator

//x        if (type == JSON_IN_OBJECT)  Expandable_AppendString(sb, "{");
//x        if (type == JSON_IN_ARRAY)  Expandable_AppendString(sb, "[");
        if (type == JSON_IN_OBJECT)  sb.append("{");
        if (type == JSON_IN_ARRAY)  sb.append("[");

//x        for (Ui idx = 0; list[idx]; idx++) {
        boolean first = true;
        for (Json json : list) {
            if (!first) {
                sb.append(",");
            }

            if (type == JSON_IN_OBJECT) {
                //if (list[idx]->key) {
                sb.append("\"");
                sb.append(json.key);
                sb.append("\"");
                //  }
                //else {
                //  _i64toa_s(list[idx]->ikey, num_key, sizeof num_key, 10);
                //  Expandable_AppendString(sb, num_key);
                //  }

                sb.append(":");
            }

            stringifyNode(json);

            if (first) { first = false; }
        }

//x        if (type == JSON_IN_OBJECT)  Expandable_AppendString(sb, "}");
//x        if (type == JSON_IN_ARRAY)  Expandable_AppendString(sb, "]");
        if (type == JSON_IN_OBJECT)  sb.append("}");
        if (type == JSON_IN_ARRAY)  sb.append("]");
    }


}