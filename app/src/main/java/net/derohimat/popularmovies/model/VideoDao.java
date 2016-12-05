package net.derohimat.popularmovies.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class VideoDao extends RealmObject{
    @PrimaryKey
    private long id;
    private String iso_639_1;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    public long getId() {
        return id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}