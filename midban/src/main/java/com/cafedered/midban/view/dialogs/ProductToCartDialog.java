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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.TextChanged;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.ProductUom;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.service.repositories.ProductUomRepository;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.utils.InjectionUtils;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;

public class ProductToCartDialog extends Dialog   {

    private Product product;
    private ImageView productImage;

    @Wire(view = R.id.dialog_product_to_cart_product_name)
    private TextView productName;
    @Wire(view = R.id.dialog_product_to_cart_product_code)
    private TextView productCode;
    @Wire(view = R.id.dialog_product_to_cart_packaging)
    private TextView productPackaging;
    @Wire(view = R.id.dialog_product_to_cart_stock_content)
    private TextView productStock;
    @Wire(view = R.id.dialog_product_to_cart_discount_et)
    private EditText productDiscount;
    @Wire(view = R.id.dialog_product_to_cart_amount_content)
    private TextView amount;
    @Wire(view = R.id.dialog_product_to_cart_margin)
    private TextView margin;
    @Wire(view = R.id.dialog_product_to_cart_check_sample)
    private CheckBox sampleCheck;
    @Wire(view = R.id.dialog_product_to_cart_price_et)
    EditText price;
    @Wire(view = R.id.dialog_product_to_cart_root_view)
    View rootView;
    @Wire(view = R.id.dialog_product_to_cart_quantity_uom_et)
    private EditText productQuantityUom;
    @Wire(view = R.id.dialog_product_to_cart_quantity_uos_et)
    private EditText productQuantityUos;
    @Wire(view = R.id.dialog_product_to_cart_quantity_uom_name_label)
    private TextView uomName;
    @Wire(view = R.id.dialog_product_to_cart_quantity_uom_units_label)
    private TextView unitsPackagingUom;
    @Wire(view = R.id.dialog_product_to_cart_quantity_uos_name_label)
    private TextView uosName;
    @Wire(view = R.id.dialog_product_to_cart_quantity_uos_units_label)
    private TextView unitsPackagingUos;

//    SelectProductDialog selectProductDialog;

    OrderLine line;

    private boolean synchronizinQuantities = false;

    public ProductToCartDialog(Context context) {
        super(context);
    }

    public ProductToCartDialog(Context context, Product product, OrderLine line) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCanceledOnTouchOutside(false);
        this.setContentView(R.layout.dialog_product_to_cart);
        if (line != null)
            this.line = line;
        try {
            InjectionUtils.injectAnnotatedFieldsAndMethods(
                    findViewById(R.id.dialog_product_to_cart_root_view), this);
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
//        if (product != null) {
            this.product = product;
            loadUi();
//        } else {
//            selectProductDialog = new SelectProductDialog(getContext());
//        }
    }

//    @Override
//    public void show() {
//        super.show();
//        if (selectProductDialog != null)
//            selectProductDialog.openDialogForSelectingProduct(
//                    getContext().getResources()
//                            .getString(
//                                    R.string.activity_portada_select_product_dialog),
//                    ContextAttributes.PRODUCT_SELECTED, null,
//                    this);
//    }

//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        product = (Product) MidbanApplication.getValueFromContext(ContextAttributes.PRODUCT_SELECTED);
//        loadUi();
//    }

