package net.derohimat.popularmovies.view.activity.main;

import android.content.Context;

import net.derohimat.baseapp.presenter.BasePresenter;
import net.derohimat.popularmovies.BaseApplication;
import net.derohimat.popularmovies.R;
import net.derohimat.popularmovies.data.remote.APIService;
import net.derohimat.popularmovies.events.FavoriteEvent;
import net.derohimat.popularmovies.model.BaseListApiDao;
import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.util.Constant;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MainPresenter implements BasePresenter<MainMvpView> {

    @Inject
    MainPresenter(Context context) {
        ((BaseApplication) context.getApplicationContext()).getApplicationComponent().inject(this);
    }

    @Inject
    APIService mAPIService;
    @Inject
    EventBus mEventBus;
    @Inject
    Realm mRealm;

    private MainMvpView mView;
    private Subscription mSubscription;
    private BaseListApiDao<MovieDao> mBaseListApiDao;
    private BaseApplication mBaseApplication;

    @Override
    public void attachView(MainMvpView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    void discoverMovies(String sortBy) {

        mView.showProgress();
        if (mSubscription != null) mSubscription.unsubscribe();


        if (mBaseApplication != null) {
            mBaseApplication = BaseApplication.get(mView.getContext());
        } else {
            mBaseApplication = BaseApplication.get(mView.getContext());
        }

        mSubscription = mAPIService.discoverMovie(sortBy, Constant.MOVIEDB_APIKEY)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mBaseApplication.getSubscribeScheduler())
                .subscribe(new Subscriber<BaseListApiDao<MovieDao>>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("Movies loaded " + mBaseListApiDao);
                        mView.showDiscoverMovie(mBaseListApiDao);
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Timber.e("Error loading Movies", error);
                        if (isHttp404(error)) {
                            mEventBus.post(new FavoriteEvent(false, mBaseApplication.getString(R.string.error_not_found)));
                        } else {
                            mEventBus.post(new FavoriteEvent(false, mBaseApplication.getString(R.string.error_loading_movie)));
                        }

                        mView.hideProgress();
                    }

                    @Override
                    public void onNext(BaseListApiDao<MovieDao> baseListApiDao) {
                        mBaseListApiDao = baseListApiDao;
                    }
                });
    }

    void discoverFavoritesMovies() {
        mView.showProgress();

        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
        }

        if (mBaseApplication == null) {
            mBaseApplication = BaseApplication.get(mView.getContext());
        }

        final RealmResults<MovieDao> movieDaos = mRealm.where(MovieDao.class).equalTo("favorite", true).findAll();
        movieDaos.size();

        if (movieDaos.isEmpty()) {
            mEventBus.post(new FavoriteEvent(false, mBaseApplication.getString(R.string.no_favorite)));
        } else {
            mView.showFavoritesMovie(movieDaos);
        }

        mView.hideProgress();
    }

    void closeRealm() {
        mRealm.close();
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }
}