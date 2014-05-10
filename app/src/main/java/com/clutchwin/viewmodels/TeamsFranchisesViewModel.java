package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeamsFranchisesViewModel {

    private static TeamsFranchisesViewModel _instance;
    public static TeamsFranchisesViewModel Instance() {
        if(_instance == null){
            _instance = new TeamsFranchisesViewModel();
        }
        return _instance;
    }

    public List<Franchise> ITEMS = new ArrayList<Franchise>();

    private void addItem(Franchise item) {
        ITEMS.add(item);
    }

    public void updateList(List<Franchise> franchiseList) {
        ITEMS.clear();
        for (Franchise franchise : franchiseList) {
            addItem(franchise);
        }

        Collections.sort(ITEMS, new Comparator<Franchise>() {
            public int compare(Franchise o1, Franchise o2) {
                return o1.getLocation().compareTo(o2.getLocation());
            }
        });
    }

    /**
     * Franchise model
     */
    public static class Franchise {
        private String franchiseAbbr;
        private String league;
        private String division;
        private String location;
        private String name;
        private String alternateName;
        private String firstGameDt;
        private String lastGameDt;
        private String city;
        private String state;

        @JsonCreator
        public Franchise(@JsonProperty("franchise_abbr") String franchise_abbr,
                         @JsonProperty("league") String league,
                         @JsonProperty("division") String division,
                         @JsonProperty("location") String location,
                         @JsonProperty("name") String name,
                         @JsonProperty("alternate_name") String alternate_name,
                         @JsonProperty("first_game_dt") String first_game_dt,
                         @JsonProperty("last_game_dt") String last_game_dt,
                         @JsonProperty("city") String city,
                         @JsonProperty("state") String state)
        {
            this.franchiseAbbr = franchise_abbr;
            this.league = league;
            this.division = division;
            this.location = location;
            this.name = name;
            this.alternateName = alternate_name;
            this.firstGameDt = first_game_dt;
            this.lastGameDt = last_game_dt;
            this.city = city;
            this.state = state;
        }

        public String getLocation() { return location; }
        public String getName() { return name; }
        public String getLeagueId() { return league; }
        public String getRetroId() { return franchiseAbbr; }

    }
}
