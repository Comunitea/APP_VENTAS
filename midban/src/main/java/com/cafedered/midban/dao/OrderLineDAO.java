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
package com.cafedered.midban.dao;

import android.database.Cursor;

import com.cafedered.midban.entities.OrderLine;

public class OrderLineDAO extends BaseDAO<OrderLine> {

    private static OrderLineDAO instance;

    public static OrderLineDAO getInstance() {
        if (instance == null)
            instance = new OrderLineDAO();
        return instance;
    }

    public OrderLine getLastOrderLineForProductAndPartner(Long productId,
            Long partnerId) {
        String query = "SELECT ol.*, o.date_order "
                + "FROM sale_order_line ol, sale_order o, product_product p "
                + "WHERE ol.product_id = ? "
                + "AND ol.order_partner_id = ? "
                + "AND ol.order_id = o.id "
                + "AND ol.product_id = p.id "
                + "ORDER BY o.date_order DESC";
        Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery(query,
                new String[] { productId.toString(), partnerId.toString() });
        if (!cursor.isAfterLast()) {
            cursor.moveToFirst();
            OrderLine line = new OrderLine();
            line.setDiscount(cursor.getDouble(cursor.getColumnIndex("discount")));
            line.setName(cursor.getString(cursor.getColumnIndex("name")));
            line.setOrderPartnerId(partnerId);
            line.setProductUomQuantity(cursor.getDouble(cursor
                    .getColumnIndex("product_uom_qty")));
            line.setPriceUnit(cursor.getDouble(cursor
                    .getColumnIndex("price_unit")));
            line.setProductId(productId);
            line.setProductPackaging(cursor.getInt(cursor
                    .getColumnIndex("product_packaging")));
            line.setPriceSubtotal(line.getProductUomQuantity().doubleValue()
                    * line.getPriceUnit().doubleValue()
                    - line.getDiscount().doubleValue()
                    * (line.getProductUomQuantity().doubleValue() * line
                            .getPriceUnit().doubleValue()));
            return line;
        }
        return null;
    }
}
