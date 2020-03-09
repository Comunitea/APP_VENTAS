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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.ItemSelected;
import com.cafedered.midban.annotations.Transformer;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.ProductUl;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.service.repositories.ProductUlRepository;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.OrderActivity;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.activities.ProductCatalogActivity;
import com.cafedered.midban.view.adapter.ProductCatalogItemAdapter;
import com.cafedered.midban.view.adapter.ProductImageAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.dialogs.ProductToCartDialog;
import com.cafedered.midban.view.transformers.ProductTransformer;

@Fragment(R.layout.fragment_product_card)
public class ProductCardFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_product_card_product_name,
            field = "nameTemplate")
    private TextView productName;
    @Wire(view = R.id.fragment_product_card_product_code,
            field = "defaultCode")
    private TextView productCode;
    @Wire(view = R.id.fragment_product_card_product_stock,
            field = "virtualAvailable")
    private TextView productStock;
    @Wire(view = R.id.fragment_product_card_product_price)
    private TextView productPrice;
    @Wire(view = R.id.fragment_product_card_big_image)
    private ImageView bigImage;
    @Wire(view = R.id.fragment_product_card_image_list)
    private ListView imageList;
    @Wire(view = R.id.fragment_product_card_general_tab_content)
    private LinearLayout tabContentGeneral;
    @Wire(view = R.id.fragment_product_card_logistics_tab_content)
    private ScrollView tabContentLogistics;
    @Wire(view = R.id.fragment_product_card_sustitutive_tab_content)
    private LinearLayout tabContentSustitutive;
    @Wire(view = R.id.fragment_product_card_tab_general)
    private TextView tabGeneral;
    @Wire(view = R.id.fragment_product_card_tab_logistics)
    private TextView tabLogistics;


    @Wire(view = R.id.fragment_product_card_tab_sustitutive)
    private TextView tabSustitutive;
    @Wire(view = R.id.fragment_product_card_add_to_cart_button)
    private Button cartButton;
    @Wire(view = R.id.fragment_product_card_substitutives_listview)
    private ListView substitutivesListView;

    @Wire(view = R.id.fragment_product_card_product_unit_per_box,
            field = "boxUnits")
    private TextView unitsPerBox;
    @Wire(view = R.id.fragment_product_card_product_box_per_palet,
            field = "boxesPerPallet")
    private TextView boxesPerPalet;
    @Wire(view = R.id.fragment_product_card_product_weight_per_palet,
            field = "palletGrossWeight")
    private TextView palletGrossWeight;
    @Wire(view = R.id.fragment_product_card_product_total_height_per_palet,
            field = "palletTotalHeight")
    private TextView palletTotalHeight;
    @Wire(view = R.id.fragment_product_card_product_type_of_palet,
            field = "typeOfPallet")
    private TextView typeOfPallet;

    @Wire(view = R.id.fragment_product_card_product_ean13,
            field = "ean13")
    private TextView productEan13;
    @Wire(view = R.id.fragment_product_card_product_dun14,
            field = "dun14")
    private TextView productDun14;



    // TODO add productpackaging

    @Transformer
    private ProductTransformer transformer;

    private Product product;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);

