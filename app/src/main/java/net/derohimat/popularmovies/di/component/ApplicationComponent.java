package net.derohimat.popularmovies.di.component;

import net.derohimat.popularmovies.BaseApplication;
import net.derohimat.popularmovies.data.local.PreferencesHelper;
import net.derohimat.popularmovies.data.remote.APIService;
import net.derohimat.popularmovies.data.remote.UnauthorisedInterceptor;
import net.derohimat.popularmovies.di.module.ApplicationModule;
import net.derohimat.popularmovies.view.activity.main.MainPresenter;
import net.derohimat.popularmovies.view.fragment.detail.DetailPresenter;
import net.derohimat.popularmovies.widget.services.StackRemoteViewsFactory;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(StackRemoteViewsFactory remoteViewsFactory);

    void inject(MainPresenter mainPresenter);

    void inject(DetailPresenter detailPresenter);

    void inject(BaseApplication baseApplication);

    void inject(UnauthorisedInterceptor unauthorisedInterceptor);

    APIService apiService();

    EventBus eventBus();

    Realm realm();

    PreferencesHelper prefsHelper();

}