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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.clutchwin.cachetasks.PlayersBattersCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.service.PlayersBattersAsyncTask;
import com.clutchwin.viewmodels.PlayersBattersViewModel;

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
public class PlayersBattersFragment extends Fragment implements AbsListView.OnItemClickListener,
        PlayersBattersAsyncTask.OnLoadCompleteListener,
        PlayersBattersCacheAsyncTask.OnLoadCompleteListener {

    private OnFragmentInteractionListener mListener;
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
     * Progress dialog for the cache and service async background tasks.
     */
    private ProgressDialog progressDialog;

    public static PlayersBattersFragment newInstance() {
        PlayersBattersFragment fragment = new PlayersBattersFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayersBattersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        PlayersBattersCacheAsyncTask cacheTask;
        cacheTask = (PlayersBattersCacheAsyncTask)ClutchWinApplication.getInstance().getTask(Config.PB_CacheFileKey);

        PlayersBattersAsyncTask serviceTask;
        serviceTask = (PlayersBattersAsyncTask)ClutchWinApplication.getInstance().getTask(Config.PB_SvcTaskKey);

        if(ClutchWinApplication.getPlayersBattersViewModel().ITEMS.isEmpty() &&
                !ClutchWinApplication.getPlayersBattersViewModel().getIsBusy()) {

            if(cacheTask == null && Helpers.checkFileExists(activity, Config.PB_CacheFileKey)) {

                PlayersBattersCacheAsyncTask cacheAsyncTask = new PlayersBattersCacheAsyncTask(activity);

                ClutchWinApplication.getInstance().registerTask(Config.PB_CacheFileKey, cacheAsyncTask);
                cacheAsyncTask.setOnCompleteListener(this);
                ClutchWinApplication.getPlayersBattersViewModel().setIsBusy(true);
                progressDialog.show();
                cacheAsyncTask.execute();

            } else {
                if(serviceTask == null) {
                    initiateServiceCall(false);
                }
            }

        }else {
            if(serviceTask == null) {
                initiateServiceCall(false);
            }
        }

        mAdapter = new ArrayAdapter<PlayersBattersViewModel.Batter>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                ClutchWinApplication.getPlayersBattersViewModel().ITEMS);

        if(cacheTask != null){
            progressDialog.show();
            cacheTask.setOnCompleteListener(this);
        }
        if(serviceTask != null){
            progressDialog.show();
            serviceTask.setOnCompleteListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playersbatters, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        Button yearButton = (Button) view.findViewById(R.id.btnYears);
        String year = ClutchWinApplication.getPlayersContextViewModel().getYearId();
        if(year != null && year.length() >= 1){
            yearButton.setText(year + " >");
        }
        yearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onGoToYearsInteraction();
            }
        });

        Button teamButton = (Button) view.findViewById(R.id.btnTeams);
        String team = ClutchWinApplication.getPlayersContextViewModel().getTeamId();
        if(team != null && team.length() >= 1){
            teamButton.setText(team + " >");
        }

        String yearId = ClutchWinApplication.getPlayersContextViewModel().getYearId();
        if(yearId != null && yearId.length() > 0) {
            teamButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mListener.onGoToTeamsInteraction();
                }
            });
        }

        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        emptyText.setText(getString(R.string.select_season_team));
        mListView.setEmptyView(emptyText);
        //emptyText.setVisibility(TextView.INVISIBLE);

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

        dismissProgressDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPlayersBattersInteraction(ClutchWinApplication.getPlayersBattersViewModel().ITEMS.get(position).getRetroPlayerId());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        if(mListView != null) {
            View emptyView = mListView.getEmptyView();

            if (emptyText instanceof String) {
                ((TextView) emptyView).setText(emptyText);
            }
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    */
    public interface OnFragmentInteractionListener {
        public void onPlayersBattersInteraction(String id);
        public void onPlayersBattersInteractionFail(String type);
        public void onGoToYearsInteraction();
        public void onGoToTeamsInteraction();
    }

    @Override
    public void onBatterServiceComplete(List<PlayersBattersViewModel.Batter> result){
        PlayersBattersAsyncTask task;
        task = (PlayersBattersAsyncTask)ClutchWinApplication.getInstance().getTask(Config.PB_SvcTaskKey);
        if(task != null){
            task.setOnCompleteListener(null);
        }
        ClutchWinApplication.getInstance().unregisterTask(Config.PB_SvcTaskKey);

        ClutchWinApplication.getPlayersBattersViewModel().updateList(result);
        ((ArrayAdapter) mAdapter).notifyDataSetChanged();
        ClutchWinApplication.getPlayersBattersViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onBatterServiceFailure(){
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that a failure has happened.
            mListener.onPlayersBattersInteractionFail("");
        }
        dismissProgressDialog();
    }

    @Override
    public void onBatterCacheComplete(List<PlayersBattersViewModel.Batter> result){
        PlayersBattersCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (PlayersBattersCacheAsyncTask)ClutchWinApplication.getInstance().getTask(Config.PB_CacheFileKey);
        if(cacheAsyncTask != null){
            cacheAsyncTask.setOnCompleteListener(null);
        }
        ClutchWinApplication.getInstance().unregisterTask(Config.PB_CacheFileKey);

        ClutchWinApplication.getPlayersBattersViewModel().updateList(result);
        ((ArrayAdapter) mAdapter).notifyDataSetChanged();
        ClutchWinApplication.getPlayersBattersViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onBatterCacheFailure(){
        PlayersBattersCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (PlayersBattersCacheAsyncTask)ClutchWinApplication.getInstance().getTask(Config.PB_CacheFileKey);
        if(cacheAsyncTask != null){
            cacheAsyncTask.setOnCompleteListener(null);
        }
        ClutchWinApplication.getInstance().unregisterTask(Config.PB_CacheFileKey);
        dismissProgressDialog();
        //if any failure occurs loading cache, just call the service for fresh seasons
        initiateServiceCall(true);
    }

    private void initiateServiceCall(boolean force){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(ClutchWinApplication.getPlayersContextViewModel().shouldExecuteLoadBatters(netAvailable) || force) {
            if(netAvailable) {
                setEmptyText(getString(R.string.no_search_results));

                PlayersBattersAsyncTask task = new PlayersBattersAsyncTask(getActivity(),
                        ClutchWinApplication.getPlayersContextViewModel());

                ClutchWinApplication.getInstance().registerTask(Config.PB_SvcTaskKey, task);
                task.setOnCompleteListener(this);
                ClutchWinApplication.getPlayersBattersViewModel().setIsBusy(true);
                progressDialog.show();
                task.execute();

            } else {
                if (null != mListener) {
                    mListener.onPlayersBattersInteractionFail(Config.NoInternet);
                }
            }
        }
    }

    private void dismissProgressDialog() {
        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
