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
import android.util.Log;

import com.cafedered.cafedroidlitedao.exceptions.BadConfigurationException;
import com.cafedered.cafedroidlitedao.exceptions.BadInvocationException;
import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.JDBCQueryMaker;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Product;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends BaseDAO<Product> {

    private static ProductDAO instance;

    public static ProductDAO getInstance() {
        if (instance == null)
            instance = new ProductDAO();
        return instance;
    }

    public List<Product> getByExample(Product productSearch, Restriction restriction,
                                      boolean exactMatching, Integer numItems, Integer offset,
                                      boolean ordenarPorCategoria, boolean ordenarAlfabeticamente) throws DatabaseException {

        ArrayList<Product> result = new ArrayList<Product>();
        Cursor cursor = null;

        try {
            String e = JDBCQueryMaker.extractTablesAndColumnsForQueryObject(productSearch, restriction, exactMatching);
            if(MidbanApplication.getContext().getApplicationContext().getString(R.string.configuration_debug_enabled).equals("true")) {
                Log.i(this.getClass().getName(), "Antes proceso: " + e);
            }
            if (e.indexOf("WHERE") > 0) {
                e = e.substring(e.indexOf("WHERE") + 6);
                e = " WHERE (" + e;
                e = e.replace(restriction.getCondition() + " id", restriction.getCondition() + " p.id");
                e = e.replace("1=1", "1=1)");
                e = e.replace("0=1", "0=1)");
            } else
                e = " WHERE 1=1 ";
            e = "SELECT p.* FROM product_product p, product_template t, product_category c " + e;
            e = e + " AND p.product_tmpl_id = t.id AND t.categ_id = c.id ";
            if (ordenarPorCategoria) {
                e = e + " ORDER BY c.name ";
            } else if (ordenarAlfabeticamente) {
                e = e + " ORDER BY p.name_template ";
            }
            if(numItems != null && offset != null) {
                e = e + " LIMIT " + numItems + ", " + offset;
            }

            if(MidbanApplication.getContext().getApplicationContext().getString(R.string.configuration_debug_enabled).equals("true")) {
                Log.i(this.getClass().getName(), e);
            }

            cursor = getDaoHelper().getReadableDatabase().rawQuery(e, (String[])null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                while(!cursor.isAfterLast()) {
                    result.add((Product)JDBCQueryMaker.getObjectFromCursor(cursor, productSearch));
                    cursor.move(1);
                }
            }
        } catch (BadConfigurationException var19) {
            var19.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var19.getMessage());
        } catch (BadInvocationException var20) {
            var20.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var20.getMessage());
        } catch (IllegalArgumentException var21) {
            var21.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var21.getMessage());
        } catch (SecurityException var22) {
            var22.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var22.getMessage());
        } catch (InstantiationException var23) {
            var23.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var23.getMessage());
        } catch (IllegalAccessException var24) {
            var24.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var24.getMessage());
        } catch (InvocationTargetException var25) {
            var25.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var25.getMessage());
        } catch (NoSuchMethodException var26) {
            var26.printStackTrace();
            throw new DatabaseException("Error while trying to retrieve objects " + var26.getMessage());
        } finally {
            if(cursor != null) {
                cursor.close();
            }

        }

        return result;
    }
}
