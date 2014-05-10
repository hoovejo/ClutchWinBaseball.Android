package com.clutchwin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.clutchwin.viewmodels.PlayersYearsViewModel;

public class PlayersYearsActivity extends FragmentActivity implements PlayersYearsFragment.OnFragmentInteractionListener {

    private PlayersYearsViewModel yearsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playersyears);

        yearsViewModel = PlayersYearsViewModel.Instance();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PlayersYearsFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onPlayersYearsInteraction(String id) {
        yearsViewModel.setYearId(id);

        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPlayersYearsInteractionFail() {
        showMessage(getString(R.string.fatal_error));
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
}
