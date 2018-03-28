package net.derohimat.mademovie;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import net.derohimat.baseapp.ui.view.BaseImageView;
import net.derohimat.mademovie.utils.Constant;

import static net.derohimat.mademovie.db.DatabaseContract.MovieColumns.POSTER_PATH;
import static net.derohimat.mademovie.db.DatabaseContract.getColumnString;

public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_row, viewGroup, false);
        return view;
    }


    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            BaseImageView imgPoster = (BaseImageView) view.findViewById(R.id.iv_poster);

            String imagePath = Constant.ROOT_POSTER_IMAGE_URL + getColumnString(cursor, POSTER_PATH);

            imgPoster.setImageUrl(imagePath);
        }
    }
}