package com.clutchwin.viewmodels;

public class PlayersContextViewModel {

    public static final String CacheFileKey = "playersContextViewModel.json";

    public PlayersContextViewModel(){}

    private transient boolean isHydratedObject = false;
    public boolean getIsHydratedObject() { return isHydratedObject; }
    public void setIsHydratedObject(boolean b) { isHydratedObject = b; }

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

    private String lastYearId;

    public boolean shouldExecuteLoadTeams(boolean update){
        boolean returnValue;
        if(lastYearId == null || !lastYearId.equals(yearId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastYearId = yearId;
        }
        return returnValue;
    }

    private String lastTeamId;
    private boolean loadBatters;
    public void setVoteLoadBatters(boolean value) {
        if(teamId != null && lastTeamId != teamId ) {
            loadBatters = value;
        }
    }

    public boolean shouldExecuteLoadBatters(boolean update){
        boolean returnValue;
        if(loadBatters){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastTeamId = teamId;
        }
        return returnValue;
    }

    private String lastBatterId;

    public boolean shouldExecuteLoadPitchers(boolean update){
        boolean returnValue;
        if(lastBatterId == null || !lastBatterId.equals(batterId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastBatterId = batterId;
        }
        return returnValue;
    }

    private String lastSearchBatterId;
    private String lastSearchPitcherId;

    public boolean shouldExecutePlayerResultsSearch(boolean update){
        boolean returnValue;
        if(batterId == null || pitcherId == null ||
                lastSearchBatterId == null || lastSearchPitcherId == null ||
                !lastSearchBatterId.equals(batterId) || !lastSearchPitcherId.equals(pitcherId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastSearchBatterId = batterId;
            lastSearchPitcherId = pitcherId;
        }
        return returnValue;
    }

    private String lastDrillDownBatterId;
    private String lastDrillDownPitcherId;
    private String lastDrillDownResultYearId;

    public boolean shouldExecutePlayersDrillDownSearch(boolean update){
        boolean returnValue;
        if(batterId == null || pitcherId == null || resultYearId == null ||
                lastDrillDownBatterId == null || lastDrillDownPitcherId == null ||
                lastDrillDownResultYearId == null ||
                !lastDrillDownBatterId.equals(batterId) || !lastDrillDownPitcherId.equals(pitcherId) ||
                !lastDrillDownResultYearId.equals(resultYearId) ){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastDrillDownBatterId = batterId;
            lastDrillDownPitcherId = pitcherId;
            lastDrillDownResultYearId = resultYearId;
        }
        return returnValue;
    }
}
