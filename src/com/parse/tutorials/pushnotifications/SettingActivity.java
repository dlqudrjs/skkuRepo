package com.parse.tutorials.pushnotifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingActivity extends Activity{
	Button loginbtn;
	Button vbtn;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		
		intent = new Intent(this,LoginActivity.class);
		loginbtn = (Button)findViewById(R.id.login);
		vbtn = (Button)findViewById(R.id.version);
		
		loginbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removePreferences();
				startActivity(intent);
				finish();
			}
		});
		vbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
				alert.setPositiveButton("»Æ¿Œ", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();     //¥›±‚
				    }
				});
				alert.setMessage("Version : 1.0.0");
				alert.show();
			}
		});
	}
	
    private void removePreferences(){
        SharedPreferences pref = getSharedPreferences("LOGINKEY", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("key");
        editor.commit();
    }
}
