package com.clutchwin.viewmodels;

public class PlayersContextViewModel {

    public PlayersContextViewModel(){}

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
        //needed for teams svc call
        if(yearId == null) return false;

        boolean returnValue;
        returnValue = lastYearId == null || !lastYearId.equals(yearId);
        if(update) {
            lastYearId = yearId;
        }
        return returnValue;
    }

    private String lastTeamId;
    private boolean loadBatters;
    public void setVoteLoadBatters(boolean value) {
        if(teamId != null && !teamId.equals(lastTeamId)) {
            loadBatters = value;
        }
    }

    public boolean shouldExecuteLoadBatters(boolean update){
        //needed for batter svc call
        if(teamId == null) return false;

        boolean returnValue;
        if(loadBatters){
            returnValue = true;
            loadBatters = false;
        } else {
            returnValue = false;
        }
        if(update) {
            lastTeamId = teamId;
        }
        return returnValue;
    }

    private String lastBatterId;

    public boolean playersPitchersServiceCallAllowed(){
        //needed for pitcher svc call
        return !(batterId == null || yearId == null);
    }

    public boolean shouldExecuteLoadPitchers(boolean update){
        //needed for pitcher svc call
        if(batterId == null || yearId == null) return false;

        boolean returnValue;
        returnValue = lastBatterId == null || !lastBatterId.equals(batterId);
        if(update) {
            lastBatterId = batterId;
        }
        return returnValue;
    }

    private String lastSearchBatterId;
    private String lastSearchPitcherId;

    public boolean playersResultsServiceCallAllowed(){
        //needed for results svc call
        return !(batterId == null || pitcherId == null);
    }

    public boolean shouldExecutePlayerResultsSearch(boolean update){
        //needed for results svc call
        if(batterId == null || pitcherId == null) return false;

        boolean returnValue;
        returnValue = lastSearchBatterId == null || lastSearchPitcherId == null ||
                !lastSearchBatterId.equals(batterId) || !lastSearchPitcherId.equals(pitcherId);
        if(update) {
            lastSearchBatterId = batterId;
            lastSearchPitcherId = pitcherId;
        }
        return returnValue;
    }

    private String lastDrillDownBatterId;
    private String lastDrillDownPitcherId;
    private String lastDrillDownResultYearId;

    public boolean playersDrillDownServiceCallAllowed(){
        //needed for drillDown svc call
        return !(batterId == null || pitcherId == null || resultYearId == null);
    }

    public boolean shouldExecutePlayersDrillDownSearch(boolean update){
        //needed for drillDown svc call
        if(batterId == null || pitcherId == null || resultYearId == null) return false;

        boolean returnValue;
        returnValue = lastDrillDownBatterId == null || lastDrillDownPitcherId == null ||
                lastDrillDownResultYearId == null ||
                !lastDrillDownBatterId.equals(batterId) || !lastDrillDownPitcherId.equals(pitcherId) ||
                !lastDrillDownResultYearId.equals(resultYearId);
        if(update) {
            lastDrillDownBatterId = batterId;
            lastDrillDownPitcherId = pitcherId;
            lastDrillDownResultYearId = resultYearId;
        }
        return returnValue;
    }
}
