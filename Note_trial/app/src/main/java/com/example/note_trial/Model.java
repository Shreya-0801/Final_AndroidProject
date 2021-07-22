package com.example.note_trial;

public class Model {

    String content;
    String title;
   // String url;



    public Model()
    {

    }

    public  Model (String title, String content)
    {
        this.title=title;
        this.content=content;
    }
    /*public Model(String url) {
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
*/


    public  String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public  String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

