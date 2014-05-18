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

import com.clutchwin.adapters.PlayersResultsAdapter;
import com.clutchwin.cachetasks.PlayersResultsCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.PlayersResultsAsyncTask;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersResultsViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlayersResultsFragment extends Fragment implements AbsListView.OnItemClickListener, IOnShowFragment {

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
    private PlayersResultsAdapter mAdapter;

    /**
     * The view models for this fragment
     */
    private PlayersContextViewModel playersContextViewModel;
    private PlayersResultsViewModel resultsViewModel;

    public static PlayersResultsFragment newInstance() {
        PlayersResultsFragment fragment = new PlayersResultsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayersResultsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();
        ClutchWinApplication app = (ClutchWinApplication)activity.getApplicationContext();
        playersContextViewModel = app.getPlayersContextViewModel();
        resultsViewModel = app.getPlayersResultsViewModel();

        if(resultsViewModel.ITEMS.isEmpty() && !resultsViewModel.getIsBusy()) {

            if(Helpers.checkFileExists(activity, Config.PR_CacheFileKey)) {
                onCacheComplete = new CacheCompleteImpl();
                PlayersResultsCacheAsyncTask cacheAsyncTask = new PlayersResultsCacheAsyncTask(activity, resultsViewModel);
                cacheAsyncTask.setOnCompleteListener(onCacheComplete);
                cacheAsyncTask.execute();
            }
        }

        mAdapter = new PlayersResultsAdapter(activity,
                R.layout.listview_playersresults_row, resultsViewModel.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playersresults, container, false);

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
            PlayersResultsViewModel.PlayersResult row = resultsViewModel.ITEMS.get(position);
            mListener.onPlayersResultsInteraction(row.getYear());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof String) {
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
        public void onPlayersResultsInteraction(String id);
        public void onPlayersResultsInteractionFail(String type);
    }

    public void onShowedFragment(){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(playersContextViewModel.shouldExecutePlayerResultsSearch(netAvailable) && !resultsViewModel.getIsBusy()) {
            if(netAvailable) {
                setEmptyText(getString(R.string.no_search_results));
                onServiceComplete = new ServiceCompleteImpl();
                PlayersResultsAsyncTask task = new PlayersResultsAsyncTask(getActivity(), playersContextViewModel,
                        resultsViewModel);
                task.setOnCompleteListener(onServiceComplete);
                task.execute();
            } else {
                if (null != mListener) {
                    mListener.onPlayersResultsInteractionFail(Config.NoInternet);
                }
            }
        } else {
            //likely new session and tabbed to results tab
            setEmptyText(getString(R.string.select_pitcher_first));
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ServiceCompleteImpl implements PlayersResultsAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersResultsInteractionFail("");
            }
        }
    }

    private class CacheCompleteImpl implements PlayersResultsCacheAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){ mAdapter.notifyDataSetChanged(); }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersResultsInteractionFail("");
            }
        }
    }
}
