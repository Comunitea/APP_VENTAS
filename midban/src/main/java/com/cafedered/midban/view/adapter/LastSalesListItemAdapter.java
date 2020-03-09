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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafedered.midban.R;
import com.cafedered.midban.entities.decorators.LastSaleCustomObject;

public class LastSalesListItemAdapter extends BaseAdapter {

    private List<LastSaleCustomObject> lines;
    private static LayoutInflater inflater = null;

    public LastSalesListItemAdapter(Context context,
            List<LastSaleCustomObject> lines) {
        this.lines = lines;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return lines.size();
    }

    public Object getItem(int position) {
        return lines.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView date;
        public TextView quantity;
        public ImageView unit;
        public TextView lines;
        public TextView price;
        public TextView discount;
        public TextView total;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.last_sales_list_item, null);
            holder = new ViewHolder();
            holder.date = (TextView) vi
                    .findViewById(R.id.last_sales_list_item_date);
            holder.lines = (TextView) vi
                    .findViewById(R.id.last_sales_list_item_lines);
            holder.quantity = (TextView) vi
                    .findViewById(R.id.last_sales_list_item_quantity);
            holder.unit = (ImageView) vi
                    .findViewById(R.id.last_sales_list_item_unit);
            holder.price = (TextView) vi
                    .findViewById(R.id.last_sales_list_item_price);
            holder.discount = (TextView) vi
                    .findViewById(R.id.last_sales_list_item_discount);
            holder.total = (TextView) vi
                    .findViewById(R.id.last_sales_list_item_total);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        final LastSaleCustomObject line = lines.get(position);
        if (line.getDate() != null)
            holder.date.setText(line.getDate());
        if (line.getQuantity() != null)
            holder.quantity.setText(line.getQuantity().toString());
        if (line.getLines() != null)
            holder.lines.setText(line.getLines().toString());
        if (line.getPackaging() != null && line.getPackaging().equals("Unidad(es)"))
            holder.unit.setImageResource(R.drawable.ficha_producto_unidad);
        else if (line.getPackaging() != null && line.getPackaging().equals("Caja(s)"))
            holder.unit.setImageResource(R.drawable.ficha_producto_caja);
        // TODO falta imagen de "both" ambos formatos
        if (line.getPrice() != null)
            holder.price.setText(line.getPrice() + " €");
        if (line.getDiscount() != null)
            holder.discount.setText(line.getDiscount() + "%");
        if (line.getTotal() != null)
            holder.total.setText(line.getTotal() + " €");
        return vi;
    }

}