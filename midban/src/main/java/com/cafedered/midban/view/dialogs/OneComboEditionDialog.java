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

import java.util.LinkedHashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.view.adapter.CustomArrayAdapter;

public class OneComboEditionDialog extends Dialog {

    private Context mContext;
    private View clickedView;

    public OneComboEditionDialog(View view) {
        super(view.getContext());
        mContext = view.getContext();
        clickedView = view;
    }

    @SuppressLint("DefaultLocale")
    public void openDialog(Set<Object> comboItems, String name) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View dialogView = li
                .inflate(R.layout.dialog_single_combo_edition, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.create().requestWindowFeature(
                Window.FEATURE_NO_TITLE);
        final TextView title = (TextView) dialogView
                .findViewById(R.id.dialog_single_combo_title);
        title.setText((mContext.getResources().getString(R.string.edit) + " "
                + name + ":").toUpperCase());
        final Spinner spinnerSet = (Spinner) dialogView
                .findViewById(R.id.single_combo_edition_popup_comboitem);
        Set<String> items = new LinkedHashSet<String>();
        items.add("");
        for (Object obj : comboItems)
            items.add(obj.toString());
        spinnerSet.setAdapter(new CustomArrayAdapter<String>(mContext, items));
        for (int i = 0; i < spinnerSet.getCount(); i++) {
            if (spinnerSet.getItemAtPosition(i).equals(
                    ((TextView) clickedView).getText())) {
                spinnerSet.setSelection(i);
                break;
            }
        }
        alertDialogBuilder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((TextView) clickedView).setText(spinnerSet
                                .getSelectedItem().toString());
                        dialog.cancel();
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
