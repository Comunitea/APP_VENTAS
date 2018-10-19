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

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.annotations.Fragment;
import com.cafedered.midban.annotations.Wire;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.decorators.LastSaleCustomObject;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.view.activities.PortadaActivity;
import com.cafedered.midban.view.adapter.LastSalesListItemAdapter;
import com.cafedered.midban.view.base.BaseSupportFragment;

@Fragment(R.layout.fragment_last_sales)
public class LastSalesFragment extends BaseSupportFragment {

    @Wire(view = R.id.fragment_last_sales_partner_name_tv)
    private TextView partnerName;
    @Wire(view = R.id.fragment_last_sales_partner_code_tv)
    private TextView partnerCode;
    @Wire(view = R.id.fragment_last_sales_product_image)
    private ImageView productImage;
    @Wire(view = R.id.fragment_last_sales_product_name_value_tv)
    private TextView productName;
    @Wire(view = R.id.fragment_last_sales_product_number_value_tv)
    private TextView productCode;
    @Wire(view = R.id.fragment_last_sales_lines)
    private ListView lastSalesLines;

    private Partner partner;
    private Product product;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        setHasOptionsMenu(true);
        partner = (Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_ORDER);
        product = (Product) MidbanApplication
                .getValueFromContext(ContextAttributes.PRODUCT_TO_LAST_SALES);
        if (partner == null) // it came from partner detail
            partner = (Partner) MidbanApplication
                    .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
        partnerName.setText(partner.getName());
        partnerCode.setText("" + partner.getId());
        productImage.setImageBitmap(ImageUtil.byteArrayToBitmap(product
                .getImageMedium()));
        productName.setText(product.getNameTemplate());
        productCode.setText("" + product.getId());
        new AsyncTask<Void, Void, List<LastSaleCustomObject>>() {
            @Override
            protected List<LastSaleCustomObject> doInBackground(Void... params) {
                return OrderRepository.getInstance()
                        .getProductLastSalesForPartner(product, partner);
            }

            @Override
            protected void onPostExecute(List<LastSaleCustomObject> result) {
                super.onPostExecute(result);
                lastSalesLines.setAdapter(new LastSalesListItemAdapter(
rootView
                        .getContext(), result));
            }
        }.execute();
        return rootView;
    }

    @Click(view = R.id.fragment_last_sales_back_to_cart_btn)
    public void onCartButtonPressed() {
        getActivity().finish();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.last_sales, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
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
