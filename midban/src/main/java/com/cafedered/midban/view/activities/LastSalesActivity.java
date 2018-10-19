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
package com.cafedered.midban.view.activities;

import android.os.Bundle;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Activity;
import com.cafedered.midban.view.base.BaseSupportActivity;
import com.cafedered.midban.view.fragments.LastSalesFragment;

@Activity(displayAppIcon = true,
        initFragment = LastSalesFragment.class,
        layout = R.layout.activity_last_sales,
        rootView = R.id.activity_last_sales_container,
        fragmentContainerView = R.id.activity_last_sales_container,
        title = R.string.activity_last_sales_title)
public class LastSalesActivity extends BaseSupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_sales);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_last_sales_container,
                            new LastSalesFragment()).commit();
        }
    }
}
