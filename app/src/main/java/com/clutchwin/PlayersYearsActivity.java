package com.clutchwin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;

import java.io.IOException;

public class PlayersYearsActivity extends FragmentActivity implements PlayersYearsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playersyears);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PlayersYearsFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onPlayersYearsInteraction(String id) {
        ClutchWinApplication app = (ClutchWinApplication)getApplicationContext();
        PlayersContextViewModel playersContextViewModel = app.getPlayersContextViewModel();
        playersContextViewModel.setYearId(id);

        try {
            Helpers.updateFileState(playersContextViewModel, this, Config.PC_CacheFileKey);
        } catch (IOException e) {
            Log.e("PlayersYearsActivity::onPlayersYearsInteraction", e.getMessage(), e);
        }

        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPlayersYearsInteractionFail(String type) {

        if(Config.NoInternet.equals(type)){
            showMessage(getString(R.string.no_internet));
        } else {
            showMessage(getString(R.string.fatal_error));
        }
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }
}
