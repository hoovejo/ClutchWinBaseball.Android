package com.clutchwin.viewmodels;

import com.clutchwin.common.Config;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayersBattersViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public List<PlayersBattersViewModel.Batter> ITEMS = new ArrayList<PlayersBattersViewModel.Batter>();

    private void addItem(PlayersBattersViewModel.Batter item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersBattersViewModel.Batter> batterList) {
        ITEMS.clear();
        for (PlayersBattersViewModel.Batter batter : batterList) {
            addItem(batter);
        }

        try {
            Collections.sort(ITEMS, new Comparator<Batter>() {
                public int compare(Batter o1, Batter o2) {
                    return o1.getFirstName().compareTo(o2.getFirstName());
                }
            });
        } catch (Exception e){}
    }

    /**
     * Batter model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Batter {
        private String firstName;
        private String lastName;
        private String retroPlayerId;

        @JsonCreator
        public Batter(@JsonProperty("first_name") String first_name,
                    @JsonProperty("last_name") String last_name,
                    @JsonProperty("player_retro_id") String player_retro_id)
        {
            this.firstName =  (first_name==null)? "":first_name;
            this.lastName = (last_name==null)? "":last_name;
            this.retroPlayerId = (player_retro_id==null)? "":player_retro_id;
        }

        public String getFirstName() { return firstName; }
        public String getRetroPlayerId() { return retroPlayerId; }

        @Override
        public String toString() {
            return firstName + Config.Space + lastName;
        }
    }
}
