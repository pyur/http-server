package ru.pyur.tst.json;

import java.util.ArrayList;


public class Json {

    public static final int JSON_TYPE_OBJECT = 1;
    public static final int JSON_TYPE_ARRAY = 2;
    public static final int JSON_TYPE_INTEGER = 3;
    public static final int JSON_TYPE_DOUBLE = 4;
    public static final int JSON_TYPE_STRING = 5;
    public static final int JSON_TYPE_BOOLEAN = 6;
    public static final int JSON_TYPE_NULL = 7;


    private int type;
    private String key;

    private String string;              // type 5
    private ArrayList<Json> lsJson;     // type 1, 2

    //private int integer;                // type 3
    private long integer;              // type 3b
    //private float fnum;               // type 4a
    private double fractional;           // type 4b



    // ---------------- Parser, Stringifier ---------------- //

    private static final int JSON_IN_OBJECT = 1;
    private static final int JSON_IN_ARRAY = 2;

    private static final int JSON_SEEK_KEY = 1;    // only "
    private static final int JSON_SEEK_COLON = 2;  // only :
    private static final int JSON_SEEK_VALUE = 3;  // " 0 or { [
    private static final int JSON_SEEK_COMMA = 4;  // , or ] }


    private String src;
    private int off;

    private StringBuilder sb;

    private int level;




    public Json() {
        type = JSON_TYPE_OBJECT;
        lsJson = new ArrayList<>();
    }


    // -------- without key -------- //

    //public Json(ArrayList<Json> arr) {
    //    type = JSON_TYPE_OBJECT;
    //    lsJson = arr;
    //}


    public Json array() {
        type = JSON_TYPE_ARRAY;
        return this;
    }


    public Json(int num) {
        type = JSON_TYPE_INTEGER;
        integer = num;
    }


