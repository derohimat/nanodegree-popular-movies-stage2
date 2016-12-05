package net.derohimat.popularmovies.view.fragment.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.derohimat.baseapp.ui.fragment.BaseFragment;
import net.derohimat.baseapp.ui.view.BaseImageView;
import net.derohimat.popularmovies.R;
import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.model.ReviewDao;
import net.derohimat.popularmovies.model.VideoDao;
import net.derohimat.popularmovies.util.Constant;
import net.derohimat.popularmovies.util.DialogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class DetailFragment extends BaseFragment implements DetailMvpView {

    private static final String ARG_DATA = "ARG_DATA";
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
    @Bind(R.id.ly_reviews)
    LinearLayout mLyReviews;
    @Bind(R.id.ly_videos)
    LinearLayout mLyVideos;

    private DetailPresenter mPresenter;
    private ProgressBar mProgressBar = null;
    private MovieDao mMovieDao;

    public static DetailFragment newInstance(MovieDao movieDao) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA, movieDao);
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.detail_fragment;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        mMovieDao = getArguments().getParcelable(ARG_DATA);

        setUpPresenter();

        //for recreation of the toolbar
        setHasOptionsMenu(true);
    }

    private void setUpPresenter() {
        mPresenter = new DetailPresenter(getActivity());
        mPresenter.attachView(this);
        mPresenter.loadMovie(mMovieDao);
//        mPresenter.showVideos();
//        mPresenter.showReviews();
    }

    @Override
    public void showProgress() {
        if (mProgressBar == null) {
            mProgressBar = DialogFactory.DProgressBar(mContext);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMovie(MovieDao data) {
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

        if (mMovieDao.isFavorite()) {
            mTvFavorite.setText(getString(R.string.remove_from_favorite));
        } else {
            mTvFavorite.setText(getString(R.string.add_to_favorite));
        }

        mPresenter.showVideos();
    }

    @Override
    public void showVideos(List<VideoDao> videos) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i < videos.size(); i++) {
            final VideoDao video = videos.get(i);

            View v = inflater.inflate(R.layout.item_video, mLyVideos, false);
            BaseImageView ivThumb = (BaseImageView) v.findViewById(R.id.iv_video_thumbnail);
            TextView tvTitle = (TextView) v.findViewById(R.id.tv_video_title);

            tvTitle.setText(video.getName());
            ivThumb.setImageUrl(Constant.ROOT_VIDEO_THUMBNAIL + video.getKey() + "/0.jpg", R.color.Gray);

            //open youtube link
            v.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getYoutubeUrl(video.getKey())));
                startActivity(intent);
            });
            mLyVideos.addView(v);
        }
        mPresenter.showReviews();
    }

    @Override
    public void showReviews(List<ReviewDao> reviews) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i < reviews.size(); i++) {
            ReviewDao review = reviews.get(i);

            View v = inflater.inflate(R.layout.item_review, mLyReviews, false);
            TextView tvContent = (TextView) v.findViewById(R.id.tv_review_content);
            TextView tvAuthor = (TextView) v.findViewById(R.id.tv_review_author);

            tvContent.setText("\"" + review.getContent() + "\"");
            tvAuthor.setText(review.getAuthor());

            mLyReviews.addView(v);
        }
    }

    @Override
    public String getYoutubeUrl(String key) {
        return Constant.ROOT_VIDEO_KEY + key;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail_refresh:
                mPresenter.loadMovie(mMovieDao);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.tv_favorite)
    void onFavoriteClicked() {
        if (mMovieDao.isFavorite()) {
            mTvFavorite.setText(getString(R.string.remove_from_favorite));
        } else {
            mTvFavorite.setText(getString(R.string.add_to_favorite));
        }
        mPresenter.updateMovie(mMovieDao, true);
    }
}
