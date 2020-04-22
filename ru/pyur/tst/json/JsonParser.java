package ru.pyur.tst.json;

import java.util.ArrayList;

import static ru.pyur.tst.json.Json.*;


public class JsonParser {

    public static final int JSON_IN_OBJECT = 1;
    public static final int JSON_IN_ARRAY = 2;

    private static final int JSON_SEEK_KEY = 1;    // only "
    private static final int JSON_SEEK_COLON = 2;  // only :
    private static final int JSON_SEEK_VALUE = 3;  // " 0 or { [
    private static final int JSON_SEEK_COMMA = 4;  // , or ] }


    private String src;
    private int off;



    // -------------------------------- Parser -------------------------------- //

//    public Json parse(String json_string) {
//        src = json_string;
//        return parseFromRoot();
//        //Json json_tree = parseFromRoot();
//        //return json_tree.getObject();
//    }




//    private Json parseFromRoot() {
    public Json parse(String json_string) {
        src = json_string;
        off = 0;

        Json node = new Json();
        boolean terminate = false;

        for (;;) {
//x            switch (json_string[0]) {
            switch (src.charAt(off)) {
                case '{':
//x                    json_string++;
                    off++;
                    node.type = JSON_TYPE_OBJECT;
                    node.lsJson = parseNested(src, JSON_IN_OBJECT);
                    terminate = true;
                    break;


                case '[':
//x                    json_string++;
                    off++;
                    node.type = JSON_TYPE_ARRAY;
                    node.lsJson = parseNested(src, JSON_IN_ARRAY);
                    terminate = true;
                    break;


                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    break;


                default:
                    //throw new Exception("json type detect fail.");
                    break;
            }  // switch


            if (terminate)  break;

//x            json_string++;
            off++;
            if (off < src.length())  break;
        }  // for


        return node;
    }




    private ArrayList<Json> parseNested(String src, int type) {

        ArrayList<Json> list = new ArrayList<>();

        Json node = null;
        String szString;
        String key = null;
        long lame_int;

        int seek = JSON_SEEK_KEY;
        if (type == JSON_IN_ARRAY)  seek = JSON_SEEK_VALUE;
        boolean terminate = false;

        for(;;) {
            //printf("(%c)", (**string));

            //if (*string >= end) {
            //  printf("$");
            //  break;
            //  }

//x            switch (src[0][0]) {
            switch (src.charAt(off)) {

                // ---------------- Object ---------------- //

                case '{':
//x                    src[0]++;
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
                            System.out.println("\'{\' whilst seek key");
                        } else if (seek == JSON_SEEK_COLON) {
                            System.out.println("\'{\' whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            System.out.println("\'{\' whilst seek comma");
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
                            System.out.println("\'{\' whilst seek comma");
                        }
                    }
                    break;


                case '}':
                    //(*string)++;

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {

                        if (seek == JSON_SEEK_KEY) {
                            //printf("\'}\' whilst seek key\n");
                            terminate = true;  // in empty object. also fires when ",}" (trailing comma - bug or feature?)
                        } else if (seek == JSON_SEEK_COLON) {
                            System.out.println("\'}\' whilst seek colon");
                        } else if (seek == JSON_SEEK_VALUE) {
                            System.out.println("\'}\' whilst seek value");
                        } else if (seek == JSON_SEEK_COMMA) {
                            //printf("\'}\' whilst seek comma\n");
                            //seek = JSON_SEEK_KEY or COMMA;  // redundant
                            terminate = true;
                        }
                    }

                    // ---- in array ---- //
                    else {
                        System.out.println("\'}\' whilst in array");
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
                            System.out.println("\'[\' whilst seek key");
                        } else if (seek == JSON_SEEK_COLON) {
                            System.out.println("\'[\' whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            System.out.println("\'[\' whilst seek comma");
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
                            System.out.println("\'[\' whilst seek comma");
                        }
                    }
                    break;


                case ']':
                    //(*string)++;

                    // ---- in object ---- //
                    if (type == JSON_IN_OBJECT) {
                        System.out.println("\']\' whilst in object");
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_KEY) {
                            //printf("\']\' whilst seek key\n");
                            terminate = true;  // impossible seek key in array
                        } else if (seek == JSON_SEEK_COLON) {
                            System.out.println("\']\' whilst seek colon");
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
                            System.out.println("\'\"\' whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            System.out.println("\'\"\' whilst seek comma");
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
                            System.out.println("\'\"\' whilst seek comma");
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
                            System.out.println("number whilst seek colon");
                        } else if (seek == JSON_SEEK_COMMA) {
                            System.out.println("number whilst seek comma");
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
                            System.out.println("number whilst seek comma");
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
                            System.out.println("\':\' whilst seek key");
                        }
                        else if (seek == JSON_SEEK_VALUE) {
                            System.out.println("\':\' whilst seek value");
                        }
                        else if (seek == JSON_SEEK_COMMA) {
                            System.out.println("\':\' whilst seek comma");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        System.out.println("\':\' separator in array");
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
                            System.out.println("\',\' whilst seek key");
                        }
                        else if (seek == JSON_SEEK_COLON) {
                            System.out.println("\',\' whilst seek colon");
                        }
                        else if (seek == JSON_SEEK_VALUE) {
                            System.out.println("\',\' whilst seek value");
                        }
                    }

                    // ---- in array ---- //
                    else {
                        if (seek == JSON_SEEK_COMMA) {
                            seek = JSON_SEEK_VALUE;
                        }
                        else if (seek == JSON_SEEK_VALUE) {
                            System.out.println("\',\' whilst seek value");
                        }
                    }
                    break;



                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    break;


                case '\0':
                    System.out.println("json parse error. unexpected (0).");
                    terminate = true;
                    break;


                default:
                    System.out.println("json parse fail. unexpected symbol: %c (%02x)" + src.charAt(off) + " (" + (int)src.charAt(off) + ")");
                    break;
            }  // switch


            //if (!src[0][0]) {
            //  printf("$");
            //  terminate = true;
            //  }

            if (terminate)  break;

//x            src[0]++;
            off++;
        }  // for


