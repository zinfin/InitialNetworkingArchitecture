package com.bignerdranch.android.networkingarchitecture.view;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.networkingarchitecture.R;

public class VenueView extends LinearLayout {
    private TextView mTitleTextView;
    private TextView mAddressTextView;
    private ImageView mVenueImage;

    public VenueView(Context context) {
        this(context, null);
    }

    public VenueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        setLayoutParams(params);

        LayoutInflater inflater = LayoutInflater.from(context);
        VenueView view = (VenueView) inflater.inflate(
                R.layout.view_venue, this, true);
        mTitleTextView = (TextView) view.findViewById(
                R.id.view_venue_list_VenueTitleTextView);
        mAddressTextView = (TextView) view.findViewById(
                R.id.view_venue_list_VenueLocationTextView);
        mVenueImage = (ImageView)view.findViewById(R.id.view_venue_list_icon);
    }

    public void setVenueTitle(final String title) {
        mTitleTextView.setText(title);
    }

    public void setVenueAddress(String address) {
        mAddressTextView.setText(address);
    }

    public void setVenueImage(Uri uri){ mVenueImage.setImageURI( uri);}
}
