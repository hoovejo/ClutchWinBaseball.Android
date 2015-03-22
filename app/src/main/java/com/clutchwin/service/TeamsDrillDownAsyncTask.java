package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.ClutchWinApplication;
import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;
import com.crittercism.app.Crittercism;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class TeamsDrillDownAsyncTask extends AsyncTask<Void, Void, List<TeamsDrillDownViewModel.TeamsDrillDown>> {

    private OnLoadCompleteListener onCompleteListener;

    public TeamsDrillDownAsyncTask() {}

    @Override
    protected List<TeamsDrillDownViewModel.TeamsDrillDown> doInBackground(Void... params) {

        List<TeamsDrillDownViewModel.TeamsDrillDown> list = null;

        try {

            //http://clutchwin.com/api/v1/games/for_team.json?
            //&access_token=abc&franchise_abbr=TOR&opp_franchise_abbr=BAL&season=2013&fieldset=basic
            final String baseUrl = Config.FranchiseYearSearch;
            StringBuilder finalUrl;
            finalUrl = new StringBuilder(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.FranchiseIdKey)
                    .append(ClutchWinApplication.getTeamsContextViewModel().getFranchiseId())
                    .append(Config.OpponentIdKey)
                    .append(ClutchWinApplication.getTeamsContextViewModel().getOpponentId())
                    .append(Config.SeasonIdKey)
                    .append(ClutchWinApplication.getTeamsContextViewModel().getYearId())
                    .append(Config.FieldSetBasicKeyValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            list = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsDrillDownViewModel.TeamsDrillDown[].class));

            /*
            try {
                if(list != null && list.size() > 0) {
                    Helpers.writeListToInternalStorage(list, Config.TF_CacheFileKey);
                } else {
                    Helpers.deleteCacheFile(Config.TF_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("TeamsDrillDownAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }
            */

        } catch (Exception e) {
            Log.e("TeamsDrillDownAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsDrillDownServiceFailure(e);
            }

            Crittercism.logHandledException(e);
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<TeamsDrillDownViewModel.TeamsDrillDown> result) {

        if(onCompleteListener != null){
            onCompleteListener.onTeamsDrillDownServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsDrillDownServiceComplete(List<TeamsDrillDownViewModel.TeamsDrillDown> result);
        public void onTeamsDrillDownServiceFailure(Exception e);
    }
}
