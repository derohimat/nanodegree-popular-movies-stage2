package net.derohimat.popularmovies.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.franmontiel.localechanger.LocaleChanger;

import net.derohimat.baseapp.ui.BaseActivity;
import net.derohimat.popularmovies.BaseApplication;
import net.derohimat.popularmovies.di.component.ActivityComponent;
import net.derohimat.popularmovies.di.component.DaggerActivityComponent;

import butterknife.ButterKnife;
import timber.log.Timber;

public abstract class AppBaseActivity extends BaseActivity {

    private ActivityComponent mComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());
        ButterKnife.bind(this);
        Timber.tag(getClass().getSimpleName());
        mInflater = LayoutInflater.from(mContext);
        mComponent = DaggerActivityComponent.builder().applicationComponent(getApp().getApplicationComponent()).build();
        onViewReady(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(newBase);
    }

    protected ActivityComponent getComponent() {
        return mComponent;
    }

    protected BaseApplication getApp() {
        return (BaseApplication) getApplicationContext();
    }

}
