package ru.pyur.tst.tags;

import ru.pyur.tst.PStr;
import ru.pyur.tst.Util;

import java.util.ArrayList;

public class Tag {

    protected StringBuilder text;

    protected String tag;

    protected boolean closing = true;
    // false for td, tr
    protected boolean self_closing = false;
    // img, input, br, hr
    // iframe
    // link, meta (head)
    // source, track (video / audio)
    // area, base, embed, param

    //protected boolean clo = false;

    protected boolean hasPre = false;
    protected boolean hasNested = false;


    protected String id;
    protected String name;
    //protected String clas;
    protected ArrayList<String> classes = new ArrayList<>();

    protected ArrayList<PStr> style;

    protected String width;
    protected String height;

    protected String border;






    public Tag() {
        //System.out.println("tag constructor");
        text = new StringBuilder();
        style = new ArrayList<>();
    }


    public void put(String str) { text.append(str); }


    public void width(int v) { width = v + "px"; }

    public void height(int v) { height = v + "px"; }

    public void border(String v) { border = v; }




    public String renderPre() { return null; }

    public String renderNested() { return null; }



    public String render() {
        StringBuilder t = new StringBuilder();

        if (hasPre) {
            t.append(renderPre());
        }

        t.append("<");
        t.append(tag);

        if (id != null) {
            t.append(" id=\"");
            t.append(id);
            t.append("\"");
        }

        if (name != null) {
            t.append(" name=\"");
            t.append(name);
            t.append("\"");
        }

        if (classes.size() != 0) {
            t.append(" class=\"");
            t.append(Util.implode(" ", classes));
            t.append("\"");
        }

        // -------- style -------- //

        if (width != null)  style.add(new PStr("width", width));
        if (height != null)  style.add(new PStr("height", height));

        if (border != null)  style.add(new PStr("border", border));


        // ---- implode ---- //

        if (style.size() != 0) {
            StringBuilder style_a = new StringBuilder();
            boolean first = true;

            for (PStr st : style) {
                if (!first) { style_a.append("; "); }
                style_a.append(st.key);
                style_a.append(": ");
                style_a.append(st.value);
                if (first) { first = false; }
            }

            t.append(" style=\"");
            t.append(style_a.toString());
            t.append("\"");
        }

        if (self_closing) { t.append(" /"); }
        t.append(">");


        if (hasNested) {
            t.append(renderNested());
        }

        //if ...
        t.append(text.toString());


        if (closing) {
            t.append("</");
            t.append(tag);
            t.append(">");
        }

        return t.toString();
    }


}