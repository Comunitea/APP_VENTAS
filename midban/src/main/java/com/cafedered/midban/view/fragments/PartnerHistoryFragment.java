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

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.PartnerHistoryListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterPartnerHistoryDialog;

@Fragment(R.layout.fragment_partner_history)
public class PartnerHistoryFragment extends BaseSupportFragment {

    @Wire(view = R.id.nsvScrollView)
    private NestedScrollView nsvScrollView;

    @Wire(view = R.id.fragment_partner_history_list_listview)
    ListView list;
    @Wire(view = R.id.fragment_partner_history_list_search_field)
    EditText searchField;
    List<Order> currentValues;
    Partner partner;
    boolean isFirst = true;
    PartnerHistoryListItemAdapter adapter;
    Order order;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);

        nsvScrollView.setNestedScrollingEnabled(true);

        partner = ((Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL));
        if (partner == null)
            partner = ((Partner) MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_INCOME));
        loadOrders();
        return rootView;
    }

    @TextChanged(view = R.id.fragment_partner_history_list_search_field)
    public void searchOrder() {
        loadOrders();
    }

    @Click(view = R.id.fragment_partner_history_filter_tv)
    public void clickFilters() {
        FilterPartnerHistoryDialog dialog = FilterPartnerHistoryDialog
                .getInstance(getActivity(), this, list, isFirst, partner);
        isFirst = false;
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        wlp.y = 80;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.show();
    }

    private void loadOrders() {
        new AsyncTask<Void, Void, List<Order>>() {
            @Override
            protected List<Order> doInBackground(Void... params) {
                try {
                    order = new Order();
                    if (partner.getIsCompany() == true) {
                        order.setPartnerId(partner.getId());
                    } else {
                        order.setPartnerShippingId(partner.getId());
                    }
                    if (searchField.getText() != null
                            && !searchField.getText().toString().isEmpty()) {
                        try {
                            order.setId(Long.parseLong(searchField.getText()
                                    .toString()));
                        } catch (NumberFormatException e) {
                            return new ArrayList<Order>();
                        }
                    }
                    List<Order> orders = OrderRepository.getInstance()
                            .getByExample(order, Restriction.AND, false, 0, 10);
                    return orders;
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
                return new ArrayList<Order>();
            }

            @Override
            protected void onPostExecute(List<Order> result) {
                super.onPostExecute(result);
                currentValues = (ArrayList<Order>) result;
                adapter = new PartnerHistoryListItemAdapter(
                        PartnerHistoryFragment.this, currentValues);
                list.setAdapter(adapter);
                list.setOnScrollListener(new OrderScrollListener());
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        MidbanApplication.putValueInContext(
                                ContextAttributes.ORDER_TO_DETAIL,
                                (Order) parent.getItemAtPosition(position));
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_partner_detail_frame,
                                        new PartnerHistoryOrderDetailFragment())
                                .commit();
                    }
                });
            }
        }.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.partner_delivery_note_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
        case R.id.home_item:
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            PortadaActivity.class), 0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public class OrderScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView arg0, int arg1) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, final int totalItemCount) {
            if (totalItemCount > 0) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount) {
                    try {
                        currentValues.addAll(OrderRepository.getInstance()
                                .getByExample(order, Restriction.AND, false,
                                        totalItemCount, 5));

                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

}
