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
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.AccountMoveLine;
import com.cafedered.midban.entities.Voucher;
import com.cafedered.midban.service.repositories.AccountMoveLineRepository;
import com.cafedered.midban.service.repositories.AccountRepository;
import com.cafedered.midban.service.repositories.VoucherRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.AccountMoveLineListItemAdapter;
import com.cafedered.midban.view.adapter.IncomeListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.listeners.ITodayDataChangedListenerFragment;

@Fragment(R.layout.fragment_income_list)
public class IncomeListFragment extends BaseSupportFragment implements
        ITodayDataChangedListenerFragment {

    @Wire(view = R.id.fragment_income_num_incomes)
    TextView numIncomes;
//    @Wire(view = R.id.fragment_income_cash_amount_incomes)
//    TextView amountCashIncomes;
//    @Wire(view = R.id.fragment_income_check_amount_incomes)
//    TextView checkIncomes;
    @Wire(view = R.id.fragment_income_total_amount_incomes)
    TextView totalAmountIncomes;
    @Wire(view = R.id.fragment_income_list_listview)
    ListView list;
    AccountMoveLine example;
    List<AccountMoveLine> currentVouchers;
    IncomeListItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        loadListItems(
                MidbanApplication.getValueFromContext(
                        ContextAttributes.TODAY_ACTIVITY_DATE_SELECTED)
                        .toString(), rootView);
        return rootView;
    }

    private void loadListItems(final String date, final View view) {
        new AsyncTask<Void, Void, List<AccountMoveLine>>() {
            @Override
            protected List<AccountMoveLine> doInBackground(Void... params) {
                try {
                    example = new AccountMoveLine();
                    example.setPaymentMade(1);
                    example.setPaymentMadeDate(date);
                    return AccountMoveLineRepository.getInstance().getByExample(
                            example, Restriction.AND, false, 0, 1000);
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    return new ArrayList<AccountMoveLine>();
                }
            }

            @Override
            protected void onPostExecute(List<AccountMoveLine> result) {
                super.onPostExecute(result);
                currentVouchers = result;
                adapter = new IncomeListItemAdapter(IncomeListFragment.this,
                        currentVouchers);
                list.setAdapter(adapter);
//                list.setOnScrollListener(new IncomeScrollListener());
                numIncomes.setText(view.getResources().getString(
                        R.string.fragment_income_list_num_incomes)
                        + " " + result.size());
                float amountCash = 0.0F;
                float amountCheck = 0.0F;
                float amountTotal = 0.0F;
                for (AccountMoveLine income : result) {
                    try {
                        if (income.getAccountId() != null && AccountRepository.getInstance().getById(income.getAccountId().longValue()).getType().contains("bank") && income.getPaymentMadeValue() != null)
                            amountCheck += income.getPaymentMadeValue().floatValue();
                        if (income.getAccountId() != null && AccountRepository.getInstance().getById(income.getAccountId().longValue()).getType().contains("cash") && income.getPaymentMadeValue() != null)
                            amountCash += income.getPaymentMadeValue().floatValue();
                        if (income.getPaymentMadeValue() != null)
                            amountTotal += income.getPaymentMadeValue().floatValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                amountCashIncomes.setText(view.getResources().getString(
//                        R.string.fragment_income_list_cash_amount_incomes)
//                        + " "
//                        + new BigDecimal(amountCash).setScale(2, BigDecimal.ROUND_HALF_UP)
//                        + view.getResources().getString(
//                                R.string.currency_symbol));
//                checkIncomes.setText(view.getResources().getString(
//                        R.string.fragment_income_list_check_amount_incomes)
//                        + " "
//                        + new BigDecimal(amountCheck).setScale(2, BigDecimal.ROUND_HALF_UP)
//                        + view.getResources().getString(
//                                R.string.currency_symbol));
                totalAmountIncomes.setText(view.getResources().getString(
                        R.string.fragment_income_list_amount_incomes)
                        + " "
                        + new BigDecimal(amountTotal).setScale(2, BigDecimal.ROUND_HALF_UP)
                        + view.getResources().getString(
                                R.string.currency_symbol));
            }
        }.execute();
    }

    @Override
    public void notifyDataChangedListener(String newDate) {
        loadListItems(newDate, getView());
    }

//    public class IncomeScrollListener implements
//            AbsListView.OnScrollListener {
//        @Override
//        public void onScrollStateChanged(AbsListView arg0, int arg1) {
//
//        }
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem,
//                int visibleItemCount, final int totalItemCount) {
//            if (totalItemCount > 0) {
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if (lastInScreen == totalItemCount) {
//                    try {
//                        currentVouchers.addAll(VoucherRepository.getInstance()
//                                .getByExample(example, Restriction.AND, false,
//                                        totalItemCount, 5));
//
//                    } catch (ServiceException e) {
//                        e.printStackTrace();
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        }
//    }

}
