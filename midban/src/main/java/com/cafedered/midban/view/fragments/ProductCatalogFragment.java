/**
 * ****************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 * Copyright (C) 2014  CafedeRed
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * *****************************************************************************
 */
package com.cafedered.midban.view.fragments;

import java.util.ArrayList;
import java.util.List;

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
import com.cafedered.midban.entities.Company;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.service.repositories.CompanyRepository;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.ProductCatalogItemAdapter;
import com.cafedered.midban.view.adapter.ProductOrderItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterProductCatalogDialog;

@Fragment(R.layout.fragment_product_catalog)
public class ProductCatalogFragment extends BaseSupportFragment implements CancelAsyncTaskListener {

    @Wire(view = R.id.fragment_product_catalog_listview)
    ListView list;
    @Wire(view = R.id.fragment_product_catalog_search_field)
    EditText searchField;
    private boolean isFirst = true;
    ProductCatalogItemAdapter adapter;
    List<Product> currentProducts = new ArrayList<Product>();
    List<Product> allProducts;
    Long lastTimeSearch = 0L;
    boolean ordenarPorCategoria = true;
    boolean ordenarAlfabeticamente = false;
    Menu menu;
    LinearLayout footerView;
    SearchTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private String priceListToFilter(){
        String priceListId = "";

        if (OrderRepository.getInstance().isOrderInitialized()
                && OrderRepository.getCurrentOrder().getPartnerId() != null){
            priceListId = (String) MidbanApplication.getValueFromContext(ContextAttributes.ACTUAL_TARIFF);
            //priceListId = OrderRepository.getCurrentOrder().getPartner().getPricelistId().toString();
        }
        if ((priceListId == null) || ("".equals(priceListId))){
            if (!MidbanApplication.priceListIdActualCompany().equals("")) {
                priceListId = MidbanApplication.priceListIdActualCompany();
            }
        }
        return priceListId;
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

        final ProductCatalogFragment fragment = this;

        try {
            if (MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL) != null) {
                Partner partner = (Partner) MidbanApplication
                        .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
                allProducts =  ProductRepository.getInstance()
                        .getAllForPartner(partner.getId(), 0, 15, ordenarPorCategoria, ordenarAlfabeticamente);
            }
            if (MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION) != null) {
                Partner partner = (Partner) MidbanApplication
                        .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION);
                allProducts =  ProductRepository.getInstance()
                        .getAllForPartner(partner.getId(), 0, 15, ordenarPorCategoria, ordenarAlfabeticamente);
            }
            if (MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER) != null) {
                Partner partner = (Partner) MidbanApplication
                        .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
                allProducts = ProductRepository.getInstance()
                        .getAllForPartner(partner.getId(), 0, 15, ordenarPorCategoria, ordenarAlfabeticamente);
            }
            allProducts =  ProductRepository.getInstance().getAll(0, 15, ordenarPorCategoria, ordenarAlfabeticamente, priceListToFilter());

        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        if (allProducts == null)
            allProducts = new ArrayList<Product>();
        currentProducts.addAll(allProducts);
        adapter = new ProductCatalogItemAdapter(fragment,
                currentProducts);
