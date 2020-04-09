package ru.pyur.tst.tags;


public class A extends Tag {


    public A() {
        tag_name = "a";
        //hasNested = true;
    }




    public void setLink(String link) {
        //maybe check attribute for presence
        addAttribute("href", link);
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