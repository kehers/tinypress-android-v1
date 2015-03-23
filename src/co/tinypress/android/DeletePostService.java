package co.tinypress.android;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.IntentService;
import android.content.Intent;

public class DeletePostService extends IntentService {
	
    public DeletePostService() {
		super("DeletePostService");
	}
    
    @Override
    public void onCreate(){
    	super.onCreate();
    }

	@Override
    protected void onHandleIntent(Intent intent) {

		String sha = intent.getStringExtra(Constants.EXTRA_SHA);
		String path = intent.getStringExtra(Constants.EXTRA_PATH);
		String title = intent.getStringExtra(Constants.EXTRA_TITLE);
		
		JSONObject params = new JSONObject();
		try {
			params.put("path", path);
			params.put("sha", sha);
			params.put("message", "Post deleted: "+title);
		} catch (JSONException e) {}		
		
	    API api = new API();
	    String endpoint = Constants.API_ROOT+"repos/"+Tinypress.getUsername()+"/"+Tinypress.getPageRepo()
	    		  +"/contents/"+path;
		try {
		  api.delete(endpoint, Tinypress.getToken(), params, new ResponseCallback() {
				@Override
				public void handleResponse(int responseCode, String data) {}
		  });
		}
		catch(IOException io){}
    }
}