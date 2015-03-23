package co.tinypress.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PostsFragment extends ListFragment
		implements LoaderCallbacks<ArrayList<HashMap<String, String>>>{
	
	PostsAdapter mAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

    	// Initialize an empty adapter here
    	mAdapter = new PostsAdapter(getActivity(), R.layout.posts_fragment);
    	// Add adapter
        setListAdapter(mAdapter);
        
    	// Initialize loader here
    	getLoaderManager().initLoader(0, getArguments(), this).forceLoad();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        
        return inflater.inflate(R.layout.posts_fragment, container, false);
    }
    
    @Override
    public void onResume() {
    	super.onResume();    	
    }

    @Override
    public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int id, Bundle args) {
    	// Create AsyncLoader and return here
    	return new PostsLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<HashMap<String, String>>> loader, ArrayList<HashMap<String, String>> data) {
    	// Show list
    	//Log.e("DEBUG", "Load finished "+data.size());
    	mAdapter.setData(data);
    	mAdapter.addAll(data); // <-- crazy hack to force view refresh. I'm not proud of this
    	mAdapter.notifyDataSetChanged();
    }
    
    public void delete(int position){
    	mAdapter.remove(mAdapter.getItem(position));
    	mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<HashMap<String, String>>> loader) {
    	// Loader reset
    	mAdapter.clear();
    }
    
    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
    	HashMap<String, String> post = mAdapter.getItem(position);

		Intent i = new Intent(getActivity(), PostActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("body", post.get("body"));
		bundle.putString("title", post.get("title"));
		bundle.putString("draft", post.get("draft"));
		bundle.putString("sha", post.get("sha"));
		bundle.putString("path", post.get("path"));
		bundle.putInt("position", position);
		
		i.putExtras(bundle);
		startActivity(i);
    }

	public void reset(Bundle args) {
		// Refresh
		getLoaderManager().restartLoader(0, args, this).forceLoad();
	}

}
