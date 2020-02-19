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
import com.cafedered.midban.entities.Shop;
import com.cafedered.midban.utils.ReflectionUtils;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ReflectionException;
import com.cafedered.midban.utils.exceptions.ServiceException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO extends BaseDAO<Shop> {

    private static ShopDAO instance;

    public static ShopDAO getInstance() {
        if (instance == null)
            instance = new ShopDAO();
        return instance;
    }

    @SuppressWarnings("unchecked")
    public List<Shop> getAll(Integer numElements, Integer offset)
            throws ConfigurationException, DatabaseException {
        List<Shop> result = new ArrayList();
        Cursor cursor = null;

        Object obj = new Shop();
        try {
            String query = JDBCQueryMaker.extractTablesAndColumnsForQueryObject(obj, Restriction.VOID, false);
            query = query + " ORDER BY sequence";

            cursor = getDaoHelper().getReadableDatabase().rawQuery(query, (String[])null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                while(!cursor.isAfterLast()) {
                    result.add((Shop)JDBCQueryMaker.getObjectFromCursor(cursor, obj));
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
            if (cursor != null) {
                cursor.close();
            }

        }

        return result;

    }


}
