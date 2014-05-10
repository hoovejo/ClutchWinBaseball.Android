package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TeamsDrillDownViewModel {

    private static TeamsDrillDownViewModel _instance;
    public static TeamsDrillDownViewModel Instance() {
        if(_instance == null){
            _instance = new TeamsDrillDownViewModel();
        }
        return _instance;
    }

    public List<TeamsDrillDownViewModel.Row> ITEMS = new ArrayList<TeamsDrillDownViewModel.Row>();

    private void addItem(TeamsDrillDownViewModel.Row item) {
        ITEMS.add(item);
    }

    public void updateList(List<Row> rows) {
        ITEMS.clear();
        for (TeamsDrillDownViewModel.Row item : rows) {
            addItem(item);
        }
    }

    /**
     * TeamDrillDown model
     */
    public static class TeamDrillDown {
        @JsonCreator
        public TeamDrillDown(@JsonProperty("fieldnames") List<FieldNames> fieldNames, @JsonProperty("rows") List<Row> rows)
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
        public Row(@JsonProperty("Game Date") String gameDate,
                    @JsonProperty("team") String team,
                    @JsonProperty("opponent") String opponent,
                    @JsonProperty("win") Number win,
                    @JsonProperty("loss") Number loss,
                    @JsonProperty("runs_for") Number runsFor,
                    @JsonProperty("runs_against") Number runsAgainst)
        {
            this.gameDate = gameDate;
            this.loss = loss;
            this.opponent = opponent;
            this.runsAgainst = runsAgainst;
            this.runsFor = runsFor;
            this.team = team;
            this.win = win;
        }

        private String gameDate;
        private String team;
        private String opponent;
        private Number win;
        private Number loss;
        private Number runsFor;
        private Number runsAgainst;

        public String getGameDate(){ return this.gameDate; }
        public String getTeam(){ return this.team; }
        public String getOpponent(){ return this.opponent; }
        public Number getWin(){ return this.win; }
        public Number getLoss(){ return this.loss; }
        public Number getRunsFor(){ return this.runsFor; }
        public Number getRunsAgainst(){ return this.runsAgainst; }

    }
}
