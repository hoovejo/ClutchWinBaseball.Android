package com.clutchwin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.clutchwin.adapters.PlayersTeamsAdapter;
import com.clutchwin.cachetasks.PlayersTeamsCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.service.PlayersTeamsAsyncTask;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlayersTeamsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private ServiceCompleteImpl onServiceComplete;
    private CacheCompleteImpl onCacheComplete;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private PlayersTeamsAdapter mAdapter;

    /**
     * The view models for this fragment
     */
    private PlayersContextViewModel playersContextViewModel;
    private PlayersTeamsViewModel playersTeamsViewModel;

    public static PlayersTeamsFragment newInstance() {
        PlayersTeamsFragment fragment = new PlayersTeamsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayersTeamsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();
        ClutchWinApplication app = (ClutchWinApplication)activity.getApplicationContext();
        playersContextViewModel = app.getPlayersContextViewModel();
        playersTeamsViewModel = app.getPlayersTeamsViewModel();

        if(playersTeamsViewModel.ITEMS.isEmpty() && !playersTeamsViewModel.getIsBusy()) {

            if(Helpers.checkFileExists(activity, Config.PT_CacheFileKey)) {
                onCacheComplete = new CacheCompleteImpl();
                PlayersTeamsCacheAsyncTask cacheAsyncTask = new PlayersTeamsCacheAsyncTask(activity, playersTeamsViewModel);
                cacheAsyncTask.setOnCompleteListener(onCacheComplete);
                cacheAsyncTask.execute();
            } else {
                initiateServiceCall(false);
            }
        } else {
            initiateServiceCall(false);
        }

        mAdapter = new PlayersTeamsAdapter(activity,
                R.layout.listview_teamsfranchises_row, playersTeamsViewModel.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playersteams, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        emptyText.setText(getString(R.string.no_search_results));
        mListView.setEmptyView(emptyText);
        emptyText.setVisibility(TextView.INVISIBLE);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.must_implement));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        onServiceComplete = null;
        onCacheComplete = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPlayersTeamsInteraction(playersTeamsViewModel.ITEMS.get(position).getTeamId());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    */
    public interface OnFragmentInteractionListener {
        public void onPlayersTeamsInteraction(String id);
        public void onPlayersTeamsInteractionFail(String type);
    }

    private void initiateServiceCall(boolean force){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(playersContextViewModel.shouldExecuteLoadTeams(netAvailable) || force) {
            if(netAvailable) {
                onServiceComplete = new ServiceCompleteImpl();
                PlayersTeamsAsyncTask task = new PlayersTeamsAsyncTask(getActivity(), playersContextViewModel,
                        playersTeamsViewModel);
                task.setOnCompleteListener(onServiceComplete);
                task.execute();
            } else {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that a failure has happened.
                    mListener.onPlayersTeamsInteractionFail(Config.NoInternet);
                }
            }
        }
    }

    private class ServiceCompleteImpl implements PlayersTeamsAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersTeamsInteractionFail("");
            }
        }
    }

    private class CacheCompleteImpl implements PlayersTeamsCacheAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){ mAdapter.notifyDataSetChanged(); }
        @Override
        public void onFailure(){
            //if any failure occurs loading cache, just call the service for fresh seasons
            initiateServiceCall(true);
        }
    }
}
