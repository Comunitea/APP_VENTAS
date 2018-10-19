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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.LongClick;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Transformer;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.AccountPaymentTerm;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.PaymentMode;
import com.cafedered.midban.service.repositories.AccountPaymentTermRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.PaymentModeRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.GoogleMapsActivity;
import com.cafedered.midban.view.activities.PartnerEditionActivity;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.OneComboEditionDialog;
import com.cafedered.midban.view.dialogs.OneFieldEditionDialog;
import com.cafedered.midban.view.dialogs.PartnerAddressEditionDialog;
import com.cafedered.midban.view.transformers.PartnerTransformer;

import java.util.List;

@Fragment(R.layout.fragment_partner_detail_tab_card)
public class PartnerDetailTabCardFragment extends BaseSupportFragment {
    @Wire(view = R.id.fragment_partner_detail_tab_card_contact_name_data_tv,
            field = "name")
    private TextView partnerName;
    @Wire(view = R.id.fragment_partner_detail_tab_card_typology_data_tv,
            field = "type")
    private TextView partnerType;
    @Wire(view = R.id.fragment_partner_detail_tab_card_invoice_address_data_tv)
    private TextView partnerInvoiceAddress;
    @Wire(view = R.id.fragment_partner_detail_tab_card_delivery_address_data_tv)
    private TextView partnerDeliveryAddress;
    @Wire(view = R.id.fragment_partner_detail_tab_card_phone_data_tv,
            field = "phone")
    private TextView partnerPhone;
    @Wire(view = R.id.fragment_partner_detail_tab_card_fax_data_tv,
            field = "fax")
    private TextView partnerFax;
    @Wire(view = R.id.fragment_partner_detail_tab_card_email_data_tv,
            field = "email")
    private TextView partnerEmail;
    @Wire(view = R.id.fragment_partner_detail_tab_card_email_icons_tv)
    private ImageView eMailImage;
    @Wire(view = R.id.fragment_partner_detail_tab_card_invoice_address_icons_tv)
    private ImageView invoiceGeoIcon;
    @Wire(view = R.id.fragment_partner_detail_tab_card_delivery_address_icons_tv)
    private ImageView deliveryGeoIcon;
    @Wire(view = R.id.fragment_partner_detail_tab_card_payment_mode_data_tv)
    private TextView customerPaymentMode;
    @Wire(view = R.id.fragment_partner_detail_tab_card_account_payment_term_data_tv)
    private TextView propertyPaymentTerm;

    @Transformer
    private PartnerTransformer transformer;

    private Partner partner;

