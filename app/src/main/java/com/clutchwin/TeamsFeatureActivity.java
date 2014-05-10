package com.clutchwin;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.clutchwin.common.Helpers;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsOpponentsViewModel;
import com.clutchwin.viewmodels.TeamsResultsViewModel;


public class TeamsFeatureActivity extends ActionBarActivity implements ActionBar.TabListener,
        TeamsFranchisesFragment.OnFragmentInteractionListener,
        TeamsOpponentsFragment.OnFragmentInteractionListener,
        TeamsResultsFragment.OnFragmentInteractionListener,
        TeamsDrillDownFragment.OnFragmentInteractionListener  {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * The view models for this activity
     */
    TeamsFranchisesViewModel franchiseViewModel;
    TeamsOpponentsViewModel opponentViewModel;
    TeamsResultsViewModel teamResultsViewModel;
    TeamsDrillDownViewModel teamDrillDownViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamsfeature);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.teams_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                Fragment fragment = (Fragment) mSectionsPagerAdapter.instantiateItem(mViewPager, position);
                if(fragment instanceof IOnShowFragment) {
                    ((IOnShowFragment) fragment).onShowedFragment();
                }
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        franchiseViewModel = TeamsFranchisesViewModel.Instance();
        opponentViewModel =  TeamsOpponentsViewModel.Instance();
        teamResultsViewModel = TeamsResultsViewModel.Instance();
        teamDrillDownViewModel =  TeamsDrillDownViewModel.Instance();
    }

    @Override
    public void onTeamsFranchisesInteraction(String id) {
        if(Helpers.isNetworkAvailable(this)) {
            franchiseViewModel.setFranchiseId(id);
            getSupportActionBar().setSelectedNavigationItem(1);
        } else {
            showMessage(getString(R.string.no_internet));
        }
    }

    @Override
    public void onTeamsFranchisesInteractionFail() {
        showMessage(getString(R.string.fatal_error));
    }

    @Override
    public void onTeamsOpponentsInteraction(String id) {
        if(Helpers.isNetworkAvailable(this)) {
            opponentViewModel.setOpponentId(id);
            getSupportActionBar().setSelectedNavigationItem(2);
        } else {
            showMessage(getString(R.string.no_internet));
        }
    }

    @Override
    public void onTeamsResultsInteraction(String id) {
        if(Helpers.isNetworkAvailable(this)) {
            teamResultsViewModel.setYearId(id);
            getSupportActionBar().setSelectedNavigationItem(3);
        } else {
            showMessage(getString(R.string.no_internet));
        }
    }

    @Override
    public void onTeamsResultsInteractionFail() {
        showMessage(getString(R.string.fatal_error));
    }

    @Override
    public void onTeamsDrillDownInteraction(String id) {
    }

    @Override
    public void onTeamsDrillDownInteractionFail() {
        showMessage(getString(R.string.fatal_error));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teams_feature, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_home:
                return navigateToHome();
            case R.id.action_player:
                return navigateToPlayers();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        if(Helpers.isNetworkAvailable(this)) {
            mViewPager.setCurrentItem(tab.getPosition());
        } else {
            showMessage(getString(R.string.no_internet));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                navigateToHome();
            }
        });
    }

    private boolean navigateToHome(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    private boolean navigateToPlayers(){
        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = TeamsFranchisesFragment.newInstance();
                    break;
                case 1:
                    frag = TeamsOpponentsFragment.newInstance();
                    break;
                case 2:
                    frag = TeamsResultsFragment.newInstance();
                    break;
                case 3:
                    frag = TeamsDrillDownFragment.newInstance();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
                switch (position) {
                    case 0:
                        return getString(R.string.title_teams_feature_team).toUpperCase(l);
                    case 1:
                        return getString(R.string.title_teams_feature_opponent).toUpperCase(l);
                    case 2:
                        return getString(R.string.title_teams_feature_result).toUpperCase(l);
                    case 3:
                        return getString(R.string.title_teams_feature_detail).toUpperCase(l);
                }
            return null;
        }
    }

}
