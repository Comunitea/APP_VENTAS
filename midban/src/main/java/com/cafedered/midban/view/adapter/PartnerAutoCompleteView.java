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

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cafedered.midban.entities.Partner;

public class PartnerAutoCompleteView extends RelativeLayout {

    TextView textName;

    public PartnerAutoCompleteView(Context context, Partner partner) {
        super(context);
        textName = new TextView(context);
        textName.setText(partner.getId() + " - " + partner.getName());
        textName.setPadding(5, 0, 0, 5);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        addView(textName, params);
    }

    public static PartnerAutoCompleteView create(Context context,
            Partner partner, int i) {
        PartnerAutoCompleteView result = new PartnerAutoCompleteView(context,
                partner);
        result.textName.setTextSize(i);
        return result;
    }

    public void setID(Long value) {
        textName.setId(value.intValue());
    }

    public void setName(String value) {
        textName.setText(value);
    }
}

