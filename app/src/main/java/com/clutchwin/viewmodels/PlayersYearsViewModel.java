package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayersYearsViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public List<PlayersYearsViewModel.Year> ITEMS = new ArrayList<PlayersYearsViewModel.Year>();

    private void addItem(PlayersYearsViewModel.Year item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersYearsViewModel.Year> yearList) {
        ITEMS.clear();
        for (PlayersYearsViewModel.Year year : yearList) {
            addItem(year);
        }
    }

    /**
     * Year model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Year {
        private String season;

        @JsonCreator
        public Year(@JsonProperty("season") String season){
            this.season = (season==null)? "":season;
        }

        public String getId() { return season; }

        @Override
        public String toString() {
            return season;
        }
    }
}
