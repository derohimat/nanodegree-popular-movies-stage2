package net.derohimat.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MovieDao extends RealmObject implements Parcelable {
    @PrimaryKey
    private long id;

    private boolean adult;
    private String backdrop_path;
//    private List<Integer> genre_ids;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private double popularity;
    private String title;
    private boolean video;
    private double vote_average;
    private int vote_count;
    private boolean favorite;

    public boolean isAdult() {
        return adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

//    public List<Integer> getGenre_ids() {
//        return genre_ids;
//    }

    public long getId() {
        return id;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public MovieDao() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.backdrop_path);
//        dest.writeList(this.genre_ids);
        dest.writeString(this.original_language);
        dest.writeString(this.original_title);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
        dest.writeString(this.poster_path);
        dest.writeDouble(this.popularity);
        dest.writeString(this.title);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.vote_average);
        dest.writeInt(this.vote_count);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
    }

    protected MovieDao(Parcel in) {
        this.id = in.readLong();
        this.adult = in.readByte() != 0;
        this.backdrop_path = in.readString();
//        this.genre_ids = new ArrayList<Integer>();
//        in.readList(this.genre_ids, Integer.class.getClassLoader());
        this.original_language = in.readString();
        this.original_title = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.poster_path = in.readString();
        this.popularity = in.readDouble();
        this.title = in.readString();
        this.video = in.readByte() != 0;
        this.vote_average = in.readDouble();
        this.vote_count = in.readInt();
        this.favorite = in.readByte() != 0;
    }

    public static final Creator<MovieDao> CREATOR = new Creator<MovieDao>() {
        @Override
        public MovieDao createFromParcel(Parcel source) {
            return new MovieDao(source);
        }

        @Override
        public MovieDao[] newArray(int size) {
            return new MovieDao[size];
        }
    };
}