package com.bignerdranch.android.networkingarchitecture.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.networkingarchitecture.R;
import com.bignerdranch.android.networkingarchitecture.VenueApplication;
import com.bignerdranch.android.networkingarchitecture.listener.VenueSearchListener;
import com.bignerdranch.android.networkingarchitecture.model.TokenStore;
import com.bignerdranch.android.networkingarchitecture.model.Venue;
import com.bignerdranch.android.networkingarchitecture.view.VenueListAdapter;
import com.bignerdranch.android.networkingarchitecture.web.DataManager;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class VenueListFragment extends Fragment implements VenueSearchListener {
    private static final int AUTHENTICATION_ACTIVITY_REQUEST = 0;

    private RecyclerView mRecyclerView;
    private VenueListAdapter mVenueListAdapter;
    private List<Venue> mVenueList;
    private TokenStore mTokenStore;



    @Inject
    DataManager mDataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mTokenStore = TokenStore.get(getContext());
        VenueApplication.component(getContext()).inject(this);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.venueListRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mVenueListAdapter = new VenueListAdapter(Collections.EMPTY_LIST);
        mRecyclerView.setAdapter(mVenueListAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
     //   mDataManager = DataManager.get(getContext());
        mDataManager.addVenueSearchListener(this);
        mDataManager.fetchVenueSearch();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDataManager.removeVenueSearchListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mTokenStore.getAccessToken() == null) {
            inflater.inflate(R.menu.menu_sign_in, menu);
        } else {
            inflater.inflate(R.menu.menu_sign_out, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_in:
                Intent authenticationIntent = AuthenticationActivity
                        .newIntent(getActivity());
                startActivityForResult(authenticationIntent,
                        AUTHENTICATION_ACTIVITY_REQUEST);
                return true;
            case R.id.sign_out:
                mTokenStore.setAccessToken(null);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTHENTICATION_ACTIVITY_REQUEST) {
            getActivity().invalidateOptionsMenu();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onVenueSearchFinished() {
        mVenueList = mDataManager.getVenueList();
        mVenueListAdapter.setVenueList(mVenueList);
    }
}
