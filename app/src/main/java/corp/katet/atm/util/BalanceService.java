package corp.katet.atm.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.User;
import corp.katet.atm.ui.AuthActivity;
import corp.katet.atm.ui.MenuActivity;

public class BalanceService extends Service {

	private ScheduledExecutorService mScheduler;
	private Runnable mNotifier;
	private ScheduledFuture<?> mNotifierHandle;
	private NotificationManager mNotificationManager;
	private User mUser;
	private int mPeriod;

    public static final String SERVICE_PERIOD = "servicePeriodicity";
    private static final String NOTIFICATION_CHANNEL_ID = "balanceServiceNotificationChannelId";
    private static final int NOTIFICATION_ID = 19999;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
		    mUser = DAOFactory.getInstance(this).getUserDAO()
                    .getUserFromId(intent.getLongExtra(AuthActivity.USER_ID, 0));
			mPeriod = intent.getIntExtra(SERVICE_PERIOD, 2000);
		}
		mNotifierHandle = mScheduler.scheduleAtFixedRate(mNotifier, mPeriod,
				mPeriod, TimeUnit.MILLISECONDS);
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mScheduler = Executors.newScheduledThreadPool(1);
		mPeriod = getResources().getInteger(R.integer.service_period);

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		createNotificationChannel();
        mNotifier = new Runnable() {
            public void run() {
                if (mUser != null) {
                    showNotification(mUser.getCurrentBalance());
                }
            }
        };
	}

	@Override
	public void onDestroy() {
		mNotifierHandle.cancel(true);
		mNotificationManager.cancel(NOTIFICATION_ID);
		destroyNotificationChannel();
	}

	/**
	 * Show a notification while this service is running.
	 * 
	 * @param currentBalance
	 *            The user's current balance
	 */
	private void showNotification(float currentBalance) {
		// We'll use the same text for the ticker and the expanded notification
		CharSequence text = getString(R.string.balance_msg, currentBalance);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent intent = new Intent(this, MenuActivity.class);
		intent.putExtra(AuthActivity.USER_ID, mUser.getIdUser());
		PendingIntent contentIntent = PendingIntent.getActivity(
            getApplicationContext(), 0, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

		// Set the info for the views that show in the notification panel.
        Notification notification =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
				.setSmallIcon(R.drawable.atm) // the status icon
				.setTicker(text) // the status text
				.setWhen(System.currentTimeMillis()) // the time stamp
				.setContentTitle(getString(R.string.app_name)) // the label of
																// the entry
				.setContentText(text) // the contents of the entry
				.setContentIntent(contentIntent) // The intent to send when the
													// entry is clicked
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true).build();

		// Send the notification.
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	private void createNotificationChannel() {
	    // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.balance_notification_channel_name);
            String description = getString(R.string.balance_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private void destroyNotificationChannel() {
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
        }
    }
}
