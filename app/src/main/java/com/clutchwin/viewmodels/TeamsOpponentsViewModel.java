package com.clutchwin.viewmodels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeamsOpponentsViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    //public static final String CacheFileKey = "opponents.json";

    public List<TeamsFranchisesViewModel.Franchise> ITEMS = new ArrayList<TeamsFranchisesViewModel.Franchise>();

    private void addItem(TeamsFranchisesViewModel.Franchise item) {
        ITEMS.add(item);
    }

    public void filterList(List<TeamsFranchisesViewModel.Franchise> baseList, String filter) {

        ITEMS.clear();
        for (TeamsFranchisesViewModel.Franchise franchise : baseList) { // complete base list
            if( !franchise.getRetroId().equals(filter) ) {
                addItem(franchise);
            }
        }

        Collections.sort(ITEMS, new Comparator<TeamsFranchisesViewModel.Franchise>() {
            public int compare(TeamsFranchisesViewModel.Franchise o1, TeamsFranchisesViewModel.Franchise o2) {
                return o1.getLocation().compareTo(o2.getLocation());
            }
        });
    }
}
