package com.codebrat.lodestarsample.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codebrat.lodestarsample.app.Config;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Shikhar on 05-08-2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
	private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

	@Override
	public void onTokenRefresh() {
		super.onTokenRefresh();
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();

		// Saving reg id to shared preferences
		storeRegIdInPref(refreshedToken);

		// Notify UI that registration has completed, so the progress indicator can be hidden.
		Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
		registrationComplete.putExtra("token", refreshedToken);
		LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
	}

	private void storeRegIdInPref(String token) {
		SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("regId", token);
		editor.commit();
	}
}
