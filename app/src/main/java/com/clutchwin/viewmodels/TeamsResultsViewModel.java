package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeamsResultsViewModel {

    private static TeamsResultsViewModel _instance;
    public static TeamsResultsViewModel Instance() {
        if(_instance == null){
            _instance = new TeamsResultsViewModel();
        }
        return _instance;
    }

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public static final String CacheFileKey = "teamsResults.json";

    public List<TeamsResultsViewModel.TeamsResult> ITEMS = new ArrayList<TeamsResultsViewModel.TeamsResult>();

    private void addItem(TeamsResultsViewModel.TeamsResult item) {
        ITEMS.add(item);
    }

    public void updateList(List<TeamsResult> rows) {
        ITEMS.clear();
        for (TeamsResultsViewModel.TeamsResult item : rows) {
            addItem(item);
        }

        Collections.sort(ITEMS, new Comparator<TeamsResult>() {
            public int compare(TeamsResult o1, TeamsResult o2) {
                return o2.getYear().compareTo(o1.getYear());
            }
        });
    }

    /**
     * TeamsResult model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsResult {

        @JsonCreator
        public TeamsResult(@JsonProperty("season") String season,
                         @JsonProperty("team_abbr") String team_abbr,
                         @JsonProperty("opp_abbr") String opp_abbr,
                         @JsonProperty("win") Number win,
                         @JsonProperty("loss") Number loss,
                         @JsonProperty("score") Number score,
                         @JsonProperty("opp_score") Number opp_score)
        {
            this.year = season;
            this.team = team_abbr;
            this.opponent = opp_abbr;
            this.wins = win;
            this.losses = loss;
            this.runsFor = score;
            this.runsAgainst = opp_score;
        }

        private String year;
        private String team;
        private String opponent;
        private Number wins;
        private Number losses;
        private Number runsFor;
        private Number runsAgainst;

        public Number getGames() {
            return wins.intValue() + losses.intValue();
        }

        public Number getLosses() {
            return this.losses;
        }

        public String getOpponent() {
            return this.opponent;
        }

        public Number getRunsAgainst() {
            return this.runsAgainst;
        }

        public Number getRunsFor() {
            return this.runsFor;
        }

        public String getTeam() {
            return this.team;
        }

        public Number getWins() {
            return this.wins;
        }

        public String getYear() {
            return this.year;
        }
    }
}
