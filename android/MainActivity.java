package com.example.messenger;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends Activity {

	private ProgressDialog mDialog;
	JSONParser mJSONP;
	EditText mUserName;
	EditText mUserPassword;
	
	private static final String TAG_SUCCESS = "success";

	//url for the sign in page.
	private static String url_user_sign = "http://169.235.215.105/user_signin.php";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
   
    	//Just sets the values to the username and passwords
    	mUserName = (EditText) findViewById(R.id.userName);
    	mUserPassword = (EditText) findViewById(R.id.userPassword);
    	
    	//need an onclick listerner for the button sigin
    	
    }
    
    /*
     * User sign in Asynchronous Task in order to retrieve username and 
     * password from the database
    */
	class SignIn extends AsyncTask<String, String, String>{
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mDialog = new ProgressDialog(MainActivity.this);
			mDialog.setMessage("Attempting to Signin...");
			mDialog.setIndeterminate(false);
			mDialog.setCancelable(true);
			mDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			String name = mUserName.getText().toString();
			String password = mUserPassword.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();


			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = mJSONP.makeHttpRequest(url_user_sign,
					"GET", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully retrieved user data
					//Intent i = new Intent(getApplicationContext(), .class);
					//startActivity(i);
					
					// closing this screen
					finish();
				} else {
					// failed to create product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			mDialog.dismiss();
			
			
			//then going to display data for the new UI
		}
	}
}