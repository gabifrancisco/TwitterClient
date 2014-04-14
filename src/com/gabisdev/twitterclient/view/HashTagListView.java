package com.gabisdev.twitterclient.view;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.content.Context;
import android.graphics.Color;
import android.widget.ListView;

import com.GGLibrary.GG.Resize;
import com.gabisdev.twitterclient.R;

public class HashTagListView extends PullToRefreshLayout {

	private ListView listView;
	
	public ListView getListView() {
		return listView;
	}

	public HashTagListView(Context context) {
		super(context);
		
		Resize.setup(context, false);
		
		listView = new ListView(context);
		listView.setCacheColorHint(0);
        listView.setSelector(R.drawable.transparent);
        listView.setBackgroundColor(Color.TRANSPARENT);
        listView.setLayoutParams(Resize.createParams(0, 10, 480, 800, 0, null));
        
		this.addView(listView);
		
	}

}
