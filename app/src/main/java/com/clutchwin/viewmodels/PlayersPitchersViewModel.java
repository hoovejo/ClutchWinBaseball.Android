package com.clutchwin.viewmodels;

import com.clutchwin.common.Config;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayersPitchersViewModel {

    private boolean _isBusy = false;
    public boolean getIsBusy() { return _isBusy; }
    public void setIsBusy(boolean b) { _isBusy = b; }

    public List<PlayersPitchersViewModel.Row> ITEMS = new ArrayList<PlayersPitchersViewModel.Row>();

    private void addItem(PlayersPitchersViewModel.Row item) {
        ITEMS.add(item);
    }

    public void updateList(List<PlayersPitchersViewModel.Row> pitcherList) {
        ITEMS.clear();
        for (PlayersPitchersViewModel.Row pitcher : pitcherList) {
            addItem(pitcher);
        }

        Collections.sort(ITEMS, new Comparator<PlayersPitchersViewModel.Row>() {
            public int compare(PlayersPitchersViewModel.Row o1, PlayersPitchersViewModel.Row o2) {
                return o1.getFirstName().compareTo(o2.getFirstName());
            }
        });
    }

    /**
     * PitchersResult model
     */
    public static class PitchersResult {
        @JsonCreator
        public PitchersResult(@JsonProperty("fieldnames") List<FieldNames> fieldNames, @JsonProperty("rows") List<Row> rows)
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
        public Row(@JsonProperty("first_name") String firstName,
                   @JsonProperty("last_name") String lastName,
                   @JsonProperty("retro_id") String retroId)
        {
            this.firstName = firstName;
            this.lastName = lastName;
            this.retroId = retroId;
        }

        private String firstName;
        private String lastName;
        private String retroId;

        public String getFirstName() { return firstName; }
        public String getRetroId() {
            return this.retroId;
        }

        @Override
        public String toString() {
            return firstName + Config.Space + lastName;
        }

    }
}
