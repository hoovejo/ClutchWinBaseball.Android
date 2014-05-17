package com.clutchwin.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsOpponentsViewModel;


public class TeamsOpponentsAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsContextViewModel teamsContextViewModel;
    private TeamsOpponentsViewModel teamsOpponentsViewModel;
    private TeamsFranchisesViewModel teamsFranchisesViewModel;

    public TeamsOpponentsAsyncTask(Context inContext,
                                   TeamsContextViewModel inContextViewModel,
                                   TeamsOpponentsViewModel inOpponentsViewModel,
                                   TeamsFranchisesViewModel inFranchisesViewModel){
        context = inContext;
        teamsContextViewModel = inContextViewModel;
        teamsOpponentsViewModel = inOpponentsViewModel;
        teamsFranchisesViewModel = inFranchisesViewModel;
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

            teamsOpponentsViewModel.setIsBusy(true);
            teamsOpponentsViewModel.filterList(teamsFranchisesViewModel.ITEMS, teamsContextViewModel.getFranchiseId());

            //Helpers.writeListToInternalStorage(teamsOpponentsViewModel.ITEMS, context, teamsOpponentsViewModel.CacheFileKey);

        } catch (Exception e) {
            Log.e("TeamsOpponentsAsyncTask::doInBackground", e.getMessage(), e);
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
        teamsOpponentsViewModel.setIsBusy(false);
    }

    public void setOnCompleteListener(OnLoadCompleteListener inOnCompleteListener){
        onCompleteListener = inOnCompleteListener;
    }

    public interface OnLoadCompleteListener {
        public void onComplete();
        public void onFailure();
    }
}
