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

import com.cafedered.midban.entities.User;
import com.cafedered.midban.utils.DateUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class UserDAO extends BaseDAO<User> {

	private static UserDAO instance;
	
	public static UserDAO getInstance() {
		if (instance == null)
			instance = new UserDAO();
		return instance;
	}

	public boolean isUserLoggedLastTwoHours() {
		Calendar ago2Date = Calendar.getInstance();
		ago2Date.add(Calendar.HOUR_OF_DAY, -2);
		Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery("SELECT * FROM appusers WHERE fecha_login = (SELECT max(fecha_login) FROM appusers) ", null);
		if (cursor != null && cursor.getCount() == 1) {
			cursor.moveToFirst();
			String fechaUltimoLogin = cursor.getString(cursor.getColumnIndex("fecha_login"));
			try {
				Date date = DateUtil.parseDate(fechaUltimoLogin, "ddMMyyyyHHmmss");
				return date.compareTo(ago2Date.getTime()) > 0;
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public User getUserLoggedLastTwoHours() {
		Calendar ago2Date = Calendar.getInstance();
		ago2Date.add(Calendar.HOUR_OF_DAY, -2);
		Cursor cursor = getDaoHelper().getReadableDatabase().rawQuery("SELECT * FROM appusers WHERE fecha_login = (SELECT max(fecha_login) FROM appusers) ", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			User user = User.create(cursor.getString(cursor.getColumnIndex("login")), cursor.getString(cursor.getColumnIndex("passwd")), cursor.getInt(cursor.getColumnIndex("id")));
			user.setFechaLogin(cursor.getString(cursor.getColumnIndex("fecha_login")));
			user.setCompanyId(cursor.getLong(cursor.getColumnIndex("company_id")));
			return user;
		}
		return null;
	}
}
