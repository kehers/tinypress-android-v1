package co.tinypress.android;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


public class PostService extends IntentService {
	
    public PostService() {
		super("PostService");
	}
    
    @Override
    public void onCreate(){
    	super.onCreate();
    }

	@Override
    protected void onHandleIntent(Intent intent) {
		final int id = 1 + (int)(Math.random() * 10);

		String url = intent.getStringExtra(Constants.EXTRA_URL);
		String paramString = intent.getStringExtra(Constants.EXTRA_PARAM);
		JSONObject params = null;
		try {
			params = new JSONObject(paramString);
		} catch (JSONException e) {}		
		
		final NotificationManager notifyManager =
		        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentTitle("Tinypress")
		    .setContentText("Posting content")
		    .setSmallIcon(R.drawable.ic_notification);

	    builder.setProgress(1, 0, false);
	    notifyManager.notify(id, builder.build());

	    API api = new API();
	    String endpoint = Constants.API_ROOT+"repos/"+Tinypress.getUsername()+"/"+Tinypress.getPageRepo()
	    		  +"/contents/_posts/"+url;
		try {
		  api.put(endpoint, Tinypress.getToken(), params, new ResponseCallback() {
				@Override
				public void handleResponse(int responseCode, String data) {
					String s = null;
					if (responseCode == 200 || responseCode == 201) {
						s = getResources().getString(R.string.post_committed);
						// Send done broadcast
						Intent broadcastIntent = new Intent();
						broadcastIntent.setAction(Constants.POST_BROADCAST);
						sendBroadcast(broadcastIntent);
					}
					else {
						try {
							JSONObject j = new JSONObject(data);
							s = j.getString("message");
						} catch (JSONException e) {}
					}
					
			        builder.setContentText(s).setProgress(0, 0, false);
			        notifyManager.notify(id, builder.build());
				}
		  });
		}
		catch(IOException io){
	        builder.setContentText(getResources().getString(R.string.network_error)).setProgress(0, 0, false);
	        notifyManager.notify(id, builder.build());			
		}
    }
}