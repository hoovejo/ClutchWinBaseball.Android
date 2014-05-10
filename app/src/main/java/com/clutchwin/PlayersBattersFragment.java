package com.clutchwin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.clutchwin.service.PlayersBattersAsyncTask;
import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.clutchwin.viewmodels.PlayersContextViewModel;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlayersBattersFragment extends Fragment implements AbsListView.OnItemClickListener {

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
    private ListAdapter mAdapter;

    /**
     * The view models for this fragment
     */
    private PlayersContextViewModel playersContextViewModel;
    private PlayersBattersViewModel playersBattersViewModel;

    private static PlayersBattersFragment _instance;
    public static PlayersBattersFragment Instance() {
        if(_instance == null){
            _instance = new PlayersBattersFragment();
        }
        return _instance;
    }

    public static PlayersBattersFragment newInstance() {
        _instance = new PlayersBattersFragment();
        return _instance;
    }

    /**
     * Dynamic buttons
     */
    private Button yearButton;
    private Button teamButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayersBattersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playersContextViewModel = PlayersContextViewModel.Instance();
        playersBattersViewModel = playersContextViewModel.getPlayersBattersViewModel();

        if(playersContextViewModel.shouldExecuteLoadBatters()) {
            onServiceComplete = new ServiceCompleteImpl();
            PlayersBattersAsyncTask task = new PlayersBattersAsyncTask(getActivity(), playersBattersViewModel,
                    playersContextViewModel.getTeamId(), playersContextViewModel.getYearId());
            task.setOnCompleteListener(onServiceComplete);
            task.execute();
        }

        mAdapter = new ArrayAdapter<PlayersBattersViewModel.Batter>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, playersBattersViewModel.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playersbatters, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        yearButton = (Button) view.findViewById(R.id.btnYears);
        String year = playersContextViewModel.getYearId();
        if(year != null && year.length() >= 1){
            yearButton.setText(year + " >");
        }
        yearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onGoToYearsInteraction();
            }
        });

        teamButton = (Button) view.findViewById(R.id.btnTeams);
        String team = playersContextViewModel.getTeamId();
        if(team != null && team.length() >= 1){
            teamButton.setText(team + " >");
        }

        String yearId = playersContextViewModel.getYearId();
        if(yearId != null && yearId.length() > 0) {
            teamButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mListener.onGoToTeamsInteraction();
                }
            });
        }

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
            mListener.onPlayersBattersInteraction(playersBattersViewModel.ITEMS.get(position).getRetroPlayerId());
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
        public void onPlayersBattersInteraction(String id);
        public void onPlayersBattersInteractionFail();
        public void onGoToYearsInteraction();
        public void onGoToTeamsInteraction();
    }

    private class ServiceCompleteImpl implements PlayersBattersAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            ((ArrayAdapter) mAdapter).notifyDataSetChanged();
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersBattersInteractionFail();
            }
        }
    }
}
