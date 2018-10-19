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

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.AccountMoveLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Voucher;
import com.cafedered.midban.service.repositories.AccountRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.base.BaseSupportFragment;

public class IncomeListItemAdapter extends BaseAdapter {

    private List<AccountMoveLine> incomes;
    private static LayoutInflater inflater = null;

    public IncomeListItemAdapter(BaseSupportFragment fragment, List<AccountMoveLine> incomes) {
        this.incomes = incomes;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return incomes.size();
    }

    public Object getItem(int position) {
        return incomes.get(position);
    }

    public long getItemId(int position) {
        return incomes.get(position).getId();
    }

    public static class ViewHolder {
        public TextView partnerCode;
        public TextView partnerName;
        public TextView code;
        public TextView paymentMethod;
        public TextView amount;
        public TextView state;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.income_list_item, null);
            holder = new ViewHolder();
            holder.partnerCode = (TextView) vi
                    .findViewById(R.id.income_list_item_partner_code);
            holder.partnerName = (TextView) vi
                    .findViewById(R.id.income_list_item_partner_name);
            holder.code = (TextView) vi
                    .findViewById(R.id.income_list_item_code);
            holder.paymentMethod = (TextView) vi
                    .findViewById(R.id.income_list_item_payment_method);
            holder.amount = (TextView) vi
                    .findViewById(R.id.income_list_item_amount);
            holder.state = (TextView) vi
                    .findViewById(R.id.income_list_item_state);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final AccountMoveLine voucher = incomes.get(position);
        holder.code.setText(voucher.getRef() != null ? voucher.getRef() : "");
        holder.amount.setText(voucher.getPaymentMadeValue().floatValue()
                + holder.amount.getResources().getString(
                        R.string.currency_symbol));
        holder.state.setText("");
        try {
            if (voucher.getAccountId() != null && AccountRepository.getInstance().getById(voucher.getAccountId().longValue()).getType().contains("bank"))
                holder.paymentMethod.setText("Talón");
            else
                holder.paymentMethod.setText("Efectivo");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        try {
        Partner partner = PartnerRepository.getInstance().getById(voucher.getPartnerId().longValue());
            holder.partnerCode.setText(partner.getRef());
            holder.partnerName.setText(partner.getName());
        } catch (NullPointerException e) {
            // do nothing... it is better to catch this than instantiate new
            // partner for comparison
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return vi;
    }
}