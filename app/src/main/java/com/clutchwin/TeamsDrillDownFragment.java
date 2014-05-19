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

import com.clutchwin.adapters.TeamsDrillDownAdapter;
import com.clutchwin.cachetasks.TeamsDrillDownCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.TeamsDrillDownAsyncTask;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;

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
public class TeamsDrillDownFragment extends Fragment implements AbsListView.OnItemClickListener,
         TeamsDrillDownAsyncTask.OnLoadCompleteListener,
         TeamsDrillDownCacheAsyncTask.OnLoadCompleteListener,
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
     private TeamsDrillDownAdapter mAdapter;
     /**
      * Progress dialog for the cache and service async background tasks.
      */
     private ProgressDialog progressDialog;

     public static TeamsDrillDownFragment newInstance() {
         TeamsDrillDownFragment fragment = new TeamsDrillDownFragment();
         return fragment;
     }

     /**
      * Mandatory empty constructor for the fragment manager to instantiate the
      * fragment (e.g. upon screen orientation changes).
      */
     public TeamsDrillDownFragment() {
     }

     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

         Context activity = getActivity();

         TeamsDrillDownCacheAsyncTask cacheTask;
         cacheTask = (TeamsDrillDownCacheAsyncTask) getApp().getTask(Config.TDD_CacheFileKey);

         TeamsDrillDownAsyncTask serviceTask;
         serviceTask = (TeamsDrillDownAsyncTask) getApp().getTask(Config.TDD_SvcTaskKey);

         // if we are constructing and have no active tasks in the background, ensure no other orphan
         // tasks left the viewModel as busy on an orientation change
         if(cacheTask == null && serviceTask == null){
             getDrillDownViewModel().setIsBusy(false);
         }

         if(getDrillDownViewModel().ITEMS.isEmpty() && getDrillDownViewModel().getIsBusy()) {

             if(Helpers.checkFileExists(activity, Config.TDD_CacheFileKey)) {
                 TeamsDrillDownCacheAsyncTask cacheAsyncTask = new TeamsDrillDownCacheAsyncTask();

                 getApp().registerTask(Config.TDD_CacheFileKey, cacheAsyncTask);
                 cacheAsyncTask.setOnCompleteListener(this);
                 getDrillDownViewModel().setIsBusy(true);
                 getProgressDialog().show();
                 cacheAsyncTask.execute();
             }
         }

         mAdapter = new TeamsDrillDownAdapter(activity,
                 R.layout.listview_teamsdrilldown_row,
                 getDrillDownViewModel().ITEMS);

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
         View view = inflater.inflate(R.layout.fragment_teamsdrilldown, container, false);

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
         public void onTeamsDrillDownInteraction(String id);
         public void onTeamsDrillDownInteractionFail(String type);
     }

     @Override
     public void onTeamsDrillDownServiceComplete(List<TeamsDrillDownViewModel.TeamsDrillDown> result) {
         TeamsDrillDownAsyncTask task;
         task = (TeamsDrillDownAsyncTask) getApp().getTask(Config.TDD_SvcTaskKey);
         if (task != null) {
             task.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.TDD_SvcTaskKey);

         getDrillDownViewModel().updateList(result);
         mAdapter.notifyDataSetChanged();
         getDrillDownViewModel().setIsBusy(false);
         dismissProgressDialog();
     }

     @Override
     public void onTeamsDrillDownServiceFailure() {
         TeamsDrillDownAsyncTask task;
         task = (TeamsDrillDownAsyncTask) getApp().getTask(Config.TDD_SvcTaskKey);
         if (task != null) {
             task.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.TDD_SvcTaskKey);

         getDrillDownViewModel().setIsBusy(false);
         dismissProgressDialog();

         if (null != mListener) {
             // Notify the active callbacks interface (the activity, if the
             // fragment is attached to one) that a failure has happened.
             mListener.onTeamsDrillDownInteractionFail("");
         }
     }

     @Override
     public void onTeamsDrillDownCacheComplete(List<TeamsDrillDownViewModel.TeamsDrillDown> result) {
         TeamsDrillDownCacheAsyncTask cacheAsyncTask;
         cacheAsyncTask = (TeamsDrillDownCacheAsyncTask) getApp().getTask(Config.TDD_CacheFileKey);
         if (cacheAsyncTask != null) {
             cacheAsyncTask.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.TDD_CacheFileKey);

         getDrillDownViewModel().updateList(result);
         mAdapter.notifyDataSetChanged();
         getDrillDownViewModel().setIsBusy(false);
         dismissProgressDialog();
     }

     @Override
     public void onTeamsDrillDownCacheFailure() {
         TeamsDrillDownCacheAsyncTask cacheAsyncTask;
         cacheAsyncTask = (TeamsDrillDownCacheAsyncTask) getApp().getTask(Config.TDD_CacheFileKey);
         if (cacheAsyncTask != null) {
             cacheAsyncTask.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.TDD_CacheFileKey);

         getDrillDownViewModel().setIsBusy(false);
         dismissProgressDialog();

         if (null != mListener) {
             // Notify the active callbacks interface (the activity, if the
             // fragment is attached to one) that a failure has happened.
             mListener.onTeamsDrillDownInteractionFail("");
         }
     }

     public void onShowedFragment(){

         boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

         if(getContextViewModel().shouldExecuteTeamDrillDownSearch(netAvailable) &&
                 !getDrillDownViewModel().getIsBusy()) {
             if(netAvailable) {
                 TeamsDrillDownAsyncTask task = new TeamsDrillDownAsyncTask();

                 getApp().registerTask(Config.TDD_SvcTaskKey, task);
                 task.setOnCompleteListener(this);
                 getDrillDownViewModel().setIsBusy(true);
                 getProgressDialog().show();
                 task.execute();

             } else {
                 if (null != mListener) {
                     mListener.onTeamsDrillDownInteractionFail(Config.NoInternet);
                 }
             }
         } else {
             //likely new session and tabbed to drillDown tab
             setEmptyText(getString(R.string.t_select_result_first));
             mAdapter.notifyDataSetChanged();
         }
     }

     private ClutchWinApplication getApp(){
         return ClutchWinApplication.getInstance();
     }

     private TeamsContextViewModel getContextViewModel(){
         return ClutchWinApplication.getTeamsContextViewModel();
     }

     private TeamsDrillDownViewModel getDrillDownViewModel(){
         return ClutchWinApplication.getTeamsDrillDownViewModel();
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
