package ru.pyur.tst.tags;

import ru.pyur.tst.Coordinate;
import ru.pyur.tst.PStr;
import ru.pyur.tst.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public abstract class Tag {

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

    private ArrayList<PStr> attributes = new ArrayList<>();

    private ArrayList<String> classes = new ArrayList<>();

    private ArrayList<PStr> styles = new ArrayList<>();



    // -------- Config -------- //

    private static final String CONFIG_URL = "jdbc:sqlite:config.db";

    private Connection m_config;


    private boolean action_icons_fetched = false;
    private ArrayList<SpriteIcon> action_icons = new ArrayList<>();

    private class SpriteIcon {
        public String name;
        public int x;
        public int y;

        public SpriteIcon(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }




    //public Tag() {
        //System.out.println("tag constructor");
    //}


    public void put(String str) {
        tags.add(new PlainText(str));
    }


    public void add(Tag tag) {
        tags.add(tag);
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addClass(String class_name) {
        classes.add(class_name);
    }

    public void addStyle(String name, String value) { styles.add(new PStr(name, value)); }


    public void width(int v) { styles.add(new PStr("width", v + "px")); }

    public void height(int v) { styles.add(new PStr("height", v + "px")); }

    public void border(String v) { styles.add(new PStr("border", v)); }


    public void addAttribute(String name, String value) {
        attributes.add(new PStr(name, value));
    }


    public void setUnselectable() { attributes.add(new PStr("unselectable", "on")); }


    public String renderPre() { return null; }

    public String renderNested() { return null; }

    public String renderNestedPost() { return null; }



    @Override
    public String toString() {
        if (tag_name == null) {
            if (plainText == null)  return "[NULL]";
            return plainText;
        }

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


        String nested_post = renderNestedPost();
        if (nested_post != null)  t.append(nested_post);



        if (closing) {
            t.append("</");
            t.append(tag_name);
            t.append(">");
        }

        return t.toString();
    }




    // ---- Sprite ---- //

    protected Coordinate getActionCoord(String icon_name) {
        if (!action_icons_fetched) {
            fetchActionIcons();
            action_icons_fetched = true;
        }

        for (SpriteIcon icon : action_icons) {
            if (icon_name.equals(icon.name))  return new Coordinate(icon.x, icon.y);
        }

        return new Coordinate(0, 0);  // return default icon 'n/a'
        // maybe throw exception
    }



    private void fetchActionIcons() {
        if (m_config == null) {
            try {
                m_config = DriverManager.getConnection(CONFIG_URL);
                //todo: use 'DataSource' class
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }


        String query = "SELECT `name`, `position` FROM `action_icon`";  // ORDER BY `position`";

        try {
            Statement st = m_config.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String icon_name = rs.getString(1);
                int icon_pos = rs.getInt(2);
                int y = (icon_pos / 64) * 16;
                int x = (icon_pos % 64) * 16;
                action_icons.add(new SpriteIcon(icon_name, x, y));
            }

        } catch (Exception e) { e.printStackTrace(); }

    }


}