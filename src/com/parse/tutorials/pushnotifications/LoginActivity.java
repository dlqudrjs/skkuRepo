package com.parse.tutorials.pushnotifications;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity{
	//Variable
	String str = "";
	Intent intent;
	TextView tv;
	Button lgnbtn;
	EditText pwd;
	EditText id;
	String addr = "";
	String result ="";
	String sessionid;
	HttpAsynTask hat;
	String srequest;
	String reply = "";
	boolean islogin = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		//initialize
		intent = new Intent(this,MainActivity.class);
		lgnbtn = (Button)findViewById(R.id.loginbtn);
		pwd = (EditText)findViewById(R.id.password);
		id = (EditText)findViewById(R.id.stdid);
		tv = (TextView)findViewById(R.id.tview);
		
		//Check SharedPreference
		SharedPreferences p = getSharedPreferences("LOGINKEY", MODE_PRIVATE);
		String tmp = p.getString("key",null);
		
		if(tmp != null){	//If There is a shared preference Go Main Activity
			startActivity(intent);
			finish();
		}
		
		//Button Listener
		lgnbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pwd.getText().toString().length() != 0 && id.getText().toString().length() != 0){					
					
					//Send Login Data to Server
					hat = new HttpAsynTask();
					hat.execute();
					
					Log.d("EXIT","EXIT");
				}
				else{
					AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    dialog.dismiss();     //닫기
					    }
					});
					alert.setMessage("모든 정보를 입력해 주세요.");
					alert.show();
				}
			}
		});
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
	
	class HttpAsynTask extends AsyncTask<Integer, Integer, Integer> {	
		//Inner Class For Connection between Server & Client
		@Override
		protected void onPreExecute() {
			addr = "https://vm-descartes.softcloud.kr:30982/user/login";
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			request(addr);
			return null;
		}
		
	    private String request(String urlStr) {
	        StringBuilder output = new StringBuilder();
	        try {
	        	String query = "userid=" + URLEncoder.encode(id.getText().toString(),"UTF-8");
				query += '&';
				query += "userpw=" + URLEncoder.encode(pwd.getText().toString(),"UTF-8");
				
				Log.d("query",query);
				
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
	                
                	DataOutputStream dou = new DataOutputStream(conn.getOutputStream());
                	dou.writeBytes(query);
                    dou.close();
                    
                    DataInputStream din = new DataInputStream(conn.getInputStream());
                    char tmp;
                    for( int c = din.read(); c!= -1; c = din.read())
                    	reply += (char)c;
                    
                    Log.d("result",reply);
                    din.close();
                    
	                int resCode = conn.getResponseCode();
	                if (resCode == HttpsURLConnection.HTTP_OK) {
	                    JSONObject jobj = new JSONObject(reply);
	                    reply = jobj.getString("result").toString();	//get result
	                    Log.d("reply",reply);
	                    sessionid = jobj.getString("sessionkey").toString();	//get session id
	                    Log.d("sid",sessionid);

	                    if(reply.equals("success") == true){		
	    					savePreferences("key", sessionid, "LOGINKEY");		//Make Shared Preferences
	                    	startActivity(intent);
	                    	finish();
	                    }
	                    else{
	    					AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
	    					alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
	    					    @Override
	    					    public void onClick(DialogInterface dialog, int which) {
	    					    dialog.dismiss();     //닫기
	    					    }
	    					});
	    					alert.setMessage("아이디 혹은 비밀번호가 올바르지 않습니다.");
	    					alert.show();
	    					
	    					id.setText("");
	    					pwd.setText("");
	                    }
	                    
	                    //Connection Close
	                    conn.disconnect();
	                }
	            }
	        }catch(MalformedURLException mfue) {
	        	Log.d("error",mfue.getMessage());
//	        	Toast.makeText(getApplicationContext(), "연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
	        } catch(IOException ie){
	        	Log.d("error",ie.getMessage());
//	        	Toast.makeText(getApplicationContext(), "연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
	        } catch (JSONException e) {
	        	Log.d("error",e.getMessage());
//	        	Toast.makeText(getApplicationContext(), "서버로부터 데이터를 받아오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show(); 	
			}
	        
	        return output.toString();
	    }
	}
	
    // 값 저장하기
    private void savePreferences(String key, String val, String file){
        SharedPreferences pref = getSharedPreferences(file, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,val);
        editor.commit();
    }
}

