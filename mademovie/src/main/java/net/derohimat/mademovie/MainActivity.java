package net.derohimat.mademovie;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.derohimat.baseapp.ui.BaseActivity;
import net.derohimat.mademovie.model.MovieDao;

import butterknife.Bind;

import static net.derohimat.mademovie.db.DatabaseContract.CONTENT_URI;
import static net.derohimat.mademovie.db.DatabaseContract.getMovieDao;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    @Bind(R.id.list_view)
    ListView lvMovies;

    private MovieAdapter mAdapter;
    private final int LOAD_MOVIE_ID = 110;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        mAdapter = new MovieAdapter(this, null, true);

        lvMovies.setAdapter(mAdapter);
        lvMovies.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(LOAD_MOVIE_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOAD_MOVIE_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) mAdapter.getItem(i);

        MovieDao movieDao = getMovieDao(cursor);

        Toast.makeText(mContext, movieDao.getTitle(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.setData(Uri.parse(CONTENT_URI + "/#" + movieDao.getId()));
        startActivity(intent);
    }
}
