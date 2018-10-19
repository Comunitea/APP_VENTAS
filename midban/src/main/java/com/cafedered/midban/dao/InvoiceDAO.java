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

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ReflectionException;

public class InvoiceDAO extends BaseDAO<Invoice> {

    private static InvoiceDAO instance;

    public static InvoiceDAO getInstance() {
        if (instance == null)
            instance = new InvoiceDAO();
        return instance;
    }

    public List<Invoice> getInvoicesWithFilters(String state, String dateFrom,
            String dateTo, Float amountLessThan, Float amountMoreThan,
            Long partnerId) {
        String query;
        List<Invoice> result = new ArrayList<Invoice>();
        try {
            query = "SELECT * FROM " + getTableName() + " WHERE partner_id = "
                    + partnerId;
            String[] args = new String[5];
            int count = 0;
            if (state != null) {
                query += " AND state LIKE ?";
                args[count] = state;
                count++;
            }
            if (dateFrom != null) {
                query += " AND date_invoice > ?";
                args[count] = dateFrom;
                count++;
            }
            if (dateTo != null) {
                query += " AND date_invoice < ?";
                args[count] = dateTo;
                count++;
            }
            if (amountLessThan != null) {
                query += " AND amount_total < ?";
                args[count] = "" + amountLessThan;
                count++;
            }
            if (amountMoreThan != null) {
                query += " AND amount_total > ?";
                args[count] = "" + amountMoreThan;
                count++;
            }
            String[] params = new String[count];
            for (int i = 0; i < count; i++)
                params[i] = args[i];
            if (LoggerUtil.isDebugEnabled()) {
                String parameterValues = "";
                for (String param : params)
                    parameterValues +=  param + ", ";
                Log.d(getClass().getName(), "Query: [" + query + "], params: "
                        + parameterValues);
            }
            Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery(
                    query, params);
            if (!cursor.isAfterLast()) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Invoice invoice = new Invoice();
                    invoice.setId(cursor.getLong(cursor.getColumnIndex("id")));
                    invoice.setDateInvoice(cursor.getString(cursor
                            .getColumnIndex("date_invoice")));
                    invoice.setAmountTotal(cursor.getDouble(cursor
                            .getColumnIndex("amount_total")));
                    invoice.setState(cursor.getString(cursor
                            .getColumnIndex("state")));
                    invoice.setNumber(cursor.getString(cursor
                            .getColumnIndex("number")));
                    result.add(invoice);
                    cursor.move(1);
                }
                cursor.close();
            }
        } catch (ReflectionException e) {
            // unreached
        }
        return result;
    }
}
