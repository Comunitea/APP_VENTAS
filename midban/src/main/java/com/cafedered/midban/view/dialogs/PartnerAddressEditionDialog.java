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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.Partner;

public class PartnerAddressEditionDialog extends Dialog {

    private Context mContext;
    private View clickedView;

    public PartnerAddressEditionDialog(View view) {
        super(view.getContext());
        mContext = view.getContext();
        clickedView = view;
    }

    @SuppressLint("DefaultLocale")
    public void openDialog(String name, final Partner partner) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View dialogView = li.inflate(R.layout.dialog_partner_address_edition,
                null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.create().requestWindowFeature(
                Window.FEATURE_NO_TITLE);
        final TextView title = (TextView) dialogView
                .findViewById(R.id.dialog_partner_address_title);
        title.setText((mContext.getResources().getString(R.string.edit) + " "
                + name + ":").toUpperCase());
        final EditText street = (EditText) dialogView
                .findViewById(R.id.partner_address_edition_popup_street);
        street.setText(partner.getStreet());
        final EditText city = (EditText) dialogView
                .findViewById(R.id.partner_address_edition_popup_city);
        city.setText(partner.getCity());
        final EditText zip = (EditText) dialogView
                .findViewById(R.id.partner_address_edition_popup_zip);
        zip.setText(partner.getZip());
        alertDialogBuilder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        partner.setStreet(street.getText().toString());
                        partner.setCity(city.getText().toString());
                        partner.setZip(zip.getText().toString());
                        ((TextView) clickedView).setText(partner
                                .getCompleteAddress());
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
    }

}
