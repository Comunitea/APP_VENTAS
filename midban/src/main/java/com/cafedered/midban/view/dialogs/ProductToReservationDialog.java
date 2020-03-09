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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.service.repositories.ReservationRepository;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.utils.InjectionUtils;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;

public class ProductToReservationDialog extends Dialog {

    private Product product;
    private ImageView productImage;

    @Wire(view = R.id.dialog_product_to_reservation_product_name)
    private TextView productName;
    @Wire(view = R.id.dialog_product_to_reservation_product_code)
    private TextView productCode;
    @Wire(view = R.id.dialog_product_to_reservation_packaging)
    private TextView productPackaging;
    @Wire(view = R.id.dialog_product_to_reservation_stock_content)
    private TextView productStock;
    @Wire(view = R.id.dialog_product_to_reservation_quantity_et)
    private EditText productQuantity;
    @Wire(view = R.id.dialog_product_to_reservation_discount_et)
    private EditText productDiscount;
    @Wire(view = R.id.dialog_product_to_reservation_quantity_units_packaging)
    private TextView productUnit;
    @Wire(view = R.id.dialog_product_to_reservation_amount_content)
    private TextView amount;
    @Wire(view = R.id.dialog_product_to_reservation_margin)
    private TextView margin;
    @Wire(view = R.id.dialog_product_to_reservation_quantity_units_packaging)
    private TextView unitsPackaging;
    @Wire(view = R.id.dialog_product_to_reservation_price_et)
    EditText price;
    @Wire(view = R.id.dialog_product_to_reservation_root_view)
    View rootView;

    public ProductToReservationDialog(Context context) {
        super(context);
    }

    public ProductToReservationDialog(Context context, Product product) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCanceledOnTouchOutside(false);
        this.setContentView(R.layout.dialog_product_to_reservation);
        try {
            InjectionUtils.injectAnnotatedFieldsAndMethods(
                    findViewById(R.id.dialog_product_to_reservation_root_view),
                    this);
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        this.product = product;
        setImage();
        productName.setText(product.getNameTemplate());
        productCode.setText("" + product.getId());
        productStock.setText("" + product.getVirtualAvailable());
        // Obtener precio del producto
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                Partner partner = null;
                partner = (Partner) MidbanApplication
                        .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION);
                if (partner == null)
                    partner = (Partner) MidbanApplication
                            .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
                return ProductRepository
                        .getInstance()
                        .getCalculatedPrice(
                                ProductToReservationDialog.this.product,
                                partner,
                                (String) MidbanApplication.getValueFromContext(ContextAttributes.ACTUAL_TARIFF),
                                ((User) MidbanApplication
                                        .getValueFromContext(ContextAttributes.LOGGED_USER))
                                        .getLogin(),
                                ((User) MidbanApplication
                                        .getValueFromContext(ContextAttributes.LOGGED_USER))
                                        .getPasswd())
                        + params[0];
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                price.setText(result);
            }
        }.execute("");
        productDiscount.setText("0"); // initialization
        // TODO remove hardcoding
        // TODO where should we put sample checkbox?
        unitsPackaging.setText("(Mínimo una Caja)");
        if (product.getProductUl() != null)
            productPackaging.setText(product.getProductUl().getName());
    }

    @TextChanged(views = { R.id.dialog_product_to_reservation_quantity_et,
            R.id.dialog_product_to_reservation_discount_et,
            R.id.dialog_product_to_reservation_price_et })
    public void calculateTotals(View changedText) {
        String errors = validateData();
        if (errors.isEmpty()) {
            Float amountToShow = (Float.parseFloat(productQuantity.getText()
                    .toString()) * Float.parseFloat(price.getText().toString()) - (Float
                    .parseFloat(productQuantity.getText().toString())
                    * Float.parseFloat(price.getText().toString())
                    * Float.parseFloat(productDiscount.getText().toString()) / 100F));
            BigDecimal decimal = new BigDecimal(amountToShow).setScale(2,
                    RoundingMode.HALF_UP);
            amount.setText("" + decimal.doubleValue());
            // FIXME remove hardcoded value for margin
            margin.setText("0.0");
        }
    }

    @Click(view = R.id.dialog_product_to_reservation_add_btn)
    public void onAddReservationClicked() {
        new AsyncTask<User, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(User... user) {
                try {
                    return ReservationRepository
                            .getInstance()
                            .createReservation(
                                    ((Partner) MidbanApplication
                                            .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL))
                                            .getId(),
                                    product.getId(),
                                    (Integer) product.getUomId(),
                                    Double.parseDouble(productQuantity
                                            .getText().toString()),
                                    Double.parseDouble(price.getText()
                                            .toString()),
                                    user[0].getLogin(), user[0].getPasswd());
                } catch (NumberFormatException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    return false;
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    return false;
                }
            }

            protected void onPostExecute(Boolean result) {
                if (result)
                    MessagesForUser.showMessage(rootView, "Reserva creada",
                            Toast.LENGTH_LONG, Level.INFO);
                else
                    MessagesForUser.showMessage(rootView,
                            "No se ha podido crear la reserva",
                            Toast.LENGTH_LONG, Level.SEVERE);
            };
        }.execute((User) MidbanApplication
                .getValueFromContext(ContextAttributes.LOGGED_USER));
        this.cancel();

    }

    @Click(view = R.id.dialog_product_to_reservation_cancel_btn)
    public void onCancelButtonPressed() {
        this.cancel();
    }

    private String validateData() {
        String errors = "";
        try {
            Float quantityEntered = Float.parseFloat(productQuantity.getText()
                    .toString());
            if (quantityEntered > product.getVirtualAvailable().floatValue()
                    || quantityEntered <= 0) {
                errors += this.getContext().getResources()
                        .getString(R.string.stock_not_available);
                productQuantity.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.red));
            } else
                productQuantity.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.midban_grey));
            Float priceEntered = Float.parseFloat(price.getText().toString());
            // FIXME corregir cuando el minPrice esté disponible.
            if (/* priceEntered < product.getMinPrice() || */0 > priceEntered) {
                errors += getContext().getResources().getString(
                        R.string.price_under_min_limit);
                price.setBackgroundColor(getContext().getResources().getColor(
                        R.color.red));
            } else
                price.setBackgroundColor(getContext().getResources().getColor(
                        R.color.midban_grey));
            Float discountEntered = Float.parseFloat(productDiscount.getText()
                    .toString());
            if (discountEntered > product.getMaxDiscount()
                    || discountEntered < 0) {
                errors += getContext().getResources().getString(
                        R.string.discount_not_valid);
                productDiscount.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.red));
            } else
                productDiscount.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.midban_grey));
        } catch (NumberFormatException e) {
            errors += getContext().getResources().getString(
                    R.string.invalid_data_entered);
        }
        return errors;
    }

    private void setImage() {
        productImage = (ImageView) findViewById(R.id.dialog_product_to_reservation_image);
        if (!ImageCache.getInstance().exists(
                Product.class.getName() + product.getId() + "0"))
            ImageCache.getInstance().putInCache(
                    Product.class.getName() + product.getId() + "0",
                    ImageUtil.byteArrayToBitmap(product.getImageMedium()));
        productImage.setImageBitmap(ImageCache.getInstance().getFromCache(
                Product.class.getName() + product.getId() + "0"));
    }

}
