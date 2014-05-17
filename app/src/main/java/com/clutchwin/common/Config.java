package com.clutchwin.common;

public class Config {

    public static final String AccessTokenKey = "&access_token=";
    public static final String AccessTokenValue = "joe";

    public static final String FranchiseIdKey = "&franchise_abbr=";
    public static final String OpponentIdKey = "&opp_franchise_abbr=";
    public static final String TeamIdKey = "&team_abbr=";
    public static final String FranchiseSearchKeyValue = "&group=season,team_abbr,opp_abbr&fieldset=basic";
    public static final String SeasonIdKey = "&season=";
    public static final String FranchiseYearSearchKeyValue = "&fieldset=basic";

    public static final String BatterIdKey = "&bat_id=";
    public static final String PitcherIdKey = "&pit_id=";
    public static final String GroupSeasonKeyValue = "&group=season";
    public static final String GroupGameDateKeyValue = "&group=game_date";

    public static final String Space = " ";
    public static final String Slash = "/";
    public static final String JsonSuffix = ".json";

    public static final String Franchise = "http://clutchwin.com/api/v1/franchises.json?";
    public static final String FranchiseSearch = "http://clutchwin.com/api/v1/games/for_team/summary.json?";
    public static final String FranchiseYearSearch = "http://clutchwin.com/api/v1/games/for_team.json?";

    public static final String Years = "http://clutchwin.com/api/v1/seasons.json?";
    public static final String Teams = "http://clutchwin.com/api/v1/teams.json?";
    public static final String RosterSearch = "http://clutchwin.com/api/v1/players.json?";
    public static final String OpponentsForBatter = "http://versus.skeenshare.com/search/opponents_for_batter/";
    public static final String PlayerPlayerSearch = "http://clutchwin.com/api/v1/events/summary.json?";
    public static final String PlayerPlayerYearSearch = "http://clutchwin.com/api/v1/events/summary.json?";

}
