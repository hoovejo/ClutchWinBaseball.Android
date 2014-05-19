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

import com.clutchwin.adapters.PlayersDrillDownAdapter;
import com.clutchwin.cachetasks.PlayersDrillDownCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.PlayersDrillDownAsyncTask;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;

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
public class PlayersDrillDownFragment extends Fragment implements AbsListView.OnItemClickListener,
         PlayersDrillDownAsyncTask.OnLoadCompleteListener,
         PlayersDrillDownCacheAsyncTask.OnLoadCompleteListener,
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
    private PlayersDrillDownAdapter mAdapter;
    /**
     * Progress dialog for the cache and service async background tasks.
     */
    private ProgressDialog progressDialog;

    public static PlayersDrillDownFragment newInstance() {
        PlayersDrillDownFragment fragment = new PlayersDrillDownFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayersDrillDownFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context activity = getActivity();

        PlayersDrillDownCacheAsyncTask cacheTask;
        cacheTask = (PlayersDrillDownCacheAsyncTask) getApp().getTask(Config.PDD_CacheFileKey);

        PlayersDrillDownAsyncTask serviceTask;
        serviceTask = (PlayersDrillDownAsyncTask) getApp().getTask(Config.PDD_SvcTaskKey);

        if (getDrillDownViewModel().ITEMS.isEmpty() && !getDrillDownViewModel().getIsBusy()) {

            if (Helpers.checkFileExists(activity, Config.PDD_CacheFileKey)) {
                PlayersDrillDownCacheAsyncTask cacheAsyncTask = new PlayersDrillDownCacheAsyncTask();

                getApp().registerTask(Config.PDD_CacheFileKey, cacheAsyncTask);
                cacheAsyncTask.setOnCompleteListener(this);
                getDrillDownViewModel().setIsBusy(true);
                getProgressDialog().show();
                cacheAsyncTask.execute();
            }
        }

        mAdapter = new PlayersDrillDownAdapter(activity,
                R.layout.listview_playersdrilldown_row, getDrillDownViewModel().ITEMS);

        // Hook back up to running tasks if this fragment was recreated in the middle of a running task
        if (cacheTask != null) {
            getProgressDialog().show();
            cacheTask.setOnCompleteListener(this);
        }
        if (serviceTask != null) {
            getProgressDialog().show();
            serviceTask.setOnCompleteListener(this);
        }
        if (cacheTask == null && serviceTask == null) {
            getDrillDownViewModel().setIsBusy(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playersdrilldown, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

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
            mListener.onPlayersDrillDownInteraction("");
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
        public void onPlayersDrillDownInteraction(String id);

        public void onPlayersDrillDownInteractionFail(String type);
    }

    @Override
    public void onPlayerDrillDownServiceComplete(List<PlayersDrillDownViewModel.PlayersDrillDown> result) {
        PlayersDrillDownAsyncTask task;
        task = (PlayersDrillDownAsyncTask) getApp().getTask(Config.PDD_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PDD_SvcTaskKey);

        getDrillDownViewModel().updateList(result);
        mAdapter.notifyDataSetChanged();
        getDrillDownViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onPlayerDrillDownServiceFailure() {
        PlayersDrillDownAsyncTask task;
        task = (PlayersDrillDownAsyncTask) getApp().getTask(Config.PDD_SvcTaskKey);
        if (task != null) {
            task.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PDD_SvcTaskKey);

        getDrillDownViewModel().setIsBusy(false);
        dismissProgressDialog();

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that a failure has happened.
            mListener.onPlayersDrillDownInteractionFail("");
        }
    }

    @Override
    public void onPlayersDrillDownCacheComplete(List<PlayersDrillDownViewModel.PlayersDrillDown> result) {
        PlayersDrillDownCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (PlayersDrillDownCacheAsyncTask) getApp().getTask(Config.PDD_CacheFileKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PDD_CacheFileKey);

        getDrillDownViewModel().updateList(result);
        mAdapter.notifyDataSetChanged();
        getDrillDownViewModel().setIsBusy(false);
        dismissProgressDialog();
    }

    @Override
    public void onPlayersDrillDownCacheFailure() {
        PlayersDrillDownCacheAsyncTask cacheAsyncTask;
        cacheAsyncTask = (PlayersDrillDownCacheAsyncTask) getApp().getTask(Config.PDD_CacheFileKey);
        if (cacheAsyncTask != null) {
            cacheAsyncTask.setOnCompleteListener(null);
        }
        getApp().unregisterTask(Config.PDD_CacheFileKey);
        getDrillDownViewModel().setIsBusy(false);
        dismissProgressDialog();

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that a failure has happened.
            mListener.onPlayersDrillDownInteractionFail("");
        }
    }

    public void onShowedFragment() {

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if (getContextViewModel().shouldExecutePlayersDrillDownSearch(netAvailable) && !getDrillDownViewModel().getIsBusy()) {
            if (netAvailable) {
                setEmptyText(getString(R.string.no_search_results));
                PlayersDrillDownAsyncTask task = new PlayersDrillDownAsyncTask();

                getApp().registerTask(Config.PDD_SvcTaskKey, task);
                task.setOnCompleteListener(this);
                getDrillDownViewModel().setIsBusy(true);
                getProgressDialog().show();
                task.execute();

            } else {
                if (null != mListener) {
                    mListener.onPlayersDrillDownInteractionFail(Config.NoInternet);
                }
            }
        } else {
            //likely new session and tabbed to results tab
            setEmptyText(getString(R.string.p_select_results_first));
            mAdapter.notifyDataSetChanged();
        }
    }

    private ClutchWinApplication getApp() {
        return ClutchWinApplication.getInstance();
    }

    private PlayersContextViewModel getContextViewModel() {
        return ClutchWinApplication.getPlayersContextViewModel();
    }

    private PlayersDrillDownViewModel getDrillDownViewModel() {
        return ClutchWinApplication.getPlayersDrillDownViewModel();
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
