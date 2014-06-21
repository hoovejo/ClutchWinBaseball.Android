package com.clutchwin.viewmodels;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeamsFranchisesViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public List<Franchise> ITEMS = new ArrayList<Franchise>();

    private void addItem(Franchise item) {
        ITEMS.add(item);
    }

    public void updateList(List<Franchise> franchiseList) {
        ITEMS.clear();
        for (Franchise franchise : franchiseList) {
            addItem(franchise);
        }

        try {
            Collections.sort(ITEMS, new Comparator<Franchise>() {
                public int compare(Franchise o1, Franchise o2) {
                    return o1.getLocation().compareTo(o2.getLocation());
                }
            });
        } catch (Exception e){
            Log.e("Sort error", e.getMessage(), e);
        }
    }

    /**
     * Franchise model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Franchise {
        private String franchiseAbbr;
        private String league;
        private String location;
        private String name;

        @JsonCreator
        public Franchise(@JsonProperty("franchise_abbr") String franchise_abbr,
                         @JsonProperty("league") String league,
                         @JsonProperty("location") String location,
                         @JsonProperty("name") String name)
        {
            this.franchiseAbbr = (franchise_abbr==null)? "":franchise_abbr;
            this.league = (league==null)? "":league;
            this.location = (location==null)? "":location;
            this.name = (name==null)? "":name;
        }

        public String getLocation() { return location; }
        public String getName() { return name; }
        public String getLeagueId() { return league; }
        public String getRetroId() { return franchiseAbbr; }

    }
}
