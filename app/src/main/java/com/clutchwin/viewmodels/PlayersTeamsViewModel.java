package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.clutchwin.common.Config;

import java.util.ArrayList;
import java.util.List;

public class PlayersTeamsViewModel {

    private static PlayersTeamsViewModel _instance;
    public static PlayersTeamsViewModel Instance() {
        if(_instance == null){
            _instance = new PlayersTeamsViewModel();
        }
        return _instance;
    }

    private String teamId;
    public String getTeamId() { return teamId; }
    public void setTeamId(String id) { teamId = id; }

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
    public static class Team {
        private Number id;
        private Number yearId;
        private String teamId;
        private String teamType;
        private String leagueId;
        private String location;
        private String name;

        @JsonCreator
        public Team(@JsonProperty("id") Number id, @JsonProperty("year_id") Number yearId, @JsonProperty("team_id") String teamId,
                    @JsonProperty("team_type") String teamType, @JsonProperty("league_id") String leagueId,
                    @JsonProperty("location") String location, @JsonProperty("name") String name)
        {
            this.id = id;
            this.yearId = yearId;
            this.teamId = teamId;
            this.teamType = teamType;
            this.leagueId = leagueId;
            this.location = location;
            this.name = name;
        }

        public String getTeamId() { return teamId.toString(); }

        @Override
        public String toString() {
            return location + Config.Space + name;
        }
    }

}
