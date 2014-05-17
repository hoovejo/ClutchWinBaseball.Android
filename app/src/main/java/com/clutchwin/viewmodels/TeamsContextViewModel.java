package com.clutchwin.viewmodels;

public class TeamsContextViewModel {

    private static TeamsContextViewModel _instance;
    public static TeamsContextViewModel Instance() {
        if(_instance == null){
            _instance = new TeamsContextViewModel();
        }
        return _instance;
    }

    public static final String CacheFileKey = "teamsContextViewModel.json";

    public TeamsContextViewModel(){}

    private transient TeamsFranchisesViewModel teamsFranchisesViewModel;
    public TeamsFranchisesViewModel getTeamsFranchisesViewModel() { return teamsFranchisesViewModel; };

    private transient TeamsOpponentsViewModel teamsOpponentsViewModel;
    public TeamsOpponentsViewModel getTeamsOpponentsViewModel() { return teamsOpponentsViewModel; };

    private transient TeamsResultsViewModel teamsResultsViewModel;
    public TeamsResultsViewModel getTeamsResultsViewModel() { return teamsResultsViewModel; };

    private transient TeamsDrillDownViewModel teamsDrillDownViewModel;
    public TeamsDrillDownViewModel getTeamsDrillDownViewModel() { return teamsDrillDownViewModel; };

    public boolean needsInnerRehydrate(){
        return (teamsFranchisesViewModel == null || teamsOpponentsViewModel == null ||
                teamsResultsViewModel == null || teamsDrillDownViewModel == null);
    }

    public void rehydrateSecondaries(){
        teamsFranchisesViewModel = TeamsFranchisesViewModel.Instance();
        teamsOpponentsViewModel = TeamsOpponentsViewModel.Instance();
        teamsResultsViewModel = TeamsResultsViewModel.Instance();
        teamsDrillDownViewModel = TeamsDrillDownViewModel.Instance();
        _instance = this;
    }

    private String franchiseId;
    public String getFranchiseId() { return franchiseId; }
    public void setFranchiseId(String id) { franchiseId = id; }

    private String opponentId;
    public String getOpponentId() { return opponentId; }
    public void setOpponentId(String id) { opponentId = id; }

    private String yearId;
    public String getYearId() { return yearId; }
    public void setYearId(String id) { yearId = id; }

    private String lastOpponentFilterFranchiseId;

    public boolean shouldFilterOpponents(){
        boolean returnValue;
        if(lastOpponentFilterFranchiseId == null || !lastOpponentFilterFranchiseId.equals(franchiseId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastOpponentFilterFranchiseId = franchiseId;
        return returnValue;
    }

    private String lastSearchFranchiseId;
    private String lastSearchOpponentId;

    public boolean shouldExecuteTeamResultsSearch(){
        boolean returnValue;
        if(franchiseId == null || opponentId == null ||
                lastSearchFranchiseId == null || lastSearchOpponentId == null ||
                !lastSearchFranchiseId.equals(franchiseId) || !lastSearchOpponentId.equals(opponentId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastSearchFranchiseId = franchiseId;
        lastSearchOpponentId = opponentId;
        return returnValue;
    }

    private String lastDrillDownFranchiseId;
    private String lastDrillDownOpponentId;
    private String lastDrillDownYearId;

    public boolean shouldExecuteTeamDrillDownSearch(){
        boolean returnValue;
        if(franchiseId == null || opponentId == null || yearId == null ||
                lastDrillDownFranchiseId == null || lastDrillDownOpponentId == null ||
                lastDrillDownYearId == null ||
                !lastDrillDownFranchiseId.equals(franchiseId) || !lastDrillDownOpponentId.equals(opponentId) ||
                !lastDrillDownYearId.equals(yearId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        lastDrillDownFranchiseId = franchiseId;
        lastDrillDownOpponentId = opponentId;
        lastDrillDownYearId = yearId;
        return returnValue;
    }
}
