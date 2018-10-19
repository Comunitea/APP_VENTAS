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
import com.cafedered.midban.R;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.RouteDetail;
import com.cafedered.midban.utils.DateUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class RouteDetailDAO extends BaseDAO<RouteDetail> {

	private static RouteDetailDAO instance;
	
	public static RouteDetailDAO getInstance() {
		if (instance == null)
			instance = new RouteDetailDAO();
		return instance;
	}

	public RouteDetail getByRouteId(long routeId, Date date) throws DatabaseException {

		RouteDetail result = null;

		Cursor cursor = null;

		try {
			String e = "SELECT * FROM route_detail r WHERE r.route_id = " + routeId + " AND r.date LIKE '" + DateUtil.toFormattedString(date, "yyyy-MM-dd") + "%'";

			if(MidbanApplication.getContext().getApplicationContext().getString(R.string.configuration_debug_enabled).equals("true")) {
				Log.i(this.getClass().getName(), e);
			}

			cursor = getDaoHelper().getReadableDatabase().rawQuery(e, (String[])null);
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				result = ((RouteDetail)JDBCQueryMaker.getObjectFromCursor(cursor, new RouteDetail()));
			}
		} catch (BadConfigurationException var19) {
			var19.printStackTrace();
			throw new DatabaseException("Error while trying to retrieve objects " + var19.getMessage());
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