//        try {
//            allProducts = ProductRepository.getInstance().getByExample(new Product(), Restriction.OR, false, 20, 0, true, false);
//        } catch (ServiceException e) {
//            e.printStackTrace();
//        }
        adapter = new ProductCatalogItemAdapter(this, currentProducts);
        list.setAdapter(adapter);
        list.setOnScrollListener(new InfiniteScrollListener(15) {
            @Override
            public void loadMore(final int page, final int totalItemsCount) {
                new AsyncTask<Void, List<Product>, List<Product>>() {
                    @Override
                    protected List<Product> doInBackground(Void... params) {
                        try {
                            return ProductRepository.getInstance().getByExample(new Product(), Restriction.OR, false, 15, (page - 1) * 15, true, false, priceListToFilter());
                        } catch (ServiceException e) {
                            e.printStackTrace();
                            return new ArrayList<Product>();
                        }
                    }

                    @Override
                    protected void onPostExecute(List<Product> products) {
                        super.onPostExecute(products);
                        currentProducts.addAll(products);
                        adapter.notifyDataSetChanged();
                    }
                }.execute();

            }
        });
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            footerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            if (!isCancelled()) {
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
                            productSearch, Restriction.OR, false, 15, 0, ordenarPorCategoria, ordenarAlfabeticamente, priceListToFilter()));
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            footerView.setVisibility(View.GONE);
            if (result && !isCancelled()) {
                adapter.notifyDataSetChanged();
                list.setOnScrollListener(new InfiniteScrollListener(15) {
                    @Override
                    public void loadMore(final int page, final int totalItemsCount) {
                        new AsyncTask<Void, List<Product>, List<Product>>() {
                            @Override
                            protected List<Product> doInBackground(Void... params) {
                                List<Product> result = new ArrayList<Product>();
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
                                try {
                                    result = ProductRepository.getInstance().getByExample(
                                            productSearch, Restriction.OR, false, 15, (page - 1) * 15, ordenarPorCategoria, ordenarAlfabeticamente, priceListToFilter());
                                } catch (ServiceException e) {
                                    e.printStackTrace();
                                }
                                return result;
                            }

                            @Override
                            protected void onPostExecute(List<Product> products) {
                                super.onPostExecute(products);
                                currentProducts.addAll(products);
                                adapter.notifyDataSetChanged();
                            }
                        }.execute();

                    }
                });
            }
        }
    }

    @Click(view = R.id.fragment_product_catalog_filter_tv)
    public void onFiltersClicked() {
        FilterProductCatalogDialog dialog = FilterProductCatalogDialog
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
        this.menu = menu;
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

//    public class ProductCatalogScrollListener implements
//            AbsListView.OnScrollListener {
//
//        @Override
//        public void onScrollStateChanged(AbsListView arg0, int arg1) {
//
//        }
//
//        private int previousTotalItemCount = 0;
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem,
//                             int visibleItemCount, final int totalItemCount) {
//            if (totalItemCount > 0 && totalItemCount > previousTotalItemCount) {
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if (lastInScreen >= totalItemCount - 15 && totalItemCount > lastInScreen) {
//                    new AsyncTask<Integer, Void, Void>() {
//
//                        @Override
//                        protected void onPreExecute() {
//                            super.onPreExecute();
//                            footerView.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        protected Void doInBackground(Integer... params) {
//                            if (previousTotalItemCount < params[0]) {
//                                previousTotalItemCount = params[0];
////                                if (MidbanApplication
////                                        .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL) != null) {
////                                    Partner partner = (Partner) MidbanApplication
////                                            .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
////                                    currentProducts
////                                            .addAll(ProductRepository.getInstance()
////                                                    .getAllForPartner(partner.getId(),
////                                                            params[0], 5));
////                                } else if (MidbanApplication
////                                        .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION) != null) {
////                                    Partner partner = (Partner) MidbanApplication
////                                            .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION);
////                                    currentProducts
////                                            .addAll(ProductRepository.getInstance()
////                                                    .getAllForPartner(partner.getId(),
////                                                            params[0], 5));
////                                } else if (MidbanApplication
////                                        .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER) != null) {
////                                    Partner partner = (Partner) MidbanApplication
////                                            .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
////                                    currentProducts
////                                            .addAll(ProductRepository.getInstance()
////                                                    .getAllForPartner(partner.getId(),
////                                                            params[0], 5));
////                                } else {
//                                Product productSearch = new Product();
//                                if (searchField.getText().length() > 1) {
//                                    productSearch.setNameTemplate(searchField.getText().toString());
//                                    productSearch.setCode(searchField.getText().toString());
//                                    try {
//                                        productSearch.setId(Long
//                                                .parseLong(searchField.getText().toString()));
//                                    } catch (NumberFormatException e) {
//                                        // do nothing
//                                    }
//                                }
//                                try {
//                                    currentProducts.addAll(ProductRepository.getInstance().getByExample(
//                                            productSearch, Restriction.OR, false, params[0], 5, ordenarPorCategoria, ordenarAlfabeticamente));
//                                } catch (ServiceException e) {
//                                    e.printStackTrace();
//                                }
////                                }
//                            }
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void aVoid) {
//                            super.onPostExecute(aVoid);
//                            footerView.setVisibility(View.GONE);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }.execute(totalItemCount);
//                }
//            }
//        }
//    }

    public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
        private int bufferItemCount = 10;
        private int currentPage = 0;
        private int itemCount = 0;
        private boolean isLoading = true;

        public InfiniteScrollListener(int bufferItemCount) {
            this.bufferItemCount = bufferItemCount;
        }

        public abstract void loadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Do Nothing
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount < itemCount) {
                this.itemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.isLoading = true; }
            }

            if (isLoading && (totalItemCount > itemCount)) {
                isLoading = false;
                itemCount = totalItemCount;
                currentPage++;
            }

            if (!isLoading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
                loadMore(currentPage + 1, totalItemCount);
                isLoading = true;
            }
        }
    }
}
