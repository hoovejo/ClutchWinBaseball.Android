package com.clutchwin.common;

public class Config {

    public static final String MustImplement = "must implement OnFragmentInteractionListener";

    public static final String NoInternet = "Internet";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    public static final String PREF_FILE_NAME = "PrefFile";
    public static final String TEAMS_SELECTED_NAVIGATION_ITEM = "teams_selected_nav";
    public static final String PLAYERS_SELECTED_NAVIGATION_ITEM = "players_selected_nav";

    //public static final String PC_CacheFileKey = "playersContextViewModel.json";
    public static final String PY_CacheFileKey = "playersYears.json";
    public static final String PY_SvcTaskKey = "playersYears.svc";
    public static final String PT_CacheFileKey = "playersTeams.json";
    public static final String PT_SvcTaskKey = "playersTeams.svc";
    public static final String PB_CacheFileKey = "playersBatters.json";
    public static final String PB_SvcTaskKey = "playersBatters.svc";
    public static final String PP_CacheFileKey = "playersPitchers.json";
    public static final String PP_SvcTaskKey = "playersPitchers.svc";
    public static final String PR_CacheFileKey = "playersResults.json";
    public static final String PR_SvcTaskKey = "playersResults.svc";
    public static final String PDD_CacheFileKey = "playersDrillDown.json";
    public static final String PDD_SvcTaskKey = "playersDrillDown.svc";

    //public static final String TC_CacheFileKey = "teamsContextViewModel.json";
    public static final String TF_CacheFileKey = "franchises.json";
    public static final String TF_SvcTaskKey = "franchises.svc";
    //public static final String TO_CacheTaskKey = "opponents.svc";
    public static final String TR_CacheFileKey = "teamsResults.json";
    public static final String TR_SvcTaskKey = "teamsResults.svc";
    public static final String TDD_CacheFileKey = "teamsDrillDown.json";
    public static final String TDD_SvcTaskKey = "teamsDrillDown.svc";


    public static final String AccessTokenKey = "&access_token=";
    public static final String AccessTokenValue = "bf0a9b612fcdba499d98ed720a02c3da";
    public static final String AnalyticsTokenValue = "550f03dfe0697fa449637a5a";

    public static final String FranchiseIdKey = "&franchise_abbr=";
    public static final String OpponentIdKey = "&opp_franchise_abbr=";
    public static final String TeamIdKey = "&team_abbr=";
    public static final String FranchiseSearchKeyValue = "&group=season,team_abbr,opp_abbr&fieldset=basic&max_entries=200";
    public static final String SeasonIdKey = "&season=";
    public static final String FieldSetBasicKeyValue = "&fieldset=basic";

    public static final String BatterIdKey = "&bat_id=";
    public static final String PitcherIdKey = "&pit_id=";
    public static final String GroupSeasonKeyValue = "&group=season";
    public static final String GroupGameDateKeyValue = "&group=game_date";

    public static final String Space = " ";

    public static final String Franchise = "http://clutchwin.com/api/v1/franchises.json?";
    public static final String FranchiseSearch = "http://clutchwin.com/api/v1/games/for_team/summary.json?";
    public static final String FranchiseYearSearch = "http://clutchwin.com/api/v1/games/for_team.json?";

    public static final String Years = "http://clutchwin.com/api/v1/seasons.json?";
    public static final String Teams = "http://clutchwin.com/api/v1/teams.json?";
    public static final String RosterSearch = "http://clutchwin.com/api/v1/players.json?";
    public static final String OpponentsForBatter = "http://clutchwin.com/api/v1/opponents/pitchers.json?";
    public static final String PlayerPlayerSearch = "http://clutchwin.com/api/v1/events/summary.json?";
    public static final String PlayerPlayerYearSearch = "http://clutchwin.com/api/v1/events/summary.json?";

}