/* he puesto simplemente "Volver" en el botón
        Button fragment_product_card_catalog_btn = rootView.findViewById(R.id.fragment_product_card_catalog_btn);
        if ((getActivity() != null) && (getActivity().getCallingActivity() != null) &&
                (getActivity().getCallingActivity().getClassName()
                        .equals(OrderActivity.class.getName())))
            fragment_product_card_catalog_btn.setText("Ver catálogo");
        else
            fragment_product_card_catalog_btn.setText("Volver");
*/

        if (!OrderRepository.getInstance().isOrderInitialized())
            cartButton.setVisibility(View.GONE);
        try {
            product = (Product) MidbanApplication
                    .getValueFromContext(ContextAttributes.PRODUCT_TO_DETAIL);
            transformer.toUi(product, this);
            try {
                ProductUl typePallet = ProductUlRepository.getInstance().getById(product.getTypeOfPallet().longValue());
                if (typePallet != null) {
                    typeOfPallet.setText(typePallet.getName());
                }
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            //volume.setText(new BigDecimal(product.getVolume().floatValue()).setScale(2, RoundingMode.HALF_UP).toString());
            //weight.setText(new BigDecimal(product.getWeight().floatValue()).setScale(2, RoundingMode.HALF_UP).toString());
            // Obtener substitutivos
            new AsyncTask<Void, Void, List<Product>>() {
                @Override
                protected List<Product> doInBackground(Void... params) {
                    List<Product> result = new ArrayList<Product>();
                    if (product.getSubstituteProducts() != null)
                        for (String id : product.getSubstituteProducts().split(
                                ";"))
                            try {
                                result.add(ProductRepository.getInstance()
                                        .getById(Long.parseLong(id)));
                            } catch (NumberFormatException e) {
                                if (LoggerUtil.isDebugEnabled())
                                    e.printStackTrace();
                            } catch (ConfigurationException e) {
                                if (LoggerUtil.isDebugEnabled())
                                    e.printStackTrace();
                            } catch (ServiceException e) {
                                if (LoggerUtil.isDebugEnabled())
                                    e.printStackTrace();
                            }
                    return result;
                }

                protected void onPostExecute(List<Product> result) {
                    substitutivesListView
                            .setAdapter(new ProductCatalogItemAdapter(
                                    ProductCardFragment.this, result));
                };
            }.execute();
            // Obtener precio del producto
            Partner partner = (Partner) MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
            if (partner == null)
                partner = (Partner) MidbanApplication
                        .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
            if (partner == null)
                partner = (Partner) MidbanApplication
                        .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION);

            productPrice.setText(ProductRepository.getInstance().getCalculatedPrice(
                    product,
                    partner,
                    (String) MidbanApplication.getValueFromContext(ContextAttributes.ACTUAL_TARIFF),
                    ((User) MidbanApplication
                            .getValueFromContext(ContextAttributes.LOGGED_USER))
                            .getLogin(),
                    ((User) MidbanApplication
                            .getValueFromContext(ContextAttributes.LOGGED_USER))
                            .getPasswd()).toString());
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    Partner partner = (Partner) MidbanApplication
                            .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
                    if (partner == null)
                        partner = (Partner) MidbanApplication
                                .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
                    if (partner == null)
                        partner = (Partner) MidbanApplication
                                .getValueFromContext(ContextAttributes.PARTNER_TO_RESERVATION);
                    return ProductRepository
                            .getInstance()
                            .getCalculatedPrice(
                                    product,
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
                    productPrice.setText(result);
                }
            }.execute(" " + getResources().getString(R.string.currency_symbol));
            if (!ImageCache.getInstance().exists(
                    Product.class.getName() + product.getId() + "" + 0))
                ImageCache.getInstance().putInCache(
                        Product.class.getName() + product.getId() + "" + 0,
                        ImageUtil.byteArrayToBitmap(product.getImageMedium()));
            bigImage.setImageBitmap(ImageCache.getInstance().getFromCache(
                    Product.class.getName() + product.getId() + "" + 0));
            List<byte[]> images = new ArrayList<byte[]>();
            imageList.setAdapter(new ProductImageAdapter(this, getActivity(), images,
                    product.getId()));
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return rootView;
    }

    @Click(view = R.id.fragment_product_card_add_to_cart_button)
    public void addToCartButtonPressed() {
        OrderLine lineToEdit = null;
        for (OrderLine line : OrderRepository.getCurrentOrder().getLines()) {
            if (line.getProduct().getId().equals(product.getId()))
                lineToEdit = line;
        }
        ProductToCartDialog dialog = new ProductToCartDialog(getActivity(),
                product, lineToEdit);
        // dialog.showData(R.layout.dialog_product_to_cart);
        dialog.show();
    }

    @ItemSelected(view = R.id.fragment_product_card_image_list)
    public void onImageSelected(Drawable image) {
        bigImage.setImageDrawable(image);
    }

    @Click(view = R.id.fragment_product_card_send_btn)
    public void onSendProductCardClicked() {
        MessagesForUser.showMessage(getActivity(), R.string.todo_message,
                Toast.LENGTH_LONG, Level.WARNING);
    }

    @Click(view = R.id.fragment_product_card_catalog_btn)
    public void onViewCatalogButtonClicked() {
        if ((getActivity() != null) && (getActivity().getCallingActivity() != null) &&
                (getActivity().getCallingActivity().getClassName()
                    .equals(OrderActivity.class.getName())))
            startActivityForResult(
                    getNextIntent(new Bundle(), getView(),
                            ProductCatalogActivity.class), 0);
        else
            getActivity().finish();
    }


    @Click(views = { R.id.fragment_product_card_tab_sustitutive,
            R.id.fragment_product_card_tab_general,
            R.id.fragment_product_card_tab_logistics })
    public void onClickTab(View v) {
        switch (Integer.parseInt(v.getTag().toString())) {
        case 1: {
            tabContentSustitutive.setVisibility(View.GONE);
            tabContentLogistics.setVisibility(View.GONE);
            tabContentGeneral.setVisibility(View.VISIBLE);
            tabSustitutive.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabLogistics.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabGeneral.setBackgroundColor(getResources().getColor(
                    R.color.midban_darker_grey));
            break;
        }
        case 2: {
            tabContentSustitutive.setVisibility(View.GONE);
            tabContentGeneral.setVisibility(View.GONE);
            tabContentLogistics.setVisibility(View.VISIBLE);
            tabSustitutive.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabGeneral.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabLogistics.setBackgroundColor(getResources().getColor(
                    R.color.midban_darker_grey));
            break;
        }
        case 3: {
            break;
        }
        case 4: {
            tabContentGeneral.setVisibility(View.GONE);
            tabContentLogistics.setVisibility(View.GONE);
            tabContentSustitutive.setVisibility(View.VISIBLE);
            tabGeneral.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabLogistics.setBackgroundColor(getResources().getColor(
                    R.color.midban_grey));
            tabSustitutive.setBackgroundColor(getResources().getColor(
                    R.color.midban_darker_grey));
            break;
        }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_card, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
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
}
