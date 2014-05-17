package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayersResultsViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public static final String CacheFileKey = "playersResults.json";

    public List<PlayersResultsViewModel.PlayersResult> ITEMS = new ArrayList<PlayersResultsViewModel.PlayersResult>();

    private void addItem(PlayersResultsViewModel.PlayersResult item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersResultsViewModel.PlayersResult> rows) {
        ITEMS.clear();
        for (PlayersResultsViewModel.PlayersResult item : rows) {
            addItem(item);
        }

        Collections.sort(ITEMS, new Comparator<PlayersResultsViewModel.PlayersResult>() {
            public int compare(PlayersResultsViewModel.PlayersResult o1, PlayersResultsViewModel.PlayersResult o2) {
                return o2.getYear().compareTo(o1.getYear());
            }
        });
    }

    /**
     * PlayersResult model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayersResult {
        private String season;
        private Number games;
        private Number atBat;
        private Number hit;
        private Number walks;
        private Number strikeOut;
        private Number secondBase;
        private Number thirdBase;
        private Number homeRun;
        private Number runBattedIn;

        @JsonCreator
        public PlayersResult(@JsonProperty("season") String season,
                   @JsonProperty("g") Number games,
                   @JsonProperty("ab") Number atBat,
                   @JsonProperty("h") Number hit,
                   @JsonProperty("bb") Number walks,
                   @JsonProperty("k") Number strikeOut,
                   @JsonProperty("h_2b") Number secondBase,
                   @JsonProperty("h_3b") Number thirdBase,
                   @JsonProperty("hr") Number homeRun,
                   @JsonProperty("rbi_ct") Number runBattedIn)
        {
            this.season = (season==null)? "":season;
            this.games = (games==null)? 0:games;
            this.atBat = (atBat==null)? 0:atBat;
            this.hit = (hit==null)? 0:hit;
            this.walks = (walks==null)? 0:walks;
            this.strikeOut = (strikeOut==null)? 0:strikeOut;
            this.secondBase = (secondBase==null)? 0:secondBase;
            this.thirdBase = (thirdBase==null)? 0:thirdBase;
            this.homeRun = (homeRun==null)? 0:homeRun;
            this.runBattedIn = (runBattedIn==null)? 0:runBattedIn;
        }

        public String getYear() { return this.season; }

        public Number getGames() { return this.games; }

        public Number getAtBat() { return this.atBat; }

        public Number getHit() { return this.hit; }

        public Number getWalks() { return this.walks; }

        public Number getStrikeOut() { return this.strikeOut;  }

        public Number getSecondBase() { return this.secondBase; }

        public Number getThirdBase() { return this.thirdBase; }

        public Number getHomeRun() { return this.homeRun; }

        public Number getRunBattedIn() { return this.runBattedIn; }

        public Number getAverage() { return (this.hit.floatValue() / this.atBat.floatValue());  }

    }
}
