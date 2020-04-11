package ru.pyur.tst.tags;

import ru.pyur.tst.PStr;
import ru.pyur.tst.Util;

import java.util.ArrayList;

public class Tag {

    //protected StringBuilder text = new StringBuilder();
    protected String plainText;

    private ArrayList<Tag> tags = new ArrayList<>();


    protected String tag_name;

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
    //protected boolean hasNested = false;


    protected String id;
    protected String name;

    protected ArrayList<PStr> attributes = new ArrayList<>();

    protected ArrayList<String> classes = new ArrayList<>();

    protected ArrayList<PStr> styles = new ArrayList<>();

    //protected String width;
    //protected String height;

    //protected String border;






    public Tag() {
        //System.out.println("tag constructor");
    }


    public void put(String str) {
        //text.append(str);
        tags.add(new PlainText(str));
    }


    public void add(Tag tag) {
        tags.add(tag);
    }



    public void width(int v) { styles.add(new PStr("width", v + "px")); }

    public void height(int v) { styles.add(new PStr("height", v + "px")); }

    public void border(String v) { styles.add(new PStr("border", v)); }


    public void addAttribute(String name, String value) {
        attributes.add(new PStr(name, value));
    }



    public String renderPre() { return null; }

    public String renderNested() { return null; }



    @Override
    public String toString() {
        if (plainText != null)  return plainText;

        StringBuilder t = new StringBuilder();

        if (hasPre) {
            t.append(renderPre());
        }

        t.append("<");
        t.append(tag_name);

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

        //if (width != null)  styles.add(new PStr("width", width));
        //if (height != null)  styles.add(new PStr("height", height));

        //if (border != null)  styles.add(new PStr("border", border));


        // ---- implode ---- //

        if (styles.size() != 0) {
            StringBuilder style_a = new StringBuilder();
            boolean first = true;

            for (PStr st : styles) {
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


        // ---- attributes ---- //

        for (PStr attribute : attributes) {
            t.append(" ");
            t.append(attribute.key);
            if (attribute.value != null) {
                t.append("=\"");
                t.append(attribute.value);
                t.append("\"");
            }
        }


        if (self_closing) { t.append(" /"); }
        t.append(">");


        //if (hasNested) {
        String nested = renderNested();
        if (nested != null)  t.append(nested);
        //}

        for (Tag tag : tags) {
            t.append(tag.toString());
        }


//        t.append(text.toString());


        if (closing) {
            t.append("</");
            t.append(tag_name);
            t.append(">");
        }

        return t.toString();
    }


}