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

import com.clutchwin.adapters.PlayersDrillDownAdapter;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.PlayersDrillDownAsyncTask;
import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;
import com.clutchwin.viewmodels.PlayersResultsViewModel;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;
import com.clutchwin.viewmodels.PlayersYearsViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlayersDrillDownFragment extends Fragment implements AbsListView.OnItemClickListener, IOnShowFragment {

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
    private PlayersDrillDownAdapter mAdapter;

    /**
     * The view models for this fragment
     */
    private PlayersBattersViewModel battersViewModel;
    private PlayersPitchersViewModel pitchersViewModel;
    private PlayersResultsViewModel resultsViewModel;
    private PlayersDrillDownViewModel drillDownViewModel;

    // TODO: Rename and change types of parameters
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

        battersViewModel = PlayersBattersViewModel.Instance();
        pitchersViewModel = PlayersPitchersViewModel.Instance();
        resultsViewModel = PlayersResultsViewModel.Instance();
        drillDownViewModel = PlayersDrillDownViewModel.Instance();

        mAdapter = new PlayersDrillDownAdapter(getActivity(),
                R.layout.listview_playersdrilldown_row, drillDownViewModel.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playersdrilldown, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

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
        public void onPlayersDrillDownInteraction(String id);
        public void onPlayersDrillDownInteractionFail();
    }

    public void onShowedFragment(){
        //TODO: only load if necessary
        onServiceComplete = new ServiceCompleteImpl();
        PlayersDrillDownAsyncTask task = new PlayersDrillDownAsyncTask(getActivity(), drillDownViewModel,
                battersViewModel.getBatterId(), pitchersViewModel.getPitcherId(),
                resultsViewModel.getYearId(), resultsViewModel.getGameType());
        task.setOnCompleteListener(onServiceComplete);
        task.execute();
    }

    private class ServiceCompleteImpl implements PlayersDrillDownAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersDrillDownInteractionFail();
            }
        }
    }
}
