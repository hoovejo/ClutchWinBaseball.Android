package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsResultsViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TeamsResultsAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsContextViewModel teamsContextViewModel;
    private TeamsResultsViewModel teamsResultsViewModel;

    public TeamsResultsAsyncTask(Context inContext, TeamsContextViewModel inContextViewModel,
                                 TeamsResultsViewModel inViewModel){
        context = inContext;
        teamsContextViewModel = inContextViewModel;
        teamsResultsViewModel = inViewModel;
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

            teamsResultsViewModel.setIsBusy(true);
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
            List<TeamsResultsViewModel.TeamsResult> resultsList;
            resultsList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsResultsViewModel.TeamsResult[].class));

            try {
                Helpers.writeListToInternalStorage(resultsList, context, Config.TR_CacheFileKey);
            } catch (IOException e) {
                Log.e("TeamsResultsAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

            teamsResultsViewModel.updateList(resultsList);

        } catch (Exception e) {
            Log.e("TeamsResultsAsyncTask::doInBackground", e.getMessage(), e);
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

        teamsResultsViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}