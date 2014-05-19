package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TeamsFranchisesAsyncTask extends AsyncTask<Void, Void, List<TeamsFranchisesViewModel.Franchise>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;

    public TeamsFranchisesAsyncTask(Context inContext){
        context = inContext;
    }

    @Override
    protected List<TeamsFranchisesViewModel.Franchise> doInBackground(Void... params) {

        List<TeamsFranchisesViewModel.Franchise> franchiseList = null;

        try {

            //http://clutchwin.com/api/v1/franchises.json?
            final String baseUrl = Config.Franchise;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            franchiseList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsFranchisesViewModel.Franchise[].class));

            try {
                if(franchiseList != null && franchiseList.size() > 0 ) {
                    Helpers.writeListToInternalStorage(franchiseList, context, Config.TF_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("TeamsFranchisesAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("TeamsFranchisesAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsFranchisesServiceFailure();
            }

            context = null;
        }
        return franchiseList;
    }

    @Override
    protected void onPostExecute(List<TeamsFranchisesViewModel.Franchise> results) {

        if(onCompleteListener != null){
            onCompleteListener.onTeamsFranchisesServiceComplete(results);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsFranchisesServiceComplete(List<TeamsFranchisesViewModel.Franchise> results);
        public void onTeamsFranchisesServiceFailure();
    }
}
