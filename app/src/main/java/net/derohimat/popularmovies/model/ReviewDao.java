package net.derohimat.popularmovies.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReviewDao extends RealmObject{
    @PrimaryKey
    private long id;
    private String author;
    private String content;
    private String url;

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}