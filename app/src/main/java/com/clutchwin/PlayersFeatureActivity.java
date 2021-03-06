package com.clutchwin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.clutchwin.common.Config;
import com.clutchwin.interfaces.IOnShowFragment;
import com.clutchwin.viewmodels.PlayersContextViewModel;

import java.util.Locale;

public class PlayersFeatureActivity extends ActionBarActivity implements ActionBar.TabListener,
        PlayersBattersFragment.OnFragmentInteractionListener,
        PlayersPitchersFragment.OnFragmentInteractionListener,
        PlayersResultsFragment.OnFragmentInteractionListener,
        PlayersDrillDownFragment.OnFragmentInteractionListener {

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

    @Override
    public void onPause(){
        super.onPause();
        // Store values between instances here
        SharedPreferences preferences = getSharedPreferences(Config.PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int navIndex = getSupportActionBar().getSelectedNavigationIndex();
        editor.putString(Config.PLAYERS_SELECTED_NAVIGATION_ITEM, Integer.toString(navIndex));
        editor.apply();
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    boolean isRestartingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playersfeature);

        /*
        //if cache file exists and we have a new instance, then rehydrate from cache
        if(Helpers.checkFileExists(this, Config.PC_CacheFileKey) && !getContextViewModel().getIsHydratedObject()){
            Object outObject;
            try {
                outObject = Helpers.readObjectFromInternalStorage(Config.PC_CacheFileKey);
                Gson gson = new GsonBuilder().create();
                PlayersContextViewModel vm = gson.fromJson(outObject.toString(), PlayersContextViewModel.class);
                vm.setIsHydratedObject(true);
                ClutchWinApplication.setHydratedPlayersContextViewModel(vm);
            } catch (IOException e) {
                Log.e("PlayersFeatureActivity::onCreate", e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                Log.e("PlayersFeatureActivity::onCreate", e.getMessage(), e);
            }
        }
        */

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if(isRestartingActivity) { isRestartingActivity = false; return; }

                int navIndex = actionBar.getSelectedNavigationIndex();
                if(navIndex != position) {
                    // handle swipes
                    actionBar.setSelectedNavigationItem(position);
                }
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

        SharedPreferences preferences = getSharedPreferences(Config.PREF_FILE_NAME, MODE_PRIVATE);
        String prefPosition = preferences.getString(Config.PLAYERS_SELECTED_NAVIGATION_ITEM, null);
        if (prefPosition != null){
            int position = Integer.parseInt(prefPosition);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Config.PLAYERS_SELECTED_NAVIGATION_ITEM, String.valueOf(999));
            editor.apply();

            if(position > 0 && position < 999){
                isRestartingActivity = true;
                actionBar.setSelectedNavigationItem(position);
            }
        }
    }

    @Override
    public void onGoToYearsInteraction() {
        Intent i = new Intent(this, PlayersYearsActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onGoToTeamsInteraction() {
        Intent i = new Intent(this, PlayersTeamsActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPlayersBattersInteraction(String id) {
        getContextViewModel().setBatterId(id);

        /*
        try {
            Helpers.updateFileState(getContextViewModel(), this, Config.PC_CacheFileKey);
        } catch (IOException e) {
            Log.e("PlayersFeatureActivity::onPlayersBattersInteraction", e.getMessage(), e);
        }
        */

        getSupportActionBar().setSelectedNavigationItem(1);
    }

    @Override
    public void onPlayersBattersInteractionFail(String type) {
        if(Config.NoInternet.equals(type)){
            showMessage(getString(R.string.no_internet));
        } else {
            showMessage(getString(R.string.fatal_error));
        }
    }

    @Override
    public void onPlayersPitchersInteraction(String id) {
        getContextViewModel().setPitcherId(id);

        /*
        try {
            Helpers.updateFileState(getContextViewModel(), this, Config.PC_CacheFileKey);
        } catch (IOException e) {
            Log.e("PlayersFeatureActivity::onPlayersPitchersInteraction", e.getMessage(), e);
        }
        */

        getSupportActionBar().setSelectedNavigationItem(2);
    }

    @Override
    public void onPlayersPitchersInteractionFail(String type) {
        if(Config.NoInternet.equals(type)){
            showMessage(getString(R.string.no_internet));
        } else {
            showMessage(getString(R.string.fatal_error));
        }
    }

    @Override
    public void onPlayersResultsInteraction(String id) {
        getContextViewModel().setResultYearId(id);

        /*
        try {
            Helpers.updateFileState(getContextViewModel(), this, Config.PC_CacheFileKey);
        } catch (IOException e) {
            Log.e("PlayersFeatureActivity::onPlayersResultsInteraction", e.getMessage(), e);
        }
        */

        getSupportActionBar().setSelectedNavigationItem(3);
    }

    @Override
    public void onPlayersResultsInteractionFail(String type) {
        if(Config.NoInternet.equals(type)){
            showMessage(getString(R.string.no_internet));
        } else {
            showMessage(getString(R.string.fatal_error));
        }
    }

    @Override
    public void onPlayersDrillDownInteraction(String id) {
        /*
        try {
            Helpers.updateFileState(getContextViewModel(), this, Config.PC_CacheFileKey);
        } catch (IOException e) {
            Log.e("PlayersFeatureActivity::onPlayersDrillDownInteraction", e.getMessage(), e);
        }
        */
    }

    @Override
    public void onPlayersDrillDownInteractionFail(String type) {
        if(Config.NoInternet.equals(type)){
            showMessage(getString(R.string.no_internet));
        } else {
            showMessage(getString(R.string.fatal_error));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.players_feature, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            //case R.id.action_settings:
            //    return true;
            case R.id.action_home:
                return navigateToHome();
            case R.id.action_team:
                return navigateToTeams();
            default:
                return navigateToHome();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
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
        builder.setPositiveButton(getString(R.string.button_continue), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private boolean navigateToHome(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    private boolean navigateToTeams(){
        Intent i = new Intent(this, TeamsFeatureActivity.class);
        startActivity(i);
        this.finish();
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
                    frag = PlayersBattersFragment.newInstance(); break;
                case 1:
                    frag = PlayersPitchersFragment.newInstance(); break;
                case 2:
                    frag = PlayersResultsFragment.newInstance(); break;
                case 3:
                    frag = PlayersDrillDownFragment.newInstance(); break;
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
                    return getString(R.string.title_players_feature_batter).toUpperCase(l);
                case 1:
                    return getString(R.string.title_players_feature_pitcher).toUpperCase(l);
                case 2:
                    return getString(R.string.title_players_feature_result).toUpperCase(l);
                case 3:
                    return getString(R.string.title_players_feature_detail).toUpperCase(l);
            }
            return null;
        }
    }

    private PlayersContextViewModel getContextViewModel(){
        return ClutchWinApplication.getPlayersContextViewModel();
    }
}
