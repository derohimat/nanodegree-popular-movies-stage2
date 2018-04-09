package net.derohimat.popularmovies.view.activity.settings;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.derohimat.popularmovies.R;
import net.derohimat.popularmovies.data.local.PreferencesHelper;
import net.derohimat.popularmovies.view.AppBaseActivity;
import net.derohimat.popularmovies.view.fragment.settings.MyPreferenceFragment;

import javax.inject.Inject;

import butterknife.Bind;

public class SettingsActivity extends AppBaseActivity {

    @Inject
    PreferencesHelper preferencesHelper;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        toolbar.setTitle(getString(R.string.settings));
        setupToolbar(toolbar);
        getBaseActionBar().setElevation(0);
        getBaseActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, new MyPreferenceFragment())
                .commit();
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.settings_activity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean dailyPrefs = preferencesHelper.getDailyPrefs();
        preferencesHelper.setDailyPrefs(dailyPrefs);

        boolean upcomingPrefs = preferencesHelper.getUpcomingPrefs();
        preferencesHelper.setUpcomingPrefs(upcomingPrefs);

    }
}