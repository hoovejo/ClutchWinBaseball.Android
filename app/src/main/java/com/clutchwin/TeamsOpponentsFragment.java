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

import com.clutchwin.adapters.TeamsFranchisesAdapter;
import com.clutchwin.cachetasks.TeamsOpponentsCacheAsyncTask;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.TeamsOpponentsAsyncTask;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsOpponentsViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TeamsOpponentsFragment extends Fragment implements AbsListView.OnItemClickListener, IOnShowFragment {

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
    private TeamsFranchisesAdapter mAdapter;

    /**
     * The view models for this fragment
     */
    private TeamsContextViewModel teamsContextViewModel;
    private TeamsFranchisesViewModel teamsFranchisesViewModel;
    private TeamsOpponentsViewModel teamsOpponentsViewModel;

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
        ClutchWinApplication app = (ClutchWinApplication)activity.getApplicationContext();
        teamsContextViewModel = app.getTeamsContextViewModel();
        teamsFranchisesViewModel = app.getTeamsFranchisesViewModel();
        teamsOpponentsViewModel = app.getTeamsOpponentsViewModel();

        if(teamsOpponentsViewModel.ITEMS.isEmpty() && !teamsOpponentsViewModel.getIsBusy()) {

            if(Helpers.checkFileExists(activity, teamsFranchisesViewModel.CacheFileKey)) {
                onCacheComplete = new CacheCompleteImpl();
                TeamsOpponentsCacheAsyncTask cacheAsyncTask = new TeamsOpponentsCacheAsyncTask(activity, teamsContextViewModel,
                        teamsOpponentsViewModel, teamsFranchisesViewModel);
                cacheAsyncTask.setOnCompleteListener(onCacheComplete);
                cacheAsyncTask.execute();
            }
        }

        mAdapter = new TeamsFranchisesAdapter(activity,
                R.layout.listview_teamsfranchises_row, teamsOpponentsViewModel.ITEMS);
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
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onTeamsOpponentsInteraction(teamsOpponentsViewModel.ITEMS.get(position).getRetroId());
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
        public void onTeamsOpponentsInteraction(String id);
        public void onTeamsOpponentsInteractionFail(String type);
    }

    public void onShowedFragment(){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(teamsContextViewModel.shouldFilterOpponents(netAvailable) && !teamsOpponentsViewModel.getIsBusy()) {

            if(netAvailable) {
                onServiceComplete = new ServiceCompleteImpl();
                TeamsOpponentsAsyncTask task = new TeamsOpponentsAsyncTask(getActivity(),
                        teamsContextViewModel, teamsOpponentsViewModel, teamsFranchisesViewModel);
                task.setOnCompleteListener(onServiceComplete);
                task.execute();
            } else {
                if (null != mListener) {
                    mListener.onTeamsOpponentsInteractionFail(TeamsFeatureActivity.NoInternet);
                }
            }
        }
    }

    private class ServiceCompleteImpl implements TeamsOpponentsAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){ mAdapter.notifyDataSetChanged(); }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onTeamsOpponentsInteractionFail("");
            }
        }
    }

    private class CacheCompleteImpl implements TeamsOpponentsCacheAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){ mAdapter.notifyDataSetChanged(); }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onTeamsOpponentsInteractionFail("");
            }
        }
    }
}
