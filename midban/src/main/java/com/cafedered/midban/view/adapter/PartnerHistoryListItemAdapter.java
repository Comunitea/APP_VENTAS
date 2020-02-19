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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.fragments.PartnerHistoryOrderDetailFragment;

public class PartnerHistoryListItemAdapter extends BaseAdapter {

    private List<Order> orders;
    private static LayoutInflater inflater = null;
    private static android.support.v4.app.FragmentManager fragmentManager = null;

    public PartnerHistoryListItemAdapter(BaseSupportFragment fragment, List<Order> orders) {
        this.orders = orders;
        fragmentManager = fragment.getFragmentManager();
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return orders.size();
    }

    public Object getItem(int position) {
        return orders.get(position);
    }

    public long getItemId(int position) {
        return orders.get(position).getId();
    }

    public static class ViewHolder {
        public TextView orderCode;
        public TextView date;
        public TextView amount;
        public TextView lines;
        public TextView state;
        public CheckBox invoiced;
        public CheckBox shipped;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.partner_history_list_item, null);
            holder = new ViewHolder();
            holder.orderCode = (TextView) vi
                    .findViewById(R.id.partner_history_list_item_order_code);
            holder.date = (TextView) vi
                    .findViewById(R.id.partner_history_list_item_date);
            holder.amount = (TextView) vi
                    .findViewById(R.id.partner_history_list_item_amount);
            holder.lines = (TextView) vi
                    .findViewById(R.id.partner_history_list_item_lines);
            holder.state = (TextView) vi
                    .findViewById(R.id.partner_history_list_item_state);
            holder.invoiced = (CheckBox) vi
                    .findViewById(R.id.partner_history_list_item_invoiced);
            holder.shipped = (CheckBox) vi
                    .findViewById(R.id.partner_history_list_item_shipped);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final Order order = orders.get(position);
        holder.orderCode.setText(order.getName().toString());
        if ("".equals(holder.orderCode.getText())) {
            holder.orderCode.setText(order.getId().toString());
        }
        try {
            if (order.getDateOrder() != null)
                holder.date
                    .setText(DateUtil.toFormattedString(DateUtil.parseDate(
                            order.getDateOrder(), "yyyy-MM-dd HH:mm:ss"),
                            "dd.MM.yyyy"));
            else
                holder.date.setText("");
        } catch (ParseException e) {
            holder.date.setText("");
        }
        holder.amount.setText(new BigDecimal(order.getAmountTotal()
                .doubleValue()).setScale(2, RoundingMode.HALF_UP).toString()
                + holder.amount.getResources().getString(
                        R.string.currency_symbol));
        holder.state.setText(order.getState());
        holder.invoiced.setChecked(order.isInvoiced());
        holder.shipped.setChecked(order.isShipped());
        holder.lines.setText(order.getLinesPersisted().size() + "");

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MidbanApplication.putValueInContext(
                        ContextAttributes.ORDER_TO_DETAIL,
                        order);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_partner_detail_frame,
                                new PartnerHistoryOrderDetailFragment())
                        .commit();
            }
        };

        holder.orderCode.setOnClickListener(click);
        holder.invoiced.setOnClickListener(click);
        holder.shipped.setOnClickListener(click);
        holder.amount.setOnClickListener(click);
        holder.state.setOnClickListener(click);
        holder.date.setOnClickListener(click);
        holder.lines.setOnClickListener(click);

        return vi;
    }
}
