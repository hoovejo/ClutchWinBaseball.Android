package com.clutchwin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.PlayersContextViewModel;

public class PlayersTeamsActivity extends FragmentActivity implements PlayersTeamsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playersteams);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PlayersTeamsFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onMenuItemSelected (int featureId, MenuItem item) {
        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPlayersTeamsInteraction(String id) {
        PlayersContextViewModel playersContextViewModel = ClutchWinApplication.getPlayersContextViewModel();
        playersContextViewModel.setTeamId(id);
        playersContextViewModel.setVoteLoadBatters(true);

        /*
        try {
            Helpers.updateFileState(playersContextViewModel, this, Config.PC_CacheFileKey);
        } catch (IOException e) {
            Log.e("PlayersTeamsActivity::onPlayersTeamsInteraction", e.getMessage(), e);
        }
        */

        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPlayersTeamsInteractionFail(String type) {

        if(Config.NoInternet.equals(type)){
            showMessage(getString(R.string.no_internet));
        } else {
            showMessage(getString(R.string.fatal_error));
        }
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
}
