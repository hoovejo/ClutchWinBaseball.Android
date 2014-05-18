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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.clutchwin.cachetasks.PlayersYearsCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.service.PlayersYearsAsyncTask;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersYearsViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlayersYearsFragment extends Fragment implements AbsListView.OnItemClickListener {

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
    private ListAdapter mAdapter;

    /**
     * The view models for this fragment
     */
    private PlayersContextViewModel playersContextViewModel;
    private PlayersYearsViewModel playersYearsViewModel;

    public static PlayersYearsFragment newInstance() {
        PlayersYearsFragment fragment = new PlayersYearsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayersYearsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();
        ClutchWinApplication app = (ClutchWinApplication)activity.getApplicationContext();
        playersContextViewModel = app.getPlayersContextViewModel();
        playersYearsViewModel = app.getPlayersYearsViewModel();

        if(!app.getHasLoadedSeasonsOnce()){
            initiateServiceCall();
        }

        if(playersYearsViewModel.ITEMS.isEmpty() && !playersYearsViewModel.getIsBusy()) {

            if(Helpers.checkFileExists(activity, Config.PY_CacheFileKey)) {
                onCacheComplete = new CacheCompleteImpl();
                PlayersYearsCacheAsyncTask cacheAsyncTask = new PlayersYearsCacheAsyncTask(activity, playersYearsViewModel);
                cacheAsyncTask.setOnCompleteListener(onCacheComplete);
                cacheAsyncTask.execute();
            } else {
                initiateServiceCall();
            }
        }

        mAdapter = new ArrayAdapter<PlayersYearsViewModel.Year>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, playersYearsViewModel.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playersyears, container, false);

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
            mListener.onPlayersYearsInteraction(playersYearsViewModel.ITEMS.get(position).getId());
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
        public void onPlayersYearsInteraction(String id);
        public void onPlayersYearsInteractionFail(String type);
    }

    private void initiateServiceCall(){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(netAvailable) {
            onServiceComplete = new ServiceCompleteImpl();
            PlayersYearsAsyncTask task = new PlayersYearsAsyncTask(getActivity(), playersYearsViewModel);
            task.setOnCompleteListener(onServiceComplete);
            task.execute();
        } else {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersYearsInteractionFail(Config.NoInternet);
            }
        }
    }

    private class ServiceCompleteImpl implements PlayersYearsAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            ((ArrayAdapter) mAdapter).notifyDataSetChanged();
            ClutchWinApplication app = (ClutchWinApplication)getActivity().getApplicationContext();
            app.setHasLoadedSeasonsOnce(true);
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersYearsInteractionFail("");
            }
        }
    }

    private class CacheCompleteImpl implements PlayersYearsCacheAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){ ((ArrayAdapter) mAdapter).notifyDataSetChanged(); }
        @Override
        public void onFailure(){
            //if any failure occurs loading cache, just call the service for fresh seasons
            initiateServiceCall();
        }
    }
}
