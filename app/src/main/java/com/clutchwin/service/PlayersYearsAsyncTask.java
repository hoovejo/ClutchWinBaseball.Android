package com.clutchwin.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersYearsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersYearsAsyncTask extends AsyncTask<Void, Void, List<PlayersYearsViewModel.Year>> {

    private Context context;
    private OnLoadCompleteListener onCompleteListener;

    public PlayersYearsAsyncTask(Context inContext){
        context = inContext;
    }

    @Override
    protected List<PlayersYearsViewModel.Year> doInBackground(Void... params) {

        List<PlayersYearsViewModel.Year> yearList = null;

        try {

            //http://clutchwin.com/api/v1/seasons.json?
            //access_token=abc
            final String baseUrl = Config.Years;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            yearList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersYearsViewModel.Year[].class));

            try {
                if(yearList != null && yearList.size() > 0) {
                    Helpers.writeListToInternalStorage(yearList, context, Config.PY_CacheFileKey);
                }
            } catch (IOException e) {
                Log.e("PlayersYearsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("PlayersYearsAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onPlayersYearsServiceFailure();
            }

            context = null;
        }
        return yearList;
    }

    @Override
    protected void onPostExecute(List<PlayersYearsViewModel.Year> results) {
        if(onCompleteListener != null){
            onCompleteListener.onPlayersYearsServiceComplete(results);
        }

        context = null;
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onPlayersYearsServiceComplete(List<PlayersYearsViewModel.Year> results);
        public void onPlayersYearsServiceFailure();
    }
}

