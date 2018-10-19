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
package com.cafedered.midban.view.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.PartnerAutocompleteAdapter;
import com.cafedered.midban.view.adapter.PartnerSelectionAdapter;
import com.cafedered.midban.view.customviews.CustomAutocompleteTextView;

public class SelectPartnerDialog extends Dialog {

    Partner partner;
    List<Partner> partners;
    PartnerSelectionAdapter adapter;

    public SelectPartnerDialog(Context context) {
        super(context);
    }

    public void openDialogForSelectingPartner(String title, final String attr,
            final Class activityToLaunch) {
        openDialogForSelectingPartner(title, attr, activityToLaunch, null);
    }

    @SuppressWarnings("rawtypes")
    public void openDialogForSelectingPartner(String title, final String attr,
            final Class activityToLaunch,
            DialogInterface.OnDismissListener dismissListener) {
        try {
            LayoutInflater li = LayoutInflater.from(getContext());
            final View dialogView = li.inflate(R.layout.dialog_partner_selection,
                    null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getContext());
            alertDialogBuilder.setView(dialogView);
            AlertDialog dialogAlert = alertDialogBuilder.create();
            dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAlert.setCanceledOnTouchOutside(false);
            final TextView titleTv = (TextView) dialogView
                    .findViewById(R.id.dialog_single_field_title);
            titleTv.setText(title);
            final AutoCompleteTextView searchText = (AutoCompleteTextView) dialogView
                    .findViewById(R.id.single_field_edition_popup_textfield);
            // add the listener so it will tries to suggest while the user types
            partners = new ArrayList<Partner>();
            adapter = new PartnerSelectionAdapter(
                    dialogView.getContext(), R.id.single_field_edition_popup_textfield, partners);
            searchText.setThreshold(2);
            searchText.setAdapter(adapter);
            if (dismissListener != null) {
                alertDialogBuilder.setOnDismissListener(dismissListener);
            }
            alertDialogBuilder.setPositiveButton(R.string.confirm,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                String text = searchText.getText().toString();
                                Long partnerId = 0L;
                                try {
                                    partnerId = Long.parseLong(text.substring(0,
                                            text.indexOf("-")).trim());
                                }
                                catch (Exception e){
                                    MessagesForUser.showMessage(dialogView,
                                            R.string.partner_has_no_ref,
                                            Toast.LENGTH_LONG, Level.SEVERE);
                                }
                                if (partnerId != 0L) {
                                    Partner example = new Partner();
                                    example.setRef(partnerId.toString());
                                    example.setUserId(((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER)).getId());
                                    partner = PartnerRepository.getInstance().getByExampleUser(example, Restriction.OR, true, 1, 0).get(0);
                                    MidbanApplication.putValueInContext(attr, partner);
                                    MidbanApplication.putValueInContext(ContextAttributes.READ_ONLY_ORDER_MODE,
                                            Boolean.FALSE);
                                    if (activityToLaunch != null)
                                        getContext().startActivity(
                                                new Intent(getContext(),
                                                        activityToLaunch));
                                }
                            } catch (Exception e) {
                                if (LoggerUtil.isDebugEnabled())
                                    e.printStackTrace();
                                MessagesForUser.showMessage(dialogView,
                                        R.string.cannot_retrieve_partner,
                                        Toast.LENGTH_LONG, Level.SEVERE);
                            }
                            dialog.dismiss();
                        }
                    });
            alertDialogBuilder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Partner getPartner() {
        return partner;
    }
}
