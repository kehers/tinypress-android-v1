package co.tinypress.android;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PostsAdapter extends ArrayAdapter<HashMap<String, String>> {

	ArrayList<HashMap<String, String>> mPosts;
	
    private static class ViewHolder {
        TextView date;
        TextView title;
    }
	
	public PostsAdapter(Context context, int resource,
									ArrayList<HashMap<String, String>> posts) {
		super(context, resource, posts);

		mPosts = posts;
	}

	public PostsAdapter(Context context, int resource) {
		super(context, resource);
		mPosts = new ArrayList<HashMap<String, String>>();
	}
	
	public void setData(ArrayList<HashMap<String, String>> data){
        /*for (int i=0;i<data.size();i++) {
    		mLogs.add(data.get(i));
        }*/
		mPosts = data;
	}
	
	@Override
	public int getCount(){
		return mPosts.size();
	}
	
	@Override
	public void remove(HashMap<String, String> post){
		super.remove(post);
		mPosts.remove(post);
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {
				
		HashMap<String, String> log = mPosts.get(position);
		
		ViewHolder viewHolder;
		if (row == null) {
			viewHolder = new ViewHolder();
			
			row = LayoutInflater.from(getContext())
					.inflate(R.layout.view_item_post, parent, false);
			
          viewHolder.title = (TextView) row.findViewById(R.id.post_title);
          viewHolder.date = (TextView) row.findViewById(R.id.post_date);

          row.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) row.getTag();
		}

		// Date
		viewHolder.date.setText(log.get("date"));
		viewHolder.title.setText(log.get("title"));

		return row;
	}
};