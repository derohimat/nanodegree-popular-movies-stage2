package net.derohimat.mademovie.db;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String TABLE_MOVIE = "movie";

    /*
    Penggunaan Base Columns akan memudahkan dalam penggunaan suatu table
    Untuk id yang autoincrement sudah default ada di dalam kelas BaseColumns dengan nama field _ID
     */
    public static final class MovieColumns implements BaseColumns {
        public static final String _ID = "_id";
        public static final String IS_ADULT = "adult";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String ORIGINAL_LANGUAGE = "original_language";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";
        public static final String POSTER_PATH = "poster_path";
        public static final String POPULARITY = "popularity";
        public static final String TITLE = "title";
        public static final String VIDEO = "video";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String VOTE_COUNT = "vote_count";
        public static final String IS_FAVORITE = "favorite";
    }

    // Authority yang digunakan
    public static final String CONTENT_AUTHORITY = "net.derohimat.popularmovies";

    // Base content yang digunakan untuk akses content provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_MOVIE)
            .build();

    /*
    Digunakan untuk mempermudah akses data di dalam cursor dengan parameter nama column
    */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

}
