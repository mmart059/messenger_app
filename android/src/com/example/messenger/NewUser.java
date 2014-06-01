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

public class NewUser extends Activity {
	private ProgressDialog mDialog;
	
	//User information that has to be put in
	EditText mUserName;
	EditText mUserPassword;
	EditText mUserPhone;
	JSONParser mJSONP;
	
	private static String url_create_user = "http://169.235.215.105/create_user.php";
	private static final String TAG_SUCCESS = "success";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
    /*
     * User sign in Asynchronous Task in order to retrieve username and 
     * password from the database
    */
	class CreateUser extends AsyncTask<String, String, String>{
		
		
		//Function just to show a progress dialog
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mDialog = new ProgressDialog(NewUser.this);
			mDialog.setMessage("Setting up...");
			mDialog.setIndeterminate(false);
			mDialog.setCancelable(true);
			mDialog.show();
		}
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			String name = mUserName.getText().toString();
			String password = mUserPassword.getText().toString();
			String phoneNumber = mUserPhone.getText().toString();
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", name));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("phoneNumber",phoneNumber));
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = mJSONP.makeHttpRequest(url_create_user,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully retrieved user data
					Intent i = new Intent(getApplicationContext(), UserHomeScreen.class);
					startActivity(i);
					
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
			super.onPostExecute(result);
			mDialog.dismiss();
		}
	}	
	
}
