package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayersTeamsViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public List<PlayersTeamsViewModel.Team> ITEMS = new ArrayList<PlayersTeamsViewModel.Team>();

    private void addItem(PlayersTeamsViewModel.Team item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersTeamsViewModel.Team> teamList) {
        ITEMS.clear();
        for (PlayersTeamsViewModel.Team teams : teamList) {
            addItem(teams);
        }
    }

    /**
     * Team model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
        private String teamAbbr;
        private String league;
        private String location;
        private String name;

        @JsonCreator
        public Team(@JsonProperty("team_abbr") String team_abbr,
                    @JsonProperty("league") String league,
                    @JsonProperty("location") String location,
                    @JsonProperty("name") String name)
        {
            this.teamAbbr = (team_abbr==null)? "":team_abbr;
            this.league = (league==null)? "":league;
            this.location = (location==null)? "":location;
            this.name = (name==null)? "":name;
        }

        public String getTeamId() { return teamAbbr; }
        public String getLocation() { return location; }
        public String getName() { return name; }
        public String getLeagueId() { return (league != null && league.length() == 1) ? league + "L" : league; }
    }
}
