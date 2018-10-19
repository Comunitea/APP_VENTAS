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

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.ItemClicked;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.StockPickingOut;
import com.cafedered.midban.service.repositories.StockPickingOutRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.PartnerDeliveryNoteListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterPartnerDeliveryNoteDialog;

@Fragment(R.layout.fragment_partner_deliverynote)
public class PartnerDeliveryNoteFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_partner_deliverynote_list_listview)
    ListView list;
    @Wire(view = R.id.fragment_partner_deliverynote_list_search_field)
    EditText searchField;
    List<StockPickingOut> currentValues;
    Partner partner;
    boolean isFirst = true;
    PartnerDeliveryNoteListItemAdapter adapter;
    StockPickingOut stockPickingOut;

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
        loadStockPickingOut();
        return rootView;
    }

    @TextChanged(view = R.id.fragment_partner_deliverynote_list_search_field)
    public void searchOrder() {
        loadStockPickingOut();
    }

    @ItemClicked(view = R.id.fragment_partner_deliverynote_list_listview)
    public void openDetail(StockPickingOut stockPickingSelected) {
        MidbanApplication.putValueInContext(
                ContextAttributes.STOCK_PICKING_OUT, stockPickingSelected);
        final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(getId(), new PartnerDeliveryNoteDetailFragment(),
                "NewFragmentTag");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Click(view = R.id.fragment_partner_deliverynote_filter_tv)
    public void clickFilters() {
        FilterPartnerDeliveryNoteDialog dialog = FilterPartnerDeliveryNoteDialog
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

    private void loadStockPickingOut() {
        new AsyncTask<Void, Void, List<StockPickingOut>>() {
            @Override
            protected List<StockPickingOut> doInBackground(Void... params) {
                try {
                    stockPickingOut = new StockPickingOut();
                    stockPickingOut.setPartnerId(partner.getId());
                    if (searchField.getText() != null
                            && !searchField.getText().toString().isEmpty()) {
                        stockPickingOut.setOrigin(searchField.getText()
                                .toString());
                    }
                    List<StockPickingOut> result = StockPickingOutRepository
                            .getInstance().getByExample(stockPickingOut,
                                    Restriction.OR, false, 0, 10);
                    List<StockPickingOut> processedResult = new ArrayList<StockPickingOut>();
                    for (StockPickingOut out : result) {
                        if (out.getRelatedOrder() != null)
                            processedResult.add(out);
                    }
                    return processedResult;
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
                return new ArrayList<StockPickingOut>();
            }

            @Override
            protected void onPostExecute(List<StockPickingOut> result) {
                super.onPostExecute(result);
                currentValues = result;
                adapter = new PartnerDeliveryNoteListItemAdapter(
                        PartnerDeliveryNoteFragment.this, currentValues);
                list.setAdapter(adapter);
                list.setOnScrollListener(new StockPickingOutScrollListener());

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

    public class StockPickingOutScrollListener implements
            AbsListView.OnScrollListener {
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
                        List<StockPickingOut> result = StockPickingOutRepository
                                .getInstance().getByExample(stockPickingOut,
                                        Restriction.OR, false, totalItemCount,
                                        5);
                        List<StockPickingOut> processedResult = new ArrayList<StockPickingOut>();
                        for (StockPickingOut out : result) {
                            if (out.getRelatedOrder() != null)
                                processedResult.add(out);
                        }
                        currentValues.addAll(processedResult);

                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
