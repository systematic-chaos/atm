package corp.katet.atm.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.User;
import corp.katet.atm.ui.Constants;
import corp.katet.atm.ui.MenuActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class BalanceService extends Service {

	private ScheduledExecutorService mScheduler;
	private Runnable mNotifier;
	private ScheduledFuture<?> mNotifierHandle;
	private NotificationManager mNotificationManager;
	private User mUser;
	private int mPeriod;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			mUser = DAOFactory.getInstance(this).getUserDAO()
					.getUserFromId(intent.getLongExtra(Constants.USER_ID, 0));
			mPeriod = intent.getIntExtra(Constants.SERVICE_PERIOD, 2000);
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
		mNotifier = new Runnable() {
			public void run() {
				if (mUser != null) {
					showNotification(mUser.getCurrentBalance());
				}
			}
		};
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		mNotifierHandle.cancel(true);
		mNotificationManager.cancel(Constants.NOTIFICATION);
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
		intent.putExtra(Constants.USER_ID, mUser.getIdUser());
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		// Set the info for the views that show in the notification panel.
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.atm) // the status icon
				.setTicker(text) // the status text
				.setWhen(System.currentTimeMillis()) // the time stamp
				.setContentTitle(getString(R.string.app_name)) // the label of
																// the entry
				.setContentText(text) // the contents of the entry
				.setContentIntent(contentIntent) // The intent to send when the
													// entry is clicked
				.setAutoCancel(true).build();

		// Send the notification.
		mNotificationManager.notify(Constants.NOTIFICATION, notification);
	}
}
