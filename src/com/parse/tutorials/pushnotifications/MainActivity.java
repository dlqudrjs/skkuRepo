package com.parse.tutorials.pushnotifications;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {
	ImageButton b11, b12, b21, b22;
	Intent i11, i12, i21, i22, ial;
	TextView tv;
	File f;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Initialize
		b11 = (ImageButton)findViewById(R.id.Survey);
		b12 = (ImageButton)findViewById(R.id.Todstat);
		b21 = (ImageButton)findViewById(R.id.Totstat);
		b22 = (ImageButton)findViewById(R.id.Setting);
		i11 = new Intent(this,SurveyActivity.class);
		i12 = new Intent(this,TodayStatActivity.class);
		i21 = new Intent(this,TotalStatActivity.class);
		i22 = new Intent(this,SettingActivity.class);
		f = new File(this.getFilesDir(),"survey");
		tv = (TextView)findViewById(R.id.header);
		tv.setTypeface(Typeface.createFromAsset(getAssets(),"NANUMGOTHICBOLD.TTF"));
		
		b11.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					startActivity(i11);
			}
		});
		b12.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivity(i12);
			}
		});
		b21.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivity(i21);
			}
		});
		b22.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(i22);
				finish();
			}
		});
		
	}
}
