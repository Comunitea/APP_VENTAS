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
package com.cafedered.midban.view.fragments;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.OrderActivity;
import com.cafedered.midban.view.adapter.PartnerHistoryOrderLineListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;

@Fragment(R.layout.fragment_partner_history_order_detail)
public class PartnerHistoryOrderDetailFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_partner_history_order_detail_notes)
    private TextView orderNotes;

    @Wire(view = R.id.fragment_partner_history_order_detail_order_number)
    private TextView orderNumber;

    @Wire(view = R.id.fragment_partner_history_order_detail_order_date)
    private TextView orderDate;

    @Wire(view = R.id.fragment_partner_history_order_detail_delivery_date)
    private TextView orderDeliveryDate;

    @Wire(view = R.id.fragment_partner_history_order_detail_lines)
    private TextView orderLines;

    @Wire(view = R.id.fragment_partner_history_order_detail_amount)
    private TextView orderAmount;

    @Wire(view = R.id.fragment_partner_history_order_detail_margin)
    private TextView orderMargin;

    @Wire(view = R.id.fragment_partner_history_order_detail_address)
    private TextView orderAddress;

    @Wire(view = R.id.fragment_partner_history_order_detail_list_listview)
    ListView list;

    Partner partner;
    Order order;
    List<OrderLine> currentValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        partner = ((Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL));
        if (partner == null)
            partner = ((Partner) MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_INCOME));
        order = (Order) MidbanApplication
                .getValueFromContext(ContextAttributes.ORDER_TO_DETAIL);
        orderNumber.setText(order.getId().toString());

        if (order.getNote() != null && order.getNote().length() > 0)
            orderNotes.setText(order.getNote());
        else
            orderNotes.setVisibility(View.GONE);

        try {
            if (order.getDateOrder() != null)
                orderDate.setText(DateUtil.toFormattedString(
                        DateUtil.parseDate(order.getDateOrder(),
                                "yyyy-MM-dd HH:mm:ss"), "dd.MM.yyyy"));
            else
                orderDate.setText(getResources().getString(
                        R.string.not_available));
        } catch (ParseException e) {
            orderDate.setText(getResources().getString(R.string.not_available));
        }

        // TODO how to get order delivery date?
        orderDeliveryDate.setText(getResources().getString(
                R.string.fragment_partner_history_order_detail_delivery_date)
                + " " + getResources().getString(R.string.not_available));

        if (null != order.getLinesPersisted()) {
            orderLines.setText("" + order.getLinesPersisted().size());
        } else {
            orderLines
                    .setText(getResources().getString(R.string.not_available));
        }

        if (null != order.getAmountTotal()) {
            orderAmount.setText(order.getAmountTotal().toString()
                    + getResources().getString(R.string.currency_symbol));
        } else {
            orderAmount.setText(getResources()
                    .getString(R.string.not_available));
        }
        if (null != order.getMargin()) {
            orderMargin.setText(order.getMargin().toString());
        } else {
            orderMargin.setText(getResources()
                    .getString(R.string.not_available));
        }

        orderAddress.setText(partner.getCompleteAddress());
        loadLinesOrder();
        return rootView;
    }

    @Click(view = R.id.fragment_partner_history_order_detail_send_order_btn)
    public void sendOrderByEmail() {
        try {
            ArrayList<Uri> files = new ArrayList<Uri>();
            files.add(Uri.fromFile(OrderRepository.getInstance().getOrderAsPdf(
                    order)));
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL,
                    new String[] { partner.getEmail() });
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            startActivity(intent);
        } catch (ConfigurationException e) {
            MessagesForUser.showMessage(getView(), R.string.cannot_create_pdf,
                    Toast.LENGTH_LONG, Level.SEVERE);
        } catch (ServiceException e) {
            MessagesForUser.showMessage(getView(), R.string.cannot_create_pdf,
                    Toast.LENGTH_LONG, Level.SEVERE);
        }
    }

    @Click(view = R.id.fragment_partner_history_order_detail_repeat_order_btn)
    public void repeatOrder() {
        order.cloneWithoutIdentifiers(OrderRepository.getCurrentOrder());
        startActivityForResult(
                getNextIntent(new Bundle(), getView(), OrderActivity.class), 0);
    }

    private void loadLinesOrder() {
        new AsyncTask<Void, Void, List<OrderLine>>() {
            @Override
            protected List<OrderLine> doInBackground(Void... params) {
                return order.getLinesPersisted();
            }

            @Override
            protected void onPostExecute(List<OrderLine> result) {
                super.onPostExecute(result);
                final PartnerHistoryOrderLineListItemAdapter adapter = new PartnerHistoryOrderLineListItemAdapter(
                        PartnerHistoryOrderDetailFragment.this, result);
                currentValues = result;
                list.setAdapter(adapter);
            }
        }.execute();
    }
}
