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
import java.util.Collection;
import java.util.List;

import android.database.Cursor;

import com.cafedered.midban.entities.ProductCategory;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ReflectionException;

public class ProductCategoryDAO extends BaseDAO<ProductCategory> {

    private static ProductCategoryDAO instance;

    public static ProductCategoryDAO getInstance() {
        if (instance == null)
            instance = new ProductCategoryDAO();
        return instance;
    }

    public Collection<String> getFirstLevelCategories() {
        List<String> result = new ArrayList<String>();
        try {
            Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery(
                    "SELECT complete_name FROM " + getTableName()
                            + " WHERE parent_id IS NULL", null);
            if (!cursor.isAfterLast()) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    result.add(cursor.getString(0));
                    cursor.move(1);
                }
                cursor.close();
            }

        } catch (ReflectionException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return result;
    }
}
