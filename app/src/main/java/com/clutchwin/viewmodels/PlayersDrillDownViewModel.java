package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayersDrillDownViewModel {

    private static PlayersDrillDownViewModel _instance;
    public static PlayersDrillDownViewModel Instance() {
        if(_instance == null){
            _instance = new PlayersDrillDownViewModel();
        }
        return _instance;
    }

    public List<PlayersDrillDownViewModel.Row> ITEMS = new ArrayList<PlayersDrillDownViewModel.Row>();

    private void addItem(PlayersDrillDownViewModel.Row item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersDrillDownViewModel.Row> rows) {
        ITEMS.clear();
        for (PlayersDrillDownViewModel.Row item : rows) {
            addItem(item);
        }
    }

    /**
     * PlayersDrillDown model
     */
    public static class PlayersDrillDown {
        @JsonCreator
        public PlayersDrillDown(@JsonProperty("fieldnames") List<FieldNames> fieldNames, @JsonProperty("rows") List<Row> rows)
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
                   @JsonProperty("AB") Number atBat,
                   @JsonProperty("H") Number hit,
                   @JsonProperty("2B") Number secondBase,
                   @JsonProperty("3B") Number thirdBase,
                   @JsonProperty("HR") Number homeRun,
                   @JsonProperty("RBI") Number runBattedIn,
                   @JsonProperty("SO") Number strikeOut,
                   @JsonProperty("BB") Number baseBall,
                   @JsonProperty("AVG") String average)
        {
            this.gameDate = gameDate;
            this.atBat = atBat;
            this.hit = hit;
            this.secondBase = secondBase;
            this.thirdBase = thirdBase;
            this.homeRun = homeRun;
            this.runBattedIn = runBattedIn;
            this.strikeOut = strikeOut;
            this.baseBall = baseBall;
            this.average = average;
        }

        private String gameDate;
        private Number atBat;
        private Number hit;
        private Number secondBase;
        private Number thirdBase;
        private Number homeRun;
        private Number runBattedIn;
        private Number strikeOut;
        private Number baseBall;
        private String average;

        public String getGameDate() { return this.gameDate; }

        public Number getAtBat() { return this.atBat; }

        public Number getHit() { return this.hit; }

        public Number getSecondBase() { return this.secondBase; }

        public Number getThirdBase() { return this.thirdBase; }

        public Number getHomeRun() { return this.homeRun; }

        public Number getRunBattedIn() { return this.runBattedIn; }

        public Number getStrikeOut() { return this.strikeOut;  }

        public Number getBaseBall() { return this.baseBall; }

        public String getAverage() { return this.average;  }

    }
}
