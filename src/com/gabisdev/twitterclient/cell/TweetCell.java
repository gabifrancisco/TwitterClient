package com.gabisdev.twitterclient.cell;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.GGLibrary.GG.Resize;

public class TweetCell extends RelativeLayout {

	private TextView nameTextView;
	private TextView screenNameTextView;
	private TextView tweetTextView;
	
	private ImageView profileImageView;
	
	public TextView getNameTextView() {
		return nameTextView;
	}

	public TextView getTweetTextView() {
		return tweetTextView;
	}

	public ImageView getProfileImageView() {
		return profileImageView;
	}

	public TextView getScreenNameTextView() {
		return screenNameTextView;
	}

	public TweetCell(Context context) {
		super(context);
		
		profileImageView = new ImageView(context);
		profileImageView.setLayoutParams(Resize.createParams(10, 30, 70, 70, 0, null));
		
		this.addView(profileImageView);
		
		LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(Resize.createParams(110, 20, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, null));
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setPadding(0, 10, 20, 0);
		
		this.addView(ll);
		
		nameTextView = new TextView(context);
		nameTextView.setLayoutParams(Resize.createParams(0, 0, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, null));
		nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22 * Resize.getxRatio());
		nameTextView.setTextColor(Color.BLACK);
		nameTextView.setTypeface(Typeface.DEFAULT_BOLD);
		ll.addView(nameTextView);
		
		screenNameTextView = new TextView(context);
		screenNameTextView.setLayoutParams(Resize.createParams(0, 0, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, null));
		screenNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20 * Resize.getxRatio());
		screenNameTextView.setTextColor(Color.GRAY);
		ll.addView(screenNameTextView);
		
		tweetTextView = new TextView(context);
		tweetTextView.setLayoutParams(Resize.createParams(110, 80, 325, LayoutParams.WRAP_CONTENT, 0, null));
		tweetTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20 * Resize.getxRatio());
		tweetTextView.setTextColor(Color.BLACK);
		
		this.addView(tweetTextView);
		
	}

}
