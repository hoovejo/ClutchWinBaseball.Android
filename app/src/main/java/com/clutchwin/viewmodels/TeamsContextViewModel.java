package com.clutchwin.viewmodels;

public class TeamsContextViewModel {

    public TeamsContextViewModel(){}

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
        returnValue = lastOpponentFilterFranchiseId == null || !lastOpponentFilterFranchiseId.equals(franchiseId);
        if(update) {
            lastOpponentFilterFranchiseId = franchiseId;
        }
        return returnValue;
    }

    private String lastSearchFranchiseId;
    private String lastSearchOpponentId;

    public boolean teamResultsServiceCallAllowed(){
        //needed for team results svc call
        return !(franchiseId == null || opponentId == null);
    }

    public boolean shouldExecuteTeamResultsSearch(boolean update){
        //needed for team results svc call
        if(franchiseId == null || opponentId == null) return false;

        boolean returnValue;
        returnValue = lastSearchFranchiseId == null || lastSearchOpponentId == null ||
                !lastSearchFranchiseId.equals(franchiseId) || !lastSearchOpponentId.equals(opponentId);
        if(update) {
            lastSearchFranchiseId = franchiseId;
            lastSearchOpponentId = opponentId;
        }
        return returnValue;
    }

    private String lastDrillDownFranchiseId;
    private String lastDrillDownOpponentId;
    private String lastDrillDownYearId;

    public boolean teamDrillDownServiceCallAllowed(){
        //needed for team drill down svc call
        return !(franchiseId == null || opponentId == null || yearId == null);
    }

    public boolean shouldExecuteTeamDrillDownSearch(boolean update){
        //needed for team results svc call
        if(franchiseId == null || opponentId == null || yearId == null) return false;

        boolean returnValue;
        returnValue = lastDrillDownFranchiseId == null || lastDrillDownOpponentId == null ||
                lastDrillDownYearId == null ||
                !lastDrillDownFranchiseId.equals(franchiseId) || !lastDrillDownOpponentId.equals(opponentId) ||
                !lastDrillDownYearId.equals(yearId);
        if(update) {
            lastDrillDownFranchiseId = franchiseId;
            lastDrillDownOpponentId = opponentId;
            lastDrillDownYearId = yearId;
        }
        return returnValue;
    }
}
