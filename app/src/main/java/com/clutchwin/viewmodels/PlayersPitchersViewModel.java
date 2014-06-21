package com.clutchwin.viewmodels;

import android.util.Log;

import com.clutchwin.common.Config;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayersPitchersViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public List<PlayersPitchersViewModel.Pitcher> ITEMS = new ArrayList<PlayersPitchersViewModel.Pitcher>();

    private void addItem(PlayersPitchersViewModel.Pitcher item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersPitchersViewModel.Pitcher> pitcherList) {
        ITEMS.clear();
        for (PlayersPitchersViewModel.Pitcher pitcher : pitcherList) {
            addItem(pitcher);
        }

        try {
            Collections.sort(ITEMS, new Comparator<PlayersPitchersViewModel.Pitcher>() {
                public int compare(PlayersPitchersViewModel.Pitcher o1, PlayersPitchersViewModel.Pitcher o2) {
                    return o1.getFirstName().compareTo(o2.getFirstName());
                }
            });
        } catch (Exception e){
            Log.e("Sort error", e.getMessage(), e);
        }
    }

    /**
     * Pitcher model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pitcher {
        private String firstName;
        private String lastName;
        private String playerId;

        @JsonCreator
        public Pitcher(@JsonProperty("first_name") String first_name,
                      @JsonProperty("last_name") String last_name,
                      @JsonProperty("player_id") String player_id)
        {
            this.firstName =  (first_name==null)? "":first_name;
            this.lastName = (last_name==null)? "":last_name;
            this.playerId = (player_id==null)? "":player_id;
        }

        public String getFirstName() { return firstName; }
        public String getRetroId() {
            return this.playerId;
        }

        @Override
        public String toString() {
            return firstName + Config.Space + lastName;
        }

    }
}
