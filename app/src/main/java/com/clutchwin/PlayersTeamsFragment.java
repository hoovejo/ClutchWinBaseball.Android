package com.clutchwin;

import android.app.Activity;
import android.app.ProgressDialog;
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

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlayersTeamsFragment extends Fragment implements AbsListView.OnItemClickListener,
        PlayersTeamsAsyncTask.OnLoadCompleteListener,
        PlayersTeamsCacheAsyncTask.OnLoadCompleteListener {

    private OnFragmentInteractionListener mListener;

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
     * Progress dialog for the cache and service async background tasks.
     */
    private ProgressDialog progressDialog;

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

        PlayersTeamsCacheAsyncTask cacheTask;
        cacheTask = (PlayersTeamsCacheAsyncTask) getApp().getTask(Config.PT_CacheFileKey);

        PlayersTeamsAsyncTask serviceTask;
        serviceTask = (PlayersTeamsAsyncTask) getApp().getTask(Config.PT_SvcTaskKey);

        if(getTeamsViewModel().ITEMS.isEmpty() && !getTeamsViewModel().getIsBusy()) {

            if(Helpers.checkFileExists(activity, Config.PT_CacheFileKey)) {
                PlayersTeamsCacheAsyncTask cacheAsyncTask = new PlayersTeamsCacheAsyncTask();

                getApp().registerTask(Config.PT_CacheFileKey, cacheAsyncTask);
                cacheAsyncTask.setOnCompleteListener(this);
                getTeamsViewModel().setIsBusy(true);
                getProgressDialog().show();
                cacheAsyncTask.execute();

            } else {
                if(serviceTask == null) {
                    initiateServiceCall(false);
                }
            }
        } else {
            if(serviceTask == null) {
                initiateServiceCall(false);
            }
        }

        mAdapter = new PlayersTeamsAdapter(activity,
                R.layout.listview_teamsfranchises_row, getTeamsViewModel().ITEMS);

        // Hook back up to running tasks if this fragment was recreated in the middle of a running task
        if (cacheTask != null) {
            getProgressDialog().show();
            cacheTask.setOnCompleteListener(this);
        }
        if (serviceTask != null) {
            getProgressDialog().show();
            serviceTask.setOnCompleteListener(this);
        }
        if(cacheTask == null && serviceTask == null){
            getTeamsViewModel().setIsBusy(false);
        }
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
        // kill any progress dialogs if we are being destroyed
        dismissProgressDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPlayersTeamsInteraction(getTeamsViewModel().ITEMS.get(position).getTeamId());
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

    @Override
    public void onPlayersTeamsServiceComplete(List<PlayersTeamsViewModel.Team> result) {
        PlayersTeamsAsyncTask task;
        task = (PlayersTeamsAsyncTask) getApp().getTask(Config.PT_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PT_SvcTaskKey);

        getTeamsViewModel().updateList(result);
        mAdapter.notifyDataSetChanged();
        getTeamsViewModel().setIsBusy(false);
        dismissProgressDialog();

        getApp().setHasLoadedSeasonsOnce(true);
    }

    @Override
    public void onPlayersTeamsServiceFailure() {
        PlayersTeamsAsyncTask task;
        task = (PlayersTeamsAsyncTask) getApp().getTask(Config.PT_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PT_SvcTaskKey);

        getTeamsViewModel().setIsBusy(false);
        dismissProgressDialog();

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that a failure has happened.
            mListener.onPlayersTeamsInteractionFail("");
        }
    }

    @Override
    public void onPlayersTeamsCacheComplete(List<PlayersTeamsViewModel.Team> result) {
        PlayersTeamsCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (PlayersTeamsCacheAsyncTask) getApp().getTask(Config.PT_CacheFileKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PT_CacheFileKey);

        getTeamsViewModel().updateList(result);
        mAdapter.notifyDataSetChanged();
        getTeamsViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onPlayersTeamsCacheFailure() {
        PlayersTeamsCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (PlayersTeamsCacheAsyncTask) getApp().getTask(Config.PT_CacheFileKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PT_CacheFileKey);
        getTeamsViewModel().setIsBusy(false);
        dismissProgressDialog();
        //if any failure occurs loading cache, just call the service for fresh teams
        initiateServiceCall(true);
    }

    private void initiateServiceCall(boolean force){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(getContextViewModel().shouldExecuteLoadTeams(netAvailable) || force) {
            if(netAvailable) {
                PlayersTeamsAsyncTask task = new PlayersTeamsAsyncTask();

                getApp().registerTask(Config.PT_SvcTaskKey, task);
                task.setOnCompleteListener(this);
                getTeamsViewModel().setIsBusy(true);
                getProgressDialog().show();
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

    private ClutchWinApplication getApp(){
        return ClutchWinApplication.getInstance();
    }

    private PlayersContextViewModel getContextViewModel(){
        return ClutchWinApplication.getPlayersContextViewModel();
    }

    private PlayersTeamsViewModel getTeamsViewModel(){
        return ClutchWinApplication.getPlayersTeamsViewModel();
    }

    private ProgressDialog getProgressDialog(){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        return progressDialog;
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
