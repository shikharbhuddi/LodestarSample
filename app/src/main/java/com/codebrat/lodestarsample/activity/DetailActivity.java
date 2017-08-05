package com.codebrat.lodestarsample.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.codebrat.lodestarsample.R;
import com.codebrat.lodestarsample.app.Config;
import com.codebrat.lodestarsample.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

public class DetailActivity extends AppCompatActivity {
	private TextView courseHead;
	private RoundCornerProgressBar progressBar;
	private BroadcastReceiver mRegistrationBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		progressBar = (RoundCornerProgressBar) findViewById(R.id.progress_bar);
		courseHead = (TextView) findViewById(R.id.course_head);

		Typeface face = Typeface.createFromAsset(getAssets(),
			"fonts/HelveticaNeueLight.ttf");
		courseHead.setTypeface(face);

		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				// checking for type intent filter
				if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
					// gcm successfully registered
					// now subscribe to `global` topic to receive app wide notifications
					FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

					Toast.makeText(getApplicationContext(), "Firebase Registered",
						Toast.LENGTH_LONG).show();

				} else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
					// new push notification is received

					String message = intent.getStringExtra("message");

					Toast.makeText(getApplicationContext(), "Push notification: " +
						message, Toast.LENGTH_LONG).show();
				}
			}
		};
	}


	@Override
	protected void onResume() {
		super.onResume();

		// register GCM registration complete receiver
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
			new IntentFilter(Config.REGISTRATION_COMPLETE));

		// register new push message receiver
		// by doing this, the activity will be notified each time a new message arrives
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
			new IntentFilter(Config.PUSH_NOTIFICATION));

		// clear the notification area when the app is opened
		NotificationUtils.clearNotifications(getApplicationContext());
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}
}
