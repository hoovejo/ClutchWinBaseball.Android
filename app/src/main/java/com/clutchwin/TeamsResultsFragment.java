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

import com.clutchwin.adapters.TeamsResultsAdapter;
import com.clutchwin.cachetasks.TeamsResultsCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.TeamsResultsAsyncTask;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsResultsViewModel;

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
public class TeamsResultsFragment extends Fragment implements AbsListView.OnItemClickListener,
        TeamsResultsAsyncTask.OnLoadCompleteListener,
        TeamsResultsCacheAsyncTask.OnLoadCompleteListener,
        IOnShowFragment {

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private TeamsResultsAdapter mAdapter;
    /**
     * Progress dialog for the cache and service async background tasks.
     */
    private ProgressDialog progressDialog;

    public static TeamsResultsFragment newInstance() {
        TeamsResultsFragment fragment = new TeamsResultsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TeamsResultsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();

        TeamsResultsCacheAsyncTask cacheTask;
        cacheTask = (TeamsResultsCacheAsyncTask) getApp().getTask(Config.TR_CacheFileKey);

        TeamsResultsAsyncTask serviceTask;
        serviceTask = (TeamsResultsAsyncTask) getApp().getTask(Config.TR_SvcTaskKey);

        // if we are constructing and have no active tasks in the background, ensure no other orphan
        // tasks left the viewModel as busy on an orientation change
        if(cacheTask == null && serviceTask == null){
            getResultsViewModel().setIsBusy(false);
        }

        if(getResultsViewModel().ITEMS.isEmpty() && !getResultsViewModel().getIsBusy()) {

            if(Helpers.checkFileExists(activity, Config.TR_CacheFileKey)) {
                TeamsResultsCacheAsyncTask cacheAsyncTask = new TeamsResultsCacheAsyncTask();

                getApp().registerTask(Config.TR_CacheFileKey, cacheAsyncTask);
                cacheAsyncTask.setOnCompleteListener(this);
                getResultsViewModel().setIsBusy(true);
                getProgressDialog().show();
                cacheAsyncTask.execute();
            }
        }

        mAdapter = new TeamsResultsAdapter(activity, R.layout.listview_teamsresults_row,
                getResultsViewModel().ITEMS);

        // Hook back up to running tasks if this fragment was recreated in the middle of a running task
        if (cacheTask != null) {
            getProgressDialog().show();
             cacheTask.setOnCompleteListener(this);
        }
        if (serviceTask != null) {
            getProgressDialog().show();
             serviceTask.setOnCompleteListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teamsresults, container, false);

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
            throw new ClassCastException(activity.toString() + Config.MustImplement);
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
            mListener.onTeamsResultsInteraction(getResultsViewModel().ITEMS.get(position).getYear());
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
        public void onTeamsResultsInteraction(String id);
        public void onTeamsResultsInteractionFail(String type);
    }

    @Override
    public void onTeamsResultsServiceComplete(List<TeamsResultsViewModel.TeamsResult> result) {
        TeamsResultsAsyncTask task;
        task = (TeamsResultsAsyncTask) getApp().getTask(Config.TR_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TR_SvcTaskKey);

        getResultsViewModel().updateList(result);
        mAdapter.notifyDataSetChanged();
        getResultsViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onTeamsResultsServiceFailure() {
        TeamsResultsAsyncTask task;
        task = (TeamsResultsAsyncTask) getApp().getTask(Config.TR_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TR_SvcTaskKey);

        getResultsViewModel().setIsBusy(false);
        dismissProgressDialog();

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that a failure has happened.
            mListener.onTeamsResultsInteractionFail("");
        }
    }

    @Override
    public void onTeamsResultsCacheComplete(List<TeamsResultsViewModel.TeamsResult> result) {
        TeamsResultsCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (TeamsResultsCacheAsyncTask) getApp().getTask(Config.TR_CacheFileKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TR_CacheFileKey);

        getResultsViewModel().updateList(result);
        mAdapter.notifyDataSetChanged();
        getResultsViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onTeamsResultsCacheFailure() {
        TeamsResultsCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (TeamsResultsCacheAsyncTask) getApp().getTask(Config.TR_CacheFileKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TR_CacheFileKey);

        getResultsViewModel().setIsBusy(false);
        dismissProgressDialog();

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that a failure has happened.
            mListener.onTeamsResultsInteractionFail("");
        }
    }

    public void onShowedFragment(){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(getContextViewModel().shouldExecuteTeamResultsSearch(netAvailable) &&
                !getResultsViewModel().getIsBusy()) {
            if(netAvailable) {
                TeamsResultsAsyncTask task = new TeamsResultsAsyncTask();

                getApp().registerTask(Config.TR_SvcTaskKey, task);
                task.setOnCompleteListener(this);
                getResultsViewModel().setIsBusy(true);
                getProgressDialog().show();
                task.execute();

            } else {
                if (null != mListener) {
                    mListener.onTeamsResultsInteractionFail(Config.NoInternet);
                }
            }
        } else {
            //likely new session and tabbed to results tab
            setEmptyText(getString(R.string.select_opponent_first));
            mAdapter.notifyDataSetChanged();
        }
    }

    private ClutchWinApplication getApp(){
        return ClutchWinApplication.getInstance();
    }

    private TeamsContextViewModel getContextViewModel(){
        return ClutchWinApplication.getTeamsContextViewModel();
    }

    private TeamsResultsViewModel getResultsViewModel(){
        return ClutchWinApplication.getTeamsResultsViewModel();
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
