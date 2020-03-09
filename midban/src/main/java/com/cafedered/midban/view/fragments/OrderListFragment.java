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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.OrderListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.listeners.ITodayDataChangedListenerFragment;

@Fragment(R.layout.fragment_order_list)
public class OrderListFragment extends BaseSupportFragment implements
        ITodayDataChangedListenerFragment {

    @Wire(view = R.id.fragment_order_num_orders)
    TextView numOrders;
    @Wire(view = R.id.fragment_order_amount_orders)
    TextView amountOrders;
    @Wire(view = R.id.fragment_order_margin_orders)
    TextView marginOrders;
    @Wire(view = R.id.fragment_order_list_listview)
    ListView list;
    List<Order> currentOrders;
    OrderListItemAdapter adapter;
    Order example;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        try {
            if (OrderRepository.getUnsynchronizedOrders().size() > 0) {
                String ordersToSynchronize = "Hay " + OrderRepository.getUnsynchronizedOrders().size() + " pedidos pendientes de sincronizar:\n";
                for (HashMap<String, Object> pendingOrder : OrderRepository.getUnsynchronizedOrders()) {
                    String partnerName = PartnerRepository.getInstance().getById(((Integer)pendingOrder.get("partner_id")).longValue()).getName();
                    ordersToSynchronize += "- " + partnerName + "\n";
                }
                MessagesForUser.showMessage(rootView, ordersToSynchronize, Toast.LENGTH_LONG, Level.WARNING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadListItems(
                MidbanApplication.getValueFromContext(
                        ContextAttributes.TODAY_ACTIVITY_DATE_SELECTED)
                        .toString(), rootView);
        return rootView;
    }

    private void loadListItems(final String date, final View view) {
        new AsyncTask<Void, Void, List<Order>>() {
            @Override
            protected List<Order> doInBackground(Void... params) {
                try {
                    example = new Order();
                    example.setDateOrder(date);
                    List<Order> orders = OrderRepository.getInstance().getByExample(example,
                            Restriction.AND, false, 0, 100);

                    // DAVID - voy a añadir los pedidos no sincronizados
                    if (OrderRepository.getUnsynchronizedOrders().size() > 0) {
                        for (HashMap<String, Object> pendingOrder : OrderRepository.getUnsynchronizedOrders()) {
                            Order order = new Order();
                            if (pendingOrder.get("id") != null) {
                                order.setId((Long) pendingOrder.get("id"));
                            } else {
                                order.setId(Calendar.getInstance().getTimeInMillis());
                            }
                            if (pendingOrder.get("name").equals("/")) {
                                //String partnerName = PartnerRepository.getInstance().getById(((Integer) pendingOrder.get("partner_id")).longValue()).getName();
                                order.setName("(Pendiente)");
                            } else {
                                order.setName((String) pendingOrder.get("name"));
                            }
                            order.setState((String) pendingOrder.get("state"));
                            order.setDateOrder((String) pendingOrder.get("date_order"));
                            order.setCreateDate((String) pendingOrder.get("create_date"));
                            order.setAmountTax((Number) pendingOrder.get("amount_tax"));
                            order.setAmountTotal((Number) pendingOrder.get("amount_total"));
                            order.setAmountUntaxed((Number) pendingOrder.get("amount_untaxed"));
                            order.setPartnerId((Number) pendingOrder.get("partner_id"));
                            orders.add(order);
                        }
                    }

                    Iterator<Order> itOrderes = orders.iterator();
                    while(itOrderes.hasNext()) {
                        if (itOrderes.next().getName().equals("/"))
                            itOrderes.remove();
                    }
                    return orders;
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    return new ArrayList<Order>();
                }
            }

            @Override
            protected void onPostExecute(List<Order> result) {
                super.onPostExecute(result);
                currentOrders = result;
                adapter = new OrderListItemAdapter(OrderListFragment.this,
                        currentOrders);
                list.setAdapter(adapter);
                numOrders.setText(view.getResources().getString(
                        R.string.fragment_order_list_num_orders)
                        + " " + result.size());
                float amount = 0.0F;
                float margin = 0.0F;
                for (Order order : result) {
                    amount += order.getAmountTotal() != null ? order
                            .getAmountTotal().floatValue() : 0.0F;
                    margin += order.getMargin() != null ? order.getMargin()
                            .floatValue() : 0.0F;
                }
                amountOrders.setText(view.getResources().getString(
                        R.string.fragment_order_list_amount_orders)
                        + " "
                        + new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).toString()
                        + " " + view.getResources().getString(
                                R.string.currency_symbol));
                marginOrders.setText(view.getResources().getString(
                        R.string.fragment_order_list_margin_orders)
                        + " "
                        + new BigDecimal(margin).setScale(2, RoundingMode.HALF_UP).toString()
                        + " " + view.getResources().getString(
                                R.string.currency_symbol));
            }
        }.execute();
    }

    @Override
    public void notifyDataChangedListener(String newDate) {
        loadListItems(newDate, getView());
    }
}
