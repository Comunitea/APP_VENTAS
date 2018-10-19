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
import com.cafedered.midban.entities.AccountMoveLine;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.AccountMoveLineRepository;
import com.cafedered.midban.service.repositories.InvoiceRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.InvoiceDetailActivity;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.AccountMoveLineListItemAdapter;
import com.cafedered.midban.view.adapter.InvoiceListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.FilterInvoicesDialog;
import com.cafedered.midban.view.dialogs.IncomeRegistrationDialog;

@Fragment(R.layout.fragment_invoice)
public class InvoiceFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_invoice_list_listview)
    ListView list;
    @Wire(view = R.id.fragment_invoice_list_search_field)
    EditText searchField;
    List<AccountMoveLine> currentValues;
    Partner partner;
    boolean isFirst = true;
    AccountMoveLineListItemAdapter adapter;
    AccountMoveLine accountMoveLine;

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
        loadInvoices();
        return rootView;
    }

    @TextChanged(view = R.id.fragment_invoice_list_search_field)
    public void searchInvoice() {
        loadInvoices();
    }

//    @Click(view = R.id.fragment_invoice_send_invoice_btn)
//    public void sendInvoices() {
//        String invoiceNames = "";
//        final ArrayList<Uri> files = new ArrayList<Uri>();
//        for (final Invoice accountMoveLine : ((InvoiceListItemAdapter) list
//                .getAdapter())
//                .getSelectedInvoices()) {
//            if (invoiceNames.length() != 0)
//                invoiceNames += ", ";
//            invoiceNames += accountMoveLine.getNumber();
//            new AsyncTask<Void, Void, Boolean>() {
//
//                ProgressDialog progressDialog;
//
//                @Override
//                protected void onPreExecute() {
//                    super.onPreExecute();
//                    progressDialog = ProgressDialog.show(getView().getContext(), "Enviando...",
//                            "Enviando factura(s)...", true);
//                }
//
//                @Override
//                protected Boolean doInBackground(Void... params) {
//                    try {
//                        InvoiceRepository.getInstance().sendInvoiceAsPdf(
//                                accountMoveLine);
//                    } catch (Exception e) {
//                        if (LoggerUtil.isDebugEnabled())
//                            e.printStackTrace();
//                        return false;
//                    }
//                    return true;
//                };
//
//                protected void onPostExecute(Boolean result) {
//                    progressDialog.dismiss();
//                    if (result) {
//                        MessagesForUser.showMessage(getView(),
//                                R.string.mail_sent, Toast.LENGTH_LONG,
//                                Level.INFO);
//                    } else {
//                        MessagesForUser.showMessage(getView(),
//                                R.string.mail_not_sent, Toast.LENGTH_LONG,
//                                Level.SEVERE);
//                    }
//                };
//            }.execute();
//        }
//        // Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//        // intent.setType("plain/text");
//        // intent.putExtra(Intent.EXTRA_EMAIL, new String[] { partner.getEmail()
//        // });
//        // intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Facturas: "
//        // + invoiceNames);
//        // intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
//        // startActivity(intent);
//        // } catch (ConfigurationException e) {
//        // MessagesForUser.showMessage(getView(), R.string.cannot_create_pdf,
//        // Toast.LENGTH_LONG, Level.SEVERE);
//        // } catch (ServiceException e) {
//        // MessagesForUser.showMessage(getView(), R.string.cannot_create_pdf,
//        // Toast.LENGTH_LONG, Level.SEVERE);
//        // }
//    }

    @Click(view = R.id.fragment_invoice_pay_invoice_btn)
    public void payInvoices() {
        if (((AccountMoveLineListItemAdapter) list.getAdapter()).getSelectedAccountMoveLines()
                .size() != 1)
            MessagesForUser.showMessage(getActivity(),
                    R.string.fragment_invoice_exactly_one_item_selected_to_pay,
                    Toast.LENGTH_LONG, Level.WARNING);
        else
            new IncomeRegistrationDialog(getActivity(),
                    ((AccountMoveLineListItemAdapter) list.getAdapter())
                            .getSelectedAccountMoveLines().get(0),
                    (User) MidbanApplication
                            .getValueFromContext(ContextAttributes.LOGGED_USER), this)
                    .show();
    }

    @Click(view = R.id.fragment_partner_invoice_filter_tv)
    public void clickFilters() {
        FilterInvoicesDialog dialog = FilterInvoicesDialog.getInstance(
                getActivity(), this, list, isFirst, partner);
        isFirst = false;
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        wlp.y = 80;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.show();
    }

    public void loadInvoices() {
        new AsyncTask<Void, Void, List<AccountMoveLine>>() {
            @Override
            protected List<AccountMoveLine> doInBackground(Void... params) {
                try {
                    accountMoveLine = new AccountMoveLine();
                    accountMoveLine.setPartnerId(partner.getId());
                    if (searchField.getText() != null
                            && !searchField.getText().toString().isEmpty()) {
                        try {
                            accountMoveLine.setId(Long.parseLong(searchField.getText()
                                    .toString()));
                        } catch (NumberFormatException e) {
                            return new ArrayList<AccountMoveLine>();
                        }
                    }
                    return AccountMoveLineRepository.getInstance().getByExample(
                            accountMoveLine, Restriction.AND, false, 0, 10);
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
                return new ArrayList<AccountMoveLine>();
            }

            @Override
            protected void onPostExecute(List<AccountMoveLine> result) {
                super.onPostExecute(result);
                currentValues = (ArrayList<AccountMoveLine>) result;
                adapter = new AccountMoveLineListItemAdapter(InvoiceFragment.this,
                        currentValues);
                list.setAdapter(adapter);
                list.setOnScrollListener(new InvoiceScrollListener());
//                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> parent, View view,
//                            int position, long id) {
//                        MidbanApplication.putValueInContext(
//                                ContextAttributes.INVOICE_TO_DETAIL,
//                                (Invoice) parent.getItemAtPosition(position));
//                        startActivityForResult(
//                                getNextIntent(new Bundle(), getView(),
//                                        InvoiceDetailActivity.class), 0);
//                    }
//                });
            }
        }.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.invoice, menu);
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

    public class InvoiceScrollListener implements AbsListView.OnScrollListener {
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
                        currentValues.addAll(AccountMoveLineRepository.getInstance()
                                .getByExample(accountMoveLine, Restriction.AND, false,
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
