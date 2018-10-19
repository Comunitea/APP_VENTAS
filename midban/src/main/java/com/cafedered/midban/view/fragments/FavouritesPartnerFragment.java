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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.decorators.PartnerFavouritesDecorator;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.activities.ProductCardActivity;
import com.cafedered.midban.view.activities.ProductCatalogActivity;
import com.cafedered.midban.view.adapter.PartnerFavouriteItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterPartnerFavouritesDialog;

@Fragment(R.layout.fragment_partner_favourites)
public class FavouritesPartnerFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_partner_favourites_listview)
    ListView list;
    @Wire(view = R.id.fragment_partner_favourites_search_field)
    EditText searchField;
    @Wire(view = R.id.fragment_partner_favourites_last_order_selector)
    private TextView tabLastOrder;
    @Wire(view = R.id.fragment_partner_favourites_last_30_days_selector)
    private TextView tabLastMonth;
    @Wire(view = R.id.fragment_partner_favourites_last_year_selector)
    private TextView tabLastYear;
    private boolean isFirst = true;
    private Partner partner;
    private OrderRepository.DateFilters currentDateFilter;

    public Partner getPartner() {
        return partner;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        partner = (Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
        if (partner == null)
            partner = (Partner) MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
        final FavouritesPartnerFragment fragment = this;
        currentDateFilter = OrderRepository.DateFilters.LAST_ORDER;
        new AsyncTask<Void, Void, List<PartnerFavouritesDecorator>>() {
            @Override
            protected List<PartnerFavouritesDecorator> doInBackground(
                    Void... params) {
                return OrderRepository.getInstance()
                        .getProductsOfOrdersForPartner(partner.getId(),
                                OrderRepository.DateFilters.LAST_ORDER, "", "",
                                null);
            }

            @Override
            protected void onPostExecute(List<PartnerFavouritesDecorator> result) {
                super.onPostExecute(result);
                Collections.sort(result);
                final PartnerFavouriteItemAdapter adapter = new PartnerFavouriteItemAdapter(
                        fragment, result, partner.getId());
                list.setAdapter(adapter);
                list.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        MidbanApplication.putValueInContext(
                                ContextAttributes.PRODUCT_TO_DETAIL,
                                (PartnerFavouritesDecorator) adapter
                                        .getItem(position));
                        startActivityForResult(
                                getNextIntent(new Bundle(), view,
                                        ProductCardActivity.class), 0);
                    }
                });
            }
        }.execute();
        return rootView;
    }

    @TextChanged(view = R.id.fragment_partner_favourites_search_field)
    public void onSearchTextChanged() throws ServiceException {
        Product productSearch = new Product();
        productSearch.setNameTemplate(searchField.getText().toString());
        productSearch.setCode(searchField.getText().toString());
        try {
            productSearch.setId(Long
                    .parseLong(searchField.getText().toString()));
        } catch (NumberFormatException e) {
            // do nothing
        }
        List<PartnerFavouritesDecorator> products = OrderRepository
                .getInstance()
                .getProductsOfOrdersForPartner(partner.getId(),
                        currentDateFilter, productSearch.getNameTemplate(),
                        productSearch.getCode(), productSearch.getId());
        Collections.sort(products);
        list.setAdapter(new PartnerFavouriteItemAdapter(this, products, partner
                .getId()));
    }

    @Click(view = R.id.fragment_partner_favourites_filter_tv)
    public void onFiltersClicked() {
        FilterPartnerFavouritesDialog dialog = FilterPartnerFavouritesDialog
                .getInstance(getActivity(), this, list, isFirst);
        isFirst = false;
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        wlp.y = 80;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.show();
    }

    @Click(views = { R.id.fragment_partner_favourites_last_order_selector,
            R.id.fragment_partner_favourites_last_30_days_selector,
            R.id.fragment_partner_favourites_last_year_selector })
    public void onClickTab(View v) {
        switch (Integer.parseInt(v.getTag().toString())) {
        case 1: {
            tabLastMonth.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabLastYear.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabLastOrder.setBackgroundColor(getResources().getColor(
                    R.color.midban_darker_grey));
            currentDateFilter = OrderRepository.DateFilters.LAST_ORDER;
            break;
        }
        case 2: {
            tabLastMonth.setBackgroundColor(getResources().getColor(
                    R.color.midban_darker_grey));
            tabLastYear.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabLastOrder.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            currentDateFilter = OrderRepository.DateFilters.LAST_30_DAYS;
            break;
        }
        case 3: {
            tabLastMonth.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabLastYear.setBackgroundColor(getResources().getColor(
                    R.color.midban_darker_grey));
            tabLastOrder.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            currentDateFilter = OrderRepository.DateFilters.LAST_YEAR;
            break;
        }
        }
        reloadList();
    }

    @Click(view = R.id.fragment_partner_favourites_catalog_btn)
    public void onCatalogButtonPressed() {
        startActivityForResult(
                getNextIntent(new Bundle(), getView(),
                        ProductCatalogActivity.class), 0);
    }

    @Click(view = R.id.fragment_favourites_partner_cancel_btn)
    public void onCancelButtonPressed() {
        OrderRepository.clearCurrentOrder();
        getActivity().finish();
    }

    @Click(view = R.id.fragment_partner_favourites_add_cart)
    public void onAddButtonPressed() {
        Set<OrderLine> lines = ((PartnerFavouriteItemAdapter) list.getAdapter())
                .getLinesSelected();
        if (lines.size() > 0) {
            for (OrderLine line : lines) {
                OrderRepository.getCurrentOrder().getLines().add(line);
            }
            OrderRepository.getCurrentOrder().setPartnerId(partner.getId());
            MessagesForUser.showMessage(getActivity(),
                    R.string.fragment_partner_favourites_added_to_cart,
                    Toast.LENGTH_LONG, Level.INFO);
        } else {
            MessagesForUser
                    .showMessage(
                            getActivity(),
                            R.string.fragment_partner_favourites_at_least_one_product_selected,
                            Toast.LENGTH_LONG, Level.WARNING);
        }
    }

    private void reloadList() {
        try {
            onSearchTextChanged();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.partner_favourites_menu, menu);
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
}
