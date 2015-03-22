package com.clutchwin.service;

import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.PlayersYearsViewModel;
import com.crittercism.app.Crittercism;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class PlayersYearsAsyncTask extends AsyncTask<Void, Void, List<PlayersYearsViewModel.Year>> {

    private OnLoadCompleteListener onCompleteListener;

    public PlayersYearsAsyncTask() {}

    @Override
    protected List<PlayersYearsViewModel.Year> doInBackground(Void... params) {

        List<PlayersYearsViewModel.Year> list = null;

        try {

            //http://clutchwin.com/api/v1/seasons.json?
            //access_token=abc
            final String baseUrl = Config.Years;
            StringBuilder finalUrl;
            finalUrl = new StringBuilder(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            list = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersYearsViewModel.Year[].class));

            /*
            try {
                if(list != null && list.size() > 0) {
                    Helpers.writeListToInternalStorage(list, Config.PY_CacheFileKey);
                } else {
                    Helpers.deleteCacheFile(Config.PY_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersYearsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }
            */

        } catch (Exception e) {
            Log.e("PlayersYearsAsyncTask", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersYearsServiceFailure(e);
            }

            Crittercism.logHandledException(e);
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<PlayersYearsViewModel.Year> result) {
        if(onCompleteListener != null){
            onCompleteListener.onPlayersYearsServiceComplete(result);
        }
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersYearsServiceComplete(List<PlayersYearsViewModel.Year> result);
        public void onPlayersYearsServiceFailure(Exception e);
    }
}

