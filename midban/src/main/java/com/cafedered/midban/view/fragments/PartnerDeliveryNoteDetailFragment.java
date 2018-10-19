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
import java.text.ParseException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Background;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.StockPickingOut;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.InvoiceRepository;
import com.cafedered.midban.service.repositories.StockPickingOutRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.InvoiceDetailActivity;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.OrderLinesAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.OneFieldEditionDialog;

@Fragment(R.layout.fragment_partner_delivery_note_detail)
public class PartnerDeliveryNoteDetailFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_partner_delivery_note_detail_notes)
    private TextView stockPickingOutNotes;

    @Wire(view = R.id.fragment_partner_delivery_note_detail_order_number)
    private TextView stockPickingOutNumber;

    @Wire(view = R.id.fragment_partner_delivery_note_detail_delivery_date)
    private TextView stockPickingOutDeliveryDate;

    @Wire(view = R.id.fragment_partner_delivery_note_detail_lines)
    private TextView stockPickingOutLines;

    @Wire(view = R.id.fragment_partner_delivery_note_detail_amount)
    private TextView stockPickingOutAmount;

    @Wire(view = R.id.fragment_partner_delivery_note_detail_margin)
    private TextView stockPickingOutMargin;

    @Wire(view = R.id.fragment_partner_delivery_note_detail_address)
    private TextView stockPickingOutAddress;

    @Wire(view = R.id.fragment_partner_delivery_note_detail_list_listview)
    ListView list;

    Partner partner;
    StockPickingOut stockPickingOut;
    List<OrderLine> currentValues;
    Order relatedOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        partner = ((Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL));
        stockPickingOut = (StockPickingOut) MidbanApplication
                .getValueFromContext(ContextAttributes.STOCK_PICKING_OUT);
        stockPickingOutNumber.setText(stockPickingOut.getId().toString());
        relatedOrder = stockPickingOut.getRelatedOrder();

        if (stockPickingOut.getNote() != null
                && stockPickingOut.getNote().length() > 0)
            stockPickingOutNotes.setText(stockPickingOut.getNote());
        else
            stockPickingOutNotes.setVisibility(View.GONE);

        try {
            if (stockPickingOut.getMinDate() != null)
                stockPickingOutDeliveryDate
                        .setText(getResources()
                                .getString(
                                        R.string.fragment_partner_delivery_note_detail_delivery_date)
                                + " "
                                + DateUtil.toFormattedString(
                                        DateUtil.parseDate(stockPickingOut
                                                .getMinDate()), "dd.MM.yyyy"));
        } catch (NotFoundException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ParseException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }

        if (null != stockPickingOut.getLines()) {
            stockPickingOutLines
                    .setText("" + stockPickingOut.getLines().size());
        } else {
            stockPickingOutLines.setText(getResources().getString(
                    R.string.not_available));
        }

        if (null != relatedOrder.getAmountTotal()) {
            stockPickingOutAmount.setText(new BigDecimal(relatedOrder
                    .getAmountTotal().doubleValue()).setScale(2,
                    RoundingMode.HALF_UP).toString()
                    + getResources().getString(R.string.currency_symbol));
        } else {
            stockPickingOutAmount.setText(getResources().getString(
                    R.string.not_available));
        }
        if (null != relatedOrder.getMargin()) {
            stockPickingOutMargin.setText(relatedOrder.getMargin().toString());
        } else {
            // FIXME remove hardcoded random margin
            Random rand = new Random();
            Integer randomNum = rand.nextInt((24 - 19) + 1) + 19;
            stockPickingOutMargin.setText(new BigDecimal(relatedOrder
                    .getAmountTotal().doubleValue()
                    * randomNum.doubleValue()
                    / 100d).setScale(2, RoundingMode.HALF_UP)
                    + " ("
                    + randomNum + "%)");
            // stockPickingOutMargin.setText(getResources().getString(
            // R.string.not_available));
        }

        stockPickingOutAddress.setText(partner.getCompleteAddress());
        loadLinesOrder();
        return rootView;
    }

    @Click(view = R.id.fragment_partner_delivery_note_detail_issue_note_btn)
    public void addNote() {
        new OneFieldEditionDialog(stockPickingOutNotes)
                .openDialogForUniqueTextField(stockPickingOutNotes.getTag()
                        .toString());
    }

    @Background(onOKFinish = true,
            onOK = R.string.saved,
            onKO = R.string.cannot_save)
    @Click(view = R.id.fragment_partner_delivery_note_detail_accept_delivery_note_btn)
    public void acceptDeliveryNote() throws ServiceException {
        // TODO accept delivery note in openerp's stock.picking.out
        // TODO create issue if needed (how to do that?)
        User loggedUser = (User) MidbanApplication
                .getValueFromContext(ContextAttributes.LOGGED_USER);
        stockPickingOut.setNote(stockPickingOutNotes.getText().toString());
        StockPickingOutRepository.getInstance().updateRemoteObject(
                stockPickingOut, loggedUser.getLogin(), loggedUser.getPasswd());
    }

    @Click(view = R.id.fragment_partner_delivery_note_detail_view_invoice_btn)
    public void viewInvoice() {
        try {
            // TODO allow to go to InvoiceDetail with the relationship between
            // Order
            // and Invoice
            Invoice detailInvoice = InvoiceRepository.getInstance().getById(
                    relatedOrder.getPartnerInvoiceId().longValue());
            if (detailInvoice != null) {
                MidbanApplication.putValueInContext(
                        ContextAttributes.INVOICE_TO_DETAIL, detailInvoice);
                startActivityForResult(
                        getNextIntent(new Bundle(), getView(),
                                InvoiceDetailActivity.class), 0);
            } else {
                MessagesForUser
                        .showMessage(
                                getView(),
                                R.string.fragment_partner_delivery_note_detail_this_order_has_not_invoice_related,
                                Toast.LENGTH_LONG, Level.WARNING);
            }
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }

    private void loadLinesOrder() {
        new AsyncTask<Void, Void, List<OrderLine>>() {
            @Override
            protected List<OrderLine> doInBackground(Void... params) {
                return relatedOrder.getLinesPersisted();
            }

            @Override
            protected void onPostExecute(List<OrderLine> result) {
                super.onPostExecute(result);
                final OrderLinesAdapter adapter = new OrderLinesAdapter(
                        getView().getContext(), result, false, null);
                currentValues = result;
                list.setAdapter(adapter);
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
}
