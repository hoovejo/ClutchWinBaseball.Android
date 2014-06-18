package com.clutchwin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            case R.id.action_team:
                return navigateToTeams();
            case R.id.action_player:
                return navigateToPlayers();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean navigateToTeams(){
        Intent i = new Intent(this, TeamsFeatureActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    private boolean navigateToPlayers(){
        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        switch (position) {
            case 0: //Home
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance())
                        .commit();
                break;
            case 1: //Teams
                Intent iTeams = new Intent(this, TeamsFeatureActivity.class);
                startActivity(iTeams);
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case 2: //Players
                Intent iPlayers = new Intent(this, PlayersFeatureActivity.class);
                startActivity(iPlayers);
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public static PlaceholderFragment newInstance() {
            PlaceholderFragment fragment = new PlaceholderFragment();
            return fragment;
        }

        public PlaceholderFragment() {
        }

        private AdView adView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            adView = (AdView)rootView.findViewById(R.id.adView);
            adView.setAdListener(new AdListener() {
                     /**
                      * Called when an ad is clicked and about to return to the application.
                      */
                     @Override
                     public void onAdClosed() {
                     }

                     /**
                      * Called when an ad failed to load.
                      */
                     @Override
                     public void onAdFailedToLoad(int error) {
                     }
                 });

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
                    .build();
            adView.loadAd(adRequest);
            return rootView;
        }

        @Override
        public void onPause() {
            adView.pause();
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
            adView.resume();
        }

        @Override
        public void onDestroy() {
            adView.destroy();
            super.onDestroy();
        }
    }
}
