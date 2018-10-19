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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.ReflectionUtils;
import com.cafedered.midban.utils.exceptions.ReflectionException;

public class OneFieldEditionDialog extends Dialog {

    private Context mContext;
    private View clickedView;

    public OneFieldEditionDialog(View view) {
        super(view.getContext());
        mContext = view.getContext();
        clickedView = view;
    }

    public void openDialogForUniqueTextField(String name) {
        openDialogForUniqueTextField(name, "", null, null, true);
    }

    @SuppressLint("DefaultLocale")
    public void openDialogForUniqueTextField(String name,
            final String validatorRegex, final Object obj,
            final String objectFieldName, final boolean setVisibleOnChange) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View dialogView = li
                .inflate(R.layout.dialog_single_field_edition, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.create().requestWindowFeature(
                Window.FEATURE_NO_TITLE);
        final TextView title = (TextView) dialogView
                .findViewById(R.id.dialog_single_field_title);
        title.setText((mContext.getResources().getString(R.string.edit) + " "
                + name + ":").toUpperCase());
        final EditText textField = (EditText) dialogView
                .findViewById(R.id.single_field_edition_popup_textfield);
        textField.setText(((TextView) clickedView).getText());
        alertDialogBuilder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean validatedData = true;
                        if (validatorRegex != null && !validatorRegex.isEmpty()) {
                            Pattern pattern = Pattern.compile(validatorRegex);
                            Matcher matcher = pattern.matcher(textField
                                    .getText());
                            validatedData = matcher.matches();
                        }
                        if (!((TextView) clickedView).getText().equals(
                                textField.getText())
                                && validatedData) {
                            ((TextView) clickedView).setText(textField
                                    .getText());
                            if (obj != null) {
                                try {
                                    ReflectionUtils.setValue(obj,
                                            objectFieldName, textField
                                                    .getText().toString());
                                } catch (ReflectionException e) {
                                    if (LoggerUtil.isDebugEnabled())
                                        e.printStackTrace();
                                }
                            }
                            if (setVisibleOnChange)
                                clickedView.setVisibility(View.VISIBLE);
                            dialog.cancel();
                        } else {
                            if (!validatedData)
                                textField.setTextColor(Color.RED);
                            else
                                dialog.cancel();
                        }
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
