package net.derohimat.popularmovies.data.provider;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    //Database schema information
    public static final String TABLE_MOVIE = "movie";

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

    //Unique authority string for the content provider
    public static final String CONTENT_AUTHORITY = "net.derohimat.popularmovies";

    /* Sort order constants */
    //Priority first, Completed last, the rest by date
    public static final String DEFAULT_SORT = String.format("%s ASC, %s DESC, %s ASC",
            MovieColumns.IS_FAVORITE, MovieColumns.IS_ADULT, MovieColumns.RELEASE_DATE);

    //Completed last, then by date, followed by priority
    public static final String DATE_SORT = String.format("%s ASC, %s ASC, %s DESC",
            MovieColumns.IS_FAVORITE, MovieColumns.RELEASE_DATE, MovieColumns.IS_ADULT);

    //Base content Uri for accessing the provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_MOVIE)
            .build();

    /* Helpers to retrieve column values */
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