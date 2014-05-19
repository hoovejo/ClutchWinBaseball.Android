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

import com.clutchwin.adapters.TeamsFranchisesAdapter;
import com.clutchwin.cachetasks.TeamsOpponentsCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsOpponentsViewModel;

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
public class TeamsOpponentsFragment extends Fragment implements AbsListView.OnItemClickListener,
        TeamsOpponentsCacheAsyncTask.OnLoadCompleteListener,
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
    private TeamsFranchisesAdapter mAdapter;
    /**
     * Progress dialog for the cache and service async background tasks.
     */
    private ProgressDialog progressDialog;

    public static TeamsOpponentsFragment newInstance() {
        TeamsOpponentsFragment fragment = new TeamsOpponentsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TeamsOpponentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();

        TeamsOpponentsCacheAsyncTask cacheTask;
        cacheTask = (TeamsOpponentsCacheAsyncTask) getApp().getTask(Config.TO_CacheTaskKey);

        // if we are constructing and have no active tasks in the background, ensure no other orphan
        // tasks left the viewModel as busy on an orientation change
        if(cacheTask == null){
            getOpponentsViewModel().setIsBusy(false);
        }

        if(getOpponentsViewModel().ITEMS.isEmpty() &&
                !getOpponentsViewModel().getIsBusy()
                && getContextViewModel().getFranchiseId() != null) {

            if(Helpers.checkFileExists(activity, Config.TF_CacheFileKey)) {
                TeamsOpponentsCacheAsyncTask cacheAsyncTask = new TeamsOpponentsCacheAsyncTask();

                getApp().registerTask(Config.TO_CacheTaskKey, cacheAsyncTask);
                cacheAsyncTask.setOnCompleteListener(this);
                getOpponentsViewModel().setIsBusy(true);
                getProgressDialog().show();
                cacheAsyncTask.execute();
            }
        }

        mAdapter = new TeamsFranchisesAdapter(activity,
                R.layout.listview_teamsfranchises_row,
                getOpponentsViewModel().ITEMS);

        // Hook back up to running tasks if this fragment was recreated in the middle of a running task
        if (cacheTask != null) {
            getProgressDialog().show();
             cacheTask.setOnCompleteListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teamsopponents, container, false);

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
            mListener.onTeamsOpponentsInteraction(getOpponentsViewModel().ITEMS.get(position).getRetroId());
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
        public void onTeamsOpponentsInteraction(String id);
        public void onTeamsOpponentsInteractionFail(String type);
    }

    @Override
    public void onTeamsOpponentsCacheComplete(List<TeamsFranchisesViewModel.Franchise> result) {
        TeamsOpponentsCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (TeamsOpponentsCacheAsyncTask) getApp().getTask(Config.TO_CacheTaskKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TO_CacheTaskKey);

        getOpponentsViewModel().filterList(
                result,
                getContextViewModel().getFranchiseId());

        mAdapter.notifyDataSetChanged();
        getOpponentsViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onTeamsOpponentsCacheFailure() {
        TeamsOpponentsCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (TeamsOpponentsCacheAsyncTask) getApp().getTask(Config.TO_CacheTaskKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TO_CacheTaskKey);
        dismissProgressDialog();
        if (null != mListener) {
             // Notify the active callbacks interface (the activity, if the
             // fragment is attached to one) that a failure has happened.
             mListener.onTeamsOpponentsInteractionFail("");
         }
    }

    public void onShowedFragment(){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(getContextViewModel().shouldFilterOpponents(netAvailable) &&
                !getOpponentsViewModel().getIsBusy()) {

            if(netAvailable) {

                getOpponentsViewModel().setIsBusy(true);
                getOpponentsViewModel().filterList(getFranchisesViewModel().ITEMS,
                        getContextViewModel().getFranchiseId());

                mAdapter.notifyDataSetChanged();

                getOpponentsViewModel().setIsBusy(false);

            } else {
                if (null != mListener) {
                    mListener.onTeamsOpponentsInteractionFail(Config.NoInternet);
                }
            }
        } else {
            //likely new session and tabbed to opponents tab
            setEmptyText(getString(R.string.select_team_first));
            mAdapter.notifyDataSetChanged();
        }
    }

    private ClutchWinApplication getApp(){
        return ClutchWinApplication.getInstance();
    }

    private TeamsContextViewModel getContextViewModel(){
        return ClutchWinApplication.getTeamsContextViewModel();
    }

    private TeamsFranchisesViewModel getFranchisesViewModel(){
        return ClutchWinApplication.getTeamsFranchisesViewModel();
    }

    private TeamsOpponentsViewModel getOpponentsViewModel(){
        return ClutchWinApplication.getTeamsOpponentsViewModel();
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
