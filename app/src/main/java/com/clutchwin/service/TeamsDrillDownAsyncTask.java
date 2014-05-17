package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TeamsDrillDownAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsDrillDownViewModel viewModel;
    private String franchiseId;
    private String opponentId;
    private String yearId;

    public TeamsDrillDownAsyncTask(Context inContext, TeamsDrillDownViewModel inViewModel,
                                 String inFranchiseId, String inOpponentId, String inYearId){
        context = inContext;
        viewModel = inViewModel;
        franchiseId = inFranchiseId;
        opponentId = inOpponentId;
        yearId = inYearId;
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
            //http://clutchwin.com/api/v1/games/for_team.json?
            //&access_token=abc&franchise_abbr=TOR&opp_franchise_abbr=BAL&season=2013&fieldset=basic
            final String baseUrl = Config.FranchiseYearSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue)
                    .append(Config.FranchiseIdKey).append(franchiseId)
                    .append(Config.OpponentIdKey).append(opponentId)
                    .append(Config.SeasonIdKey).append(yearId)
                    .append(Config.FranchiseYearSearchKeyValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            List<TeamsDrillDownViewModel.TeamsDrillDown> resultsList;
            resultsList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsDrillDownViewModel.TeamsDrillDown[].class));

            viewModel.updateList(resultsList);

            try {
                Helpers.writeListToInternalStorage(resultsList, context, viewModel.CacheFileKey);
            } catch (IOException e) {
                Log.e("TeamsDrillDownAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

        } catch (Exception e) {
            Log.e("TeamsDrillDownAsyncTask::doInBackground", e.getMessage(), e);
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
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}
