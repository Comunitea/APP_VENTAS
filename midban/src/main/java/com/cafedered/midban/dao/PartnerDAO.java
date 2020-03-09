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
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PartnerDAO extends BaseDAO<Partner> {

	private static PartnerDAO instance;
	
	public static PartnerDAO getInstance() {
		if (instance == null)
			instance = new PartnerDAO();
		return instance;
	}

	public List<Partner> getByExampleUser(Partner partner, Restriction restriction, boolean exactMatching, Integer offset, Integer limit) throws DatabaseException {
		ArrayList<Partner> result = new ArrayList<Partner>();
		Cursor cursor = null;

		try {
			String e = JDBCQueryMaker.extractTablesAndColumnsForQueryObject(partner, restriction, exactMatching);
			if (MidbanApplication.getContext().getApplicationContext().getString(R.string.configuration_debug_enabled).equals("true")) {
				Log.i(this.getClass().getName(), "Antes proceso: " + e);
			}
			String[] conditions = e.substring(e.indexOf("WHERE") + 6).split(restriction.getCondition());
			e = e.substring(0, e.indexOf("WHERE"));
			// String where = "WHERE 1=1 AND (";
 			String where = "WHERE user_id = " + partner.getUserId() + " AND (";
			if (conditions.length > 2) {
				for (String condition : conditions) {
					if (!condition.contains("user_id"))
						where = where + condition + " " + restriction.getCondition() + " ";
				}
				if (restriction.equals(Restriction.OR))
					where = where + " 0=1) ";
				else
					where = where + " 1=1)";
			}
			else {
				where = where + "1=1)";
			}
			e = e + where;
			e = e + " ORDER BY name";
			if(limit != null && offset != null) {
				e = e + " LIMIT " + limit + ", " + offset;
			}

			if(MidbanApplication.getContext().getApplicationContext().getString(R.string.configuration_debug_enabled).equals("true")) {
				Log.i(this.getClass().getName(), e);
			}

			cursor = getDaoHelper().getReadableDatabase().rawQuery(e, (String[])null);
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();

				while(!cursor.isAfterLast()) {
					result.add((Partner)JDBCQueryMaker.getObjectFromCursor(cursor, partner));
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
