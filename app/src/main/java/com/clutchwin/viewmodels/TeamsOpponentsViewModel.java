package com.clutchwin.viewmodels;

import java.util.ArrayList;
import java.util.List;

public class TeamsOpponentsViewModel {

    private static TeamsOpponentsViewModel _instance;
    public static TeamsOpponentsViewModel Instance() {
        if(_instance == null){
            _instance = new TeamsOpponentsViewModel();
        }
        return _instance;
    }

    private String opponentId;
    public String getOpponentId() { return opponentId; }
    public void setOpponentId(String id) { opponentId = id; }

    public List<TeamsFranchisesViewModel.Franchise> ITEMS = new ArrayList<TeamsFranchisesViewModel.Franchise>();

    private void addItem(TeamsFranchisesViewModel.Franchise item) {
        ITEMS.add(item);
    }

    public void filterList(List<TeamsFranchisesViewModel.Franchise> baseList, String filter) {
        ITEMS.clear();
        for (TeamsFranchisesViewModel.Franchise franchise : baseList) { // complete base list
            if( franchise.getRetroId() != filter) {
                addItem(franchise);
            }
        }
    }
}