    boolean alreadyCreated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View result = super.onCreateView(inflater, container,
                savedInstanceState);
        fillPartnerFields();
        alreadyCreated = true;
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        alreadyCreated = false;
        fillPartnerFields();
    }

    private void fillPartnerFields() {
        partner = (Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
        try {
            transformer.transformEntityToUi(partner, this);
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        if (partner != null) {
            partnerInvoiceAddress.setText(partner.getCompleteAddress());
            partnerDeliveryAddress.setText(partner.getCompleteAddress());
            if (partner.getEmail() == null || partner.getEmail().isEmpty())
                eMailImage.setVisibility(View.INVISIBLE);
            if (getActivity().getClass().equals(PartnerEditionActivity.class)) {
                deliveryGeoIcon.setVisibility(View.INVISIBLE);
                invoiceGeoIcon.setVisibility(View.INVISIBLE);
                eMailImage.setVisibility(View.INVISIBLE);
            }
            if (partner.getCustomerPaymentMode() != null){
                try {
                    PaymentMode pm = PaymentModeRepository.getInstance().getById(partner.getCustomerPaymentMode().longValue());
                    if (pm != null){
                        customerPaymentMode.setText(pm.getName());
                    }
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            if (partner.getPropertyPaymentTerm() != null){
                try {
                    AccountPaymentTerm pm = AccountPaymentTermRepository.getInstance().getById(partner.getPropertyPaymentTerm().longValue());
                    if (pm != null){
                        propertyPaymentTerm.setText(pm.getName());
                    }
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Click(view = R.id.fragment_partner_detail_tab_card_invoice_address_icons_tv)
    public void onClickInvoiceAddress() {
        openGeolocalization();
    }

    @Click(view = R.id.fragment_partner_detail_tab_card_delivery_address_icons_tv)
    public void onClickDeliveryAddress() {
        openGeolocalization();
    }

    private void openGeolocalization() {
        Bundle bundle = new Bundle();
        bundle.putString("partner_address", partnerInvoiceAddress.getText()
                .toString().replace("\n", ", "));
        bundle.putString("partner_phone", partnerPhone.getText().toString());
        bundle.putString("partner_name", partnerName.getText().toString());
        startActivityForResult(
                getNextIntent(bundle, getView(), GoogleMapsActivity.class), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.partner_detail_card, menu);
        if (getActivity().getClass().equals(PartnerEditionActivity.class)) {
            menu.findItem(R.id.edit_item).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
        case R.id.edit_item:
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            PartnerEditionActivity.class), 0);
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

    @Click(view = R.id.fragment_partner_detail_tab_card_email_icons_tv)
    public void onClickEmailButton() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        Uri uri = Uri.parse("mailto:" + partner.getEmail());
        intent.setData(uri);
        startActivity(intent);
    }

    @LongClick(views = {
            R.id.fragment_partner_detail_tab_card_contact_name_data_tv,
            R.id.fragment_partner_detail_tab_card_phone_data_tv,
            R.id.fragment_partner_detail_tab_card_fax_data_tv,
            R.id.fragment_partner_detail_tab_card_email_data_tv })
    public void onClickPartnerName(View longClicked) {
        if (longClicked.equals(partnerEmail))
            new OneFieldEditionDialog(longClicked)
                    .openDialogForUniqueTextField(
                            longClicked.getTag().toString(),
                            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
                            partner, "email", true);
        if (longClicked.equals(partnerName))
            new OneFieldEditionDialog(longClicked)
                    .openDialogForUniqueTextField(longClicked.getTag()
                            .toString(), "", partner, "name", true);
        if (longClicked.equals(partnerPhone))
            new OneFieldEditionDialog(longClicked)
                    .openDialogForUniqueTextField(longClicked.getTag()
                            .toString(), "", partner, "phone", true);
        if (longClicked.equals(partnerFax))
            new OneFieldEditionDialog(longClicked)
                    .openDialogForUniqueTextField(longClicked.getTag()
                            .toString(), "", partner, "fax", true);
    }

    @LongClick(views = {
            R.id.fragment_partner_detail_tab_card_invoice_address_data_tv,
            R.id.fragment_partner_detail_tab_card_delivery_address_data_tv })
    public void onClickPartnerAddresses(View longClicked) {
        new PartnerAddressEditionDialog(longClicked).openDialog(longClicked
                .getTag().toString(), partner);
    }

    @LongClick(view = R.id.fragment_partner_detail_tab_card_typology_data_tv)
    public void onClickTypologyPartner(View longClicked)
            throws ServiceException {
        new OneComboEditionDialog(longClicked).openDialog(PartnerRepository
                .getInstance().getAllDistinctSomeProperty("type"), longClicked
                .getTag().toString());
    }

    @TextChanged(views = {
            R.id.fragment_partner_detail_tab_card_contact_name_data_tv,
            R.id.fragment_partner_detail_tab_card_phone_data_tv,
            R.id.fragment_partner_detail_tab_card_fax_data_tv,
            R.id.fragment_partner_detail_tab_card_email_data_tv,
            R.id.fragment_partner_detail_tab_card_typology_data_tv,
            R.id.fragment_partner_detail_tab_card_invoice_address_data_tv,
            R.id.fragment_partner_detail_tab_card_delivery_address_data_tv,
            R.id.fragment_partner_detail_tab_card_payment_mode_data_tv,
            R.id.fragment_partner_detail_tab_card_account_payment_term_data_tv})
    public void onPartnerDataChanged(View textChanged) {
        if (alreadyCreated) {
            try {
                transformer.transformUiToEntity(partner, this);
                MidbanApplication.putValueInContext(
                        ContextAttributes.PARTNER_TO_DETAIL, partner);
                if (!getActivity().getClass().equals(
                        PartnerEditionActivity.class))
                    startActivityForResult(
                            getNextIntent(new Bundle(), getView(),
                                    PartnerEditionActivity.class), 0);
            } catch (ConfigurationException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
        }
    }
}
