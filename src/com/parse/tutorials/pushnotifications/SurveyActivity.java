package com.parse.tutorials.pushnotifications;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SurveyActivity extends Activity{
	Button submitbtn, backbtn;
	Intent intent;
	String survey;	//Survey Data
	String addr;
	String question[];
	String stdid[];
	String totid[];
	String qid[];
	String reply[];
	File f;
	TextView tv;
	boolean issurvey;
	boolean success = true;
//	String qrequest = null;
    String qresponse = "";
    String srequest = "";
    String sresponse = "";
    
	ReadAsynTask rat;
	WriteAsynTask wat;
	RadioGroup rg1, rg2, rg3, rg4, rg5, rg6, rg7, rg8, rg9;
	JSONArray sendjarr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survey_activity2);

		intent = new Intent(this,MainActivity.class);
		
		//Make Temporary survey file
		rat = new ReadAsynTask();
		wat = new WriteAsynTask();
		
		try {
			rat.execute();
			Log.d("qresponse",qresponse);
			JSONObject resobj = new JSONObject(qresponse);
			if(resobj.getString("result").toString().equals("success") == true){
				//Get Question Data
				JSONArray data = resobj.getJSONArray("questions");
				question = new String[9];
				stdid = new String[9];
				totid = new String[9];
				qid = new String[9];
				
				for(int i=0;i<9;i++){
					question[i] = data.getJSONObject(i).getString("label");
					stdid[i] = data.getJSONObject(i).getString("studentid");
					totid[i] = data.getJSONObject(i).getString("totalizationid");
					qid[i] = data.getJSONObject(i).getString("questionid");
				}
			} else{
				if(resobj.getString("error").toString().equals("loginfail") == true){
					AlertDialog.Builder alert = new AlertDialog.Builder(SurveyActivity.this);
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    dialog.dismiss();     //닫기
					    }
					});
					alert.setMessage("세션이 만료되었습니다...");
					alert.show();
					
					removePreferences();
					intent = new Intent(this,LoginActivity.class);
					startActivity(intent);
					finish();
				}
				else{
					AlertDialog.Builder alert = new AlertDialog.Builder(SurveyActivity.this);
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    dialog.dismiss();     //닫기
					    }
					});
					alert.setMessage("금주 설문조사를 이미 하셨거나 설문조사가 없습니다.");
					alert.show();
					
					startActivity(intent);
					finish();
				}
			}
		} catch (JSONException e1) {
			Toast.makeText(getApplicationContext(), "서버로부터 데이터를 받아오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show(); 	
		}
		
		//Set q1~q9
		TextView tv1 = (TextView)findViewById(R.id.q1);
		TextView tv2 = (TextView)findViewById(R.id.q2);
		TextView tv3 = (TextView)findViewById(R.id.q3);
		TextView tv4 = (TextView)findViewById(R.id.q4);
		TextView tv5 = (TextView)findViewById(R.id.q5);
		TextView tv6 = (TextView)findViewById(R.id.q6);
		TextView tv7 = (TextView)findViewById(R.id.q7);
		TextView tv8 = (TextView)findViewById(R.id.q8);
		TextView tv9 = (TextView)findViewById(R.id.q9);
		
		tv1.setText(question[0]);
		tv1.setText(question[1]);
		tv1.setText(question[2]);
		tv1.setText(question[3]);
		tv1.setText(question[4]);
		tv1.setText(question[5]);
		tv1.setText(question[6]);
		tv1.setText(question[7]);
		tv1.setText(question[8]);
		
		//Initialize Groups and buttons
		rg1 = (RadioGroup)findViewById(R.id.rg1);
		rg2 = (RadioGroup)findViewById(R.id.rg2);
		rg3 = (RadioGroup)findViewById(R.id.rg3);
		rg4 = (RadioGroup)findViewById(R.id.rg4);
		rg5 = (RadioGroup)findViewById(R.id.rg5);
		rg6 = (RadioGroup)findViewById(R.id.rg6);
		rg7 = (RadioGroup)findViewById(R.id.rg7);
		rg8 = (RadioGroup)findViewById(R.id.rg8);
		rg9 = (RadioGroup)findViewById(R.id.rg9);
		submitbtn = (Button)findViewById(R.id.submit);
		backbtn = (Button)findViewById(R.id.back);
		tv = (TextView)findViewById(R.id.header);
		tv.setTypeface(Typeface.createFromAsset(getAssets(),"NANUMGOTHICBOLD.TTF"));
		
		submitbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				//TODO Transfer result as a json object to Server
				JSONObject sendjobj = new JSONObject();
				sendjarr = new JSONArray();
				reply = new String[9];
				RadioButton rb1 = (RadioButton)findViewById(rg1.getCheckedRadioButtonId());
				RadioButton rb2 = (RadioButton)findViewById(rg2.getCheckedRadioButtonId());
				RadioButton rb3 = (RadioButton)findViewById(rg3.getCheckedRadioButtonId());
				RadioButton rb4 = (RadioButton)findViewById(rg4.getCheckedRadioButtonId());
				RadioButton rb5 = (RadioButton)findViewById(rg5.getCheckedRadioButtonId());
				RadioButton rb6 = (RadioButton)findViewById(rg6.getCheckedRadioButtonId());
				RadioButton rb7 = (RadioButton)findViewById(rg7.getCheckedRadioButtonId());
				RadioButton rb8 = (RadioButton)findViewById(rg8.getCheckedRadioButtonId());
				RadioButton rb9 = (RadioButton)findViewById(rg9.getCheckedRadioButtonId());
				
				//Save Result
				reply[0] = rb1.getText().toString();
				reply[1] = rb2.getText().toString();
				reply[2] = rb3.getText().toString();
				reply[3] = rb4.getText().toString();
				reply[4] = rb5.getText().toString();
				reply[5] = rb6.getText().toString();
				reply[6] = rb7.getText().toString();
				reply[7] = rb8.getText().toString();
				reply[8] = rb9.getText().toString();
				
				for(int i=0;i<9;i++){
					try{
						JSONObject tmp = new JSONObject();
						tmp.put("for_studentid", stdid[i]);
						tmp.put("for_questionid", qid[i]);
						tmp.put("for_totalizationid", totid[i]);
						tmp.put("reply",reply[i]);
						sendjarr.put(tmp);	
					} catch (JSONException e) {
						Toast.makeText(getApplicationContext(), "서버로부터 데이터를 받아오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					}
				}
				try {
					sendjobj.put("results", sendjarr);
					srequest = sendjobj.toString();
				} catch (JSONException e) {
					Log.d("error",e.getMessage());
				}
				wat.execute();
			}
		});
		backbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(intent);		//Go To Main Page
				finish();
			}
		});
	}
    private void removePreferences(){
        SharedPreferences pref = getSharedPreferences("LOGINKEY", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("key");
        editor.commit();
    }
    
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	private static void trustAllHosts(){
		TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
			
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[]{};
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}
		}};
		
		try{
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null,trustAllCerts,new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch(Exception e){
			Log.d("error","Certificate Error..");
		}
	}

	class ReadAsynTask extends AsyncTask<Integer, Integer, Integer> {	
		//Read Task
		@Override
		protected void onPreExecute() {
			addr = "https://vm-descartes.softcloud.kr:30982/question/request";
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			request(addr);
			return null;
		}
		
	    private String request(String urlStr) {
	        StringBuilder output = new StringBuilder();
	        try {	  
	            String query = "";	 
	            
	            //Make query
        		SharedPreferences p = getSharedPreferences("LOGINKEY", MODE_PRIVATE);
        		String key = p.getString("key",null);
        		query += "sessionkey=";
        		query += URLEncoder.encode(key,"UTF-8");;
        		query += '&';
        		query += "lim=";
//        		query += URLEncoder.encode(lim,"UTF-8");
        		query += '&';
        		query += "page=";
//        		query += URLEncoder.encode(page,"UTF-8");
        		Log.d("surv_query",query);
        		
	            URL url = new URL(urlStr);
	            HttpURLConnection conn; 
	            if(url.getProtocol().toLowerCase().equals("https")){
	            	trustAllHosts();
	            	HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
		            https.setHostnameVerifier(DO_NOT_VERIFY);
	            	conn = https;
	            } else{
	            	conn = (HttpURLConnection)url.openConnection();
	            };
	            if (conn != null) {
	                conn.setConnectTimeout(10000);
	                conn.setRequestMethod("POST");
	                conn.setRequestProperty("Content-length", String.valueOf(query.length()));
	                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
	                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 

	                conn.setDoInput(true);
	                conn.setDoOutput(true);
            		
	                DataOutputStream dou = new DataOutputStream(conn.getOutputStream());
                	dou.writeBytes(query);
                    dou.close();
                    Log.d("Request","Succeed");
                    
                    DataInputStream din = new DataInputStream(conn.getInputStream());
                    char tmp;
                    for( int c = din.read(); c!= -1; c = din.read())
                    	qresponse += (char)c;
                    din.close();
                    
                    Log.d("qresponse",qresponse);
                    
	                int resCode = conn.getResponseCode();
	                if (resCode == HttpURLConnection.HTTP_OK)	conn.disconnect();
	            }
	        } catch(MalformedURLException mfue) {
	        	Log.d("error",mfue.getMessage());
//	        	Toast.makeText(getApplicationContext(), "연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
	        } catch(IOException ie){
	        	Log.d("error",ie.getMessage());
//	        	Toast.makeText(getApplicationContext(), "연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
	        }
	        
	        return qresponse;
	    }
	}
	class WriteAsynTask extends AsyncTask<Integer, Integer, Integer> {	
		//Write Task
		@Override
		protected void onPreExecute() {
			addr = "https://vm-descartes.softcloud.kr:30982/question/submit";
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			survey = request(addr);
			return null;
		}
		
	    private String request(String urlStr) {
	        StringBuilder output = new StringBuilder();
	        try {
	        	String query = "";	 
	        	//Make query
        		SharedPreferences p = getSharedPreferences("LOGINKEY", MODE_PRIVATE);
        		String key = p.getString("key",null);
        		query += "sessionkey=" + key;
        		query += '&';
        		query += "results=" + srequest;
        		
	            URL url = new URL(urlStr);	            
	            HttpURLConnection conn; 
	            if(url.getProtocol().toLowerCase().equals("https")){
	            	trustAllHosts();
	            	HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
		            https.setHostnameVerifier(DO_NOT_VERIFY);
	            	conn = https;
	            } else{
	            	conn = (HttpURLConnection)url.openConnection();
	            }
	            if (conn != null) {
	                conn.setConnectTimeout(10000);
	                conn.setRequestMethod("POST");
	                conn.setRequestProperty("Content-length", String.valueOf(query.length()));
	                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
	                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 

	                conn.setDoInput(true);
	                conn.setDoOutput(true);
            		
            		Log.d("submit_query",query);
	                DataOutputStream dou = new DataOutputStream(conn.getOutputStream());
                	dou.writeBytes(query);
                    dou.close();
                    
                    
                    DataInputStream din = new DataInputStream(conn.getInputStream());
                    char tmp;
                    for( int c = din.read(); c!= -1; c = din.read())
                    	sresponse += (char)c;
                    din.close();
                    
                    Log.d("sresponse",sresponse);
            		
	                int resCode = conn.getResponseCode();
	                if (resCode == HttpURLConnection.HTTP_OK) {
	                    //Get response
	                    JSONObject jo = new JSONObject(sresponse);
	                    String result = jo.getString("result").toString();
	                    if(result.equals("success") == false){
	                    	success = false;
	        				AlertDialog.Builder alert = new AlertDialog.Builder(SurveyActivity.this);
	        				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
	        				    @Override
	        				    public void onClick(DialogInterface dialog, int which) {
	        				    dialog.dismiss();     //닫기
	        				    }
	        				});
	        				alert.setMessage("전송 실패, 다시 시도해 주세요.");
	        				alert.show();
	                    }
	                    
	                    if(success == true){
	                    	startActivity(intent);
	                    	finish();
	                    }
	                    //Connection Close
	                    conn.disconnect();
	                }
	            }
	        } catch(MalformedURLException mfue) {
	        	Toast.makeText(getApplicationContext(), "연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
	        } catch(IOException ie){
	        	Toast.makeText(getApplicationContext(), "연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
	        } catch (JSONException e) {
	        	Toast.makeText(getApplicationContext(), "연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show(); 	
			}	
	        
	        return sresponse;
	    }
	}
}
