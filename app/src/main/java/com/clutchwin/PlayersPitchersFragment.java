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
import android.widget.ListAdapter;
import android.widget.TextView;

import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.PlayersPitchersAsyncTask;
import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;
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
public class PlayersPitchersFragment extends Fragment implements AbsListView.OnItemClickListener, IOnShowFragment {

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
    private PlayersYearsViewModel yearsViewModel;
    private PlayersBattersViewModel battersViewModel;
    private PlayersPitchersViewModel pitchersViewModel;

    public static PlayersPitchersFragment newInstance() {
        PlayersPitchersFragment fragment = new PlayersPitchersFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayersPitchersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        yearsViewModel = PlayersYearsViewModel.Instance();
        battersViewModel = PlayersBattersViewModel.Instance();
        pitchersViewModel = PlayersPitchersViewModel.Instance();

        mAdapter = new ArrayAdapter<PlayersPitchersViewModel.Row>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, pitchersViewModel.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playerspitchers, container, false);

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
            mListener.onPlayersPitchersInteraction(pitchersViewModel.ITEMS.get(position).getRetroId());
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
        public void onPlayersPitchersInteraction(String id);
        public void onPlayersPitchersInteractionFail();
    }

    public void onShowedFragment(){
        //TODO: only load if necessary
        onServiceComplete = new ServiceCompleteImpl();
        PlayersPitchersAsyncTask task = new PlayersPitchersAsyncTask(getActivity(), pitchersViewModel,
                battersViewModel.getBatterId(), yearsViewModel.getYearId());
        task.setOnCompleteListener(onServiceComplete);
        task.execute();
    }

    private class ServiceCompleteImpl implements PlayersPitchersAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){
            ((ArrayAdapter) mAdapter).notifyDataSetChanged();
        }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersPitchersInteractionFail();
            }
        }
    }
}
