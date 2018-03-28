package net.derohimat.mademovie;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static net.derohimat.mademovie.db.DatabaseContract.CONTENT_URI;
import static net.derohimat.mademovie.db.DatabaseContract.MovieColumns._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private MovieAdapter mAdapter;
    private ListView lvNotes;

    private final int LOAD_MOVIE_ID = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvNotes = (ListView) findViewById(R.id.list_view);

        mAdapter = new MovieAdapter(this, null, true);

        lvNotes.setAdapter(mAdapter);
        lvNotes.setOnItemClickListener(this);

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

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
//        Intent intent = new Intent(MainActivity.this, FormActivity.class);
//        intent.setData(Uri.parse(CONTENT_URI + "/" + id));
//        startActivity(intent);
    }
}
