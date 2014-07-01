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
            case R.id.action_credits:
                return showCredits();
            case R.id.action_team:
                return navigateToTeams();
            case R.id.action_player:
                return navigateToPlayers();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean showCredits(){
        Intent i = new Intent(this, CreditsActivity.class);
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
            return new PlaceholderFragment();
        }

        public PlaceholderFragment() {
        }

        private AdView adView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            if (rootView != null) {
                adView = (AdView)rootView.findViewById(R.id.adView);
                adView.setAdListener(new AdListener() {
                    // hide ad block if none could be found
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        rootView.findViewById(R.id.adView).setVisibility(View.GONE);
                        super.onAdFailedToLoad(errorCode);
                    }
                    // show ad block if one was found
                    @Override
                    public void onAdLoaded() {
                        rootView.findViewById(R.id.adView).setVisibility(View.VISIBLE);
                        super.onAdLoaded();
                    }
                });

                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("7131D30965CD130CF2B191C5F971E83C") //nexus 5
                        .addTestDevice("6E5BC0EE5F0BAC66ED402756B91B18D5") //nexus 7
                        .build();
                adView.loadAd(adRequest);
            }
            return rootView;
        }

        @Override
        public void onPause() {
            adView.pause();
            super.onPause();
        }

        @Override
        public void onResume() {
            adView.resume();
            super.onResume();
        }

        @Override
        public void onDestroy() {
            adView.destroy();
            super.onDestroy();
        }
    }
}
