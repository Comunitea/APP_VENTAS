/*******************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 *     Copyright (C) 2014  CafedeRed
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.cafedered.midban.view.adapter;

import java.text.ParseException;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.SynchronizationSummary;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.view.fragments.SynchronizationFragment;

public class SynchronizationListItemAdapter extends BaseAdapter {

    private List<SynchronizationSummary> synchronizations;
    private static LayoutInflater inflater = null;

    public SynchronizationListItemAdapter(SynchronizationFragment fragment,
            List<SynchronizationSummary> synchronizations) {
        this.synchronizations = synchronizations;
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return synchronizations.size();
    }

    public Object getItem(int position) {
        return synchronizations.get(position);
    }

    public long getItemId(int position) {
        return synchronizations.get(position).getId();
    }

    public static class ViewHolder {
        public TextView number;
        public TextView result;
        public TextView date;
        public TextView time;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.synchronization_list_item, null);
            holder = new ViewHolder();
            holder.number = (TextView) vi
                    .findViewById(R.id.synchronization_list_item_number);
            holder.result = (TextView) vi
                    .findViewById(R.id.synchronization_list_item_result);
            holder.date = (TextView) vi
                    .findViewById(R.id.synchronization_list_item_date);
            holder.time = (TextView) vi
                    .findViewById(R.id.synchronization_list_item_time);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final SynchronizationSummary synchronization = synchronizations
                .get(position);
        holder.result.setText(synchronization.getResult());
        holder.number.setText("" + (position + 1));
        try {
            holder.date
                    .setText(DateUtil.toFormattedString(DateUtil.parseDate(
                            synchronization.getDate(), "yyyyMMddHHmmss"),
                    "dd.MM.yyyy HH:mm"));
        } catch (ParseException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        long minutes = synchronization.getTimeInSeconds().longValue() / 60;
        long seconds = synchronization.getTimeInSeconds().longValue() % 60;
        holder.time.setText(minutes + "m " + seconds + "s");
        return vi;
    }
}
