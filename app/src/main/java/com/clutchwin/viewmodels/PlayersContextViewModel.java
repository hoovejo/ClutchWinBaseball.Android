package com.clutchwin.viewmodels;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayersContextViewModel implements Parcelable {

    private static PlayersContextViewModel _instance;
    public static PlayersContextViewModel Instance() {
        if(_instance == null){
            _instance = new PlayersContextViewModel();
        }
        return _instance;
    }

    private PlayersYearsViewModel playersYearsViewModel;
    public PlayersYearsViewModel getPlayersYearsViewModel() { return playersYearsViewModel; };
    private PlayersTeamsViewModel playersTeamsViewModel;
    public PlayersTeamsViewModel getPlayersTeamsViewModel() { return playersTeamsViewModel; };
    private PlayersBattersViewModel playersBattersViewModel;
    public PlayersBattersViewModel getPlayersBattersViewModel() { return playersBattersViewModel; };
    private PlayersPitchersViewModel playersPitchersViewModel;
    public PlayersPitchersViewModel getPlayersPitchersViewModel() { return playersPitchersViewModel; };
    private PlayersResultsViewModel playersResultsViewModel;
    public PlayersResultsViewModel getPlayersResultsViewModel() { return playersResultsViewModel; };
    private PlayersDrillDownViewModel playersDrillDownViewModel;
    public PlayersDrillDownViewModel getPlayersDrillDownViewModel() { return playersDrillDownViewModel; };

    public PlayersContextViewModel(){
        playersYearsViewModel = PlayersYearsViewModel.Instance();
        playersTeamsViewModel = PlayersTeamsViewModel.Instance();
        playersBattersViewModel = PlayersBattersViewModel.Instance();
        playersPitchersViewModel = PlayersPitchersViewModel.Instance();
        playersResultsViewModel = PlayersResultsViewModel.Instance();
        playersDrillDownViewModel = PlayersDrillDownViewModel.Instance();
    }

    private String yearId;
    public String getYearId() { return yearId; }
    public void setYearId(String id) { yearId = id; }

    private String teamId;
    public String getTeamId() { return teamId; }
    public void setTeamId(String id) { teamId = id; }

    private String batterId;
    public String getBatterId() { return batterId; }
    public void setBatterId(String id) { batterId = id; }

    private String pitcherId;
    public String getPitcherId() { return pitcherId; }
    public void setPitcherId(String id) { pitcherId = id; }

    private String resultYearId;
    public String getResultYearId() { return resultYearId; }
    public void setResultYearId(String id) { resultYearId = id; }

    private String gameType;
    public String getGameType() { return gameType; }
    public void setGameType(String type) { gameType = type; }

    private String lastYearId;

    public boolean shouldExecuteLoadTeams(){
        boolean returnValue;
        if(lastYearId == null || !lastYearId.equals(yearId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastYearId = yearId;
        return returnValue;
    }

    private String lastTeamId;
    private boolean loadBatters;
    public void setVoteLoadBatters(boolean value) {
        if(teamId != null && lastTeamId != teamId ) {
            loadBatters = value;
        }
    }

    public boolean shouldExecuteLoadBatters(){
        boolean returnValue;
        if(loadBatters){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastTeamId = teamId;
        return returnValue;
    }

    private String lastBatterId;

    public boolean shouldExecuteLoadPitchers(){
        boolean returnValue;
        if(lastBatterId == null || !lastBatterId.equals(batterId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastBatterId = batterId;
        return returnValue;
    }

    private String lastSearchBatterId;
    private String lastSearchPitcherId;

    public boolean shouldExecutePlayerResultsSearch(){
        boolean returnValue;
        if(batterId == null || pitcherId == null ||
                lastSearchBatterId == null || lastSearchPitcherId == null ||
                !lastSearchBatterId.equals(batterId) || !lastSearchPitcherId.equals(pitcherId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastSearchBatterId = batterId;
        lastSearchPitcherId = pitcherId;
        return returnValue;
    }

    private String lastDrillDownBatterId;
    private String lastDrillDownPitcherId;
    private String lastDrillDownResultYearId;
    private String lastDrillDownGameType;

    public boolean shouldExecutePlayersDrillDownSearch(){
        boolean returnValue;
        if(batterId == null || pitcherId == null || resultYearId == null || gameType == null ||
                lastDrillDownBatterId == null || lastDrillDownPitcherId == null ||
                lastDrillDownResultYearId == null || lastDrillDownGameType == null ||
                !lastDrillDownBatterId.equals(batterId) || !lastDrillDownPitcherId.equals(pitcherId) ||
                !lastDrillDownResultYearId.equals(resultYearId) || !lastDrillDownGameType.equals(gameType)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastDrillDownBatterId = batterId;
        lastDrillDownPitcherId = pitcherId;
        lastDrillDownResultYearId = resultYearId;
        lastDrillDownGameType = gameType;
        return returnValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
