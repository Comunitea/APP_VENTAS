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

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.StockPickingOut;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.view.base.BaseSupportFragment;

public class PartnerDeliveryNoteListItemAdapter extends BaseAdapter {

    private List<StockPickingOut> stocksPickingOut;
    private static LayoutInflater inflater = null;

    public PartnerDeliveryNoteListItemAdapter(BaseSupportFragment fragment,
            List<StockPickingOut> stocksPickingOut) {
        this.stocksPickingOut = stocksPickingOut;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return stocksPickingOut.size();
    }

    public Object getItem(int position) {
        return stocksPickingOut.get(position);
    }

    public long getItemId(int position) {
        return stocksPickingOut.get(position).getId();
    }

    public static class ViewHolder {
        public TextView number;
        public TextView date;
        public TextView lines;
        public TextView state;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater
                    .inflate(R.layout.partner_deliverynote_list_item, null);
            holder = new ViewHolder();
            holder.number = (TextView) vi
                    .findViewById(R.id.partner_deliverynote_list_item_number);
            holder.date = (TextView) vi
                    .findViewById(R.id.partner_deliverynote_list_item_date);
            holder.lines = (TextView) vi
                    .findViewById(R.id.partner_deliverynote_list_item_lines);
            holder.state = (TextView) vi
                    .findViewById(R.id.partner_deliverynote_list_item_state);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final StockPickingOut stockPickingOut = stocksPickingOut.get(position);
        holder.number.setText(stockPickingOut.getOrigin());
        try {
            if (stockPickingOut.getDate() != null)
                holder.date.setText(DateUtil.toFormattedString(DateUtil
                        .parseDate(stockPickingOut.getDate(),
                                "yyyy-MM-dd HH:mm:ss"), "dd.MM.yyyy"));
            else
                holder.date.setText("");
        } catch (ParseException e) {
            holder.date.setText("");
        }
        // TODO state of the order or state of the delivery note???
        holder.state.setText(stockPickingOut.getRelatedOrder().getState());
        holder.lines.setText(stockPickingOut.getLines().size() + "");
        return vi;
    }
}
