package com.clutchwin;

import android.app.Application;
import android.os.AsyncTask;

import com.clutchwin.common.Helpers;
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

import java.util.HashMap;

public class ClutchWinApplication extends Application {

    private static ClutchWinApplication singleton;

    public static ClutchWinApplication getInstance(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        final Thread.UncaughtExceptionHandler subclass = Thread.currentThread().getUncaughtExceptionHandler();
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
            try{
                Helpers.purgeAllCacheFiles(getApplicationContext());
            } catch (Exception e){}

            // carry on with prior flow
            subclass.uncaughtException(thread, ex);
            }
        });
    }

    private HashMap<String, AsyncTask<?,?,?>> tasks = null;

    public void registerTask(String tag, AsyncTask<?,?,?> task) {
        if(tasks == null ){
            tasks = new HashMap<String, AsyncTask<?,?,?>>();
        }
        tasks.put(tag, task);
    }

    public void unregisterTask(String tag) {
        if(tasks == null ){
            tasks = new HashMap<String, AsyncTask<?,?,?>>();
        }
        tasks.remove(tag);
    }

    public AsyncTask<?,?,?> getTask(String tag) {
        if(tasks == null ){
            tasks = new HashMap<String, AsyncTask<?,?,?>>();
        }
        return tasks.get(tag);
    }


    private boolean hasLoadedFranchisesOncePerSession = false;
    public boolean getHasLoadedFranchisesOnce() { return hasLoadedFranchisesOncePerSession; }
    public void setHasLoadedFranchisesOnce(boolean b) { hasLoadedFranchisesOncePerSession = b;}

    private boolean hasLoadedSeasonsOncePerSession = false;
    public boolean getHasLoadedSeasonsOnce() { return hasLoadedSeasonsOncePerSession; }
    public void setHasLoadedSeasonsOnce(boolean b) { hasLoadedSeasonsOncePerSession = b;}

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
