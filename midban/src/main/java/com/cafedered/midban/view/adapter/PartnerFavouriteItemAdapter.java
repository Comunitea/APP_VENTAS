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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.decorators.PartnerFavouritesDecorator;
import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.service.repositories.OrderLineRepository;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.view.dialogs.ProductToCartDialog;
import com.cafedered.midban.view.fragments.FavouritesPartnerFragment;

public class PartnerFavouriteItemAdapter extends BaseAdapter {

    private List<PartnerFavouritesDecorator> products;

    private Set<OrderLine> linesSelected = new LinkedHashSet<OrderLine>();

    private Long partnerId;

    private static LayoutInflater inflater = null;

    public PartnerFavouriteItemAdapter(FavouritesPartnerFragment fragment,
            List<PartnerFavouritesDecorator> products, Long partnerId) {
        this.products = products;
        this.partnerId = partnerId;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<PartnerFavouritesDecorator> getProducts() {
        return products;
    }

    public int getCount() {
        return products.size();
    }

    public Object getItem(int position) {
        return products.get(position);
    }

    public long getItemId(int position) {
        return products.get(position).getProduct().getId();
    }

    public static class ViewHolder {
        public CheckBox check;
        public ImageView image;
        public TextView name;
        public TextView code;
        public TextView packaging;
        public TextView priceUnit;
        public TextView uomQty;
        public TextView category;
        public TextView subcategory;
        public TextView stock;
        public ViewGroup icons;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.partner_favourite_item,
                    null);
            holder = new ViewHolder();
            holder.check = (CheckBox) convertView
                    .findViewById(R.id.partner_favourite_item_check);
            holder.check
                    .setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                boolean isChecked) {
                            if (isChecked) {
                                linesSelected
                                        .add(OrderLineRepository
                                                .getInstance()
                                                .getLastOrderLineForProductAndPartner(
                                                        ((PartnerFavouritesDecorator) holder.check
                                                                .getTag())
                                                                .getProduct()
                                                                .getId(),
                                                        partnerId));
                                ((PartnerFavouritesDecorator) holder.check
                                        .getTag()).setChecked(true);
                            } else {
                                Iterator<OrderLine> lines = linesSelected
                                        .iterator();
                                while (lines.hasNext()) {
                                    OrderLine line = lines.next();
                                    if (line.getProductId().longValue() == ((PartnerFavouritesDecorator) holder.check
                                            .getTag()).getProduct().getId()
                                            .longValue())
                                        lines.remove();
                                }
                                ((PartnerFavouritesDecorator) holder.check
                                        .getTag()).setChecked(false);
                            }
                        }
                    });
            holder.image = (ImageView) convertView
                    .findViewById(R.id.partner_favourite_item_image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_name);
            holder.code = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_code);
            holder.packaging = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_packaging);
            holder.priceUnit = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_price_unit);
            holder.uomQty = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_product_uom_qty);
            holder.category = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_category);
            holder.subcategory = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_subcategory);
            holder.stock = (TextView) convertView
                    .findViewById(R.id.partner_favourite_item_stock);
            holder.icons = (ViewGroup) convertView
                    .findViewById(R.id.partner_favourite_item_icons);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final PartnerFavouritesDecorator product = products.get(position);
        if (!ImageCache.getInstance().exists(
                Product.class.getName() + product.getProduct().getId() + "0"))
            ImageCache.getInstance().putInCache(
                    Product.class.getName() + product.getProduct().getId()
                            + "0",
                    ImageUtil.byteArrayToBitmap(product.getProduct()
                            .getImageMedium()));
        holder.image.setImageBitmap(ImageCache.getInstance().getFromCache(
                Product.class.getName() + product.getProduct().getId() + "0"));
        holder.name.setText(product.getProduct().getNameTemplate());
        // TODO Obtener precio del producto (actual o el de la última línea??)
        // new AsyncTask<String, Void, String>() {
        // @Override
        // protected String doInBackground(String... params) {
        // try {
        // return ProductRepository.getInstance().getCalculatedPrice(
        // product.getProduct(),
        // PartnerRepository.getInstance().getById(partnerId),
        // ((User) MidbanApplication
        // .getValueFromContext(ContextAttributes.LOGGED_USER))
        // .getLogin(),
        // ((User) MidbanApplication
        // .getValueFromContext(ContextAttributes.LOGGED_USER))
        // .getPasswd())
        // + params[0];
        // } catch (ConfigurationException e) {
        // return "";
        // } catch (ServiceException e) {
        // return "";
        // }
        // }
        //
        // @Override
        // protected void onPostExecute(String result) {
        // super.onPostExecute(result);
        // holder.priceUnit.setText(result);
        // }
        // }.execute(MidbanApplication.getContext().getResources()
        // .getString(R.string.currency_symbol));
        holder.priceUnit.setText(OrderLineRepository
                .getInstance()
                .getLastOrderLineForProductAndPartner(
                        product.getProduct().getId(), partnerId).getPriceUnit()
                + MidbanApplication.getContext().getString(
                R.string.currency_symbol));
        holder.uomQty.setText("" + product.getUomQty());
        if (product.getProductPackaging() != null && product.getProductPackaging().equals("unit"))
            holder.uomQty.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    MidbanApplication.getContext().getResources()
                            .getDrawable(R.drawable.ficha_producto_unidad),
                    null);
        else if (product.getProductPackaging() != null && product.getProductPackaging().equals("box"))
            holder.uomQty.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    MidbanApplication.getContext().getResources()
                            .getDrawable(R.drawable.ficha_producto_caja), null);
        holder.code.setText(product.getProduct().getId().toString());
        if (null != product.getProduct().getProductUl()) {
            holder.packaging.setText(product.getProduct().getProductUl()
                    .getName());
        }

        String[] categories;
        if (product.getProduct().getProductTemplate()
                .getProductCategory() != null) {
            categories = product.getProduct().getProductTemplate()
                .getProductCategory().getCompleteName().split("/");
            if (categories.length > 1)
                holder.category.setText(categories[categories.length-2]);
            else holder.category.setText("");
            if (categories.length > 1)
                holder.subcategory.setText(product.getProduct().getProductTemplate()
                        .getProductCategory().getName());
        }
        else holder.category.setText("");

        holder.stock.setText(convertView.getResources().getString(
                R.string.fragment_partner_favourite_stock)
                + " " + product.getProduct().getVirtualAvailable());

        holder.icons.removeAllViews();
        boolean showCestaActivada = false;
        if (OrderRepository.getInstance().isOrderInitialized()) {
            for (OrderLine line : OrderRepository.getCurrentOrder().getLines()) {
                if (line.getProductId().longValue() == product.getProduct()
                        .getId().longValue()) {
                    ImageView anIcon = new ImageView(convertView.getContext());
                    anIcon.setImageDrawable(convertView.getResources()
                            .getDrawable(
                            R.drawable.cesta_compra_anadir_desactivado));
                    anIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do nothing
                        }
                    });
                    holder.icons.addView(anIcon);
                    showCestaActivada = true;
                }
            }
            if (!showCestaActivada) {
                ImageView anIcon = new ImageView(convertView.getContext());
                anIcon.setImageDrawable(convertView.getResources().getDrawable(
                        R.drawable.cesta_compra_anadir_activado));
                anIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ProductToCartDialog(v.getContext(), product
                                .getProduct(), null).show();
                    }
                });
                holder.icons.addView(anIcon);
            }
        }
        holder.check.setTag(product);
        holder.check.setChecked(product.isChecked());
        return convertView;
    }

    public Set<OrderLine> getLinesSelected() {
        return linesSelected;
    }

}
