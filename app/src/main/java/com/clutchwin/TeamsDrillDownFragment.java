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

import com.clutchwin.adapters.TeamsDrillDownAdapter;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.TeamsDrillDownAsyncTask;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TeamsDrillDownFragment extends Fragment implements AbsListView.OnItemClickListener, IOnShowFragment {

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
    private TeamsDrillDownAdapter mAdapter;

    /**
     * The view models for this fragment
     */
    private TeamsContextViewModel teamsContextViewModel;
    private TeamsDrillDownViewModel drillDownViewModel;

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

        teamsContextViewModel = TeamsContextViewModel.Instance();
        drillDownViewModel = teamsContextViewModel.getTeamsDrillDownViewModel();

        mAdapter = new TeamsDrillDownAdapter(getActivity(),
                R.layout.listview_teamsdrilldown_row, drillDownViewModel.ITEMS);
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
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onTeamsDrillDownInteraction("");
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
        public void onTeamsDrillDownInteraction(String id);
        public void onTeamsDrillDownInteractionFail();
    }

    public void onShowedFragment(){

        if(teamsContextViewModel.shouldExecuteTeamDrillDownSearch()) {
            onServiceComplete = new ServiceCompleteImpl();
            TeamsDrillDownAsyncTask task = new TeamsDrillDownAsyncTask(getActivity(), drillDownViewModel,
                    teamsContextViewModel.getFranchiseId(), teamsContextViewModel.getOpponentId(), teamsContextViewModel.getYearId());
            task.setOnCompleteListener(onServiceComplete);
            task.execute();
        }
    }

    private class ServiceCompleteImpl implements TeamsDrillDownAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onTeamsDrillDownInteractionFail();
            }
        }
    }
}
