package net.derohimat.popularmovies.view.fragment.detail;

import android.content.Context;

import net.derohimat.baseapp.presenter.BasePresenter;
import net.derohimat.popularmovies.BaseApplication;
import net.derohimat.popularmovies.data.remote.APIService;
import net.derohimat.popularmovies.events.FavoriteEvent;
import net.derohimat.popularmovies.model.MovieDao;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.realm.Realm;
import rx.Subscription;

public class DetailPresenter implements BasePresenter<DetailMvpView> {

    private DetailMvpView mView;
    private Subscription mSubscription;
    private MovieDao mMovieDao;

    @Inject
    DetailPresenter(Context context) {
        ((BaseApplication) context.getApplicationContext()).getApplicationComponent().inject(this);
    }

    //    Just Prepare if Data Not Completed
    @Inject
    APIService mAPIService;
    @Inject
    Realm mRealm;
    @Inject
    EventBus mEventBus;

    @Override
    public void attachView(DetailMvpView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    void loadMovie(MovieDao movieDao) {
        mMovieDao = movieDao;
        mView.showMovie(mMovieDao);
    }

    void updateFavorite(MovieDao movieDao) {
        movieDao.setFavorite(!movieDao.isFavorite());

        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
        }

        mRealm.copyToRealmOrUpdate(movieDao);
        mRealm.commitTransaction();

        mMovieDao = movieDao;

        mView.showMovie(mMovieDao);
        mEventBus.post(new FavoriteEvent(true, "success update favorite"));
    }
}