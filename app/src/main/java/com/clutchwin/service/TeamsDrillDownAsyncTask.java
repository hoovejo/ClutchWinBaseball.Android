package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Config;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

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
            //"http://versus.skeenshare.com/search/franchise_vs_franchise_by_year/ATL/BOS/2012.json";
            final String baseUrl = Config.FranchiseYearSearch;
            StringBuffer finalUrl = new StringBuffer(baseUrl);
            finalUrl.append(Config.Slash).append(franchiseId)
                    .append(Config.Slash).append(opponentId)
                    .append(Config.Slash).append(yearId)
                    .append(Config.JsonSuffix);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            TeamsDrillDownViewModel.TeamDrillDown result = restTemplate.getForObject(finalUrl.toString(), TeamsDrillDownViewModel.TeamDrillDown.class);
            viewModel.updateList(result.rows);
        } catch (Exception e) {
            Log.e("TeamsDrillDownAsyncTask", e.getMessage(), e);
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
