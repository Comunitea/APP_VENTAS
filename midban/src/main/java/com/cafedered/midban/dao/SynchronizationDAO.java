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

import com.cafedered.midban.entities.Synchronization;

public class SynchronizationDAO extends BaseDAO<Synchronization> {

    private static SynchronizationDAO instance;

    public static SynchronizationDAO getInstance() {
        if (instance == null)
            instance = new SynchronizationDAO();
        return instance;
    }

    public String getMaxDateFor(String name) {
        String query = "SELECT MAX(date) FROM synchronizations WHERE class_name LIKE ? AND error LIKE ''";
        Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery(query,
                new String[] { name });
        String result = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.move(1);
            result = cursor.getString(0);
        }
        cursor.close();
        if (result == null)
            result = "0001-01-01 00:00:00";
        return result;
    }
}
