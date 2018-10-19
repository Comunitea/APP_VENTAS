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
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.view.base.BaseSupportFragment;

public class InvoiceListItemAdapter extends BaseAdapter {

    private List<Invoice> invoices;
    private static LayoutInflater inflater = null;
    private List<Invoice> selectedInvoices = new ArrayList<Invoice>();

    public InvoiceListItemAdapter(BaseSupportFragment fragment, List<Invoice> invoices) {
        this.invoices = invoices;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return invoices.size();
    }

    public Object getItem(int position) {
        return invoices.get(position);
    }

    public long getItemId(int position) {
        return invoices.get(position).getId();
    }

    public static class ViewHolder {
        public CheckBox check;
        public TextView code;
        public TextView date;
        public TextView amount;
        public TextView state;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.invoice_list_item, null);
            holder = new ViewHolder();
            holder.check = (CheckBox) vi
                    .findViewById(R.id.invoice_list_item_check);
            holder.code = (TextView) vi
                    .findViewById(R.id.invoice_list_item_code);
            holder.date = (TextView) vi
                    .findViewById(R.id.invoice_list_item_date);
            holder.amount = (TextView) vi
                    .findViewById(R.id.invoice_list_item_amount);
            holder.state = (TextView) vi
                    .findViewById(R.id.invoice_list_item_state);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final Invoice invoice = invoices.get(position);
        if (invoice.getNumber() != null)
            holder.code.setText(invoice.getNumber().toString());
        try {
            if (invoice.getDateInvoice() != null)
                holder.date.setText(DateUtil.toFormattedString(DateUtil
                        .parseDate(
                    invoice.getDateInvoice(), "yyyy-MM-dd HH:mm:ss"),
                    "dd.MM.yyyy"));
            else
                holder.date.setText("");
        } catch (ParseException e) {
            holder.date.setText("");
        }
        holder.amount.setText(new BigDecimal(invoice.getResidual()
                .floatValue()).setScale(2, RoundingMode.HALF_UP).toString()
                + holder.amount.getResources().getString(
                        R.string.currency_symbol));
        holder.state.setText(invoice.getState());
        holder.check
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        if (isChecked)
                            selectedInvoices.add(invoice);
                        else
                            selectedInvoices.remove(invoice);
                    }
                });
        return vi;
    }

    public List<Invoice> getSelectedInvoices() {
        return selectedInvoices;
    }

}