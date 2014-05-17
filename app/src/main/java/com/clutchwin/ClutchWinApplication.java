package com.clutchwin;

import android.app.Application;
import android.content.res.Configuration;

import com.clutchwin.viewmodels.PlayersBattersViewModel;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersDrillDownViewModel;
import com.clutchwin.viewmodels.PlayersPitchersViewModel;
import com.clutchwin.viewmodels.PlayersResultsViewModel;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;
import com.clutchwin.viewmodels.PlayersYearsViewModel;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsOpponentsViewModel;
import com.clutchwin.viewmodels.TeamsResultsViewModel;

public class ClutchWinApplication extends Application {

    private static ClutchWinApplication singleton;

    public ClutchWinApplication getInstance(){
        return singleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * All teams context view models
     */
    private static TeamsContextViewModel _teamsContextViewModel;
    public static void setHydratedTeamsContextViewModel(TeamsContextViewModel model) {
        _teamsContextViewModel = model;
    }

    public static TeamsContextViewModel getTeamsContextViewModel() {
        if(_teamsContextViewModel == null){
            _teamsContextViewModel = new TeamsContextViewModel();
        }
        return _teamsContextViewModel;
    }

    private static TeamsFranchisesViewModel _teamsFranchisesViewModel;
    public static TeamsFranchisesViewModel getTeamsFranchisesViewModel() {
        if(_teamsFranchisesViewModel == null){
            _teamsFranchisesViewModel = new TeamsFranchisesViewModel();
        }
        return _teamsFranchisesViewModel;
    }

    private static TeamsOpponentsViewModel _teamsOpponentsViewModel;
    public static TeamsOpponentsViewModel getTeamsOpponentsViewModel() {
        if(_teamsOpponentsViewModel == null){
            _teamsOpponentsViewModel = new TeamsOpponentsViewModel();
        }
        return _teamsOpponentsViewModel;
    }

    private static TeamsResultsViewModel _teamsResultsViewModel;
    public static TeamsResultsViewModel getTeamsResultsViewModel() {
        if(_teamsResultsViewModel == null){
            _teamsResultsViewModel = new TeamsResultsViewModel();
        }
        return _teamsResultsViewModel;
    }

    private static TeamsDrillDownViewModel _teamsDrillDownViewModel;
    public static TeamsDrillDownViewModel getTeamsDrillDownViewModel() {
        if(_teamsDrillDownViewModel == null){
            _teamsDrillDownViewModel = new TeamsDrillDownViewModel();
        }
        return _teamsDrillDownViewModel;
    }

    /**
     * All players context view models
     */
    private static PlayersContextViewModel _playersContextViewModel;
    public static void setHydratedPlayersContextViewModel(PlayersContextViewModel model) {
        _playersContextViewModel = model;
    }

    public static PlayersContextViewModel getPlayersContextViewModel() {
        if(_playersContextViewModel == null){
            _playersContextViewModel = new PlayersContextViewModel();
        }
        return _playersContextViewModel;
    }

    private static PlayersYearsViewModel _playersYearsViewModel;
    public static PlayersYearsViewModel getPlayersYearsViewModel() {
        if(_playersYearsViewModel == null){
            _playersYearsViewModel = new PlayersYearsViewModel();
        }
        return _playersYearsViewModel;
    }

    private static PlayersTeamsViewModel _playersTeamsViewModel;
    public static PlayersTeamsViewModel getPlayersTeamsViewModel() {
        if(_playersTeamsViewModel == null){
            _playersTeamsViewModel = new PlayersTeamsViewModel();
        }
        return _playersTeamsViewModel;
    }

    private static PlayersBattersViewModel _playersBattersViewModel;
    public static PlayersBattersViewModel getPlayersBattersViewModel() {
        if(_playersBattersViewModel == null){
            _playersBattersViewModel = new PlayersBattersViewModel();
        }
        return _playersBattersViewModel;
    }

    private static PlayersPitchersViewModel _playersPitchersViewModel;
    public static PlayersPitchersViewModel getPlayersPitchersViewModel() {
        if(_playersPitchersViewModel == null){
            _playersPitchersViewModel = new PlayersPitchersViewModel();
        }
        return _playersPitchersViewModel;
    }

    private static PlayersResultsViewModel _playersResultsViewModel;
    public static PlayersResultsViewModel getPlayersResultsViewModel() {
        if(_playersResultsViewModel == null){
            _playersResultsViewModel = new PlayersResultsViewModel();
        }
        return _playersResultsViewModel;
    }

    private static PlayersDrillDownViewModel _playersDrillDownViewModel;
    public static PlayersDrillDownViewModel getPlayersDrillDownViewModel() {
        if(_playersDrillDownViewModel == null){
            _playersDrillDownViewModel = new PlayersDrillDownViewModel();
        }
        return _playersDrillDownViewModel;
    }
}
