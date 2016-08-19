package com.bignerdranch.android.networkingarchitecture.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.ImageView;
import com.bignerdranch.android.networkingarchitecture.R;
import com.bignerdranch.android.networkingarchitecture.controller.VenueDetailActivity;
import com.bignerdranch.android.networkingarchitecture.model.Category;
import com.bignerdranch.android.networkingarchitecture.model.Icon;
import com.bignerdranch.android.networkingarchitecture.model.Venue;
import com.squareup.picasso.Picasso;

public class VenueHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private VenueView mVenueView;
    private Venue mVenue;
    private ImageView mImageView;

    public VenueHolder(View itemView) {
        super(itemView);

        mVenueView = (VenueView) itemView;
        mVenueView.setOnClickListener(this);
        mImageView = (ImageView) mVenueView.findViewById(R.id.view_venue_list_icon);
    }

    public void bindVenue(Venue venue) {
        mVenue = venue;
        mVenueView.setVenueTitle(mVenue.getName());
        mVenueView.setVenueAddress(mVenue.getFormattedAddress());
        Category cat = mVenue.getPrimaryCategory();
        Icon icon = cat.getIcon();
        Uri uri = Uri.parse(icon.getPrefix()+"32" +icon.getSuffix());
        Context context = this.mVenueView.getContext();
        Picasso.with(context).load(uri).into(mImageView);

    }

    @Override
    public void onClick(View view) {
        Context context = view.getContext();
        Intent intent = VenueDetailActivity.newIntent(context, mVenue.getId());
        context.startActivity(intent);
    }
}

