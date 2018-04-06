package net.derohimat.popularmovies.widget.services;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.Target;

import net.derohimat.popularmovies.R;
import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.util.Constant;
import net.derohimat.popularmovies.widget.FavoritesMovieWidgetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Bitmap defaultBitmap;
    private List<MovieDao> movieDaos = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;
    private AppWidgetTarget appWidgetTarget;

    public StackRemoteViewsFactory(Context context, Intent intent) {

        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        defaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dbs_first);
    }

    //
//    public void onCreate() {
//        defaultBitmap.add(BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.dbs_first));
//        defaultBitmap.add(BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.dbs_first));
//        defaultBitmap.add(BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.dbs_first));
//        defaultBitmap.add(BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.dbs_first));
//        defaultBitmap.add(BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.dbs_first));
//        defaultBitmap.add(BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.dbs_first));
//    }

    @Override
    public void onDataSetChanged() {
        Realm mRealm = Realm.getDefaultInstance();

        final RealmResults<MovieDao> realmResult = mRealm.where(MovieDao.class).equalTo("favorite", true).findAll();

        if (!realmResult.isEmpty()) {
            movieDaos.addAll(mRealm.copyFromRealm(realmResult));
        }
    }

    @Override
    public void onDestroy() {
        movieDaos.clear();
    }

    @Override
    public int getCount() {
        return movieDaos.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        if (position <= getCount()) {
            MovieDao item = movieDaos.get(position);

            appWidgetTarget = new AppWidgetTarget(mContext, rv, R.id.imgWidget, mAppWidgetId);

            String imagePath = Constant.ROOT_POSTER_IMAGE_URL + item.getPoster_path();
//            String imagePath = Constant.ROOT_POSTER_IMAGE_URL + "/eKi8dIrr8voobbaGzDpe8w0PVbC.jpg";

            Bitmap bmp;
            try {
                bmp = Glide.with(mContext)
                        .load(imagePath)
                        .asBitmap()
                        .error(new ColorDrawable(mContext.getResources().getColor(R.color.Black)))
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();

            } catch (InterruptedException | ExecutionException e) {
                Log.d("Widget Load Error", "error");
                bmp = defaultBitmap;
            }

            rv.setImageViewBitmap(R.id.imgWidget, bmp);
            rv.setTextViewText(R.id.txtLabelWidget, Html.fromHtml(item.getTitle()));

            Bundle extras = new Bundle();
            extras.putInt(FavoritesMovieWidgetProvider.EXTRA_ITEM, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);

            rv.setOnClickFillInIntent(R.id.imgWidget, fillInIntent);
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}