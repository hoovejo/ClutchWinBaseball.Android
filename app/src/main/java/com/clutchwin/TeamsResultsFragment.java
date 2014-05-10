package com.clutchwin;

import android.app.Activity;
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
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.TeamsResultsAsyncTask;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsOpponentsViewModel;
import com.clutchwin.viewmodels.TeamsResultsViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TeamsResultsFragment extends Fragment implements AbsListView.OnItemClickListener, IOnShowFragment {

    private OnFragmentInteractionListener mListener;
    private ServiceCompleteImpl onServiceComplete;
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
     * The view models for this fragment
     */
    private TeamsFranchisesViewModel franchiseViewModel;
    private TeamsOpponentsViewModel opponentViewModel;
    private TeamsResultsViewModel resultsViewModel;

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

        franchiseViewModel = TeamsFranchisesViewModel.Instance();
        opponentViewModel =  TeamsOpponentsViewModel.Instance();
        resultsViewModel =  TeamsResultsViewModel.Instance();

        mAdapter = new TeamsResultsAdapter(getActivity(),
                R.layout.listview_teamsresults_row, resultsViewModel.ITEMS);
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
            mListener.onTeamsResultsInteraction(resultsViewModel.ITEMS.get(position).getYear());
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
        public void onTeamsResultsInteraction(String id);
        public void onTeamsResultsInteractionFail();
    }

    public void onShowedFragment(){
        //TODO: only load if necessary
        onServiceComplete = new ServiceCompleteImpl();
        TeamsResultsAsyncTask task = new TeamsResultsAsyncTask(getActivity(), resultsViewModel,
                franchiseViewModel.getFranchiseId(), opponentViewModel.getOpponentId());
        task.setOnCompleteListener(onServiceComplete);
        task.execute();
    }

    private class ServiceCompleteImpl implements TeamsResultsAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onTeamsResultsInteractionFail();
            }
        }
    }
}
