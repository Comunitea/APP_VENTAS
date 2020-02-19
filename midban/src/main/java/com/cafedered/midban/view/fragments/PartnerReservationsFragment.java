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
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.async.CancelAsyncTaskListener;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.ProductCatalogItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterProductReservationsDialog;
import com.cafedered.midban.view.dialogs.ProductToReservationDialog;

@Fragment(R.layout.fragment_partner_reservations_list_product)
public class PartnerReservationsFragment extends BaseSupportFragment implements CancelAsyncTaskListener{

    @Wire(view = R.id.fragment_partner_reservations_list_product_listview)
    ListView list;
    @Wire(view = R.id.fragment_partner_reservations_list_product_search_field)
    EditText searchField;
    private boolean isFirst = true;
    ProductCatalogItemAdapter adapter;
    List<Product> currentProducts;
    boolean ordenarPorCategoria = true;
    boolean ordenarAlfabeticamente = false;
    Menu menu;
    LinearLayout footerView;
    Long lastTimeSearch = 0L;
    SearchTask task;

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
        footerView = (LinearLayout) rootView.findViewById(R.id.list_footer);
        ImageView imgWheel = (ImageView) rootView.findViewById(R.id.img_animated_wheel);
        AnimationDrawable frameAnimation = (AnimationDrawable) imgWheel.getDrawable();
        frameAnimation.start();
        imgWheel.setImageDrawable(frameAnimation);
        final PartnerReservationsFragment fragment = this;
        new AsyncTask<Void, Void, List<Product>>() {
            @Override
            protected List<Product> doInBackground(Void... params) {
                try {
                    if (MidbanApplication
                            .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL) != null) {
                        Partner partner = (Partner) MidbanApplication
                                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
                        return ProductRepository.getInstance()
                                .getAllForPartner(partner.getId(), 0, 10, ordenarPorCategoria, ordenarAlfabeticamente);
                    }
                    if (MidbanApplication
                            .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION) != null) {
                        Partner partner = (Partner) MidbanApplication
                                .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION);
                        return ProductRepository.getInstance()
                                .getAllForPartner(partner.getId(), 0, 10, ordenarPorCategoria, ordenarAlfabeticamente);
                    }
                    if (MidbanApplication
                            .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER) != null) {
                        Partner partner = (Partner) MidbanApplication
                                .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
                        return ProductRepository.getInstance()
                                .getAllForPartner(partner.getId(), 0, 10, ordenarPorCategoria, ordenarAlfabeticamente);
                    }
                    return ProductRepository.getInstance().getAll(0, 10, ordenarPorCategoria, ordenarAlfabeticamente, MidbanApplication.priceListIdActualCompany());
                } catch (ConfigurationException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
                return new ArrayList<Product>();
            }

            @Override
            protected void onPostExecute(List<Product> result) {
                super.onPostExecute(result);
                currentProducts = result;
                adapter = new ProductCatalogItemAdapter(fragment,
                        currentProducts);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        ProductToReservationDialog dialog = new ProductToReservationDialog(
                                getActivity(), (Product) adapter
                                        .getItem(position));
                        dialog.show();
                    }
                });
                list.setOnScrollListener(new PartnerReservationsScrollListener());
            }
        }.execute();
        return rootView;
    }

    @TextChanged(view = R.id.fragment_product_catalog_search_field)
    public void onSearchTextChanged() {
        if (task != null)
            task.cancel(true);
        task = new SearchTask();
        task.execute();
    }

    @Override
    public void cancelAllAsyncs() {
        if (task != null)
            task.cancel(true);
    }

    class SearchTask extends AsyncTask<Void, Boolean, Boolean> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(PartnerReservationsFragment.this.getActivity(), getResources().getString(R.string.loading),
                    getResources().getString(R.string.loading), true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            lastTimeSearch = new Date().getTime();
            Long copy = lastTimeSearch;
            boolean result = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (copy == lastTimeSearch) {
                result = true;
                Product productSearch = new Product();
                if (searchField.getText().length() > 1) {
                    productSearch.setNameTemplate(searchField.getText().toString());
                    productSearch.setCode(searchField.getText().toString());
                    try {
                        productSearch.setId(Long
                                .parseLong(searchField.getText().toString()));
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                }
                currentProducts.clear();
                try {
                    if (LoggerUtil.isDebugEnabled())
                        Log.d(ProductCatalogFragment.class.getName(), "Búsqueda: " + ordenarAlfabeticamente + ", categoria: " + ordenarPorCategoria);
                    currentProducts.addAll(ProductRepository.getInstance().getByExample(
                            productSearch, Restriction.OR, false, 10, 0, ordenarPorCategoria, ordenarAlfabeticamente, MidbanApplication.priceListIdActualCompany()));
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                adapter.notifyDataSetChanged();
                list.setOnScrollListener(new PartnerReservationsScrollListener());
            }
            progress.dismiss();
        }
    }

    @Click(view = R.id.fragment_partner_reservations_list_product_filter_tv)
    public void onFiltersClicked() {
        FilterProductReservationsDialog dialog = FilterProductReservationsDialog
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_catalog_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.category_order:
                this.ordenarPorCategoria = true;
                this.ordenarAlfabeticamente = false;
                menu.getItem(1).getSubMenu().getItem(1).setIcon(null);
                item.setIcon(R.drawable.ic_action_accept_holo_dark);
                onSearchTextChanged();
                return true;
            case R.id.alphabetical_order:
                this.ordenarPorCategoria = false;
                this.ordenarAlfabeticamente = true;
                menu.getItem(1).getSubMenu().getItem(0).setIcon(null);
                item.setIcon(R.drawable.ic_action_accept_holo_dark);
                onSearchTextChanged();
                return true;
            case R.id.home_item:
                startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            PortadaActivity.class), 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PartnerReservationsScrollListener implements
            AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView arg0, int arg1) {

        }

        private int previousTotalItemCount = 0;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, final int totalItemCount) {
            if (totalItemCount > 0 && totalItemCount > previousTotalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen >= totalItemCount - 15 && totalItemCount > lastInScreen) {
                    new AsyncTask<Integer, Void, Void>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            footerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected Void doInBackground(Integer... params) {
                            if (previousTotalItemCount < params[0]) {
                                previousTotalItemCount = params[0];
                                footerView.setVisibility(View.VISIBLE);
                                try {
                                    if (MidbanApplication
                                            .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL) != null) {
                                        Partner partner = (Partner) MidbanApplication
                                                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
                                        currentProducts
                                                .addAll(ProductRepository.getInstance()
                                                        .getAllForPartner(partner.getId(),
                                                                params[0], 5, ordenarPorCategoria, ordenarAlfabeticamente));
                                    } else if (MidbanApplication
                                            .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION) != null) {
                                        Partner partner = (Partner) MidbanApplication
                                                .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION);
                                        currentProducts
                                                .addAll(ProductRepository.getInstance()
                                                        .getAllForPartner(partner.getId(),
                                                                params[0], 5, ordenarPorCategoria, ordenarAlfabeticamente));
                                    } else if (MidbanApplication
                                            .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER) != null) {
                                        Partner partner = (Partner) MidbanApplication
                                                .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
                                        currentProducts
                                                .addAll(ProductRepository.getInstance()
                                                        .getAllForPartner(partner.getId(),
                                                                params[0], 5, ordenarPorCategoria, ordenarAlfabeticamente));
                                    } else
                                        currentProducts.addAll(ProductRepository
                                                .getInstance().getAll(params[0], 5));

                                } catch (ServiceException e) {
                                    e.printStackTrace();
                                } catch (ConfigurationException e) {
                                    e.printStackTrace();
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            footerView.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    }.execute(totalItemCount);

                }
            }
        }
    }
}
