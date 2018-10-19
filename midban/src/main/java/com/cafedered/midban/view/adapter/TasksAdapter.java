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

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.Task;

public class TasksAdapter extends BaseAdapter {

    private List<Task> tasks;
    private static LayoutInflater inflater = null;

    public TasksAdapter(Context context, List<Task> tasks) {
        Collections.sort(tasks);
        this.tasks = tasks;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return tasks.size();
    }

    public Object getItem(int position) {
        return tasks.get(position);
    }

    public long getItemId(int position) {
        return tasks.get(position).getId();
    }

    public static class ViewHolder {
        public TextView hour;
        public TextView title;
        public TextView partner;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.task_list_item, null);
            holder = new ViewHolder();
            holder.hour = (TextView) vi
                    .findViewById(R.id.task_list_item_init_hour);
            holder.title = (TextView) vi
                    .findViewById(R.id.task_list_item_title);
            holder.partner = (TextView) vi
                    .findViewById(R.id.task_list_item_partner);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final Task task = tasks.get(position);
        holder.title.setText(task.getTitle());
        holder.hour.setText(task.getInitHour());
        holder.partner.setText(task.getPartner().getName() + " - "
                + task.getPartner().getId());
        return vi;
    }

}