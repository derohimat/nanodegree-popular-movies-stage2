package net.derohimat.popularmovies.reminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import net.derohimat.popularmovies.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DailyAlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_ID = "notif_id";
    public static final int NOTIF_ID_DAILY = 101;
    public static final int NOTIF_ID_UPCOMING = 102;

    public DailyAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        int notifId = intent.getIntExtra(EXTRA_ID, NOTIF_ID_DAILY);
        String title = context.getString(R.string.app_name);
        if (notifId == NOTIF_ID_DAILY) {
            showAlarmNotification(context, title, message, NOTIF_ID_DAILY);
        } else {
            startUpcomingScheduler(context);
        }
    }

    private void showAlarmNotification(Context context, String title, String message, int notifId) {
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_local_movies_white_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);
        notificationManagerCompat.notify(notifId, builder.build());
    }

    public void setDailyReminderAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, context.getString(R.string.reminded_message));
        intent.putExtra(EXTRA_ID, NOTIF_ID_DAILY);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIF_ID_DAILY, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void setUpcomingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, context.getString(R.string.reminded_message));
        intent.putExtra(EXTRA_ID, NOTIF_ID_UPCOMING);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIF_ID_UPCOMING, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void startUpcomingScheduler(Context context) {
        ComponentName mServiceComponent = new ComponentName(context, UpcomingMovieJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(NOTIF_ID_UPCOMING, mServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPeriodic(TimeUnit.DAYS.toMillis(1));
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIF_ID_DAILY, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}