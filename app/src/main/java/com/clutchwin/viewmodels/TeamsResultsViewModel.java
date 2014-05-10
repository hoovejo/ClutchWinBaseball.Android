package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TeamsResultsViewModel {

    private static TeamsResultsViewModel _instance;
    public static TeamsResultsViewModel Instance() {
        if(_instance == null){
            _instance = new TeamsResultsViewModel();
        }
        return _instance;
    }

    private String yearId;
    public String getYearId() { return yearId; }
    public void setYearId(String id) { yearId = id; }

    public List<TeamsResultsViewModel.Row> ITEMS = new ArrayList<TeamsResultsViewModel.Row>();

    private void addItem(TeamsResultsViewModel.Row item) {
        ITEMS.add(item);
    }

    public void updateList(List<Row> rows) {
        ITEMS.clear();
        for (TeamsResultsViewModel.Row item : rows) {
            addItem(item);
        }
    }

    /**
     * TeamResult model
     */
    public static class TeamResult {
        @JsonCreator
        public TeamResult(@JsonProperty("fieldnames") List<FieldNames> fieldNames, @JsonProperty("rows") List<Row> rows)
        {
            this.fieldNames = fieldNames;
            this.rows = rows;
        }
        public List<FieldNames> fieldNames;
        public List<Row> rows;
    }

    public static class FieldNames {
        @JsonCreator
        public FieldNames(@JsonProperty("name") String name)
        {
            this.name = name;
        }

        private String name;
        public String getName() { return this.name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Row {
        @JsonCreator
        public Row(@JsonProperty("year") String year,
                    @JsonProperty("games") Number games,
                    @JsonProperty("team") String team,
                    @JsonProperty("opponent") String opponent,
                    @JsonProperty("wins") Number wins,
                    @JsonProperty("losses") Number losses,
                    @JsonProperty("runs_for") Number runsFor,
                    @JsonProperty("runs_against") Number runsAgainst)
        {
            this.year = year;
            this.games = games;
            this.team = team;
            this.opponent = opponent;
            this.wins = wins;
            this.losses = losses;
            this.runsFor = runsFor;
            this.runsAgainst = runsAgainst;
        }

        private String year;
        private Number games;
        private String team;
        private String opponent;
        private Number wins;
        private Number losses;
        private Number runsFor;
        private Number runsAgainst;

        public Number getGames() {
            return this.games;
        }

        public Number getLosses() {
            return this.losses;
        }

        public String getOpponent() {
            return this.opponent;
        }

        public Number getRunsAgainst() {
            return this.runsFor;
        }

        public Number getRunsFor() {
            return this.runsAgainst;
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