    public Json(double num) {
        type = JSON_TYPE_DOUBLE;
        fractional = num;
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

    //public Json(String key, ArrayList<Json> arr) {
    //    type = JSON_TYPE_OBJECT;
    //    this.key = key;
    //    lsJson = arr;
    //}


    public Json(String key, int num) {
        type = JSON_TYPE_INTEGER;
        this.key = key;
        integer = num;
    }


    public Json(String key, double num) {
        type = JSON_TYPE_DOUBLE;
        this.key = key;
        fractional = num;
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


    public ArrayList<Json> toObject() {
        if (type == JSON_TYPE_OBJECT) {
            return lsJson;
        }

        return null;
    }


    public ArrayList<Json> toArray() {
    //todo: public JsonArray getArray() {
        if (type == JSON_TYPE_ARRAY) {
            return lsJson;
        }

        return null;
    }


    public String toString() {
        switch (type) {
            case JSON_TYPE_OBJECT:
                return "[Object]";

            case JSON_TYPE_ARRAY:
                return "[Array]";

            case JSON_TYPE_INTEGER:
                return "" + integer;

            case JSON_TYPE_DOUBLE:
                return "" + fractional;

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



    public int toInt() {
        switch (type) {
            case JSON_TYPE_OBJECT:
                //throw new Exception("not convertible");
                return 0;

            case JSON_TYPE_ARRAY:
                return 0;

            case JSON_TYPE_INTEGER:
                return (int)integer;

            case JSON_TYPE_DOUBLE:
                return (int) fractional;

            case JSON_TYPE_STRING:
                return Integer.parseInt(string);

            case JSON_TYPE_BOOLEAN:
                return (integer == 0) ? 0 : 1;

            case JSON_TYPE_NULL:
                return 0;
        }

        return 0;  // unknown type
    }



    public long toLong() {
        switch (type) {
            case JSON_TYPE_OBJECT:
                //throw new Exception("not convertible");
                return 0L;

            case JSON_TYPE_ARRAY:
                return 0L;

            case JSON_TYPE_INTEGER:
                return integer;

            case JSON_TYPE_DOUBLE:
                return (long) fractional;

            case JSON_TYPE_STRING:
                return Long.parseLong(string);

            case JSON_TYPE_BOOLEAN:
                return (integer == 0) ? 0 : 1;

            case JSON_TYPE_NULL:
                return 0;
        }

        return 0;  // unknown type
    }


    //public float toFloat() {}
    //public double toDouble() {}
    //public boolean toBoolean() {}




    // -------------------------------- Object, Array adder -------------------------------- //

    public void add(Json json) throws Exception {
        if (type != JSON_TYPE_OBJECT && type != JSON_TYPE_ARRAY)  throw new Exception("type not object or array");
        lsJson.add(json);
    }


    public void add(ArrayList<Json> json_list) throws Exception {
        if (type != JSON_TYPE_OBJECT && type != JSON_TYPE_ARRAY) throw new Exception("type not object or array");
        for (Json json : json_list) {
            lsJson.add(json);
        }
    }


    public void add(String key, int value) throws Exception {
        add(new Json(key, value));
    }


    public void add(String key, double value) throws Exception {
        add(new Json(key, value));
    }


    public void add(String key, String value) throws Exception {
        add(new Json(key, value));
    }


    public void add(String key, boolean value) throws Exception {
        add(new Json(key, value));
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

        throw new Exception("node \'" + key + "\' miss");
    }



    public Json getNode(int pos) throws Exception {
        if (type != JSON_TYPE_ARRAY)  throw new Exception("json type not 'array'");

        if (pos > lsJson.size())  throw new Exception("out of boundary");

        return lsJson.get(pos);
    }



    public String getString(String key) throws Exception {
        Json node = getNode(key);
        return node.toString();
    }


    public int getInt(String key) throws Exception {
        Json node = getNode(key);
        return node.toInt();
    }


    public long getLong(String key) throws Exception {
        Json node = getNode(key);
        return node.toLong();
    }



    // ------------------------------------------------------------------------ //
    // -------------------------------- Parser -------------------------------- //
    // ------------------------------------------------------------------------ //

    public Json parse(String json_string) throws Exception {
        if (json_string.length() == 0)  throw new Exception("can't parse empty string.");
        src = json_string;
        off = 0;

        boolean terminate = false;

        for (;;) {
            switch (src.charAt(off)) {
                case '{':
                    off++;
                    type = JSON_TYPE_OBJECT;
                    lsJson = parseNested(src, JSON_IN_OBJECT);
                    terminate = true;
                    break;


                case '[':
                    off++;
                    type = JSON_TYPE_ARRAY;
                    lsJson = parseNested(src, JSON_IN_ARRAY);
                    terminate = true;
                    break;


                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    break;


                default:
                    break;
            }  // switch


            if (terminate)  break;

            off++;
            if (off >= src.length())  throw new Exception("json type detect fail.");
        }  // for


        return this;
    }




    private ArrayList<Json> parseNested(String src, int type) throws Exception {

        ArrayList<Json> list = new ArrayList<>();

        Json node = null;
        String szString;
        String key = null;
        long lame_int;

        int seek = JSON_SEEK_KEY;
        if (type == JSON_IN_ARRAY)  seek = JSON_SEEK_VALUE;
        boolean terminate = false;

        for(;;) {

            switch (src.charAt(off)) {

                // ---------------- Object ---------------- //

                case '{':
                    off++;

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_OBJECT;
                            node.key = key;
                            node.lsJson = parseNested(src, JSON_TYPE_OBJECT);
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_KEY) {
                            //System.out.println("\'{\' whilst seek key");
                            throw new Exception("\'{\' whilst seek key");
                        } else if (seek == JSON_SEEK_COLON) {
                            //System.out.println("\'{\' whilst seek colon");
                            throw new Exception("\'{\' whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("\'{\' whilst seek comma");
                            throw new Exception("\'{\' whilst seek comma");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_OBJECT;
                            node.key = key;
                            node.lsJson = parseNested(src, JSON_TYPE_OBJECT);
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("\'{\' whilst seek comma");
                            throw new Exception("\'{\' whilst seek comma");
                        }
                    }
                    break;


                case '}':

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {

                        if (seek == JSON_SEEK_KEY) {
                            //printf("\'}\' whilst seek key\n");
                            terminate = true;  // in empty object. also fires when ",}" (trailing comma - bug or feature?)
                        } else if (seek == JSON_SEEK_COLON) {
                            //System.out.println("\'}\' whilst seek colon");
                            throw new Exception("\'}\' whilst seek colon");
                        } else if (seek == JSON_SEEK_VALUE) {
                            //System.out.println("\'}\' whilst seek value");
                            throw new Exception("\'}\' whilst seek value");
                        } else if (seek == JSON_SEEK_COMMA) {
                            //printf("\'}\' whilst seek comma\n");
                            //seek = JSON_SEEK_KEY or COMMA;  // redundant
                            terminate = true;
                        }
                    }

                    // ---- in array ---- //
                    else {
                        //System.out.println("\'}\' whilst in array");
                        throw new Exception("\'}\' whilst in array");
                    }

                    break;


                // ---------------- Array ---------------- //

                case '[':
//x                    src[0]++;
                    off++;

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_ARRAY;
                            node.key = key;
                            node.lsJson = parseNested(src, JSON_TYPE_ARRAY);
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_KEY) {
                            //System.out.println("\'[\' whilst seek key");
                            throw new Exception("\'[\' whilst seek key");
                        } else if (seek == JSON_SEEK_COLON) {
                            //System.out.println("\'[\' whilst seek colon");
                            throw new Exception("\'[\' whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("\'[\' whilst seek comma");
                            throw new Exception("\'[\' whilst seek comma");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_ARRAY;
                            node.key = key;
                            node.lsJson = parseNested(src, JSON_TYPE_ARRAY);
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("\'[\' whilst seek comma");
                            throw new Exception("\'[\' whilst seek comma");
                        }
                    }
                    break;


                case ']':
                    //(*string)++;

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        //System.out.println("\']\' whilst in object");
                        throw new Exception("\']\' whilst in object");
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_KEY) {
                            //printf("\']\' whilst seek key\n");
                            terminate = true;  // impossible seek key in array
                        } else if (seek == JSON_SEEK_COLON) {
                            //System.out.println("\']\' whilst seek colon");
                            throw new Exception("\']\' whilst seek colon");
                        } else if (seek == JSON_SEEK_VALUE) {
                            //printf("\n\']\' whilst seek value");
                            terminate = true;  // in empty array
                        } else if (seek == JSON_SEEK_COMMA) {
                            //printf("\']\' whilst seek comma\n");
                            //seek = JSON_SEEK_KEY or COMMA;  // redundant
                            terminate = true;
                        }
                    }
                    break;


                // ---------------- String ---------------- //

                case '"':
//x                    src[0]++;
                    off++;
                    szString = cutString(src);
                    //printf("-%s-", szString);
                    //if (!**string) {
                    //  printf("$");
                    //  terminate = true;
                    //  }

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        if (seek == JSON_SEEK_KEY) {
                            key = szString;
                            seek = JSON_SEEK_COLON;
                        } else if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_STRING;
                            node.key = key;
                            node.string = szString;
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_COLON) {
                            //System.out.println("\'\"\' whilst seek colon");
                            throw new Exception("\'\"\' whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("\'\"\' whilst seek comma");
                            throw new Exception("\'\"\' whilst seek comma");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_STRING;
                            node.string = szString;
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("\'\"\' whilst seek comma");
                            throw new Exception("\'\"\' whilst seek comma");
                        }
                    }
                    break;


                // ---------------- Number ---------------- //

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '-':
                    //(*string)++;
                    szString = cutNumber(src);
                    //printf("=%s=", szString);
                    // recognize number. if at end '.' or 'e' - invalid
                    //json_type = RecognizeNumber(szString);
//x                    lame_int = atoi(szString);
                    lame_int = Long.parseLong(szString);
                    //printf("%d=", lame_int);
                    //if (!*string)  terminate = true;
//x                    String_Destroy(szString);

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        if (seek == JSON_SEEK_KEY) {
                            key = szString;
                            seek = JSON_SEEK_COLON;
                        } else if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_INTEGER;
                            node.key = key;
                            node.integer = lame_int;
                            //node->string = szString;
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_COLON) {
                            //System.out.println("number whilst seek colon");
                            throw new Exception("number whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("number whilst seek comma");
                            throw new Exception("number whilst seek comma");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_VALUE) {
                            node = new Json();
                            node.type = JSON_TYPE_INTEGER;
                            node.integer = lame_int;
                            //node->string = szString;
                            list.add(node);
                            seek = JSON_SEEK_COMMA;
                        } else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("number whilst seek comma");
                            throw new Exception("number whilst seek comma");
                        }
                    }
                    break;


