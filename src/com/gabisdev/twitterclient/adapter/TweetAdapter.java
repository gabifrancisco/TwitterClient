package com.gabisdev.twitterclient.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gabisdev.twitterclient.cell.TweetCell;
import com.gabisdev.twitterclient.core.Twitter;
import com.gabisdev.twitterclient.model.Tweet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TweetAdapter extends BaseAdapter {

	private Twitter tweetList;
	private Context context;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	public TweetAdapter(Twitter list, Context context) {
		tweetList = list;
		this.context = context;
		
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tweetList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TweetCell cell = (TweetCell) convertView;
		
		Tweet tweet = tweetList.get(position);
		
		if (convertView == null) {
			cell = new TweetCell(context);
		}
		
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).build();
		
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), cell.getProfileImageView(), options);
		
		cell.getNameTextView().setText(tweet.getUser().getName());
		cell.getScreenNameTextView().setText("@" + tweet.getUser().getScreenName());
		cell.getTweetTextView().setText(tweet.getText());
		
		return cell;
	}

}
