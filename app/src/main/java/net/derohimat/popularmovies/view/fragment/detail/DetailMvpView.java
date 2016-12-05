package net.derohimat.popularmovies.view.fragment.detail;

import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.model.ReviewDao;
import net.derohimat.popularmovies.model.VideoDao;
import net.derohimat.popularmovies.view.MvpView;

import java.util.List;

interface DetailMvpView extends MvpView {

    void showProgress();

    void hideProgress();

    void showMovie(MovieDao data);

    void showReviews(List<ReviewDao> reviews);

    void showVideos(List<VideoDao> videos);

    String getYoutubeUrl(String key);

}