package com.clutchwin.viewmodels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayersYearsViewModel {

    private static PlayersYearsViewModel _instance;
    public static PlayersYearsViewModel Instance() {
        if(_instance == null){
            _instance = new PlayersYearsViewModel();
        }
        return _instance;
    }

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
    public static class Year {
        private Number id;

        @JsonCreator
        public Year(@JsonProperty("id") Number id)
        {
            this.id = id;
        }

        public String getId() { return id.toString(); }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
