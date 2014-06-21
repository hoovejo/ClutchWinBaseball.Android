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
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.clutchwin.adapters.PlayersResultsAdapter;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.PlayersResultsAsyncTask;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersResultsViewModel;

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
public class PlayersResultsFragment extends Fragment implements AbsListView.OnItemClickListener,
         PlayersResultsAsyncTask.OnLoadCompleteListener,
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
     private PlayersResultsAdapter mAdapter;
     /**
      * Progress dialog for the cache and service async background tasks.
      */
     private ProgressDialog progressDialog;

     public static PlayersResultsFragment newInstance() {
         return new PlayersResultsFragment();
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

         mAdapter = new PlayersResultsAdapter(activity,
                 R.layout.listview_playersresults_row, getResultsViewModel().ITEMS);

         PlayersResultsAsyncTask serviceTask;
         serviceTask = (PlayersResultsAsyncTask)getApp().getTask(Config.PR_SvcTaskKey);

         // Hook back up to running tasks if this fragment was recreated in the middle of a running task
         if (serviceTask != null) {
             getProgressDialog().show();
             serviceTask.setOnCompleteListener(this);
         } else {

             // if we are constructing and have no active tasks in the background, ensure no other orphan
             // tasks left the viewModel as busy on an orientation change
             getResultsViewModel().setIsBusy(false);

             if(getResultsViewModel().ITEMS.isEmpty() ) {
                 initiateServiceCall();
             }
         }
     }

    private void initiateServiceCall(){
        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(getContextViewModel().playersResultsServiceCallAllowed()) {
            if(netAvailable) {
                setEmptyText(getString(R.string.no_search_results));
                PlayersResultsAsyncTask task = new PlayersResultsAsyncTask();

                getApp().registerTask(Config.PR_SvcTaskKey, task);
                task.setOnCompleteListener(this);
                getResultsViewModel().setIsBusy(true);
                getProgressDialog().show();
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

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_playersresults, container, false);

         // Set the adapter
         mListView = (AbsListView) view.findViewById(android.R.id.list);
         mListView.setAdapter(mAdapter);

         // Set OnItemClickListener so we can be notified on item clicks
         mListView.setOnItemClickListener(this);

         TextView emptyText = (TextView) view.findViewById(android.R.id.empty);

         if(getResultsViewModel().ITEMS.isEmpty() ) {
             emptyText.setText(getString(R.string.select_pitcher_first));
             mListView.setEmptyView(emptyText);
             emptyText.setVisibility(TextView.VISIBLE);
         } else {
             emptyText.setText(getString(R.string.no_search_results));
             mListView.setEmptyView(emptyText);
             emptyText.setVisibility(TextView.INVISIBLE);
         }

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

         PlayersResultsAsyncTask task;
         task = (PlayersResultsAsyncTask)getApp().getTask(Config.PR_SvcTaskKey);
         if(task != null){
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
             PlayersResultsViewModel.PlayersResult row = getResultsViewModel().ITEMS.get(position);
             mListener.onPlayersResultsInteraction(row.getYear());
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
                 if (emptyView != null) {
                     ((TextView) emptyView).setText(emptyText);
                 }
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
         public void onPlayersResultsInteraction(String id);
         public void onPlayersResultsInteractionFail(String type);
     }

     @Override
     public void onPlayersResultsServiceComplete(List<PlayersResultsViewModel.PlayersResult> result){
         PlayersResultsAsyncTask task;
         task = (PlayersResultsAsyncTask)getApp().getTask(Config.PR_SvcTaskKey);
         if(task != null){
             task.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.PR_SvcTaskKey);

         getResultsViewModel().updateList(result);
         mAdapter.notifyDataSetChanged();
         getResultsViewModel().setIsBusy(false);
         dismissProgressDialog();
     }

     @Override
     public void onPlayersResultsServiceFailure(Exception e){
         PlayersResultsAsyncTask task;
         task = (PlayersResultsAsyncTask)getApp().getTask(Config.PR_SvcTaskKey);
         if(task != null){
             task.setOnCompleteListener(null);
         }
         getApp().unregisterTask(Config.PR_SvcTaskKey);

         getResultsViewModel().setIsBusy(false);
         dismissProgressDialog();

         if (null != mListener) {
             // Notify the active callbacks interface (the activity, if the
             // fragment is attached to one) that a failure has happened.
             mListener.onPlayersResultsInteractionFail("");
         }
         BugSenseHandler.sendException(e);
     }

     public void onShowedFragment(){

         boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

         if(!getResultsViewModel().getIsBusy() && getContextViewModel().shouldExecutePlayerResultsSearch(netAvailable)) {
             if(netAvailable) {
                 setEmptyText(getString(R.string.no_search_results));
                 PlayersResultsAsyncTask task = new PlayersResultsAsyncTask();

                 getApp().registerTask(Config.PR_SvcTaskKey, task);
                 task.setOnCompleteListener(this);
                 getResultsViewModel().setIsBusy(true);
                 getProgressDialog().show();
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

    private ClutchWinApplication getApp(){
        return ClutchWinApplication.getInstance();
    }

    private PlayersContextViewModel getContextViewModel(){
        return ClutchWinApplication.getPlayersContextViewModel();
    }

    private PlayersResultsViewModel getResultsViewModel(){
        return ClutchWinApplication.getPlayersResultsViewModel();
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
        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
