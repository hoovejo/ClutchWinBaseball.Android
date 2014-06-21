package com.clutchwin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clutchwin.R;
import com.clutchwin.viewmodels.TeamsDrillDownViewModel;

import java.util.List;

public class TeamsDrillDownAdapter extends BaseAdapter {

    private final Context context;
    private int layoutResourceId;
    private List<TeamsDrillDownViewModel.TeamsDrillDown> values;

    public TeamsDrillDownAdapter(Context context, int layoutResourceId, List<TeamsDrillDownViewModel.TeamsDrillDown> values) {
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
                holder.elementOne = (TextView)convertView.findViewById(R.id.textGameDate);
                holder.elementTwo = (TextView)convertView.findViewById(R.id.textTeam);
                holder.elementThree = (TextView)convertView.findViewById(R.id.textOpponent);
                holder.elementFour = (TextView)convertView.findViewById(R.id.textWin);
                holder.elementFive = (TextView)convertView.findViewById(R.id.textLoss);
                holder.elementSix = (TextView)convertView.findViewById(R.id.textRunsFor);
                holder.elementSeven = (TextView)convertView.findViewById(R.id.textRunsAgainst);
                convertView.setTag(holder);
            }
        }
        else
        {
            holder = (PerformanceViewElementHolder)convertView.getTag();
        }

        TeamsDrillDownViewModel.TeamsDrillDown resultRow = values.get(position);
        holder.elementOne.setText(resultRow.getGameDate());
        holder.elementTwo.setText(resultRow.getTeam());
        holder.elementThree.setText(resultRow.getOpponent());
        holder.elementFour.setText(resultRow.getWin().toString());
        holder.elementFive.setText(resultRow.getLoss().toString());
        holder.elementSix.setText(resultRow.getRunsFor().toString());
        holder.elementSeven.setText(resultRow.getRunsAgainst().toString());

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
