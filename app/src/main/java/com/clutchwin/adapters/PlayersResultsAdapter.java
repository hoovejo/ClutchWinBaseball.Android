package com.clutchwin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clutchwin.R;
import com.clutchwin.viewmodels.PlayersResultsViewModel;

import java.util.List;

public class PlayersResultsAdapter extends BaseAdapter {

    private final Context context;
    private int layoutResourceId;
    private List<PlayersResultsViewModel.Row> values;

    public PlayersResultsAdapter(Context context, int layoutResourceId, List<PlayersResultsViewModel.Row> values) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PerformanceViewElementHolder holder;
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new PerformanceViewElementHolder();
            holder.elementOne = (TextView)convertView.findViewById(R.id.textYear);
            holder.elementTwo = (TextView)convertView.findViewById(R.id.textType);
            holder.elementThree = (TextView)convertView.findViewById(R.id.textGames);
            holder.elementFour = (TextView)convertView.findViewById(R.id.textAtBat);
            holder.elementFive = (TextView)convertView.findViewById(R.id.textHit);
            holder.elementSix = (TextView)convertView.findViewById(R.id.textSecondBase);
            holder.elementSeven = (TextView)convertView.findViewById(R.id.textThirdBase);
            holder.elementEight = (TextView)convertView.findViewById(R.id.textHomeRun);
            holder.elementNine = (TextView)convertView.findViewById(R.id.textRunBattedIn);
            holder.elementTen = (TextView)convertView.findViewById(R.id.textStrikeOut);
            holder.elementEleven = (TextView)convertView.findViewById(R.id.textBaseBall);
            holder.elementTwelve = (TextView)convertView.findViewById(R.id.textAverage);
            convertView.setTag(holder);
        }
        else
        {
            holder = (PerformanceViewElementHolder)convertView.getTag();
        }

        PlayersResultsViewModel.Row resultRow = values.get(position);
        holder.elementOne.setText(resultRow.getYear());
        holder.elementTwo.setText(resultRow.getType());
        holder.elementThree.setText(resultRow.getGames().toString());
        holder.elementFour.setText(resultRow.getAtBat().toString());
        holder.elementFive.setText(resultRow.getHit().toString());
        holder.elementSix.setText(resultRow.getSecondBase().toString());
        holder.elementSeven.setText(resultRow.getThirdBase().toString());
        holder.elementEight.setText(resultRow.getHomeRun().toString());
        holder.elementNine.setText(resultRow.getRunBattedIn().toString());
        holder.elementTen.setText(resultRow.getStrikeOut().toString());
        holder.elementEleven.setText(resultRow.getBaseBall().toString());
        holder.elementTwelve.setText(resultRow.getAverage());

        return convertView;
    }

    static class PerformanceViewElementHolder
    {
        public TextView elementOne;
        public TextView elementTwo;
        public TextView elementThree;
        public TextView elementFour;
        public TextView elementFive;
        public TextView elementSix;
        public TextView elementSeven;
        public TextView elementEight;
        public TextView elementNine;
        public TextView elementTen;
        public TextView elementEleven;
        public TextView elementTwelve;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
