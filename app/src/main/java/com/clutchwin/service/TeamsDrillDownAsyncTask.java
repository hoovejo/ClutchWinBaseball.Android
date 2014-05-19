package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TeamsDrillDownAsyncTask extends AsyncTask<Void, Void, List<TeamsDrillDownViewModel.TeamsDrillDown>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsContextViewModel teamsContextViewModel;

    public TeamsDrillDownAsyncTask(Context inContext, TeamsContextViewModel inContextViewModel){
        context = inContext;
        teamsContextViewModel = inContextViewModel;
    }

    @Override
    protected List<TeamsDrillDownViewModel.TeamsDrillDown> doInBackground(Void... params) {

        List<TeamsDrillDownViewModel.TeamsDrillDown> resultsList = null;

        try {

            //http://clutchwin.com/api/v1/games/for_team.json?
            //&access_token=abc&franchise_abbr=TOR&opp_franchise_abbr=BAL&season=2013&fieldset=basic
            final String baseUrl = Config.FranchiseYearSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.FranchiseIdKey).append(teamsContextViewModel.getFranchiseId())
                    .append(Config.OpponentIdKey).append(teamsContextViewModel.getOpponentId())
                    .append(Config.SeasonIdKey).append(teamsContextViewModel.getYearId())
                    .append(Config.FranchiseYearSearchKeyValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            resultsList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsDrillDownViewModel.TeamsDrillDown[].class));

            try {
                if(resultsList != null && resultsList.size() > 0) {
                    Helpers.writeListToInternalStorage(resultsList, context, Config.TDD_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("TeamsDrillDownAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("TeamsDrillDownAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsDrillDownServiceFailure();
            }

            context = null;
        }
        return resultsList;
    }

    @Override
    protected void onPostExecute(List<TeamsDrillDownViewModel.TeamsDrillDown> results) {

        if(onCompleteListener != null){
            onCompleteListener.onTeamsDrillDownServiceComplete(results);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsDrillDownServiceComplete(List<TeamsDrillDownViewModel.TeamsDrillDown> results);
        public void onTeamsDrillDownServiceFailure();
    }
}
