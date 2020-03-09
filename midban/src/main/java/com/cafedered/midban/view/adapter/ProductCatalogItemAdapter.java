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
package com.cafedered.midban.view.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.ProductUom;
import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.service.repositories.ProductUomRepository;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.activities.ProductCardActivity;
import com.cafedered.midban.view.base.BaseSupportFragment;
import com.cafedered.midban.view.fragments.OrderNewDispositionFragment;
import com.cafedered.midban.view.fragments.PartnerReservationsFragment;
import com.cafedered.midban.view.fragments.ProductCardFragment;
import com.cafedered.midban.view.fragments.ProductCatalogFragment;

public class ProductCatalogItemAdapter extends BaseAdapter {

    private List<Product> products;
    private static LayoutInflater inflater = null;
    private BaseSupportFragment fragment;

    public ProductCatalogItemAdapter(OrderNewDispositionFragment fragment,
                                     List<Product> products) {
        this.products = products;
        this.fragment = fragment;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ProductCatalogItemAdapter(ProductCatalogFragment fragment,
            List<Product> products) {
        this.products = products;
        this.fragment = fragment;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ProductCatalogItemAdapter(PartnerReservationsFragment fragment,
            List<Product> products) {
        this.products = products;
        this.fragment = fragment;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ProductCatalogItemAdapter(ProductCardFragment fragment,
            List<Product> products) {
        this.products = products;
        this.fragment = fragment;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return products.size();
    }

    public Object getItem(int position) {
        return products.get(position);
    }

    public long getItemId(int position) {
        return products.get(position).getId();
    }

    public static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView code;
        public TextView packaging;
        public TextView category;
        public TextView subcategory;
        public TextView stock;
        public ViewGroup icons;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.product_catalog_item, null);
            holder = new ViewHolder();
            holder.image = (ImageView) vi
                    .findViewById(R.id.product_catalog_item_image);
            holder.name = (TextView) vi
                    .findViewById(R.id.product_catalog_item_name);
            holder.code = (TextView) vi
                    .findViewById(R.id.product_catalog_item_code);
            holder.packaging = (TextView) vi
                    .findViewById(R.id.product_catalog_item_packaging);
            holder.category = (TextView) vi
                    .findViewById(R.id.product_catalog_item_category);
            holder.subcategory = (TextView) vi
                    .findViewById(R.id.product_catalog_item_subcategory);
            holder.stock = (TextView) vi
                    .findViewById(R.id.product_catalog_item_stock);
            holder.icons = (ViewGroup) vi
                    .findViewById(R.id.product_catalog_item_icons);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        if (position < products.size()) {
            final Product product = products.get(position);
            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MidbanApplication.putValueInContext(
                            ContextAttributes.PRODUCT_TO_DETAIL, product);
                    Intent intent = new Intent(v.getContext(),
                            ProductCardActivity.class);
                    intent.putExtras(new Bundle());
                    if (fragment instanceof ProductCatalogFragment)
                        ((ProductCatalogFragment)fragment).cancelAllAsyncs();
                    if (fragment instanceof PartnerReservationsFragment)
                        ((PartnerReservationsFragment)fragment).cancelAllAsyncs();
                    if (fragment instanceof OrderNewDispositionFragment)
                        ((OrderNewDispositionFragment)fragment).cancelAllAsyncs();
                    v.getContext().startActivity(intent);
                }
            });
            if (!ImageCache.getInstance().exists(
                    Product.class.getName() + product.getId() + "0"))
                ImageCache.getInstance().putInCache(
                        Product.class.getName() + product.getId() + "0",
                        ImageUtil.byteArrayToBitmap(product.getImageMedium()));
            holder.image.setImageBitmap(ImageCache.getInstance().getFromCache(
                    Product.class.getName() + product.getId() + "0"));
            holder.name.setText(product.getNameTemplate());
            holder.code.setText(product.getCode());
            if (null != product.getProductUl() && (Long) product.getUl() != 0) {
                holder.packaging.setText(product.getProductUl().getName());
            }
            if (product.getProductTemplate() != null) {
                if (product.getProductTemplate().getProductCategory() != null && product.getProductTemplate().getProductCategory().getCompleteName() != null) {
                    String[] categories = product.getProductTemplate().getProductCategory()
                            .getCompleteName().split("/");
                    int arrayLength = categories.length;
                    if (arrayLength > 1) {
                        holder.category.setText(categories[arrayLength - 2].trim());
                        holder.subcategory.setText(categories[arrayLength - 1].trim());
                    } else {
                        if (arrayLength > 0)
                            holder.category.setText(categories[arrayLength - 1].trim());
                    }
                } else {
                    holder.category.setText("");
                }
            }
            if (product.getVirtualAvailable() != null) {
                holder.stock.setText(vi.getResources().getString(
                        R.string.fragment_product_catalog_stock)
                        + " " + new BigDecimal(product.getVirtualAvailable().floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                if (product.getUomId() != null) {
                    ProductUom uom = null;
                    try {
                        uom = ProductUomRepository.getInstance().getById(product.getUomId().longValue());
                    } catch (ConfigurationException e) {
                        e.printStackTrace();
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    if (uom != null && uom.getName() != null) {
                        holder.stock.setText(holder.stock.getText() + " " + uom.getName());
                    }
                }
            }

            holder.icons.removeAllViews();
            //TODO remove this mock stuff
            if (position % 3 == 0) {
                ImageView anIcon = new ImageView(vi.getContext());
                anIcon.setImageDrawable(vi.getResources().getDrawable(
                        R.drawable.ficha_producto_congelado));
                holder.icons.addView(anIcon);
            }
        }
        return vi;
    }

}
