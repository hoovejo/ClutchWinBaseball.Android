package com.clutchwin.common;

public class Config {

    public static final String AccessTokenKey = "&access_token=";
    public static final String AccessTokenValue = "joe";

    public static final String FranchiseIdKey = "&franchise_abbr=";
    public static final String OpponentIdKey = "&opp_franchise_abbr=";
    public static final String FranchiseSearchKeyValue = "&group=season,team_abbr,opp_abbr&fieldset=basic";
    public static final String SeasonIdKey = "&season=";
    public static final String FranchiseYearSearchKeyValue = "&fieldset=basic";

    public static final String Space = " ";
    public static final String Slash = "/";
    public static final String Ampersand = "&";
    public static final String JsonSuffix = ".json";
    public static final String Franchise = "http://clutchwin.com/api/v1/franchises.json?";
    public static final String FranchiseSearch = "http://clutchwin.com/api/v1/games/for_team/summary.json?";
    public static final String FranchiseYearSearch = "http://clutchwin.com/api/v1/games/for_team.json?";
    public static final String OpponentsForBatter = "http://versus.skeenshare.com/search/opponents_for_batter/";
    public static final String PlayerPlayerSearch = "http://versus.skeenshare.com/search/player_vs_player/";
    public static final String PlayerPlayerYearSearch = "http://versus.skeenshare.com/search/player_vs_player_by_year/";
    public static final String RosterSearch = "http://versus.skeenshare.com/roster_for_team_and_year/";
    public static final String Teams = "http://versus.skeenshare.com/teams/";
    public static final String Years = "http://versus.skeenshare.com/years.json";

}
