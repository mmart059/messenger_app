package com.example.messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	private InputStream mInputStream = null;
	private JSONObject mJSONObject = null;
	private String mJSON = "";

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {

		// Making HTTP request
		try {
			
			// check for request method
			if(method.equalsIgnoreCase("POST")){
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				//getting the content from the httpPost
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				mInputStream = httpEntity.getContent();
				
			}else if(method.equalsIgnoreCase("GET")){
				// request method is GET
				Log.d("JSON PARSER is attempting to GET from " , url );
				
				//get the request according to the given url
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);

				//receiving the response from the http request
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				mInputStream = httpEntity.getContent();
			}			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//receiving informatino through the input stream and converting to JSON object
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					mInputStream, "iso-8859-1"), 8);
			StringBuilder mStringBuild = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				mStringBuild.append(line + "\n");
			}
			mInputStream.close();
			mJSON = mStringBuild.toString();
		} catch (Exception e) {
			Log.d("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			mJSONObject = new JSONObject(mJSON);
		} catch (JSONException e) {
			Log.d("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return mJSONObject;

	}
}
