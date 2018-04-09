package net.derohimat.popularmovies.reminder;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import net.derohimat.popularmovies.BaseApplication;
import net.derohimat.popularmovies.R;
import net.derohimat.popularmovies.data.local.PreferencesHelper;
import net.derohimat.popularmovies.data.remote.APIService;
import net.derohimat.popularmovies.model.BaseListApiDao;
import net.derohimat.popularmovies.model.MovieDao;
import net.derohimat.popularmovies.util.Constant;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UpcomingMovieJobService extends JobService {

    public UpcomingMovieJobService() {
    }

    @Inject APIService mAPIService;
    @Inject PreferencesHelper preferencesHelper;

    private BaseListApiDao<MovieDao> mBaseListApiDao;

    @Override
    public boolean onStartJob(JobParameters params) {
        BaseApplication.get(getApplicationContext()).getApplicationComponent().inject(this);
        Timber.d("onStartJob() Executed");
        getUpcomingMovie(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Timber.d("onStopJob() Executed");
        return true;
    }

    private void getUpcomingMovie(final JobParameters job) {
        mAPIService.discoverMovie(Constant.TYPE_UP, Constant.MOVIEDB_APIKEY, preferencesHelper.getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseListApiDao<MovieDao>>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("Movies loaded " + mBaseListApiDao);

                        checkReleaseToday();

                        jobFinished(job, false);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Timber.e("Error loading Movies", error);
                        jobFinished(job, true);
                    }

                    @Override
                    public void onNext(BaseListApiDao<MovieDao> baseListApiDao) {
                        mBaseListApiDao = baseListApiDao;
                    }
                });
    }

    private void checkReleaseToday() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//        String dateNow = format.format(new Date());
        String dateNow = "2018-03-28";

        mBaseListApiDao.getResults().stream().filter(item ->
                item.getRelease_date().equals(dateNow)).forEachOrdered(item ->
                showNotification(getApplicationContext(), "Release Today",
                        item.getTitle() + " is release today", safeLongToInt(item.getId()))
        );
    }

    public static int safeLongToInt(long l) {
        return (int) Math.max(Math.min(Integer.MAX_VALUE, l), Integer.MIN_VALUE);
    }

    private void showNotification(Context context, String title, String message, int notifId) {
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_local_movies_white_24dp)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);
        notificationManagerCompat.notify(notifId, builder.build());
    }
}