package net.derohimat.popularmovies.view.activity.main;

import net.derohimat.popularmovies.model.BaseListApiDao;
import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.view.MvpView;

import java.util.List;

interface MainMvpView extends MvpView {


    void setUpPresenter();

    void setUpAdapter();

    void setUpRecyclerView();

    void showDiscoverMovie(BaseListApiDao baseListApiDao);

    void showFavoritesMovie(List<MovieDao> movieDaos);

    void showProgress();

    void hideProgress();

    void changeLanguage();
}