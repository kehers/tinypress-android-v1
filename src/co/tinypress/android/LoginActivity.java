package co.tinypress.android;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

@SuppressLint("SetJavaScriptEnabled")
public class LoginActivity extends Activity {

	AsyncTask<Void, Void, Void> mTask;
	boolean mDone;
	boolean mNoRepo;
	int mId;
	String mToken;
	String mUsername;
	String mName;
	String mAvatar;
	String mPageRepo;
	API api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		SharedPreferences mDb = getSharedPreferences(Constants.DB_FILE, 0);
		String mToken = mDb.getString(Constants.DB_TOKEN_VARIABLE, null);
		
		if (mToken != null) {
			Intent i = new Intent(this, HomeActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}
	
	public void loginBtnClicked(View v) {
		setContentView(R.layout.webview);
        Toast.makeText(this, "Loading the Github webpage to authenticate you", 
        				Toast.LENGTH_SHORT).show();
        
        mDone = false;
        mNoRepo = false;
        
        // clear all cookies
        CookieManager.getInstance().removeAllCookie();
        // We can override onPageStarted() in the web client and grab the token out.
        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String fragment = "?code=";
                int start = url.indexOf(fragment);
                if (start > -1) {
                    webview.stopLoading();
                    
                    final String code = url.substring(start + fragment.length(), url.length());
                    // Send code and get token here
                    mTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... p) {
		                    try {
		                        JSONObject mParams = new JSONObject();
		                    	mParams.put("client_id", Constants.CLIENT_ID);
		                        mParams.put("client_secret", Constants.CLIENT_SECRET);
		                        mParams.put("code", code);
		                        
		                        api = new API();
								api.post(Constants.ACCESS_TOKEN_URL, null,
										mParams, new ResponseCallback() {
									
									@Override
									public void handleResponse(int responseCode, String data) {

										Log.e("DEBUG", responseCode+" "+data);
										
										try {
											JSONObject json = new JSONObject(data);
											mToken = json.getString("access_token");
									        
											// Get user
											try {
												api.get(Constants.API_ROOT+"user", mToken, new ResponseCallback() {
													@Override
													public void handleResponse(int responseCode, String data) {

														Log.e("DEBUG", responseCode+" "+data);
														
														// User
														if (responseCode == 200) {
															try {
																JSONObject mJo = new JSONObject(data);
																mId = mJo.getInt("id");
																mUsername = mJo.getString("login");
																try {
																	mName = mJo.getString("name");
																}
																catch (JSONException j){
																	// If no name, name is username
																	mName = mUsername;
																}
																mAvatar = mJo.getString("avatar_url");
																
																// Repos
																api.get(Constants.API_ROOT
																		+"search/repositories?q="+mUsername+".github.+in:name+user:"+
																		mUsername,
																			mToken, new ResponseCallback() {
																	@Override
																	public void handleResponse(int responseCode, String data) {											
																		// User
																		Log.e("DEBUG", responseCode+" "+data);
																		if (responseCode == 200) {
																			try {
																				JSONObject mJo = new JSONObject(data);
																				JSONArray mJa = mJo.getJSONArray("items");
																				mJo = mJa.getJSONObject(0);
																				
																				mPageRepo = mJo.getString("name");
																				
																				// Save
																				Editor e = getSharedPreferences
																								(Constants.DB_FILE, 0).edit();
																				e.putString(Constants.DB_TOKEN_VARIABLE, mToken);
																				e.putString(Constants.DB_AVI_VARIABLE, mAvatar);
																				e.putString(Constants.DB_NAME_VARIABLE, mName);
																				e.putString(Constants.DB_PG_REPO_VARIABLE, mPageRepo);
																				e.putString(Constants.DB_USERNAME_VARIABLE, mUsername);
																				e.putInt(Constants.DB_ID_VARIABLE, mId);
																				e.commit();
																				
																				mDone = true;
																																								
																			} catch (JSONException e) {
																				// No repo
																				mNoRepo = true;
																			}
																		}
																		else {
																			// No repo
																			mNoRepo = true;
																		}
																	}
																});
																
															}
															catch (JSONException e) {} 
															catch (IOException e) {}
														}
													}
												});
											} catch (IOException e) {}
										} catch (JSONException e) {}
									}
								});
							}
		                    catch (IOException e) {} 
		                    catch (JSONException e) {}
		                    
		                    return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            mTask = null;
                    		setContentView(R.layout.activity_login);
                            
                            if (mDone) {
                        		Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        		startActivity(i);
                            }
                            else {
                            	if (mNoRepo) {
                            		TextView mTv = (TextView) findViewById(R.id.tagline);
                            		mTv.setText(Html.fromHtml(getString(R.string.no_repo_notification)));
                            		// 
                            		Button mButton = (Button) findViewById(R.id.button_login);
                            		mButton.setVisibility(View.GONE);
                            	}
                            	else {
							        Toast.makeText(LoginActivity.this,
							        		"Something went wrong. Please try again later :(", 
							        				Toast.LENGTH_LONG).show();
                            	}
                            }

							//finish();
                        }
                    };
                    mTask.execute();
                }
            }

        });
        webview.loadUrl(Constants.AUTH_URL+"?client_id="+Constants.CLIENT_ID+"&scope=public_repo");
	}
}
