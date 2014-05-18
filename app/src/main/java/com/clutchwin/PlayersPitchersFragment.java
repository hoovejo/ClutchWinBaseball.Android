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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.clutchwin.cachetasks.PlayersPitchersCacheAsyncTask;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.service.PlayersPitchersAsyncTask;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;

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
    private CacheCompleteImpl onCacheComplete;
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
    private PlayersPitchersViewModel playersPitchersViewModel;

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

        Context activity = getActivity();
        ClutchWinApplication app = (ClutchWinApplication)activity.getApplicationContext();
        playersContextViewModel = app.getPlayersContextViewModel();
        playersPitchersViewModel = app.getPlayersPitchersViewModel();

        if(playersPitchersViewModel.ITEMS.isEmpty() && !playersPitchersViewModel.getIsBusy()) {

            if(Helpers.checkFileExists(activity, Config.PP_CacheFileKey)) {
                onCacheComplete = new CacheCompleteImpl();
                PlayersPitchersCacheAsyncTask cacheAsyncTask = new PlayersPitchersCacheAsyncTask(activity, playersPitchersViewModel);
                cacheAsyncTask.setOnCompleteListener(onCacheComplete);
                cacheAsyncTask.execute();
            }
        }

        mAdapter = new ArrayAdapter<PlayersPitchersViewModel.Row>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, playersPitchersViewModel.ITEMS);
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
        onServiceComplete = null;
        onCacheComplete = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPlayersPitchersInteraction(playersPitchersViewModel.ITEMS.get(position).getRetroId());
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
        public void onPlayersPitchersInteraction(String id);
        public void onPlayersPitchersInteractionFail(String type);
    }

    public void onShowedFragment(){

        boolean netAvailable = Helpers.isNetworkAvailable(getActivity());

        if(playersContextViewModel.shouldExecuteLoadPitchers(netAvailable) && !playersPitchersViewModel.getIsBusy()) {
            if(netAvailable) {
                setEmptyText(getString(R.string.no_search_results));
                onServiceComplete = new ServiceCompleteImpl();
                PlayersPitchersAsyncTask task = new PlayersPitchersAsyncTask(getActivity(), playersContextViewModel,
                        playersPitchersViewModel);
                task.setOnCompleteListener(onServiceComplete);
                task.execute();
            } else {
                if (null != mListener) {
                    mListener.onPlayersPitchersInteractionFail(Config.NoInternet);
                }
            }
        } else {
            //likely new session and tabbed to pitchers tab
            setEmptyText(getString(R.string.select_batter_first));
            ((ArrayAdapter) mAdapter).notifyDataSetChanged();
        }
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
                mListener.onPlayersPitchersInteractionFail("");
            }
        }
    }

    private class CacheCompleteImpl implements PlayersPitchersCacheAsyncTask.OnLoadCompleteListener {
        @Override
        public void onComplete(){ ((ArrayAdapter) mAdapter).notifyDataSetChanged(); }
        @Override
        public void onFailure(){
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that a failure has happened.
                mListener.onPlayersPitchersInteractionFail("");
            }
        }
    }
}
