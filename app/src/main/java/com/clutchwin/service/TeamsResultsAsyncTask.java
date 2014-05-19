package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsResultsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TeamsResultsAsyncTask extends AsyncTask<Void, Void, List<TeamsResultsViewModel.TeamsResult>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsContextViewModel teamsContextViewModel;

    public TeamsResultsAsyncTask(Context inContext, TeamsContextViewModel inContextViewModel){
        context = inContext;
        teamsContextViewModel = inContextViewModel;
    }

    @Override
    protected List<TeamsResultsViewModel.TeamsResult> doInBackground(Void... params) {

        List<TeamsResultsViewModel.TeamsResult> resultsList = null;

        try {

            //http://clutchwin.com/api/v1/games/for_team/summary.json?
            //&access_token=joe&franchise_abbr=TOR&opp_franchise_abbr=BAL&group=season,team_abbr,opp_abbr&fieldset=basic
            final String baseUrl = Config.FranchiseSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.FranchiseIdKey).append(teamsContextViewModel.getFranchiseId())
                    .append(Config.OpponentIdKey).append(teamsContextViewModel.getOpponentId())
                    .append(Config.FranchiseSearchKeyValue);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            resultsList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsResultsViewModel.TeamsResult[].class));

            try {
                if(resultsList != null && resultsList.size() > 0) {
                    Helpers.writeListToInternalStorage(resultsList, context, Config.TR_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("TeamsResultsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("TeamsResultsAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsResultsServiceFailure();
            }

            context = null;
        }
        return resultsList;
    }

    @Override
    protected void onPostExecute(List<TeamsResultsViewModel.TeamsResult> results) {

        if(onCompleteListener != null){
            onCompleteListener.onTeamsResultsServiceComplete(results);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsResultsServiceComplete(List<TeamsResultsViewModel.TeamsResult> results);
        public void onTeamsResultsServiceFailure();
    }
}