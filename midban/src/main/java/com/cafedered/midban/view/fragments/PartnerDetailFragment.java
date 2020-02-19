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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Transformer;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.view.activities.NewCalendarTaskActivity;
import com.cafedered.midban.view.activities.OrderActivity;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.transformers.PartnerTransformer;

@Fragment(R.layout.fragment_partner_detail)
public class PartnerDetailFragment extends BaseSupportFragment {
    @Wire(view = R.id.fragment_partner_detail_partner_name_tv, field = "name")
    private TextView partnerName;
    @Wire(view = R.id.fragment_partner_detail_partner_code_tv, field = "ref")
    private TextView partnerCode;
    @Wire(view = R.id.fragment_partner_detail_tab_card)
    private TextView tabCard;
    @Wire(view = R.id.fragment_partner_detail_tab_history)
    private TextView tabHistory;
    @Wire(view = R.id.fragment_partner_detail_tab_invoices)
    private TextView tabInvoices;
    @Wire(view = R.id.fragment_partner_detail_tab_catalog)
    private TextView tabCatalog;
    @Wire(view = R.id.fragment_partner_detail_tab_deliverynote)
    private TextView tabDeliveryNote;
    @Wire(view = R.id.fragment_partner_detail_tab_reservations)
    private TextView tabReservations;
    @Wire(view = R.id.fragment_partner_detail_frame)
    private FrameLayout frame;
    @Wire(view = R.id.fragment_partner_detail_header_favourites)
    private TextView favouritesButton;

    Map<Integer, android.support.v4.app.Fragment> tabIds = new LinkedHashMap<Integer, android.support.v4.app.Fragment>();

    @Transformer
    private PartnerTransformer transformer;

    private Partner partner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container,
                savedInstanceState);
        partner = (Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
        try {
            transformer.transformEntityToUi(partner, this);
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        setupDefaultTab(result);
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        partner = (Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
        try {
            transformer.transformEntityToUi(partner, this);
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }

    private void setupDefaultTab(View result) {
        tabCard.setBackgroundColor(getResources().getColor(R.color.white));
        android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) new PartnerDetailTabCardFragment();
        getFragmentManager().beginTransaction()
                .replace(frame.getId(), fragment).commit();
        tabInvoices.setBackgroundColor(getResources().getColor(
                R.color.midban_grey));
        tabIds.put(tabCard.getId(), fragment);
        tabIds.put(tabHistory.getId(), new PartnerHistoryFragment());
        tabIds.put(tabInvoices.getId(), new InvoiceFragment());
        /* DAVID - SE OCULTA
        tabIds.put(tabDeliveryNote.getId(), new PartnerDeliveryNoteFragment());
        tabIds.put(tabCatalog.getId(), new ProductCatalogFragment());
        tabIds.put(tabReservations.getId(), new PartnerReservationsFragment());
        */
    }

    @Click(views = { R.id.fragment_partner_detail_tab_card,
            R.id.fragment_partner_detail_tab_history,
            R.id.fragment_partner_detail_tab_invoices,
            R.id.fragment_partner_detail_tab_deliverynote,
            R.id.fragment_partner_detail_tab_catalog,
            R.id.fragment_partner_detail_tab_reservations })
    public void clickTab(View view) {
        try {
            view.setBackgroundColor(getResources().getColor(R.color.white));
            getFragmentManager().executePendingTransactions();
            getFragmentManager().beginTransaction()
                    .replace(frame.getId(), tabIds.get(view.getId())).commit();
            for (Integer id : tabIds.keySet()) {
                if (!id.equals(view.getId())) {
                    getView().findViewById(id).setBackgroundColor(
                            getResources().getColor(R.color.midban_grey));
                }
            }
        } catch (IllegalStateException e) {
            // avoid troubles when users click like chimps over tabs! :)
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }

    @Click(view = R.id.fragment_partner_detail_header_cita)
    public void clickMeeting() {
        MidbanApplication.putValueInContext(ContextAttributes.PARTNER_TO_TASK,
                partner);
        startActivityForResult(
                getNextIntent(new Bundle(), getView(),
                        NewCalendarTaskActivity.class), 0);
    }

    @Click(view = R.id.fragment_partner_detail_header_favourites)
    public void clickFavourites(View view) {
        try {
            getFragmentManager().executePendingTransactions();
            getFragmentManager().beginTransaction()
                    .replace(frame.getId(), new FavouritesPartnerFragment())
                    .commit();
            for (Integer id : tabIds.keySet()) {
                getView().findViewById(id).setBackgroundColor(
                        getResources().getColor(R.color.midban_grey));
            }
        } catch (IllegalStateException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }

    @Click(view = R.id.fragment_partner_detail_header_cesta)
    public void clickCesta() {
        if ((MidbanApplication
                .getValueFromContext(ContextAttributes.READ_ONLY_ORDER_MODE) != null
                && (Boolean)MidbanApplication.getValueFromContext(
                ContextAttributes.READ_ONLY_ORDER_MODE)))
            OrderRepository.clearCurrentOrder();

        if (OrderRepository.getCurrentOrder().getPartnerId() == null
                || OrderRepository.getCurrentOrder().getPartnerId()
                        .equals(partner.getId())) {
            OrderRepository.getCurrentOrder().setPartnerId(partner.getId());
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(), OrderActivity.class),
                    0);
        } else
            MessagesForUser
                    .showMessage(
                            getView(),
                            R.string.cannot_open_another_order_until_cancel_or_complete_opened,
                            Toast.LENGTH_LONG, Level.WARNING);
    }
}
