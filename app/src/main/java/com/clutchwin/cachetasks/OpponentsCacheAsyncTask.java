package com.clutchwin.cachetasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clutchwin.R;
import com.clutchwin.common.Helpers;
import com.clutchwin.viewmodels.TeamsContextViewModel;
import com.clutchwin.viewmodels.TeamsFranchisesViewModel;
import com.clutchwin.viewmodels.TeamsOpponentsViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class OpponentsCacheAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnLoadCompleteListener onCompleteListener;
    private TeamsContextViewModel teamsContextViewModel;
    private TeamsOpponentsViewModel teamsOpponentsViewModel;
    private TeamsFranchisesViewModel teamsFranchisesViewModel;

    public OpponentsCacheAsyncTask(Context inContext, TeamsContextViewModel inContextViewModel,
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
        Object outObject;
        try {

            teamsOpponentsViewModel.setIsBusy(true);

            outObject = Helpers.readObjectFromInternalStorage(context, teamsFranchisesViewModel.CacheFileKey);
            Gson gson = new GsonBuilder().create();
            JSONArray jsonArray = new JSONArray(outObject.toString());
            Type listType = new TypeToken<List<TeamsFranchisesViewModel.Franchise>>(){}.getType();
            List<TeamsFranchisesViewModel.Franchise> list = gson.fromJson(jsonArray.toString(), listType);
            teamsOpponentsViewModel.filterList(list, teamsContextViewModel.getFranchiseId());

        } catch (Exception e) {
            Log.e("OpponentsCacheAsyncTask::doInBackground", e.getMessage(), e);
            if (onCompleteListener != null) {
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

