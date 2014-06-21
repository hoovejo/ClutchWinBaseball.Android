package com.clutchwin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clutchwin.R;
import com.clutchwin.viewmodels.TeamsResultsViewModel;

import java.util.List;

public class TeamsResultsAdapter extends BaseAdapter {

    private final Context context;
    private int layoutResourceId;
    private List<TeamsResultsViewModel.TeamsResult> values;

    public TeamsResultsAdapter(Context context, int layoutResourceId, List<TeamsResultsViewModel.TeamsResult> values) {
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
            if (convertView != null) {
                holder.elementOne = (TextView)convertView.findViewById(R.id.textYear);
                holder.elementTwo = (TextView)convertView.findViewById(R.id.textGames);
                holder.elementThree = (TextView)convertView.findViewById(R.id.textTeam);
                holder.elementFour = (TextView)convertView.findViewById(R.id.textOpponent);
                holder.elementFive = (TextView)convertView.findViewById(R.id.textWins);
                holder.elementSix = (TextView)convertView.findViewById(R.id.textLosses);
                holder.elementSeven = (TextView)convertView.findViewById(R.id.textRunsFor);
                holder.elementEight = (TextView)convertView.findViewById(R.id.textRunsAgainst);
                convertView.setTag(holder);
            }
        }
        else
        {
            holder = (PerformanceViewElementHolder)convertView.getTag();
        }

        TeamsResultsViewModel.TeamsResult resultRow = values.get(position);
        holder.elementOne.setText(resultRow.getYear());
        holder.elementTwo.setText(resultRow.getGames().toString());
        holder.elementThree.setText(resultRow.getTeam());
        holder.elementFour.setText(resultRow.getOpponent());
        holder.elementFive.setText(resultRow.getWins().toString());
        holder.elementSix.setText(resultRow.getLosses().toString());
        holder.elementSeven.setText(resultRow.getRunsFor().toString());
        holder.elementEight.setText(resultRow.getRunsAgainst().toString());

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
