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
package com.cafedered.midban.view.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {

	public CustomArrayAdapter(Context ctx, T[] objects) {
		super(ctx, android.R.layout.simple_spinner_item, objects);
	}

    @SuppressWarnings("unchecked")
    public CustomArrayAdapter(Context ctx, Collection<T> objects) {
        super(ctx, android.R.layout.simple_spinner_item, (T[]) objects
                .toArray(new Object[objects.size()]));
    }

	// other constructors

    @Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		// we know that simple_spinner_item has android.R.id.text1 TextView:

		/* if(isDroidX) { */
		TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setPadding(15, 15, 15, 15);
        // text.setTextColor(Color.BLUE);// choose your color :)
		/* } */

		return view;

	}
}
