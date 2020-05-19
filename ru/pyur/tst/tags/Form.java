package ru.pyur.tst.tags;


public class Form extends Tag {


    public Form() {
        tag_name = "form";
    }


    public Form(String id) {
        tag_name = "form";
        setId(id);
    }


    public void setUrl(String url) {
        addAttribute("action", url);
    }

    public void setOnSubmit(String script) {
        addAttribute("onsubmit", script);
    }



/*
    @Override
    public String renderAfterTag() {
        StringBuilder sb = new StringBuilder();


        // ---- start form script ---- //

        sb.append("\r\n<script>\r\n");
        sb.append("OnScriptLoaded(function(){");
        sb.append("FormSend('");
        sb.append(id);
        sb.append("', null, null");

//        sb.append("', [");

        // -- callback functions -- //
//        boolean first = true;
//        for (ActionButton ab : actions) {
//            if (!first)  sb.append(", ");
//            sb.append("function (row_id) { ");
//            //sb.append("alert(\"func " + ab.icon + " : \" + row_id);}");
//            sb.append(ab.getFunction());
//            sb.append(" }");
//            if (first)  first = false;
//        }
//
//        sb.append("]);\r\n");
        sb.append(");}");
        sb.append(");\r\n");
        sb.append("</script>\r\n");

        return sb.toString();
    }
*/

}