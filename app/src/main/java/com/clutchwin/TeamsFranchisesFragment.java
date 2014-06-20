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
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.service.TeamsFranchisesAsyncTask;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;

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
public class TeamsFranchisesFragment extends Fragment implements AbsListView.OnItemClickListener,
        TeamsFranchisesAsyncTask.OnLoadCompleteListener {

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

    public static TeamsFranchisesFragment newInstance() {
        TeamsFranchisesFragment fragment = new TeamsFranchisesFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TeamsFranchisesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();

        mAdapter = new TeamsFranchisesAdapter(activity, R.layout.listview_teamsfranchises_row,
                getFranchisesViewModel().ITEMS);

        TeamsFranchisesAsyncTask serviceTask;
        serviceTask = (TeamsFranchisesAsyncTask) getApp().getTask(Config.TF_SvcTaskKey);

        // Hook back up to running tasks if this fragment was recreated in the middle of a running task
        if (serviceTask != null) {
            getProgressDialog().show();
            serviceTask.setOnCompleteListener(this);
        } else {
            // if we are constructing and have no active tasks in the background, ensure no other orphan
            // tasks left the viewModel as busy on an orientation change
            getFranchisesViewModel().setIsBusy(false);

            if (!getApp().getHasLoadedFranchisesOnce() || getFranchisesViewModel().ITEMS.isEmpty() ) {
                initiateServiceCall();
            }
        }
    }

    private void initiateServiceCall() {

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if (netAvailable) {
            TeamsFranchisesAsyncTask task = new TeamsFranchisesAsyncTask();

            getApp().registerTask(Config.TF_SvcTaskKey, task);
            task.setOnCompleteListener(this);
            getFranchisesViewModel().setIsBusy(true);
            getProgressDialog().show();
            task.execute();

        } else {
            if (null != mListener) {
                mListener.onTeamsFranchisesInteractionFail(Config.NoInternet);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teamsfranchises, container, false);

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

        TeamsFranchisesAsyncTask task;
        task = (TeamsFranchisesAsyncTask) getApp().getTask(Config.TF_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }

        // kill any progress dialogs if we are being destroyed
        dismissProgressDialog();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onTeamsFranchisesInteraction(getFranchisesViewModel().ITEMS.get(position).getRetroId());
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
        public void onTeamsFranchisesInteraction(String id);

        public void onTeamsFranchisesInteractionFail(String type);
    }

    @Override
    public void onTeamsFranchisesServiceComplete(List<TeamsFranchisesViewModel.Franchise> result) {
        TeamsFranchisesAsyncTask task;
        task = (TeamsFranchisesAsyncTask) getApp().getTask(Config.TF_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TF_SvcTaskKey);

        getFranchisesViewModel().updateList(result);
        mAdapter.notifyDataSetChanged();
        getFranchisesViewModel().setIsBusy(false);
        dismissProgressDialog();

        getApp().setHasLoadedFranchisesOnce(true);
    }

    @Override
    public void onTeamsFranchisesServiceFailure() {
        TeamsFranchisesAsyncTask task;
        task = (TeamsFranchisesAsyncTask) getApp().getTask(Config.TF_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.TF_SvcTaskKey);

        getFranchisesViewModel().setIsBusy(false);
        dismissProgressDialog();

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that a failure has happened.
            mListener.onTeamsFranchisesInteractionFail("");
        }
    }

    private ClutchWinApplication getApp(){
        return ClutchWinApplication.getInstance();
    }

    private TeamsFranchisesViewModel getFranchisesViewModel(){
        return ClutchWinApplication.getTeamsFranchisesViewModel();
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
