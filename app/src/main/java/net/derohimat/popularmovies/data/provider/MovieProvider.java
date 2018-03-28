package net.derohimat.popularmovies.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import net.derohimat.popularmovies.data.provider.DatabaseContract.MovieColumns;
import net.derohimat.popularmovies.model.MovieDao;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;

public class MovieProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://net.derohimat.popularmovies/movies
        sUriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_MOVIE,
                MOVIES);

        // content://net.derohimat.popularmovies/movies/id
        sUriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY,
                DatabaseContract.TABLE_MOVIE + "/#",
                MOVIE_WITH_ID);
    }

    @Override
    public boolean onCreate() {

        //Innitializing RealmDB
        Realm.init(getContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new MyRealmMigration())
                .build();
        Realm.setDefaultConfiguration(config);

        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null; /* Not used */
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        int match = sUriMatcher.match(uri);

        //Get Realm Instance
        Realm realm = Realm.getDefaultInstance();
        MatrixCursor myCursor = new MatrixCursor(new String[]{
                MovieColumns._ID,
                MovieColumns.IS_ADULT,
                MovieColumns.BACKDROP_PATH,
                MovieColumns.ORIGINAL_LANGUAGE,
                MovieColumns.ORIGINAL_TITLE,
                MovieColumns.OVERVIEW,
                MovieColumns.RELEASE_DATE,
                MovieColumns.POSTER_PATH,
                MovieColumns.POPULARITY,
                MovieColumns.TITLE,
                MovieColumns.VIDEO,
                MovieColumns.VOTE_AVERAGE,
                MovieColumns.VOTE_COUNT,
                MovieColumns.IS_FAVORITE
        });

        try {
            switch (match) {
                //Expected "query all" Uri: content://net.derohimat.popularmovies/movies

                case MOVIES:
                    RealmResults<MovieDao> tasksRealmResults = realm.where(MovieDao.class).findAll();
                    for (MovieDao movieDao : tasksRealmResults) {
                        Object[] rowData = new Object[]{
                                movieDao.getId(),
                                movieDao.isAdult(),
                                movieDao.getBackdrop_path(),
                                movieDao.getOriginal_language(),
                                movieDao.getOriginal_title(),
                                movieDao.getOverview(),
                                movieDao.getRelease_date(),
                                movieDao.getPoster_path(),
                                movieDao.getPopularity(),
                                movieDao.getTitle(),
                                movieDao.isVideo(),
                                movieDao.getVote_average(),
                                movieDao.getVote_count(),
                                movieDao.isFavorite()
                        };
                        myCursor.addRow(rowData);
                        Log.v("RealmDB", movieDao.toString());
                    }
                    break;

                //Expected "query one" Uri: content://net.derohimat.popularmovies/movie/{id}
                case MOVIE_WITH_ID:
                    Integer id = Integer.parseInt(uri.getPathSegments().get(1));
                    MovieDao movieDao = realm.where(MovieDao.class).equalTo(MovieColumns._ID, id).findFirst();
                    myCursor.addRow(new Object[]{
                            movieDao.getId(),
                            movieDao.isAdult(),
                            movieDao.getBackdrop_path(),
                            movieDao.getOriginal_language(),
                            movieDao.getOriginal_title(),
                            movieDao.getOverview(),
                            movieDao.getRelease_date(),
                            movieDao.getPoster_path(),
                            movieDao.getPopularity(),
                            movieDao.getTitle(),
                            movieDao.isVideo(),
                            movieDao.getVote_average(),
                            movieDao.getVote_count(),
                            movieDao.isFavorite()
                    });
                    Log.v("RealmDB", movieDao.toString());
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }


            myCursor.setNotificationUri(getContext().getContentResolver(), uri);
        } finally {
            realm.close();
        }
        return myCursor;

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, final ContentValues contentValues) {
        //COMPLETE: Expected Uri: content://net.derohimat.popularmovies/movie

        //final SQLiteDatabase taskDb = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        //Get Realm Instance
        Realm realm = Realm.getDefaultInstance();
        try {
            switch (match) {
                case MOVIES:
                    realm.executeTransaction(realm1 -> {

                        Number currId = realm1.where(MovieDao.class).max(MovieColumns._ID);
                        Integer nextId = (currId == null) ? 1 : currId.intValue() + 1;

                        MovieDao movieDao = realm1.createObject(MovieDao.class, nextId);

                        movieDao.setAdult((Integer) contentValues.get(MovieColumns.IS_ADULT) == 1);
                        movieDao.setBackdrop_path(contentValues.get(MovieColumns.BACKDROP_PATH).toString());
                        movieDao.setOriginal_language(contentValues.get(MovieColumns.ORIGINAL_LANGUAGE).toString());
                        movieDao.setOriginal_title(contentValues.get(MovieColumns.ORIGINAL_TITLE).toString());
                        movieDao.setOverview(contentValues.get(MovieColumns.OVERVIEW).toString());
                        movieDao.setPoster_path(contentValues.get(MovieColumns.POSTER_PATH).toString());
                        movieDao.setRelease_date(contentValues.get(MovieColumns.RELEASE_DATE).toString());
                        movieDao.setPopularity((Double) contentValues.get(MovieColumns.POPULARITY));
                        movieDao.setTitle(contentValues.get(MovieColumns.TITLE).toString());
                        movieDao.setVideo((Integer) contentValues.get(MovieColumns.VIDEO) == 1);
                        movieDao.setVote_average((Double) contentValues.get(MovieColumns.VOTE_AVERAGE));
                        movieDao.setVote_count((Integer) contentValues.get(MovieColumns.VOTE_COUNT));
                        movieDao.setFavorite((Integer) contentValues.get(MovieColumns.IS_FAVORITE) == 1);

                    });
                    returnUri = ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, '1');
                    break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
        } finally {
            realm.close();
        }
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Expected Uri: content://net.derohimat.popularmovies/movie/{id}
        Realm realm = Realm.getDefaultInstance();

        int match = sUriMatcher.match(uri);
        int nrUpdated = 0;
        try {
            switch (match) {
                case MOVIE_WITH_ID:
                    Integer id = Integer.parseInt(uri.getPathSegments().get(1));
                    MovieDao myTask = realm.where(MovieDao.class).equalTo(MovieColumns._ID, id).findFirst();
                    realm.beginTransaction();
                    myTask.setFavorite(true);
                    myTask.setRelease_date(MovieColumns.RELEASE_DATE);
                    nrUpdated++;
                    realm.commitTransaction();
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }


        } finally {
            realm.close();
        }

        if (nrUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return nrUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        Realm realm = Realm.getDefaultInstance();
        try {
            switch (sUriMatcher.match(uri)) {
                case MOVIES:
                    selection = (selection == null) ? "1" : selection;
                    RealmResults<MovieDao> tasksRealmResults = realm.where(MovieDao.class).equalTo(selection, Integer.parseInt(selectionArgs[0])).findAll();
                    realm.beginTransaction();
                    tasksRealmResults.deleteAllFromRealm();
                    count++;
                    realm.commitTransaction();
                    break;
                case MOVIE_WITH_ID:
                    Integer id = Integer.parseInt(String.valueOf(ContentUris.parseId(uri)));
                    MovieDao myTask = realm.where(MovieDao.class).equalTo(MovieColumns._ID, id).findFirst();
                    realm.beginTransaction();
                    myTask.deleteFromRealm();
                    count++;
                    realm.commitTransaction();
                    break;
                default:
                    throw new IllegalArgumentException("Illegal delete URI");
            }
        } finally {
            realm.close();
        }
        if (count > 0) {
            //Notify observers of the change
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    // Example of REALM migration
    class MyRealmMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            RealmSchema schema = realm.getSchema();

            if (oldVersion != 0) {
                schema.create(DatabaseContract.TABLE_MOVIE)
                        .addField(MovieColumns._ID, Integer.class)
                        .addField(MovieColumns.IS_ADULT, Integer.class)
                        .addField(MovieColumns.BACKDROP_PATH, String.class)
                        .addField(MovieColumns.ORIGINAL_LANGUAGE, String.class)
                        .addField(MovieColumns.ORIGINAL_TITLE, String.class)
                        .addField(MovieColumns.OVERVIEW, String.class)
                        .addField(MovieColumns.POSTER_PATH, String.class)
                        .addField(MovieColumns.POPULARITY, Integer.class)
                        .addField(MovieColumns.TITLE, String.class)
                        .addField(MovieColumns.VOTE_AVERAGE, Double.class)
                        .addField(MovieColumns.VOTE_COUNT, Integer.class)
                        .addField(MovieColumns.IS_FAVORITE, Integer.class);
                oldVersion++;
            }

        }
    }
}
