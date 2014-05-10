package com.clutchwin.viewmodels;

import com.clutchwin.common.Config;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayersBattersViewModel {

    private static PlayersBattersViewModel _instance;
    public static PlayersBattersViewModel Instance() {
        if(_instance == null){
            _instance = new PlayersBattersViewModel();
        }
        return _instance;
    }

    public List<PlayersBattersViewModel.Batter> ITEMS = new ArrayList<PlayersBattersViewModel.Batter>();

    private void addItem(PlayersBattersViewModel.Batter item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersBattersViewModel.Batter> batterList) {
        ITEMS.clear();
        for (PlayersBattersViewModel.Batter batter : batterList) {
            addItem(batter);
        }

        Collections.sort(ITEMS, new Comparator<Batter>() {
            public int compare(Batter o1, Batter o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });
    }

    /**
     * Batter model
     */
    public static class Batter {
        private Number id;
        private String batHand;
        private String firstName;
        private String lastName;
        private String gameType;
        private String pitHand;
        private String posTx;
        private String repTeamId;
        private String retroPlayerId;
        private String retroTeamId;
        private Number yearId;

        @JsonCreator
        public Batter(@JsonProperty("id") Number id, @JsonProperty("bat_hand") String batHand, @JsonProperty("first_name") String firstName,
                    @JsonProperty("game_type") String gameType, @JsonProperty("last_name") String lastName,
                    @JsonProperty("pit_hand") String pitHand, @JsonProperty("pos_tx") String posTx,
                    @JsonProperty("rep_team_id") String repTeamId, @JsonProperty("retro_player_id") String retroPlayerId,
                    @JsonProperty("retro_team_id") String retroTeamId, @JsonProperty("year_id") Number yearId)
        {
            this.id = id;
            this.batHand = batHand;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gameType = gameType;
            this.pitHand = pitHand;
            this.posTx = posTx;
            this.repTeamId = repTeamId;
            this.retroPlayerId = retroPlayerId;
            this.retroTeamId = retroTeamId;
            this.yearId = yearId;
        }

        public String getFirstName() { return firstName; }
        public String getRetroPlayerId() { return retroPlayerId; }

        @Override
        public String toString() {
            return firstName + Config.Space + lastName;
        }
    }
}
