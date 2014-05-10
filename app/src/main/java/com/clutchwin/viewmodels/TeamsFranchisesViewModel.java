package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TeamsFranchisesViewModel {

    private static TeamsFranchisesViewModel _instance;
    public static TeamsFranchisesViewModel Instance() {
        if(_instance == null){
            _instance = new TeamsFranchisesViewModel();
        }
        return _instance;
    }

    private String franchiseId;
    public String getFranchiseId() { return franchiseId; }
    public void setFranchiseId(String id) { franchiseId = id; }

    public List<Franchise> ITEMS = new ArrayList<Franchise>();

    private void addItem(Franchise item) {
        ITEMS.add(item);
    }

    public void updateList(List<Franchise> franchiseList) {
        ITEMS.clear();
        for (Franchise franchise : franchiseList) {
            addItem(franchise);
        }
    }

    /**
     * Franchise model
     */
    public static class Franchise {
        private String id;
        private String retroId;
        private String leagueId;
        private String divisionId;
        private String location;
        private String name;
        private String alternateName;
        private String firstGameDt;
        private String lastGameDt;
        private String city;
        private String state;

        @JsonCreator
        public Franchise(@JsonProperty("id") String id,
                         @JsonProperty("retro_id") String retro_id,
                         @JsonProperty("league_id") String league_id,
                         @JsonProperty("division_id") String division_id,
                         @JsonProperty("location") String location,
                         @JsonProperty("name") String name,
                         @JsonProperty("alternate_name") String alternate_name,
                         @JsonProperty("first_game_dt") String first_game_dt,
                         @JsonProperty("last_game_dt") String last_game_dt,
                         @JsonProperty("city") String city,
                         @JsonProperty("state") String state)
        {
            this.id = id;
            this.retroId = retro_id;
            this.leagueId = league_id;
            this.divisionId = division_id;
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
        public String getLeagueId() { return leagueId; }
        public String getRetroId() { return retroId; }

    }
}