        return list;
    }




    private String cutString(String src) {
        // отмерить до конца
        // malloc
        // memcpy
        // unescape прилагается

//x        Str end = src[0];
        int off_end = off;
        char at_end;

        for (;;) {
            at_end = src.charAt(off_end);
            // ---- process escape character ---- //
            if (at_end == '\\') {
//x                end++;
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
        }

//x        size_t len = end - src[0];
//x        Buf szCrop = malloc(len + 1);
//x        memcpy(szCrop, src[0], len);
//x        szCrop[len] = 0;
        String szCrop = src.substring(off, off_end);

//todo        String szUnesc = jsonUnescape(szCrop);
        String szUnesc = szCrop;  // todo: temporary
//x        String_Destroy(szCrop);

//x        src[0] = end;
        off = off_end;

        return szUnesc;
    }




    private String cutNumber(String src) {

//x        Str end = src[0];
        int off_end = off;

        off_end++;  // skip first char (e.g. '-')

        for (;;) {
            char at_end = src.charAt(off_end);
            if ((at_end < '0' || at_end > '9') && at_end != '.' && at_end != 'e')  break;
            //printf("+%c+", end[0]);
            off_end++;
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



/*
    Str String_JsonUnescape(Str src) {
        Str pEnc = src;
        Ui unhex;
        char hex[5] = {0, 0, 0, 0, 0};

        Expandable exDec = Expandable_Create(256);

        while (pEnc[0]) {
            if (pEnc[0] == '\\') {
                pEnc++;
                if (!pEnc[0])  break;

                switch (pEnc[0]) {
                    //case '0':  // 0x00  // not listed in RFC
                    //  Expandable_Append(exDec, "\0", 1);  // ""
                    //  break;

                    case 'n':  // 0x0A
                        Expandable_Append(exDec, "\n", 1);
                        break;

                    case 'r':  // 0x0D
                        Expandable_Append(exDec, "\r", 1);
                        break;

                    case 't':  // 0x09
                        Expandable_Append(exDec, "\t", 1);
                        break;

                    case 'b':  // 0x0C
                        Expandable_Append(exDec, "\b", 1);  // ?
                        break;

                    case 'f':  // 0x08
                        Expandable_Append(exDec, "\f", 1);  // ?
                        break;

                    case '"':  // 0x22
                        Expandable_Append(exDec, "\"", 1);
                        break;

                    case '\\':  // 0x2F
                        Expandable_Append(exDec, "\\", 1);
                        break;

                    case '/':  // 0x5C
                        Expandable_Append(exDec, "/", 1);
                        break;


                    case 'u':
                        if (pEnc[1] == 0 || pEnc[2] == 0 || pEnc[3] == 0)  break;

                        pEnc++;
                        //hex[0] = pEnc[0];  hex[1] = pEnc[1];  hex[2] = pEnc[2];  hex[3] = pEnc[3];  // little endian
                        //hex[2] = pEnc[0];  hex[3] = pEnc[1];  hex[0] = pEnc[2];  hex[1] = pEnc[3];  // big endian
                        hex[0] = pEnc[2];  hex[1] = pEnc[3];  // only lower byte
                        //((unsigned int*)&hex)[0] = ((unsigned int*)pEnc)[0];
                        unhex = String_HexToInt(hex);
                        //swap bytes?
                        pEnc += 3;

                        //DebugDump((Str)&unhex, 4);
                        //Expandable_Append(exDec, (Str)&unhex, 2);
                        //Expandable_Append(exDec, &((Str)&unhex)[3], 1);
                        Expandable_Append(exDec, (Str)&unhex, 1);
                        break;



                    default:
                        Expandable_Append(exDec, pEnc, 1);
                        break;
                }
            }

            else {
                Expandable_Append(exDec, pEnc, 1);
            }

            pEnc++;
        }

        Expandable_Append(exDec, "", 1);


        return Expandable_ConvertToString(&exDec);
    }
*/



    // -------------------------------- Dump -------------------------------- //

    private int level;

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

//x            case JSON_TYPE_UINT:
//x                System.out.print("integer [%d]", json->uint);
//x                break;

//x            case JSON_TYPE_LL:
//x            case JSON_TYPE_ULL:
//x                System.out.print("integer [%d]", json->integer);
//x                break;

            case JSON_TYPE_DOUBLE:
                System.out.print("double ...");
                break;

            case JSON_TYPE_STRING:
                System.out.print("string [" + json.string + "]");
                break;

            case JSON_TYPE_BOOLEAN:
                System.out.print("boolean ...");
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

//x        for (size_t idx = 0; list[idx]; idx++) {
        for (Json json : list) {
            //printf("level: %d\n", *level);
            System.out.print("\n");
            for (int i = 0; i < level; i++)  System.out.print("  ");
            //printf("%d. ", idx);

            if (type == JSON_IN_OBJECT) {  // JSON_TYPE_OBJECT
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

}