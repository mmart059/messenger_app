package com.example.messenger;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
	String login;
	
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USR = "usr";
	private static final String TAG_LOGIN = "login";
	private static final String TAG_PASSWORD = "password";

	//url for the sign in page.
	private static String url_user_sign = "http://169.235.215.105/user_signin.php";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	Intent current = getIntent();
    	
    	login = current.getStringExtra(TAG_LOGIN);
    	
    	//Just sets the values to the username and passwords
    	mUserName = (EditText) findViewById(R.id.userSigninName);
    	mUserPassword = (EditText) findViewById(R.id.userSigninPassword);
    	
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
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("login", login));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = mJSONP.makeHttpRequest(
								url_user_sign, "GET", params);

						// check your log for json response
						Log.d("User details ", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							//successfully received user details
							JSONArray usrObj = json.getJSONArray(TAG_USR); // JSON Array
							
							// get first product object from JSON Array
							JSONObject product = usrObj.getJSONObject(0);

							// product with this pid found
							// Edit Text
							txtName = (EditText) findViewById(R.id.inputName);
							txtPrice = (EditText) findViewById(R.id.inputPrice);
							txtDesc = (EditText) findViewById(R.id.inputDesc);

							// display product data in EditText
							txtName.setText(product.getString(TAG_LOGIN));
							txtPrice.setText(product.getString(TAG_PASSWORD));

						}else{
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

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