package ru.pyur.tst.tags;


public class A extends Tag {


    public A() {
        tag_name = "a";
        //hasNested = true;
    }



    //todo: remove
    public void setLink(String link) {
        //maybe check attribute for presence
        addAttribute("href", link);
    }



    public void setHref(Url href) {
        //maybe check attribute for presence
        addAttribute("href", href.toString());
    }





//    @Override
//    public String renderNested() {
//        StringBuilder sb = new StringBuilder();
//
//        for (Tag tag : tags) {
//            sb.append(tag.render());
//        }
//
//        return sb.toString();
//    }


}