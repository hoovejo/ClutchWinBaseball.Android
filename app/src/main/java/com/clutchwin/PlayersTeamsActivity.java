package com.clutchwin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.clutchwin.interfaces.IDelayLoadFragment;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;

public class PlayersTeamsActivity extends FragmentActivity implements PlayersTeamsFragment.OnFragmentInteractionListener {

    private PlayersTeamsViewModel teamsViewModel;
    private PlayersTeamsFragment teamsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playersteams);

        teamsViewModel = PlayersTeamsViewModel.Instance();

        if (savedInstanceState == null) {
            teamsFragment = PlayersTeamsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, teamsFragment)
                    .commit();
        }
    }

    @Override
    public void onPlayersTeamsInteraction(String id) {
        teamsViewModel.setTeamId(id);

        PlayersBattersFragment fragment = PlayersBattersFragment.Instance();
        if(fragment instanceof IDelayLoadFragment) {
            ((IDelayLoadFragment) fragment).onReadyToLoad();
        }

        Intent i = new Intent(this, PlayersFeatureActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPlayersTeamsInteractionFail() {
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
