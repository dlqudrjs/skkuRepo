package com.parse.tutorials.pushnotifications;

import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class Application extends android.app.Application {

	public Application() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences p = getSharedPreferences("HASH", MODE_PRIVATE);
		String tmp = p.getString("hash", null);
		// Initialize the Parse SDK.
		Parse.initialize(this, "hkloTHDWASmFSPZLpg3U52gw8DKuktc4CpbQrfDL",
				"NMMSxNNsqn9SsRIsZksIpfXYroUkLpJOPs9xuwYi");

		// Specify an Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, SurveyActivity.class);
	}
}