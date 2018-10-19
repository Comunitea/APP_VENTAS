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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.view.adapter.ProductAutocompleteAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SelectProductDialog extends Dialog {

    Product product;
    List<Product> products;
    ProductAutocompleteAdapter adapter;

    public SelectProductDialog(Context context) {
        super(context);
    }

    public void openDialogForSelectingProduct(String title, final String attr,
            final Class activityToLaunch) {
        openDialogForSelectingProduct(title, attr, activityToLaunch, null);
    }

    @SuppressWarnings("rawtypes")
    public void openDialogForSelectingProduct(String title, final String attr,
            final Class activityToLaunch,
            OnDismissListener dismissListener) {
        LayoutInflater li = LayoutInflater.from(getContext());
        final View dialogView = li.inflate(R.layout.dialog_product_selection,
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
        products = new ArrayList<Product>();
//        adapter = new ProductAutocompleteAdapter(
//                dialogView.getContext(), products);
        searchText.setThreshold(2);
        searchText.setAdapter(adapter);
        if (dismissListener != null) {
            alertDialogBuilder.setOnDismissListener(dismissListener);
        }
        alertDialogBuilder.setPositiveButton(R.string.confirm,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String text = searchText.getText().toString();
                            Long productId = Long.parseLong(text.substring(0,
                                    text.indexOf("-")).trim());
                            Product example = new Product();
                            example.setId(productId);
                            product = ProductRepository.getInstance().getByExample(example, Restriction.OR, true, 0, 1).get(0);
                            MidbanApplication.putValueInContext(attr, product);
                        } catch (Exception e) {
                            if (LoggerUtil.isDebugEnabled())
                                e.printStackTrace();
                            MessagesForUser.showMessage(dialogView,
                                    R.string.cannot_retrieve_product,
                                    Toast.LENGTH_LONG, Level.SEVERE);
                        }
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();
    }

    public Product getProduct() {
        return product;
    }
}
