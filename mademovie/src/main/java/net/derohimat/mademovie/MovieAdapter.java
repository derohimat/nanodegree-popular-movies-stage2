package net.derohimat.mademovie;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.derohimat.baseapp.ui.view.BaseImageView;
import net.derohimat.mademovie.model.MovieDao;
import net.derohimat.mademovie.utils.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.derohimat.mademovie.db.DatabaseContract.getMovieDao;

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

            MovieDao data = getMovieDao(cursor);

            BaseImageView imgPoster = (BaseImageView) view.findViewById(R.id.iv_poster);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            TextView mTvReleaseDate = (TextView) view.findViewById(R.id.tv_releasedate);
            TextView mTvVoteAvg = (TextView) view.findViewById(R.id.tv_voteavg);
            TextView mTvFavorite = (TextView) view.findViewById(R.id.tv_favorite);

            String imagePath = Constant.ROOT_POSTER_IMAGE_URL + data.getPoster_path();

            imgPoster.setImageUrl(imagePath);

            tvTitle.setText(data.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate;
            try {
                Date dateRelease = sdf.parse(data.getRelease_date());
                formattedDate = DateFormat.format("dd MMM yyyy", dateRelease).toString();
            } catch (ParseException e) {
                e.printStackTrace();
                formattedDate = data.getRelease_date();
            }
            mTvReleaseDate.setText(formattedDate);

            mTvVoteAvg.setText(data.getVote_average() + "/" + "10");

            if (data.isFavorite()) {
                mTvFavorite.setText(context.getString(R.string.remove_from_favorite));
            } else {
                mTvFavorite.setText(context.getString(R.string.add_to_favorite));
            }
        }
    }
}