package co.tinypress.android;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AccountActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acc);
		
		// username
		final String username = Tinypress.getUsername();
		TextView tv = (TextView) findViewById(R.id.about_username);
		tv.setText(username);
		// url
		tv = (TextView) findViewById(R.id.about_url);
		tv.setText(Html.fromHtml(username+".github.io"));
		tv.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://"+username+".github.io"));
				startActivity(i);
			}
			
		});
		
		tv = (TextView) findViewById(R.id.about_section_2);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Account");
	}

	public void logoutBtnClicked(View v){
		// logout
		getSharedPreferences
		(Constants.DB_FILE, 0).edit().clear().commit();
		
		// Cant believe broadcast is still the only effective wat to logout
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(Constants.LOGOUT_BROADCAST);
		sendBroadcast(broadcastIntent);

		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
		finish();
	}
}
