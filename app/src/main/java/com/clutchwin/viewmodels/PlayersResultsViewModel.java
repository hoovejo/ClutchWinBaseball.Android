package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayersResultsViewModel {

    private static PlayersResultsViewModel _instance;
    public static PlayersResultsViewModel Instance() {
        if(_instance == null){
            _instance = new PlayersResultsViewModel();
        }
        return _instance;
    }

    public List<PlayersResultsViewModel.Row> ITEMS = new ArrayList<PlayersResultsViewModel.Row>();

    private void addItem(PlayersResultsViewModel.Row item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersResultsViewModel.Row> rows) {
        ITEMS.clear();
        for (PlayersResultsViewModel.Row item : rows) {
            addItem(item);
        }

        Collections.sort(ITEMS, new Comparator<PlayersResultsViewModel.Row>() {
            public int compare(PlayersResultsViewModel.Row o1, PlayersResultsViewModel.Row o2) {
                return o2.getYear().compareTo(o1.getYear());
            }
        });
    }

    /**
     * PlayersResult model
     */
    public static class PlayersResult {
        @JsonCreator
        public PlayersResult(@JsonProperty("fieldnames") List<FieldNames> fieldNames, @JsonProperty("rows") List<Row> rows)
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
                   @JsonProperty("Type") String type,
                   @JsonProperty("G") Number games,
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
            this.year = year;
            this.type = type;
            this.games = games;
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

        private String year;
        private String type;
        private Number games;
        private Number atBat;
        private Number hit;
        private Number secondBase;
        private Number thirdBase;
        private Number homeRun;
        private Number runBattedIn;
        private Number strikeOut;
        private Number baseBall;
        private String average;

        public String getYear() { return this.year; }

        public String getType() { return this.type; }

        public Number getGames() { return this.games; }

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