                // ---------------- Boolean, Null ---------------- //
/*
      case 'f':
        if (src[1] && src[1] == 'a') {
          if (src[2] && src[2] == 'l') {
            if (src[3] && src[3] == 's') {
              if (src[4] && src[4] == 'e') {
                // ...
                }
              }
            }
          }
        else {
          printf("\nf, but not false.");
          }
        break;



      case 't':
        if (src[1] && src[1] == 'r') {
          if (src[2] && src[2] == 'u') {
            if (src[3] && src[3] == 'e') {
              // ...
              }
            }
          }
        else {
          printf("\nt, but not true.");
          }
        break;



      case 'n':
        if (src[1] && src[1] == 'u') {
          if (src[2] && src[2] == 'l') {
            if (src[3] && src[3] == 'l') {
              // ...
              }
            }
          }
        else {
          printf("\nn, but not null.");
          }
        break;
*/


                // -------------------------------- colon --------------------------------- //

                case ':':
                    //(*string)++;

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        if (seek == JSON_SEEK_COLON) {
                            seek = JSON_SEEK_VALUE;
                            //printf("set seek to value\n");
                        }
                        //else {  // 1 or 3
                        //  printf("\':\' whilst key or value\n");
                        //  }

                        else if (seek == JSON_SEEK_KEY) {
                            //System.out.println("\':\' whilst seek key");
                            throw new Exception("\':\' whilst seek key");
                        }
                        else if (seek == JSON_SEEK_VALUE) {
                            //System.out.println("\':\' whilst seek value");
                            throw new Exception("\':\' whilst seek value");
                        }
                        else if (seek == JSON_SEEK_COMMA) {
                            //System.out.println("\':\' whilst seek comma");
                            throw new Exception("\':\' whilst seek comma");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        //System.out.println("\':\' separator in array");
                        throw new Exception("\':\' separator in array");
                    }
                    break;



