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
import android.widget.ListAdapter;
import android.widget.TextView;

import com.clutchwin.cachetasks.PlayersYearsCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.service.PlayersYearsAsyncTask;
import com.clutchwin.viewmodels.PlayersYearsViewModel;

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
public class PlayersYearsFragment extends Fragment implements AbsListView.OnItemClickListener,
         PlayersYearsAsyncTask.OnLoadCompleteListener,
         PlayersYearsCacheAsyncTask.OnLoadCompleteListener {

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

         if (!getApp().getHasLoadedSeasonsOnce()) {
             initiateServiceCall();
         }

         PlayersYearsCacheAsyncTask cacheTask;
         cacheTask = (PlayersYearsCacheAsyncTask) getApp().getTask(Config.PY_CacheFileKey);

         PlayersYearsAsyncTask serviceTask;
         serviceTask = (PlayersYearsAsyncTask) getApp().getTask(Config.PY_SvcTaskKey);

         if(getYearsViewModel().ITEMS.isEmpty() && !getYearsViewModel().getIsBusy()) {

             if(Helpers.checkFileExists(activity, Config.PY_CacheFileKey)) {
                 PlayersYearsCacheAsyncTask cacheAsyncTask = new PlayersYearsCacheAsyncTask();

                 getApp().registerTask(Config.PY_CacheFileKey, cacheAsyncTask);
                 cacheAsyncTask.setOnCompleteListener(this);
                 getYearsViewModel().setIsBusy(true);
                 getProgressDialog().show();
                 cacheAsyncTask.execute();

             } else {
                 if(serviceTask == null) {
                     initiateServiceCall();
                 }
             }
         }

         mAdapter = new ArrayAdapter<PlayersYearsViewModel.Year>(activity,
             android.R.layout.simple_list_item_1, android.R.id.text1, getYearsViewModel().ITEMS);

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
             getYearsViewModel().setIsBusy(false);
         }
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
         // kill any progress dialogs if we are being destroyed
         dismissProgressDialog();
     }

     @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         if (null != mListener) {
             // Notify the active callbacks interface (the activity, if the
             // fragment is attached to one) that an item has been selected.
             mListener.onPlayersYearsInteraction(getYearsViewModel().ITEMS.get(position).getId());
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

     @Override
     public void onPlayersYearsServiceComplete(List<PlayersYearsViewModel.Year> result) {
         PlayersYearsAsyncTask task;
         task = (PlayersYearsAsyncTask) getApp().getTask(Config.PY_SvcTaskKey);
         if (task != null) {
             task.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.PY_SvcTaskKey);

         getYearsViewModel().updateList(result);
         ((ArrayAdapter<?>) mAdapter).notifyDataSetChanged();
         getYearsViewModel().setIsBusy(false);
         dismissProgressDialog();

         getApp().setHasLoadedSeasonsOnce(true);
     }

     @Override
     public void onPlayersYearsServiceFailure() {
         PlayersYearsAsyncTask task;
         task = (PlayersYearsAsyncTask) getApp().getTask(Config.PY_SvcTaskKey);
         if (task != null) {
             task.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.PY_SvcTaskKey);

         getYearsViewModel().setIsBusy(false);
         dismissProgressDialog();

         if (null != mListener) {
             // Notify the active callbacks interface (the activity, if the
             // fragment is attached to one) that a failure has happened.
             mListener.onPlayersYearsInteractionFail("");
         }
     }

     @Override
     public void onPlayersYearsCacheComplete(List<PlayersYearsViewModel.Year> result) {
         PlayersYearsCacheAsyncTask cacheAsyncTask;
         cacheAsyncTask = (PlayersYearsCacheAsyncTask) getApp().getTask(Config.PY_CacheFileKey);
         if (cacheAsyncTask != null) {
             cacheAsyncTask.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.PY_CacheFileKey);

         getYearsViewModel().updateList(result);
         ((ArrayAdapter<?>) mAdapter).notifyDataSetChanged();
         getYearsViewModel().setIsBusy(false);
         dismissProgressDialog();
     }

     @Override
     public void onPlayersYearsCacheFailure() {
         PlayersYearsCacheAsyncTask cacheAsyncTask;
         cacheAsyncTask = (PlayersYearsCacheAsyncTask) getApp().getTask(Config.PY_CacheFileKey);
         if (cacheAsyncTask != null) {
             cacheAsyncTask.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.PY_CacheFileKey);
         getYearsViewModel().setIsBusy(false);
         dismissProgressDialog();
         //if any failure occurs loading cache, just call the service for fresh franchises
         initiateServiceCall();
     }

     private void initiateServiceCall(){

         boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

         if(netAvailable) {
             PlayersYearsAsyncTask task = new PlayersYearsAsyncTask();

             getApp().registerTask(Config.PY_SvcTaskKey, task);
             task.setOnCompleteListener(this);
             getYearsViewModel().setIsBusy(true);
             getProgressDialog().show();
             task.execute();

         } else {
             if (null != mListener) {
                 // Notify the active callbacks interface (the activity, if the
                 // fragment is attached to one) that a failure has happened.
                 mListener.onPlayersYearsInteractionFail(Config.NoInternet);
             }
         }
     }

     private ClutchWinApplication getApp(){
         return ClutchWinApplication.getInstance();
     }

     private PlayersYearsViewModel getYearsViewModel(){
         return ClutchWinApplication.getPlayersYearsViewModel();
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
