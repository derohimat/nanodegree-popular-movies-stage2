package net.derohimat.popularmovies.view.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import net.derohimat.baseapp.ui.view.BaseRecyclerView;
import net.derohimat.popularmovies.R;
import net.derohimat.popularmovies.data.local.PreferencesHelper;
import net.derohimat.popularmovies.events.FavoriteEvent;
import net.derohimat.popularmovies.model.BaseListApiDao;
import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.reminder.DailyAlarmReceiver;
import net.derohimat.popularmovies.util.Constant;
import net.derohimat.popularmovies.util.DialogFactory;
import net.derohimat.popularmovies.view.AppBaseActivity;
import net.derohimat.popularmovies.view.activity.settings.SettingsActivity;
import net.derohimat.popularmovies.view.fragment.detail.DetailFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

import static net.derohimat.popularmovies.BaseApplication.SUPPORTED_LOCALES;

public class MainActivity extends AppBaseActivity implements MainMvpView {

    @Bind(R.id.recyclerview) BaseRecyclerView mRecyclerView;
    @Bind(R.id.bottom_navigation) AHBottomNavigation bottomNavigation;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.search_view) MaterialSearchView searchView;
    private ProgressBar mProgressBar = null;
    private MainPresenter mPresenter;
    private MainRecyclerAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private final static int MAX_WIDTH_COL_DP = 185;
    private String[] mLanguageArray;
    private int mLanguageSelected = 0;
    private String mType = Constant.TYPE_NOW;
    private String mLanguage = Constant.LANG_EN;

    @Inject PreferencesHelper preferencesHelper;
    @Inject EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        setupAlarm();
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.main_activity;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        toolbar.setTitle(getString(R.string.app_name));
        setupToolbar(toolbar);
        getBaseActionBar().setElevation(0);

        getBaseFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getBaseActionBar().setDisplayHomeAsUpEnabled(true);

                bottomNavigation.setVisibility(View.GONE);
            } else {
                getBaseActionBar().setTitle(getString(R.string.app_name));
                getBaseActionBar().setDisplayHomeAsUpEnabled(false);

                bottomNavigation.setVisibility(View.VISIBLE);
            }
        });

        mLanguageArray = new String[]{getString(R.string.main_language_en),
                getString(R.string.main_language_id)};

        if (LocaleChanger.getLocale().equals(SUPPORTED_LOCALES.get(1))) {
            mLanguageSelected = 1;
            mLanguage = Constant.LANG_ID;
        }

        setupBottomMenu();
        setUpPresenter();
        setUpAdapter();
        setUpRecyclerView();
    }

    private void setupAlarm() {
        DailyAlarmReceiver dailyAlarmReceiver = new DailyAlarmReceiver();
        if (preferencesHelper.getDailyPrefs()) {
            dailyAlarmReceiver.setDailyReminderAlarm(getContext());
        }
        if (preferencesHelper.getUpcomingPrefs()) {
            dailyAlarmReceiver.setUpcomingAlarm(getContext());
        }
    }

    private void setupBottomMenu() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.tab_now_playing), R.drawable.ic_airplay_white_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.tab_upcoming), R.drawable.ic_queue_play_next_white_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.tab_favorite), R.drawable.ic_favorite_white_24dp);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        bottomNavigation.setAccentColor(ContextCompat.getColor(mContext, R.color.white));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(mContext, R.color.tab_inactive));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setColored(false);

        bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case 0:
                    if (!wasSelected) {
                        mType = Constant.TYPE_NOW;
                        mPresenter.discoverMovies(mType, mLanguage);
                    }
                    break;
                case 1:
                    if (!wasSelected) {
                        mType = Constant.TYPE_UP;
                        mPresenter.discoverMovies(mType, mLanguage);
                    }
                    break;
                case 2:
                    if (!wasSelected) {
                        mType = Constant.TYPE_FAVORITE;
                        mPresenter.discoverFavoritesMovies();
                    }
                    break;
            }
            return true;
        });
    }

    @Override
    public void setUpPresenter() {
        mPresenter = new MainPresenter(this);
        mPresenter.attachView(this);
        mPresenter.discoverMovies(mType, mLanguage);
    }

    @Override
    public void setUpAdapter() {
        mAdapter = new MainRecyclerAdapter(mContext);
        mAdapter.setOnItemClickListener((view, position) -> {
            MovieDao selectedItem = mAdapter.getDatas().get(position - 1);

            getBaseActionBar().setTitle(selectedItem.getTitle());
            getBaseFragmentManager().beginTransaction().replace(R.id.container_rellayout,
                    DetailFragment.newInstance(selectedItem)).addToBackStack(null).commit();

            bottomNavigation.setVisibility(View.GONE);
        });
    }

    @Override
    public void setUpRecyclerView() {
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        //change span dynamically based on screen width
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewWidth = mRecyclerView.getMeasuredWidth();
                        float cardViewWidth = MAX_WIDTH_COL_DP * getResources().getDisplayMetrics().density;
                        int newSpanCount = Math.max(2, (int) Math.floor(viewWidth / cardViewWidth));
                        mLayoutManager.setSpanCount(newSpanCount);
                        mLayoutManager.requestLayout();
                    }
                });

        mRecyclerView.setPullRefreshEnabled(true);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (mType.equals(Constant.TYPE_FAVORITE)) {
                    mPresenter.discoverFavoritesMovies();
                } else {
                    mPresenter.discoverMovies(mType, mLanguage);
                }
            }

            @Override
            public void onLoadMore() {
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                mPresenter.discoverMovies(query, mLanguage);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_language:
                changeLanguage();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(FavoriteEvent event) {
        if (event.ismSuccess()) {
            if (mType.equals(Constant.TYPE_FAVORITE)) {
                mPresenter.discoverFavoritesMovies();
            } else {
                mPresenter.discoverMovies(mType, mLanguage);
            }
        } else {
            DialogFactory.showErrorSnackBar(mContext, findViewById(android.R.id.content), new Throwable(event.getMessage())).show();
        }
    }

    @Override
    public void showDiscoverMovie(BaseListApiDao data) {
        mRecyclerView.refreshComplete();
        mAdapter.clear();
        mAdapter.addAll(data.getResults());
    }

    @Override
    public void showFavoritesMovie(List<MovieDao> movieDaos) {
        mRecyclerView.refreshComplete();
        mAdapter.clear();
        mAdapter.addAll(movieDaos);
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
    public void changeLanguage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.main_select_language)
                .setSingleChoiceItems(mLanguageArray,
                        mLanguageSelected,
                        (dialog, which) -> {
                            mLanguageSelected = which;
                            switch (mLanguageSelected) {
                                case 0:
                                    mLanguage = Constant.LANG_EN;
                                    preferencesHelper.setLanguage(mLanguage);
                                    mPresenter.discoverMovies(mType, mLanguage);
                                    LocaleChanger.setLocale(SUPPORTED_LOCALES.get(0));
                                    ActivityRecreationHelper.recreate(this, true);
                                    break;
                                case 1:
                                    mLanguage = Constant.LANG_ID;
                                    preferencesHelper.setLanguage(mLanguage);
                                    mPresenter.discoverMovies(mType, mLanguage);
                                    LocaleChanger.setLocale(SUPPORTED_LOCALES.get(1));
                                    ActivityRecreationHelper.recreate(this, true);
                                    break;
                                default:
                                    break;
                            }
                            dialog.dismiss();
                        });
        builder.create().show();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
