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

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.service.repositories.ImageCache;
import com.cafedered.midban.utils.ImageUtil;
import com.cafedered.midban.view.base.BaseSupportFragment;

public class PartnerHistoryOrderLineListItemAdapter extends BaseAdapter {

    private List<OrderLine> lines;
    private static LayoutInflater inflater = null;

    public PartnerHistoryOrderLineListItemAdapter(BaseSupportFragment fragment,
            List<OrderLine> lines) {
        this.lines = lines;
        if (fragment != null && fragment.getActivity() != null)
            inflater = (LayoutInflater) fragment.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return lines.size();
    }

    public Object getItem(int position) {
        return lines.get(position);
    }

    public long getItemId(int position) {
        return lines.get(position).getId();
    }

    public static class ViewHolder {
        public ImageView image;
        public TextView code;
        public TextView name;
        public TextView quantity;
        public ImageView unit;
        public TextView price;
        public TextView discount;
        public TextView total;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.order_line_detail_list_item, null);
            holder = new ViewHolder();
            holder.image = (ImageView) vi
                    .findViewById(R.id.order_line_detail_list_item_product_image);
            holder.name = (TextView) vi
                    .findViewById(R.id.order_line_detail_list_item_product_name);
            holder.code = (TextView) vi
                    .findViewById(R.id.order_line_detail_list_item_product_code);
            holder.quantity = (TextView) vi
                    .findViewById(R.id.order_line_detail_list_item_quantity);
            holder.unit = (ImageView) vi
                    .findViewById(R.id.order_line_detail_list_item_unit);
            holder.price = (TextView) vi
                    .findViewById(R.id.order_line_detail_list_item_price);
            holder.discount = (TextView) vi
                    .findViewById(R.id.order_line_detail_list_item_discount);
            holder.total = (TextView) vi
                    .findViewById(R.id.order_line_detail_list_item_total);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final OrderLine line = lines.get(position);
        holder.name.setText(line.getProduct().getNameTemplate());
        holder.code.setText(line.getProduct().getCode());
        if (!ImageCache.getInstance().exists(
                line.getProduct().getClass().getName()
                        + line.getProduct().getId()))
            ImageCache.getInstance().putInCache(
                    line.getProduct().getClass().getName()
                            + line.getProduct().getId(),
                    ImageUtil.byteArrayToBitmap(line.getProduct()
                            .getImageMedium()));
        holder.image.setImageBitmap(ImageCache.getInstance().getFromCache(
                line.getProduct().getClass().getName()
                        + line.getProduct().getId()));
        if (line.getProductUomQuantity() != null)
            holder.quantity.setText(line.getProductUomQuantity().toString());
        if (line.getProductUomQuantity() != null)
            holder.quantity.setText(line.getProductUomQuantity().toString());
        if (line.getPriceUnit() != null)
            holder.price.setText(line.getPriceUnit() + " €");
        if (line.getDiscount() != null)
            holder.discount.setText(line.getDiscount() + "%");
        if (line.getPriceSubtotal() != null)
            holder.total.setText(line.getPriceSubtotal() + " €");
        return vi;
    }

}
