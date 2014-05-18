package com.clutchwin.viewmodels;

public class TeamsContextViewModel {

    public static final String CacheFileKey = "teamsContextViewModel.json";

    public TeamsContextViewModel(){}

    private transient boolean isHydratedObject = false;
    public boolean getIsHydratedObject() { return isHydratedObject; }
    public void setIsHydratedObject(boolean b) { isHydratedObject = b; }

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

    public boolean shouldFilterOpponents(boolean update){
        //needed for opponent filtering
        if(franchiseId == null) return false;

        boolean returnValue;
        if(lastOpponentFilterFranchiseId == null || !lastOpponentFilterFranchiseId.equals(franchiseId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastOpponentFilterFranchiseId = franchiseId;
        }
        return returnValue;
    }

    private String lastSearchFranchiseId;
    private String lastSearchOpponentId;

    public boolean shouldExecuteTeamResultsSearch(boolean update){
        //needed for team results svc call
        if(franchiseId == null || opponentId == null) return false;

        boolean returnValue;
        if(lastSearchFranchiseId == null || lastSearchOpponentId == null ||
                !lastSearchFranchiseId.equals(franchiseId) || !lastSearchOpponentId.equals(opponentId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastSearchFranchiseId = franchiseId;
            lastSearchOpponentId = opponentId;
        }
        return returnValue;
    }

    private String lastDrillDownFranchiseId;
    private String lastDrillDownOpponentId;
    private String lastDrillDownYearId;

    public boolean shouldExecuteTeamDrillDownSearch(boolean update){
        //needed for team results svc call
        if(franchiseId == null || opponentId == null || yearId == null) return false;

        boolean returnValue;
        if(lastDrillDownFranchiseId == null || lastDrillDownOpponentId == null ||
                lastDrillDownYearId == null ||
                !lastDrillDownFranchiseId.equals(franchiseId) || !lastDrillDownOpponentId.equals(opponentId) ||
                !lastDrillDownYearId.equals(yearId)){
            returnValue = true;
        } else {
            returnValue = false;
        }
        if(update) {
            lastDrillDownFranchiseId = franchiseId;
            lastDrillDownOpponentId = opponentId;
            lastDrillDownYearId = yearId;
        }
        return returnValue;
    }
}
