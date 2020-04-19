package ru.pyur.tst;

import java.util.ArrayList;

public class Util {

    public static String[] explode(char separator, String string) {
        ArrayList<String> list = new ArrayList<>();
        int pos = 0;
        int pos_found = 0;
        //int size_of_separator = separator.length();

        for(;;) {
            pos_found = string.indexOf(separator, pos);

            if (pos_found != -1) {
                //StringList_AppendExist( & list, String_Substr(pPrev, pSearch - pPrev) );
                String found_string = string.substring(pos, pos_found);
                list.add(found_string);
                pos = pos_found + 1;  // size_of_separator;
            } else {
                //Str pEnd = string + strlen(string);
                //StringList_AppendExist( & list, String_Substr(pPrev, pEnd - pPrev) );
                String remaining_trail = string.substring(pos, string.length());
                list.add(remaining_trail);
                break;
            }
        }

        return list.toArray(new String[0]);
    }



    public static String[] explode(String separator, String string) {
        ArrayList<String> list = new ArrayList<>();
        int pos = 0;
        int pos_found = 0;
        int size_of_separator = separator.length();

        for(;;) {
            pos_found = string.indexOf(separator, pos);

            if (pos_found != -1) {
                //StringList_AppendExist( & list, String_Substr(pPrev, pSearch - pPrev) );
                String found_string = string.substring(pos, pos_found);
                list.add(found_string);
                pos = pos_found + size_of_separator;
            } else {
                //Str pEnd = string + strlen(string);
                //StringList_AppendExist( & list, String_Substr(pPrev, pEnd - pPrev) );
                String remaining_trail = string.substring(pos, string.length());
                list.add(remaining_trail);
                break;
            }
        }

        return list.toArray(new String[0]);
    }




    public static PStr split(char separator, String string) {
        PStr pair = new PStr();
        int pos = 0;
        int pos_found = 0;
        //int size_of_separator = separator.length();

        pos_found = string.indexOf(separator, pos);

        if (pos_found != -1) {
            String found_string = string.substring(pos, pos_found);
            pair.key = found_string;
            pos = pos_found + 1;  // size_of_separator;

            String remaining_trail = string.substring(pos, string.length());
            pair.value = remaining_trail;
        }

        else {
            String remaining_trail = string.substring(pos, string.length());
            pair.key = remaining_trail;
        }

        return pair;
    }



    public static String implode(String separator, ArrayList<String> strings) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (String string: strings) {
            if (!first) sb.append(separator);
            sb.append(string);
            if (first) first = false;
        }

        return sb.toString();
    }




    public static String stripExtension(String str) {
        if (str == null)  return null;

        int pos = str.lastIndexOf(".");

        if (pos == -1)  return str;

        return  str.substring(0, pos);
    }



    // ---- contains ---- //
    public static boolean inArray(String[] str_arr, String string) {

        for (String str: str_arr) {
            if (string.equals(str))  return true;
        }

        return false;
    }


}