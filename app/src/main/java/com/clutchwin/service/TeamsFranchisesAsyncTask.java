package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.crittercism.app.Crittercism;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class TeamsFranchisesAsyncTask extends AsyncTask<Void, Void, List<TeamsFranchisesViewModel.Franchise>> {

    private OnLoadCompleteListener onCompleteListener;

    public TeamsFranchisesAsyncTask() {}

    @Override
    protected List<TeamsFranchisesViewModel.Franchise> doInBackground(Void... params) {

        List<TeamsFranchisesViewModel.Franchise> list = null;

        try {

            //http://clutchwin.com/api/v1/franchises.json?
            final String baseUrl = Config.Franchise;
            StringBuilder finalUrl;
            finalUrl = new StringBuilder(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            list = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsFranchisesViewModel.Franchise[].class));

            /*
            try {
                if(list != null && list.size() > 0 ) {
                    Helpers.writeListToInternalStorage(list, Config.TF_CacheFileKey);
                } else {
                    Helpers.deleteCacheFile(Config.TF_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("TeamsFranchisesAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }
            */

        } catch (Exception e) {
            Log.e("TeamsFranchisesAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onTeamsFranchisesServiceFailure(e);
            }

            Crittercism.logHandledException(e);
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<TeamsFranchisesViewModel.Franchise> result) {

        if(onCompleteListener != null){
            onCompleteListener.onTeamsFranchisesServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onTeamsFranchisesServiceComplete(List<TeamsFranchisesViewModel.Franchise> result);
        public void onTeamsFranchisesServiceFailure(Exception e);
    }
}
