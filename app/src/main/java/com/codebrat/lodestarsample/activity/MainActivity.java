package com.codebrat.lodestarsample.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codebrat.lodestarsample.R;
import com.codebrat.lodestarsample.app.Config;
import com.codebrat.lodestarsample.model.User;
import com.codebrat.lodestarsample.service.MyFirebaseInstanceIDService;
import com.codebrat.lodestarsample.util.NotificationUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private AutoCompleteTextView username;
	private Button registerBtn;

	private DatabaseReference mFirebaseDatabase;
	private FirebaseDatabase mFirebaseInstance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		username = (AutoCompleteTextView) findViewById(R.id.username);
		registerBtn = (Button) findViewById(R.id.register);

		Typeface face = Typeface.createFromAsset(getAssets(),
			"fonts/HelveticaNeueLight.ttf");
		username.setTypeface(face);
		registerBtn.setTypeface(face);

		if(getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0)
			.getString("username", "")!= null && !getApplicationContext()
			.getSharedPreferences(Config.SHARED_PREF, 0).getString("username", "").isEmpty()){
			Intent intent = new Intent(MainActivity.this, DetailActivity.class);
			startActivity(intent);
			finish();
		}

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

		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(username.getText().toString().trim().isEmpty())
					return;
				else{
					SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
					String regId = pref.getString("regId", null);
					User user = new User(username.getText().toString().trim(), regId);
					storeUserInPref(username.getText().toString().trim());
					mFirebaseInstance = FirebaseDatabase.getInstance();

					// get reference to 'users' node
					mFirebaseDatabase = mFirebaseInstance.getReference("users");
					mFirebaseDatabase.child(username.getText().toString().trim()).setValue(regId);

					Intent intent = new Intent(MainActivity.this, DetailActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
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

	private void storeUserInPref(String username) {
		SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("username", username);
		editor.commit();
	}
}
