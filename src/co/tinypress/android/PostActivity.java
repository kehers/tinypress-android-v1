package co.tinypress.android;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PostActivity extends ActionBarActivity {

	String mSha;
	String mPath;
	String mDraft;
	String mTitle;
	int mPosition;
	boolean mHideDelete;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mSha = bundle.getString("sha");
			mPath = bundle.getString("path");
			mPosition = bundle.getInt("position");
			
			mTitle = bundle.getString("title");
			getSupportActionBar().setTitle(mTitle);
			EditText tv = (EditText) findViewById(R.id.input_title);
			tv.setText(mTitle);

			String body = bundle.getString("body");
			tv = (EditText) findViewById(R.id.input_content);
			tv.setText(body);
			
			mDraft = bundle.getString("draft");
			if (mDraft.equals("1")) {
				CheckBox cb = (CheckBox) findViewById(R.id.checkbox_draft);
				cb.setChecked(true);
			}
		}
		else {
			getSupportActionBar().setTitle("New Post");
			// Hide delete menu
			mHideDelete = true;
			supportInvalidateOptionsMenu();
		}
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.post_edit, menu);
		
		if (mHideDelete)
			menu.getItem(1).setVisible(false);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Send or delete
		int id = item.getItemId();
		switch(id){
			case R.id.action_delete:
				Intent i = new Intent(this, DeletePostService.class);
				i.putExtra(Constants.EXTRA_SHA, mSha);
				i.putExtra(Constants.EXTRA_PATH, mPath);
				i.putExtra(Constants.EXTRA_TITLE, mTitle);
				startService(i);

				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction(Constants.DELETE_POSITION);
				broadcastIntent.putExtra("position", mPosition);
				sendBroadcast(broadcastIntent);
				
		        Toast.makeText(this, "Post deleted", 
        				Toast.LENGTH_SHORT).show();
		        finish();
			break;
			case R.id.action_send:
				// Send
				EditText tv = (EditText) findViewById(R.id.input_title);
				String title = tv.getText().toString().trim();
				if (TextUtils.isEmpty(title)) {
					tv.requestFocus();
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        	builder.setMessage("You missed the post title.").create().show();
					return true;
				}

				tv = (EditText) findViewById(R.id.input_content);
				String body = tv.getText().toString().trim();
				
				final String url = Tinypress.urlfy(title);
				Log.e("DEBUG", url);

				HashMap<String, String> yaml = new HashMap<>();
				yaml.put("title", title);
				yaml.put("layout", "post");
				CheckBox cb = (CheckBox) findViewById(R.id.checkbox_draft);
				yaml.put("published", cb.isChecked() ? "false" : "true");
				
				StringBuffer bb = new StringBuffer();
				bb.append("---\r\n");
		        for (Map.Entry<String, String> entry : yaml.entrySet()) {
		            bb.append(entry.getKey());
		            bb.append(": ");
		            bb.append(entry.getValue());
		            bb.append("\r\n");
		        }
	            bb.append("---\r\n");
	            bb.append(body);
	            body = bb.toString();
 			
				final JSONObject params = new JSONObject();
				try {
					String commit = (mSha == null) ? "New post: " : "Post update: ";
					commit += title;
					
					params.put("path", "_posts/"+url);
					params.put("message", commit);
					params.put("content", Base64.encodeToString(body.getBytes(), Base64.DEFAULT|Base64.NO_WRAP));
					
					if (mSha != null)
						params.put("sha", mSha);
						
				} catch (JSONException e) {}
				
				i = new Intent(this, PostService.class);
				i.putExtra(Constants.EXTRA_PARAM, params.toString());
				i.putExtra(Constants.EXTRA_URL, url);
				startService(i);
				
		        Toast.makeText(this, "Posting content", 
		        				Toast.LENGTH_SHORT).show();
		        // Finish!
		        finish();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

}
