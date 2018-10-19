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
package com.cafedered.midban.view.dialogs;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.PartnerCategory;
import com.cafedered.midban.entities.State;
import com.cafedered.midban.service.repositories.PartnerCategoryRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.StateRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.CustomArrayAdapter;
import com.cafedered.midban.view.adapter.PartnerListItemAdapter;
import com.cafedered.midban.view.fragments.PartnerListFragment;

public class FilterPartnersDialog extends Dialog {

    private String type;
    private String city;
    private String zipCode;
    List<Partner> currentPartners;
    PartnerListItemAdapter adapter;
    Partner partnerExample;

    private static FilterPartnersDialog instance = null;

    public static FilterPartnersDialog getInstance(Context context,
            final PartnerListFragment fragment, ListView list, boolean first) {
        if (first)
            instance = null;
        if (instance == null)
            instance = new FilterPartnersDialog(context, fragment, list);
        return instance;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FilterPartnersDialog(Context context,
            final PartnerListFragment fragment, final ListView list) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_partner_filters);
        final Spinner types = (Spinner) findViewById(R.id.dialog_partner_filters_spinner_type);
        Set setTypes = new LinkedHashSet<String>();
        setTypes.add(getContext().getResources().getString(
                R.string.filter_partners_dialog_types));
        try {
            setTypes.addAll(PartnerCategoryRepository.getInstance()
                    .getAllDistinctSomeProperty("name"));
        } catch (ServiceException e1) {
            if (LoggerUtil.isDebugEnabled())
                e1.printStackTrace();
        }
        types.setAdapter(new CustomArrayAdapter<String>(context, setTypes));
        final Spinner cities = (Spinner) findViewById(R.id.dialog_partner_filters_spinner_city);
        Set setCities = new LinkedHashSet<String>();
        setCities.add(getContext().getResources().getString(
                R.string.filter_partners_dialog_cities));
        try {
            setCities.addAll(PartnerRepository.getInstance()
                    .getAllDistinctSomeProperty("city"));
        } catch (ServiceException e1) {
            if (LoggerUtil.isDebugEnabled())
                e1.printStackTrace();
        }
        cities.setAdapter(new CustomArrayAdapter<String>(context, setCities));

        final EditText zip = (EditText) findViewById(R.id.dialog_partner_filters_editext_zip);

        Button apply = (Button) findViewById(R.id.dialog_partner_filters_button_apply);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // FIXME add the correct values.
                type = types.getSelectedItem().toString();
                city = cities.getSelectedItem().toString();
                zipCode = zip.getText().toString();
                partnerExample = new Partner();
                if (!type.equals(getContext().getResources().getString(
                        R.string.filter_partners_dialog_types))) {
                    PartnerCategory categoryExample = new PartnerCategory();
                    categoryExample.setName(type);
                    try {
                        partnerExample.setCategoryId(PartnerCategoryRepository.getInstance().getByExample(categoryExample, Restriction.AND, false, 0, 100000).get(0).getId());
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                }
                if (!city.equals(getContext().getResources().getString(
                        R.string.filter_partners_dialog_cities)))
                    partnerExample.setCity(city);
                if (!zipCode.isEmpty())
                    partnerExample.setZip(zipCode);
                try {
                    currentPartners = PartnerRepository.getInstance()
                            .getByExample(partnerExample, Restriction.AND,
                                    true, 0, 10);
                    adapter = new PartnerListItemAdapter(fragment,
                            currentPartners);
                    list.setAdapter(adapter);
                    instance.cancel();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
            }
        });

        TextView clearFilters = (TextView) findViewById(R.id.dialog_partner_filters_clear);
        clearFilters.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                types.setSelection(0);
                cities.setSelection(0);
                zip.setText("");
            }
        });
    }

    public class FilterPartnersDialogScrollListener implements
            AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView arg0, int arg1) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, final int totalItemCount) {
            if (totalItemCount > 0) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount) {
                    try {
                        currentPartners.addAll(PartnerRepository.getInstance()
                                .getByExample(partnerExample, Restriction.AND,
                                        true, totalItemCount, 10));

                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