    private void loadUi() {
        setImage();
        productName.setText(product.getNameTemplate());
        productCode.setText("" + product.getDefaultCode());
        productStock.setText("" + product.getVirtualAvailable());
        // Obtener precio del producto
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                Partner partner = null;
                partner = (Partner) MidbanApplication
                        .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
                if (partner == null)
                    partner = (Partner) MidbanApplication
                            .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
                return ProductRepository
                        .getInstance()
                        .getCalculatedPrice(
                                ProductToCartDialog.this.product,
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
        uomName.setText(product.getUom().getName());
        uosName.setText(product.getUos().getName());
        if (this.line != null) {
            price.setText("" + this.line.getPriceUnit());
            productDiscount.setText("" + this.line.getDiscount());
            productQuantityUom.setText("" + this.line.getProductUomQuantity());
            productQuantityUos.setText("" + this.line.getProductUosQuantity());
            amount.setText("" + this.line.getPriceSubtotal());
        }
        unitsPackagingUom.setText("");
        unitsPackagingUos.setText("");
        if (product.getProductUl() != null)
            productPackaging.setText(product.getProductUl().getName());
    }

    @TextChanged(views = { R.id.dialog_product_to_cart_quantity_uom_et,
            R.id.dialog_product_to_cart_quantity_uos_et,
            R.id.dialog_product_to_cart_discount_et,
            R.id.dialog_product_to_cart_price_et })
    public void calculateTotals(View changedText) {
        if (!synchronizinQuantities){
            synchronizinQuantities = true;
            try{
                // lo primero va a ser coordinar los edits de cantidad
                if (changedText == productQuantityUom){
                    if (productQuantityUom.getText().toString().equals("")){
                      productQuantityUos.setText("");
                    }
                    else {
                        // tengo que actualizar el valor de productQuantityUos
                        float newUomQuantity = Float.parseFloat(productQuantityUom.getText().toString());
                        ProductUom puos = product.getUos();
                        Number newUosQuantity = new BigDecimal(newUomQuantity * puos.getFactor().floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                        productQuantityUos.setText(String.valueOf(newUosQuantity));
                    }
                }
                else{
                    if (changedText == productQuantityUos){
                        if (productQuantityUos.getText().toString().equals("")){
                            productQuantityUom.setText("");
                        }
                        else {
                            // tengo que actualizar el valor de productQuantityUom
                            float newUosQuantity = Float.parseFloat(productQuantityUos.getText().toString());
                            ProductUom puos = product.getUos();
                            Number newUomQuantity = new BigDecimal(newUosQuantity * puos.getFactor_inv().floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                            productQuantityUom.setText(String.valueOf(newUomQuantity));
                        }
                    }
                }

                String errors = validateData();
                if (errors.isEmpty()) {
                    Float calculatedPrice = Float.parseFloat(price.getText().toString());
                    Float amountToShow = (Float.parseFloat(productQuantityUom.getText()
                            .toString()) * calculatedPrice - (Float
                            .parseFloat(productQuantityUom.getText().toString())
                            * calculatedPrice
                            * Float.parseFloat(productDiscount.getText().toString()) / 100F));
                    BigDecimal decimal = new BigDecimal(amountToShow).setScale(2,
                            RoundingMode.HALF_UP);
                    amount.setText("" + decimal.doubleValue());
                    Float marginValue = ((Float.parseFloat(price.getText().toString()) - product
                            .getStandardPrice().floatValue()) * 100 / (Float
                            .parseFloat(price
                                    .getText().toString())));
                    margin.setText(new BigDecimal(marginValue.doubleValue()).setScale(
                            2, RoundingMode.HALF_UP) + "%");
                }
            }
            finally {
                synchronizinQuantities = false;
            }
        }

    }

    @Click(view = R.id.dialog_product_to_cart_add_btn)
    public void onAddToCartClicked() throws ServiceException {
        String errors = validateData();
        if (!errors.isEmpty()) {
            MessagesForUser.showMessage(rootView,
                    R.string.invalid_data_entered, Toast.LENGTH_LONG,
                    Level.SEVERE);
        } else {
            OrderLine line;
            if (this.line != null)
                line = OrderRepository
                        .getCurrentOrder()
                        .getLines()
                        .get(OrderRepository.getCurrentOrder().getLines()
                                .indexOf(this.line));
            else
                line = new OrderLine();
            line.setDiscount(Float.parseFloat(productDiscount.getText()
                    .toString()));
            line.setOrderPartnerId(OrderRepository.getCurrentOrder()
                    .getPartnerId());
            line.setPriceUnit(Float.parseFloat(price.getText().toString()));
            line.setProductUom(product.getUomId());
            line.setProductUos(product.getUosId());
            line.setPriceSubtotal(Float.parseFloat(productQuantityUom.getText()
                    .toString())
                    * line.getPriceUdv().floatValue()
                    - (Float.parseFloat(productQuantityUom.getText().toString())
                    * Float.parseFloat(line.getPriceUdv().toString())
                    * Float.parseFloat(productDiscount.getText()
                    .toString()) / 100F));
            line.setProductId(product.getId());
            line.setProductUosQuantity(Float.parseFloat(productQuantityUos
                    .getText().toString()));
            line.setProductUomQuantity(Float.parseFloat(productQuantityUom
                    .getText().toString()));
            line.setTaxesId(new Number[]{product.getProductTemplate().getTaxesId()});
            if (this.line == null)
                OrderRepository.getCurrentOrder().getLines().add(line);
            // if order is not initialized, initialize it because it comes from
            // partner detail
            if (OrderRepository.getCurrentOrder().getPartnerId() == null) {
                OrderRepository
                        .getCurrentOrder()
                        .setPartnerId(
                                ((Partner) MidbanApplication
                                        .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL))
                                        .getId());
                OrderRepository.getCurrentOrder().setId(
                        OrderRepository.getInstance().getNextIdNumber()
                                .longValue());
            }
            MessagesForUser.showMessage(rootView,
                    R.string.dialog_product_to_cart_line_added,
                    Toast.LENGTH_LONG, Level.INFO);
            this.cancel();
        }
    }

    @Click(view = R.id.dialog_product_to_cart_cancel_btn)
    public void onCancelButtonPressed() {
        this.cancel();
    }

    private String validateData() {
        String errors = "";
        try {
            Float quantityEntered = Float.parseFloat(productQuantityUom.getText()
                    .toString());
            if (quantityEntered > product.getVirtualAvailable().floatValue()
                    || quantityEntered <= 0) {
//                errors += this.getContext().getResources()
//                        .getString(R.string.stock_not_available);
                productQuantityUom.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.red));
                productQuantityUos.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.red));
            } else{
                productQuantityUom.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.midban_grey));
                productQuantityUos.setBackgroundColor(getContext().getResources()
                        .getColor(R.color.midban_grey));
            }
            Float priceEntered = Float.parseFloat(price.getText().toString());
            // FIXME when minPrice is available...
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
        productImage = (ImageView) findViewById(R.id.dialog_product_to_cart_image);
        if (!ImageCache.getInstance().exists(
                Product.class.getName() + product.getId() + "0"))
            ImageCache.getInstance().putInCache(
                    Product.class.getName() + product.getId() + "0",
                    ImageUtil.byteArrayToBitmap(product.getImageMedium()));
        productImage.setImageBitmap(ImageCache.getInstance().getFromCache(
                Product.class.getName() + product.getId() + "0"));
    }

}