                // -------------------------------- comma --------------------------------- //

                case ',':
                    //(*string)++;

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        if (seek == JSON_SEEK_COMMA) {
                            seek = JSON_SEEK_KEY;
                        }
                        else if (seek == JSON_SEEK_KEY) {
                            //System.out.println("\',\' whilst seek key");
                            throw new Exception("\',\' whilst seek key");
                        }
                        else if (seek == JSON_SEEK_COLON) {
                            //System.out.println("\',\' whilst seek colon");
                            throw new Exception("\',\' whilst seek colon");
                        }
                        else if (seek == JSON_SEEK_VALUE) {
                            //System.out.println("\',\' whilst seek value");
                            throw new Exception("\',\' whilst seek value");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_COMMA) {
                            seek = JSON_SEEK_VALUE;
                        }
                        else if (seek == JSON_SEEK_VALUE) {
                            //System.out.println("\',\' whilst seek value");
                            throw new Exception("\',\' whilst seek value");
                        }
                    }
                    break;



                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    break;


                case '\0':  // ??
                    //System.out.println("json parse error. unexpected (0).");
                    throw new Exception("json parse error. unexpected (0) end of string.");
                    //terminate = true;
                    //break;


                default:
                    System.out.println("json parse fail. unexpected symbol: %c (%02x)" + src.charAt(off) + " (" + (int)src.charAt(off) + ")");
                    break;
            }  // switch



            if (terminate)  break;

            off++;
            if (off >= src.length())  throw new Exception("json parse error. unexpected end of string.");
        }  // for


        return list;
    }




    private String cutString(String src) throws Exception {
        // отмерить до конца
        // malloc
        // memcpy
        // unescape прилагается

        int off_end = off;
        char at_end;

        for (;;) {
            at_end = src.charAt(off_end);
            // ---- process escape character ---- //
            if (at_end == '\\') {
                off_end++;
                at_end = src.charAt(off_end);
                //escape = Json_CutEscape(&end);
                // ---- lame process ---- //
                if (at_end == 0)  break;
                if (at_end == '"') { off_end++;  continue; }
            }

            if (at_end == '"')  break;
            if (at_end == 0)  break;  // end--;

            off_end++;
            if (off_end >= src.length())  throw new Exception("json parse error. cut string. unexpected end of string.");
        }

        String szCrop = src.substring(off, off_end);

        String szUnesc = jsonUnescape(szCrop);
//x        String szUnesc = szCrop;  // todo: temporary

        off = off_end;

        return szUnesc;
    }




    private String cutNumber(String src) throws Exception {

//x        Str end = src[0];
        int off_end = off;

        off_end++;  // skip first char (e.g. '-')

        for (;;) {
            char at_end = src.charAt(off_end);
            if ((at_end < '0' || at_end > '9') && at_end != '.' && at_end != 'e')  break;
            //printf("+%c+", end[0]);

            off_end++;
            if (off_end >= src.length())  throw new Exception("json parse error. cut string. unexpected end of string.");
        }

//x        size_t len = end - src[0];
//x        Buf crop = malloc(len + 1);
//x        memcpy(crop, src[0], len);
//x        crop[len] = 0;
        String crop = src.substring(off, off_end);

        off_end--;  // rewind to last digit
//x        src[0] = end;
        off = off_end;

        return crop;
    }




    private String jsonUnescape(String src) throws Exception {
//x        Str pEnc = src;
//x        Ui unhex;
//x        char hex[5] = {0, 0, 0, 0, 0};

//x        Expandable exDec = Expandable_Create(256);
        StringBuilder sb = new StringBuilder();

//x        while (pEnc[0]) {
        for (int i = 0; i < src.length(); i++) {
            char pEnc = src.charAt(i);

            if (pEnc == '\\') {
                i++;
                if (i >= src.length())  throw new Exception("out of bound Exception");
                pEnc = src.charAt(i);  // possible
//x                if (!pEnc[0])  break;

                switch (pEnc) {
                    //case '0':  // 0x00  // not listed in RFC
                    //  break;

                    case 'n':  // 0x0A
//x                        Expandable_Append(exDec, "\n", 1);
                        sb.append("\n");
                        break;

                    case 'r':  // 0x0D
//x                        Expandable_Append(exDec, "\r", 1);
                        sb.append("\r");
                        break;

                    case 't':  // 0x09
//x                        Expandable_Append(exDec, "\t", 1);
                        sb.append("\t");
                        break;

                    case 'b':  // 0x0C
//x                        Expandable_Append(exDec, "\b", 1);  // ?
                        sb.append("\b");
                        break;

                    case 'f':  // 0x08
//x                        Expandable_Append(exDec, "\f", 1);  // ?
                        sb.append("\f");
                        break;

                    case '"':  // 0x22
//x                        Expandable_Append(exDec, "\"", 1);
                        sb.append("\"");
                        break;

                    case '\\':  // 0x2F
//x                        Expandable_Append(exDec, "\\", 1);
                        sb.append("\\");
                        break;

                    case '/':  // 0x5C
//x                        Expandable_Append(exDec, "/", 1);
                        sb.append("/");
                        break;


                    case 'u':
//x                        if (pEnc[1] == 0 || pEnc[2] == 0 || pEnc[3] == 0)  break;
                        if (i >= src.length()-3)  throw new Exception("out of bound Exception");

                        i++;
//x                        pEnc++;
//x                        //hex[0] = pEnc[0];  hex[1] = pEnc[1];  hex[2] = pEnc[2];  hex[3] = pEnc[3];  // little endian
//x                        //hex[2] = pEnc[0];  hex[3] = pEnc[1];  hex[0] = pEnc[2];  hex[1] = pEnc[3];  // big endian
//x                        hex[0] = pEnc[2];  hex[1] = pEnc[3];  // only lower byte
//x                        //((unsigned int*)&hex)[0] = ((unsigned int*)pEnc)[0];
//x                        unhex = String_HexToInt(hex);
                        char unhexed = jsonUnhex(src.substring(i, i+4));  // limitation. only 4 digits.
                        sb.append(unhexed);
//x                        //swap bytes?
//x                        pEnc += 3;
                        i += 3;

//x                        //DebugDump((Str)&unhex, 4);
//x                        //Expandable_Append(exDec, (Str)&unhex, 2);
//x                        //Expandable_Append(exDec, &((Str)&unhex)[3], 1);
//x                        Expandable_Append(exDec, (Str)&unhex, 1);
                        break;



                    default:
//x                        Expandable_Append(exDec, pEnc, 1);
                        sb.append(pEnc);
                        break;
                }
            }

            else {
//x                Expandable_Append(exDec, pEnc, 1);
                sb.append(pEnc);
            }

            pEnc++;
        }

//x        Expandable_Append(exDec, "", 1);


//x        return Expandable_ConvertToString(&exDec);
        return sb.toString();
    }




    private char jsonUnhex(String hex) {
        int nibble;
        char val = 0;

        for (int i = 0; i < hex.length(); i++) {

            nibble = hex.charAt(i);

            if (nibble >= '0' && nibble <= '9') nibble = nibble - '0';
            else if (nibble >= 'a' && nibble <= 'f') nibble = nibble - 'a' + 10;
            else if (nibble >= 'A' && nibble <= 'F') nibble = nibble - 'A' + 10;
            else break;

            val <<= 4;
            val |= nibble & 0xF;
        }

        return val;
    }




    // ---------------------------------------------------------------------- //
    // -------------------------------- Dump -------------------------------- //
    // ---------------------------------------------------------------------- //


    public void dump(Json json) {
        level = 0;
        if (json.type == JSON_TYPE_OBJECT) {
            dumpNested(json.lsJson, JSON_IN_OBJECT);
        }

        else if (json.type == JSON_TYPE_ARRAY) {
            dumpNested(json.lsJson, JSON_IN_ARRAY);
        }

        else {
            dumpNode(json);
        }
    }


    public void dumpNode(Json json) {
        //printf("level: %d\n", *level);

        switch (json.type) {
            case JSON_TYPE_OBJECT:
                System.out.print("object ");
//      printf("{");
                level++;
                dumpNested(json.lsJson, json.type);
                for (int i = 0; i < level; i++)  System.out.print("  ");
                level--;
//      printf("\n}");
                break;

            case JSON_TYPE_ARRAY:
                System.out.print("array ");
//      printf("[");
                level++;
                dumpNested(json.lsJson, json.type);
                for (int i = 0; i < level; i++)  System.out.print("  ");
                level--;
//      printf("\n]");
                break;

            case JSON_TYPE_INTEGER:
                System.out.print("integer [" + json.integer + "]");
                break;

            case JSON_TYPE_DOUBLE:
                System.out.print("double [" + json.fractional + "]");
                break;

            case JSON_TYPE_STRING:
                System.out.print("string [" + json.string + "]");
                break;

            case JSON_TYPE_BOOLEAN:
                System.out.print("boolean [" + ((json.integer == 0) ? "false" : "true") + "]");
                break;

            case JSON_TYPE_NULL:
                System.out.print("null");
                break;


            default:
                System.out.print("unknown type");
                break;
        }

    }



    private void dumpNested(ArrayList<Json> list, int type) {

        if (type == JSON_IN_OBJECT)  System.out.print("{");
        if (type == JSON_IN_ARRAY)  System.out.print("[");

        for (Json json : list) {
            //printf("level: %d\n", *level);
            System.out.print("\n");
            for (int i = 0; i < level; i++)  System.out.print("  ");
            //printf("%d. ", idx);

            if (type == JSON_IN_OBJECT) {
                System.out.print("\"" + json.key + "\" : ");
            }

            dumpNode(json);
        }

        if (type == JSON_IN_OBJECT) {
            System.out.print("\n");
            for (int i = 0; i < level; i++)  System.out.print("  ");
            System.out.print("}");
        }
        if (type == JSON_IN_ARRAY) {
            System.out.print("\n");
            for (int i = 0; i < level; i++)  System.out.print("  ");
            System.out.print("]");
        }
    }




    // ----------------------------------------------------------------------------- //
    // -------------------------------- Stringifier -------------------------------- //
    // ----------------------------------------------------------------------------- //

    public String stringify() {
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

        return sb.toString();
    }




    private void stringifyNode(Json json) {

        switch (json.type) {
            case JSON_TYPE_OBJECT:
                stringifyNested(json.lsJson, JSON_IN_OBJECT);
                break;

            case JSON_TYPE_ARRAY:
                stringifyNested(json.lsJson, JSON_IN_ARRAY);
                break;

            case JSON_TYPE_INTEGER:
                sb.append(json.integer);
                break;

            case JSON_TYPE_DOUBLE:
                sb.append(json.fractional);
                break;

            case JSON_TYPE_STRING:
                sb.append("\"");
                sb.append(jsonEscape(json.string));
                sb.append("\"");
                break;

            case JSON_TYPE_BOOLEAN:
                sb.append((json.integer == 0) ? "false" : "true");
                break;

            case JSON_TYPE_NULL:
                sb.append("null");
                break;


            default:
                System.out.println("json stringify. error: unknown type.");
                break;
        }

    }



    private void stringifyNested(ArrayList<Json> list, int type) {
        //char num_key[32];  // max 20 +1 zero terminator

        if (type == JSON_IN_OBJECT)  sb.append("{");
        if (type == JSON_IN_ARRAY)  sb.append("[");

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

        if (type == JSON_IN_OBJECT)  sb.append("}");
        if (type == JSON_IN_ARRAY)  sb.append("]");
    }




    private String jsonEscape(String src) {
//x        Str pDec = src;
        //unsigned int unhex;
//x        char hex[7] = { '\\', 'u', '0', '0', '0', '0' };
//x        Buf hex_low = &hex[4];

//x        Expandable exEnc = Expandable_Create();
        StringBuilder sb = new StringBuilder();

//x        while (*pDec) {
        for (int i = 0; i < src.length(); i++) {
            char pDec = src.charAt(i);
            if (pDec == '"') {
//x                Expandable_Append(exEnc, "\\\"", 2);
                sb.append("\\\"");
            }

            else if (pDec == '\n') {
//x                Expandable_Append(exEnc, "\\n", 2);
                sb.append("\\n");
            }

            else if (pDec == '\r') {
//x                Expandable_Append(exEnc, "\\r", 2);
                sb.append("\\r");
            }

            else if (pDec == '\t') {
//x                Expandable_Append(exEnc, "\\t", 2);
                sb.append("\\t");
            }

            //else if (*pDec == 0) {
            //  Expandable_Append(exEnc, "\\0", 2);
            //  }

            else if (pDec == '\b') {
//x                Expandable_Append(exEnc, "\\b", 2);
                sb.append("\\b");
            }

            else if (pDec == '\f') {
//x                Expandable_Append(exEnc, "\\f", 2);
                sb.append("\\f");
            }


            else if (pDec == '\\') {
//x                Expandable_Append(exEnc, "\\\\", 2);
                sb.append("\\\\");
            }

            else if (pDec == '/') {
//x                Expandable_Append(exEnc, "\\/", 2);
                sb.append("\\/");
            }


            //else if (*pDec < 32  || *pDec > 127) {
            //
            //  }

            else if (pDec < 32) {
//x                String_ByteToHex_Buff(hex_low, *pDec);
//x                Expandable_Append(exEnc, hex, 5);
                sb.append("\\u");
                sb.append(charToHex(pDec));
            }


            else {
//x                Expandable_Append(exEnc, pDec, 1);
                sb.append(pDec);
            }

//x            pDec++;
        }

//x        Expandable_Append(exEnc, "", 1);


//x        return Expandable_ConvertToString(&exEnc);
        return sb.toString();
    }



    private String charToHex(char ch) {
        //char hex[] = {'0', '1', '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 'A' , 'B' , 'C' , 'D' , 'E' , 'F'};
        char hex[] = {'0', '1', '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 'a' , 'b' , 'c' , 'd' , 'e' , 'f'};

        char ch1 = hex[(ch & 0xF000) >> 12];
        char ch2 = hex[(ch & 0x0F00) >> 8];
        char ch3 = hex[(ch & 0x00F0) >> 4];
        char ch4 = hex[ch & 0x000F];

        return "" + ch1 + ch2 + ch3 + ch4;
    }



}