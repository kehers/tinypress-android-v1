package co.tinypress.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class PostsLoader extends AsyncTaskLoader<ArrayList<HashMap<String, String>>> {

  // We hold a reference to the Loader’s data here.
  private ArrayList<HashMap<String, String>> mData;
  int mPage;
  int mStart;
  API mApi;

  public PostsLoader(Context context, Bundle args) {
	  super(context);
	  
	  mPage = 1;
  }

  @Override
  public ArrayList<HashMap<String, String>> loadInBackground() {
	  final ArrayList<HashMap<String, String>> posts = new ArrayList<HashMap<String, String>>();
	  
      final int count = mPage * 5;
      mStart = count - 5;
      
      // Return cache posts
      final String username = Tinypress.getUsername();
      final String repo = Tinypress.getPageRepo();
      final String token = Tinypress.getToken();
      
      mApi = new API();
	  try {
		  mApi.get(Constants.API_ROOT+"repos/"+username+"/"+repo+"/contents/_posts", token, new ResponseCallback() {
				@Override
				public void handleResponse(int responseCode, String data) {
					if (responseCode == 200) {
						// Cache posts here
						try {
							JSONArray json = new JSONArray(data);
							// Convert to list
							List<JSONObject> jsonList = new ArrayList<JSONObject>();
						    for (int i = 0; i < json.length(); i++) {
						        jsonList.add(json.getJSONObject(i));
						    }
							// Reverse list
							Collections.reverse(jsonList);
							// Convert back to json
							json = new JSONArray(jsonList);
							
							for(int i=0;i<json.length();i++){
								if (i < mStart)	continue;
								if (i == count)	break;
								
								final JSONObject jo = json.getJSONObject(i);
								final String path = jo.getString("path");
								final String name = jo.getString("name");
								final String sha = jo.getString("sha");
								
								// Only ask github if there is no cached value
								try {
									HashMap<String, String> header = new HashMap<String, String>();
									header.put("Accept", "application/vnd.github.VERSION.raw");
									mApi.get(Constants.API_ROOT+"repos/"+username+"/"+repo+"/contents/"+path,
												token, header, new ResponseCallback() {
										@Override
										public void handleResponse(int responseCode, String data) {
											if (responseCode == 200) {
												// Cache posts here
												HashMap<String, String> body = Tinypress.formatBody(data);
												HashMap<String, String> post = new HashMap<String, String>();
												String [] dateURL = Tinypress.getTitleAndDate(name);
												// Get url and date
												post.put("id", sha);
												post.put("title", body.get("title"));
												post.put("draft", body.get("published").equals("true") ? "0" : "1");
												post.put("sha", sha);
												post.put("date", dateURL[0]);
												post.put("path", path);
												post.put("url", dateURL[1]);
												post.put("permalink", body.get("permalink"));
												post.put("body", body.get("body"));
												posts.add(post);
											}
										}
									});
								}
								catch (IOException e){
									Log.e("DEBUG", e.getMessage());
								}
							}
						} catch (JSONException e) {
							Log.e("DEBUG", e.getMessage());
						}
					}
				}
			});
		}
		catch (IOException e){}

	  return posts;
  }

  @Override
  public void deliverResult(ArrayList<HashMap<String, String>> data) {
	  Log.e("DEBUG", "Deliver Result?");
    if (isReset()) {
    	// The Loader has been reset; ignore the result and invalidate the data.
    	//releaseResources(data);
    	Log.e("DEBUG", "Is reset");
    	return;
    }

    // Hold a reference to the old data so it doesn't get garbage collected.
    // We must protect it until the new data has been delivered.
    //ArrayList<HashMap<String, String>> oldData = mData;
    mData = data;

    if (isStarted()) {
      // If the Loader is in a started state, deliver the results to the
      // client. The superclass method does this for us.
      super.deliverResult(data);
    }
    else {
    	Log.e("DEBUG", "Is not started :/");
    }
  }

  @Override
  protected void onStartLoading() {
	  if (mData != null) {
	      // Deliver any previously loaded data immediately.
	      deliverResult(mData);
      }
  }

  @Override
  protected void onStopLoading() {
	  cancelLoad();
  }

  @Override
  protected void onReset() {
    // Ensure the loader has been stopped.
    onStopLoading();

    // At this point we can release the resources associated with 'mData'.
    if (mData != null) {
      //releaseResources(mData);
      mData = null;
    }
  }
}