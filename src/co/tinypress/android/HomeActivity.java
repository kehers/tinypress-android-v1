package co.tinypress.android;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HomeActivity extends ActionBarActivity {

	String mToken;
	int mId;
	String mUsername;
	String mName;
	String mAvatar;
	String mPageRepo;
	boolean mDone;
	
	PostsFragment mPostsFragment;
	BroadcastReceiver mReceiver, mReceiver2, mReceiver3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// Set static params
		SharedPreferences mDb = getSharedPreferences(Constants.DB_FILE, 0);
		mToken = mDb.getString(Constants.DB_TOKEN_VARIABLE, null);
		mAvatar = mDb.getString(Constants.DB_AVI_VARIABLE, null);
		mName = mDb.getString(Constants.DB_NAME_VARIABLE, null);
		mPageRepo = mDb.getString(Constants.DB_PG_REPO_VARIABLE, null);
		mUsername = mDb.getString(Constants.DB_USERNAME_VARIABLE, null);
		mId = mDb.getInt(Constants.DB_ID_VARIABLE, 0);
		
		Tinypress.setToken(mToken);
		Tinypress.setAvatar(mAvatar);
		Tinypress.setId(mId);
		Tinypress.setPageRepo(mPageRepo);
		Tinypress.setUsername(mUsername);

		mPostsFragment = new PostsFragment();
		getSupportActionBar().setTitle(mUsername);
		
        // Logout broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOGOUT_BROADCAST);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(mReceiver, intentFilter);
        
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.DELETE_POSITION);
        mReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	int position = intent.getIntExtra("position", -1);
            	if (position == -1)	return;
            	mPostsFragment.delete(position);
            }
        };
        registerReceiver(mReceiver2, intentFilter);
        
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.POST_BROADCAST);
        mReceiver3 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	mPostsFragment.reset(null);
            }
        };
        registerReceiver(mReceiver3, intentFilter);
	}

    @Override
    public void onResume(){
    	super.onResume();
    	// Execute pending fragments before checking isAdded
    	getSupportFragmentManager().executePendingTransactions();

    	if (!mPostsFragment.isAdded()) {
	        getSupportFragmentManager().beginTransaction()
	        	.add(R.id.content_frame, mPostsFragment).commit();
    	}
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_post) {
			Intent i = new Intent(this, PostActivity.class);
			startActivity(i);
		}
		else if (id == R.id.action_acc) {
			Intent i = new Intent(this, AccountActivity.class);
			startActivity(i);
		}

		return super.onOptionsItemSelected(item);
	}
	
	public void onDestroy(){
		unregisterReceiver(mReceiver);
		unregisterReceiver(mReceiver2);
		unregisterReceiver(mReceiver3);
		super.onDestroy();
	}

}