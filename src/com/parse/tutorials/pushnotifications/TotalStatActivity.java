package com.parse.tutorials.pushnotifications;

import android.app.Activity;
import android.os.Bundle;

public class TotalStatActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//서버에 요청했을 때, 설문조사가 없으면  AlreadySurvey를 띄움
		//설문조사가 있으면 설문조사를 띄움
	}
}
