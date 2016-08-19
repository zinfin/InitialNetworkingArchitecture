package com.bignerdranch.android.networkingarchitecture.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;
import com.bignerdranch.android.networkingarchitecture.R;
import com.bignerdranch.android.networkingarchitecture.VenueApplication;
import com.bignerdranch.android.networkingarchitecture.listener.VenueCheckInListener;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.model.Venue;
import com.bignerdranch.android.networkingarchitecture.web.DataManager;

import javax.inject.Inject;

public class VenueDetailFragment extends Fragment implements VenueCheckInListener {
    private static final String ARG_VENUE_ID = "VenueDetailFragment.VenueId";
    public static final String EXPIRED_DIALOG = "expired_dialog";

    private String mVenueId;
    private Venue mVenue;
    private TextView mVenueNameTextView;
    private TextView mVenueAddressTextView;
    private TextView mVenueCheckInTextView;
    private TextView mVenueTipCntTextView;
    private TextView mVenueUserCntTextView;
    private Button mCheckInButton;
    private TokenStore mTokenStore;
    @Inject
     DataManager mDataManager;

    public static VenueDetailFragment newInstance(String venueId) {
        VenueDetailFragment fragment = new VenueDetailFragment();

        Bundle args = new Bundle();
        args.putString(ARG_VENUE_ID, venueId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VenueApplication.component(getContext()).inject(this);
        mTokenStore = TokenStore.get(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_detail, container, false);
        mVenueNameTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_venue_name_text_view);
        mVenueAddressTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_venue_address_text_view);
        mCheckInButton = (Button) view.findViewById(R.id.fragment_venue_detail_check_in_button);
        mVenueCheckInTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_checkinct);
        mVenueTipCntTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_tipct);
        mVenueUserCntTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_userct);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mVenueId = getArguments().getString(ARG_VENUE_ID);
        mDataManager.addVenueCheckInListener(this);
        mVenue = mDataManager.getVenue(mVenueId);
    }

    @Override
    public void onResume() {

        super.onResume();

        mVenueNameTextView.setText(mVenue.getName());
        mVenueAddressTextView.setText(mVenue.getFormattedAddress());
        int ct = mVenue.getStats().getCheckInsCount();
        mVenueCheckInTextView.setText("Check in count = " + Integer.toString(ct));
        mVenueUserCntTextView.setText("User count = " + Integer.toString(mVenue.getStats().getUserCount()));
        mVenueTipCntTextView.setText("Tip count = " + Integer.toString(mVenue.getStats().getTipCount()));
        if (mTokenStore.getAccessToken() != null){
            mCheckInButton.setVisibility(View.VISIBLE);
            mCheckInButton.setOnClickListener(mCheckInClickListener);
        }
    }

    @Override
    public void onStop() {

        super.onStop();
        mDataManager.removeVenueCheckInListener(this);
    }

    private View.OnClickListener mCheckInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDataManager.checkInToVenue(mVenueId);
        }
    };

    @Override public void onVenueCheckInIsFinished() {
        Toast.makeText(getActivity(), R.string.successful_check_in_message, Toast.LENGTH_SHORT).show();
    }

    @Override public void onTokenExpired() {
        mCheckInButton.setVisibility(View.GONE);
        ExpiredTokenDialogFragment dialogFragment = new ExpiredTokenDialogFragment();
        dialogFragment.show(getFragmentManager(), EXPIRED_DIALOG);
    }
}
