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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.async.CancelAsyncTaskListener;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.GoogleMapsActivity;
import com.cafedered.midban.view.adapter.PartnerListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterPartnersDialog;

@Fragment(R.layout.fragment_partner_list)
public class PartnerListFragment extends BaseSupportFragment implements CancelAsyncTaskListener{

    @Wire(view = R.id.fragment_partner_list_listview)
    ListView list;
    @Wire(view = R.id.fragment_partner_list_search_field)
    EditText searchField;
    private boolean isFirst = true;
    private List<Partner> currentValues = new ArrayList<Partner>();
    private PartnerListItemAdapter adapter;
    private static final int PAGESIZE = 10;
    SearchTask task;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        final PartnerListFragment fragment = this;
        new AsyncTask<Void, Void, List<Partner>>() {
            @Override
            protected List<Partner> doInBackground(Void... params) {
                try {
                    user =
                            ((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER));
                    if (user != null) {
                        Partner partnerSearch = new Partner();
                        partnerSearch.setUserId(user.getId());
                        return PartnerRepository.getInstance()
                                .getByExampleUser(partnerSearch, Restriction.OR, false, 15, 0);
                    }
                    else return new ArrayList<Partner>();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
                return new ArrayList<Partner>();
            }

            @Override
            protected void onPostExecute(List<Partner> result) {
                super.onPostExecute(result);
                currentValues = (ArrayList<Partner>) result;
                adapter = new PartnerListItemAdapter(fragment, currentValues);
                list.setAdapter(adapter);
                list.setOnScrollListener(new InfiniteScrollListener(15) {
                    @Override
                    public void loadMore(final int page, int totalItemsCount) {
                        new AsyncTask<Void, List<Partner>, List<Partner>>() {
                            @Override
                            protected List<Partner> doInBackground(Void... params) {
                                Partner partnerSearch = new Partner();
                                partnerSearch.setUserId(user.getId());
                                try {
                                    return PartnerRepository.getInstance()
                                        .getByExampleUser(partnerSearch,  Restriction.OR, false, 15, (page - 1) * 15);

                                } catch (ServiceException e) {
                                    e.printStackTrace();
                                    return new ArrayList<Partner>();
                                }
                            }

                            @Override
                            protected void onPostExecute(List<Partner> partners) {
                                super.onPostExecute(partners);
                                currentValues.addAll(partners);
                                adapter.notifyDataSetChanged();
                            }
                        }.execute();
                    }
                });
            }
        }.execute();
        return rootView;
    }

    @Click(view = R.id.fragment_partner_list_filter_tv)
    public void onFiltersClicked() {
        FilterPartnersDialog dialog = FilterPartnersDialog.getInstance(
                getActivity(), this, list, isFirst);
        isFirst = false;
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        wlp.y = 80;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.show();
    }

    @Click(view = R.id.fragment_partner_geolocalization_tv)
    public void geolocalizatePartners() {
        MidbanApplication.putValueInContext("partners", currentValues);
        Intent intent = new Intent(getActivity(), GoogleMapsActivity.class);
        getActivity().startActivity(intent, new Bundle());
    }

    @TextChanged(view = R.id.fragment_partner_list_search_field)
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

    class SearchTask extends AsyncTask<Void, Void, List<Partner>> {

        @Override
        protected List<Partner> doInBackground(Void... params) {
            if (!isCancelled()) {
                final Partner partnerSearch = new Partner();
                partnerSearch.setName(searchField.getText().toString());
                partnerSearch.setRef(searchField.getText().toString());
                currentValues.clear();
                if (user != null) {
                    partnerSearch.setUserId(user.getId());
                    try {
                        currentValues.addAll(PartnerRepository.getInstance()
                                .getByExampleUser(partnerSearch, Restriction.OR, false, 15, 0));
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                }
            }
            return currentValues;
        }

        @Override
        protected void onPostExecute(List<Partner> partners) {
            super.onPostExecute(partners);
            if (!isCancelled()) {
                if (user != null) {
                    final Partner partnerSearch = new Partner();
                    partnerSearch.setName(searchField.getText().toString());
                    partnerSearch.setRef(searchField.getText().toString());
                    partnerSearch.setUserId(user.getId());
                    list.setOnScrollListener(new InfiniteScrollListener(15) {
                        @Override
                        public void loadMore(final int page, int totalItemsCount) {
                            new AsyncTask<Void, List<Partner>, List<Partner>>() {
                                @Override
                                protected List<Partner> doInBackground(Void... params) {
                                    try {
                                        return PartnerRepository.getInstance()
                                                .getByExampleUser(partnerSearch, Restriction.OR, false, 15, (page - 1) * 15);

                                    } catch (ServiceException e) {
                                        e.printStackTrace();
                                        return new ArrayList<Partner>();
                                    }
                                }

                                @Override
                                protected void onPostExecute(List<Partner> partners) {
                                    super.onPostExecute(partners);
                                    currentValues.addAll(partners);
                                    adapter.notifyDataSetChanged();
                                }
                            }.execute();
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

//    @TextChanged(view = R.id.fragment_partner_list_search_field)
//    public void onSearchTextChanged() throws ServiceException {
//        final Partner partnerSearch = new Partner();
//        partnerSearch.setName(searchField.getText().toString());
//        partnerSearch.setRef(searchField.getText().toString());
//        currentValues.clear();
//        currentValues.addAll(PartnerRepository.getInstance()
//                .getByExample(partnerSearch, Restriction.OR, false, 0, 10));
//        list.setOnScrollListener(new PartnerListScrollListener(partnerSearch));
//        adapter.notifyDataSetChanged();
//    }

//    private class PartnerListScrollListener implements AbsListView.OnScrollListener {
//
//        Partner searched;
//
//        public PartnerListScrollListener(Partner searched) {
//            this.searched = searched;
//        }
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
//                if (lastInScreen >= totalItemCount - 15) {
//                    new AsyncTask<Integer, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Integer... params) {
//                            if (previousTotalItemCount < params[0]) {
//                                previousTotalItemCount = params[0];
//                                try {
//                                    if (searched.getUserId() != null) {
//                                        currentValues.addAll(PartnerRepository.getInstance()
//                                                .getByExampleUser(searched,
//                                                        Restriction.OR, false, params[0], 5));
//                                    }
//                                } catch (ServiceException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void aVoid) {
//                            super.onPostExecute(aVoid);
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
