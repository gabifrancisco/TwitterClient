package com.gabisdev.twitterclient.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.gabisdev.twitterclient.R;
import com.gabisdev.twitterclient.adapter.TweetAdapter;
import com.gabisdev.twitterclient.core.Authenticated;
import com.gabisdev.twitterclient.core.Twitter;
import com.gabisdev.twitterclient.view.HashTagListView;
import com.google.gson.Gson;

public class HashTagListActivity extends ActionBarActivity implements
		OnRefreshListener {

	private Context context;
	private ListView listView;

	private HashTagListView hashTagListView;

	private String hashTag = "android";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		hashTagListView = new HashTagListView(this);
		setContentView(hashTagListView);

		listView = hashTagListView.getListView();

		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(hashTagListView);

		hashTagListView.setRefreshing(true);

		downloadTweets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.timeline, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			openSearch();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openSearch() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Busca");
		alert.setMessage("Escreva a hashtag que você deseja buscar");

		final EditText input = new EditText(this);
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		alert.setView(input);

		input.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				hashTag = input.getText().toString();

				if (!hashTag.equals("")) {
					downloadTweets();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		alert.show();
	}

	public void downloadTweets() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			hashTagListView.setRefreshing(true);
			getSupportActionBar().setTitle("Tweets for #" + hashTag);
			new TwitterSearchAsyncTask().execute(hashTag);
		}
	}

	private class TwitterSearchAsyncTask extends
			AsyncTask<String, Void, String> {

		final static String CONSUMER_KEY = "NruZxY1UKy9EuExSqP3qmbvfZ";
		final static String CONSUMER_SECRET = "Ogjmnid90AeO90O3wp8kQRoHPgyBv72MC0jsnmUJMik7V2V5QR";
		final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";

		String twitterSearchURL;

		@Override
		protected String doInBackground(String... params) {
			twitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json?q=%23"
					+ params[0] + "&result_type=mixed&count=30";
			return getTweetsWithHashTag(twitterSearchURL);
		}

		@Override
		protected void onPostExecute(String result) {

			JSONObject tweetsObject;
			try {
				tweetsObject = new JSONObject(result);
				JSONArray tweetsArray = tweetsObject.getJSONArray("statuses");

				Twitter tweets = jsonToTwitter(tweetsArray.toString());

				TweetAdapter adapter = new TweetAdapter(tweets, context);
				listView.setAdapter(adapter);

				hashTagListView.setRefreshComplete();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private Twitter jsonToTwitter(String result) {
			Twitter twits = null;

			if (result != null && result.length() > 0) {
				try {
					Gson gson = new Gson();
					twits = gson.fromJson(result, Twitter.class);
				} catch (IllegalStateException ex) {
				}
			}
			return twits;
		}

		private Authenticated jsonToAuthenticated(String rawAuthorization) {
			Authenticated auth = null;
			if (rawAuthorization != null && rawAuthorization.length() > 0) {
				try {
					Gson gson = new Gson();
					auth = gson.fromJson(rawAuthorization, Authenticated.class);
				} catch (IllegalStateException ex) {
				}
			}
			return auth;
		}

		private String getResponseBody(HttpRequestBase request) {
			StringBuilder sb = new StringBuilder();
			try {

				DefaultHttpClient httpClient = new DefaultHttpClient(
						new BasicHttpParams());
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				String reason = response.getStatusLine().getReasonPhrase();

				if (statusCode == 200) {

					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();

					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(inputStream, "UTF-8"), 8);
					String line = null;
					while ((line = bReader.readLine()) != null) {
						sb.append(line);
					}

					bReader.close();
				} else {
					sb.append(reason);
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (ClientProtocolException ex1) {
			} catch (IOException ex2) {
			}
			return sb.toString();
		}

		private String getTweetsWithHashTag(String searchUrl) {
			String results = null;

			try {

				String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
				String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET,
						"UTF-8");

				String combined = urlApiKey + ":" + urlApiSecret;
				String base64Encoded = Base64.encodeToString(
						combined.getBytes(), Base64.NO_WRAP);

				HttpPost httpPost = new HttpPost(TwitterTokenURL);
				httpPost.setHeader("Authorization", "Basic " + base64Encoded);
				httpPost.setHeader("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				httpPost.setEntity(new StringEntity(
						"grant_type=client_credentials"));

				String rawAuthorization = getResponseBody(httpPost);
				Authenticated auth = jsonToAuthenticated(rawAuthorization);

				if (auth != null && auth.token_type.equals("bearer")) {

					HttpGet httpGet = new HttpGet(searchUrl);
					httpGet.setHeader("Authorization", "Bearer "
							+ auth.access_token);
					httpGet.setHeader("Content-Type", "application/json");
					results = getResponseBody(httpGet);
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (IllegalStateException ex1) {
			}
			return results;
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		downloadTweets();
	}
}
