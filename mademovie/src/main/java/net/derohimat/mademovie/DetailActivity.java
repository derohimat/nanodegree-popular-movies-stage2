package net.derohimat.mademovie;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import net.derohimat.baseapp.ui.BaseActivity;
import net.derohimat.baseapp.ui.view.BaseImageView;
import net.derohimat.mademovie.model.MovieDao;
import net.derohimat.mademovie.utils.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;

import static net.derohimat.mademovie.db.DatabaseContract.getMovieDao;

/**
 * Created by denirohimat on 29/03/18.
 */

public class DetailActivity extends BaseActivity {

    @Bind(R.id.iv_backdrop)
    BaseImageView mIvBackdrop;
    @Bind(R.id.iv_poster)
    BaseImageView mIvPoster;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_releasedate)
    TextView mTvReleaseDate;
    @Bind(R.id.tv_voteavg)
    TextView mTvVoteAvg;
    @Bind(R.id.tv_favorite)
    TextView mTvFavorite;
    @Bind(R.id.tv_synopsis)
    TextView mTvSynopsis;

    private MovieDao data = null;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_detail_movie;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        Uri uri = getIntent().getData();

        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    data = getMovieDao(cursor);
                }
                cursor.close();
            }
        }

        showMovieData();

    }

    private void showMovieData() {
        mTvTitle.setText(data.getTitle());

        mIvBackdrop.setImageUrl(Constant.ROOT_BACKDROP_IMAGE_URL + data.getBackdrop_path());
        mIvPoster.setImageUrl(Constant.ROOT_POSTER_IMAGE_URL + data.getPoster_path());

        //set release date
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
        mTvSynopsis.setText(data.getOverview());

        if (data.isFavorite()) {
            mTvFavorite.setText(getString(R.string.remove_from_favorite));
        } else {
            mTvFavorite.setText(getString(R.string.add_to_favorite));
        }
    }
}
