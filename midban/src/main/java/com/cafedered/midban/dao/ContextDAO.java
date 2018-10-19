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

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.cafedered.cafedroidlitedao.dao.DaoHelper;
import com.cafedered.cafedroidlitedao.exceptions.BadConfigurationException;
import com.cafedered.midban.R;
import com.cafedered.midban.conf.MidbanApplication;

import java.sql.SQLException;
import java.util.ArrayList;

public class ContextDAO extends DaoHelper {
	
	private static ContextDAO instance;

    // Database name
	public static final String dbName = MidbanApplication.getContext()
			.getResources().getString(R.string.database_name);

    // entities
	public static final String[] classNames = MidbanApplication.getContext()
			.getResources().getStringArray(R.array.entity_classes);

    protected ContextDAO(Context context, CursorFactory factory, int version)
            throws BadConfigurationException {
		super(context, dbName, factory, version, classNames);
    }
    
    public static ContextDAO getInstance() throws BadConfigurationException {
    	if (null == instance)
			instance = new ContextDAO(MidbanApplication.getContext(), null, 1);
    	return instance;
    }

	public void sentencesToUpdate(String versionOrigen, String versionFin) {
		if (versionOrigen.equals("2.6") && versionFin.equals("2.7")) {
			getWritableDatabase().execSQL("ALTER TABLE account_move_line ADD COLUMN payment_made INTEGER");
			getWritableDatabase().execSQL("ALTER TABLE account_move_line ADD COLUMN payment_made_value REAL");
		}
		if (versionOrigen.equals("2.7") && versionFin.equals("2.7.1")) {
			getWritableDatabase().execSQL("ALTER TABLE account_move_line ADD COLUMN payment_made_date TEXT");
		}

	}

	public ArrayList<Cursor> getData(String Query){
		//get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		//an array list of cursor to save two cursors one has results from the query
		//other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2= new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);


		try{
			String maxQuery = Query ;
			//execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);


			//add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });

			alc.set(1,Cursor2);
			if (null != c && c.getCount() > 0) {


				alc.set(0,c);
				c.moveToFirst();

				return alc ;
			}
			return alc;
		} catch(Exception sqlEx){
			Log.d("printing exception", sqlEx.getMessage());
			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		}


	}

	@Override
	public void setDebugEnabled(boolean debug) {
		instance.isDebugEnabled = debug;
	}
}
