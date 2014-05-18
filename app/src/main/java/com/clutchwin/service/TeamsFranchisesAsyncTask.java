package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TeamsFranchisesAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsFranchisesViewModel teamsFranchisesViewModel;

    public TeamsFranchisesAsyncTask(Context inContext, TeamsFranchisesViewModel inViewModel){
        context = inContext;
        teamsFranchisesViewModel = inViewModel;
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

            teamsFranchisesViewModel.setIsBusy(true);
            //http://clutchwin.com/api/v1/franchises.json?
            final String baseUrl = Config.Franchise;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.AccessTokenKey).append(Config.AccessTokenValue);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            List<TeamsFranchisesViewModel.Franchise> franchiseList;
            franchiseList = Arrays.asList(restTemplate.getForObject(finalUrl.toString(), TeamsFranchisesViewModel.Franchise[].class));

            try {
                Helpers.writeListToInternalStorage(franchiseList, context, Config.TF_CacheFileKey);
            } catch (IOException e) {
                Log.e("TeamsFranchisesAsyncTask::writeListToInternalStorage", e.getMessage(), e);
            }

            teamsFranchisesViewModel.updateList(franchiseList);

        } catch (Exception e) {
            Log.e("TeamsFranchisesAsyncTask::doInBackground", e.getMessage(), e);
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
        teamsFranchisesViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}
