package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.PlayersContextViewModel;
import com.clutchwin.viewmodels.PlayersTeamsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PlayersTeamsAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private PlayersContextViewModel playersContextViewModel;
    private PlayersTeamsViewModel playersTeamsViewModel;

    public PlayersTeamsAsyncTask(Context inContext, PlayersContextViewModel inContextViewModel,
                                 PlayersTeamsViewModel inViewModel){
        context = inContext;
        playersContextViewModel = inContextViewModel;
        playersTeamsViewModel = inViewModel;
    }

    @Override
    protected void onPreExecute(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            playersTeamsViewModel.setIsBusy(true);
            //http://clutchwin.com/api/v1/teams.json?
            final String baseUrl = Config.Teams;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.SeasonIdKey).append(playersContextViewModel.getYearId());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            List<PlayersTeamsViewModel.Team> teamList;
            teamList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), PlayersTeamsViewModel.Team[].class));

            try {
                Helpers.writeListToInternalStorage(teamList, context, Config.PT_CacheFileKey);
            } catch (IOException e) {
                Log.e("PlayersTeamsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

            playersTeamsViewModel.updateList(teamList);

        } catch (Exception e) {
            Log.e("PlayersTeamsAsyncTask::doInBackground", e.getMessage(), e);
            if(onCompleteListener != null){
                onCompleteListener.onFailure();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        if(onCompleteListener != null){
            onCompleteListener.onComplete();
        }

        playersTeamsViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}
