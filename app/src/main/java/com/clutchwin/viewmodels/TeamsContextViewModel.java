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
