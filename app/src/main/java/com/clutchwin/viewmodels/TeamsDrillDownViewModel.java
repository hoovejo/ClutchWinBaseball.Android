package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TeamsDrillDownViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public List<TeamsDrillDownViewModel.TeamsDrillDown> ITEMS = new ArrayList<TeamsDrillDownViewModel.TeamsDrillDown>();

    private void addItem(TeamsDrillDownViewModel.TeamsDrillDown item) {
        ITEMS.add(item);
    }

    public void updateList(List<TeamsDrillDown> rows) {
        ITEMS.clear();
        for (TeamsDrillDownViewModel.TeamsDrillDown item : rows) {
            addItem(item);
        }

        try {
            Collections.sort(ITEMS, new Comparator<TeamsDrillDown>() {
                public int compare(TeamsDrillDown o1, TeamsDrillDown o2) {
                    return o2.getGameDate().compareTo(o1.getGameDate());
                }
            });
        } catch (Exception e){}
    }

    /**
     * TeamDrillDown model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsDrillDown {
        private String gameDate;
        private String team;
        private String opponent;
        private Number win;
        private Number loss;
        private Number runsFor;
        private Number runsAgainst;

        @JsonCreator
        public TeamsDrillDown(@JsonProperty("game_date") String game_date,
                           @JsonProperty("team_abbr") String team_abbr,
                           @JsonProperty("opp_abbr") String opp_abbr,
                           @JsonProperty("win") Number win,
                           @JsonProperty("loss") Number loss,
                           @JsonProperty("score") Number score,
                           @JsonProperty("opp_score") Number opp_score)
        {
            this.gameDate = (game_date==null)? "":game_date;
            this.team = (team_abbr==null)? "":team_abbr;
            this.opponent = (opp_abbr==null)? "":opp_abbr;
            this.win = (win==null)? 0:win;
            this.loss = (loss==null)? 0:loss;
            this.runsFor = (score==null)? 0:score;
            this.runsAgainst = (opp_score==null)? 0:opp_score;
        }

        public String getGameDate() {
            return gameDate;
        }

        public String getTeam() {
            return this.team;
        }

        public String getOpponent() {
            return this.opponent;
        }

        public Number getWin() {
            return this.win;
        }

        public Number getLoss() {
            return this.loss;
        }

        public Number getRunsFor() {
            return this.runsFor;
        }

        public Number getRunsAgainst() {
            return this.runsAgainst;
        }
    }
}
