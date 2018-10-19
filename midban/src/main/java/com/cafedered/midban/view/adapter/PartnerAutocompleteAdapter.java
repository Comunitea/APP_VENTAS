/**
 * ****************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 * Copyright (C) 2014  CafedeRed
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * *****************************************************************************
 */
package com.cafedered.midban.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;

public class PartnerAutocompleteAdapter extends BaseAdapter implements Filterable {

    Context _context;
    List<Partner> partners;

    public PartnerAutocompleteAdapter(Context context, List<Partner> _partners) {
        _context = context;
        partners = _partners;
        orig = partners;
        filter = new PartnerFilter();
    }

    @Override
    public int getCount() {
        if (partners != null)
            return partners.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int arg0) {
        return partners.get(arg0).getRef() + " - "
                + partners.get(arg0).getName();
    }

    @Override
    public long getItemId(int arg0) {
        return partners.get(arg0).getId();
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        PartnerAutoCompleteView partnerView;
        if (arg1 == null && partners.size() > arg0) {
            try {
                partnerView = new PartnerAutoCompleteView(_context,
                        partners.get(arg0));
            } catch (Exception e) {
                e.printStackTrace();
                partnerView = (PartnerAutoCompleteView) arg1;
                if (arg0 < partners.size()) {
                    partnerView.setID(partners.get(arg0).getId());
                    partnerView.setName(partners.get(arg0).getRef() + " - "
                            + partners.get(arg0).getName());
                }
            }
        }
        else {
            partnerView = (PartnerAutoCompleteView) arg1;
            if (arg0 < partners.size()) {
                partnerView.setID(partners.get(arg0).getId());
                partnerView.setName(partners.get(arg0).getRef() + " - "
                        + partners.get(arg0).getName());
            }
        }
        return partnerView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private PartnerFilter filter;
    List<Partner> orig;

    @SuppressLint("DefaultLocale")
    private class PartnerFilter extends Filter {

        public PartnerFilter() {

        }

        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Partner example = new Partner();
            example.setUserId(((User)MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER)).getId());
            if (constraint != null)
                example.setRef(constraint.toString());
            if (constraint != null)
                example.setName(constraint.toString());
            try {
                if (constraint != null && constraint.length() > 1)
                    partners = PartnerRepository.getInstance().getByExampleUser(example, Restriction.OR, false, 100, 0);
                else partners = new ArrayList<Partner>();
            } catch (ServiceException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
            FilterResults oReturn = new FilterResults();
//            ArrayList<Partner> results = new ArrayList<Partner>();
//            if (orig == null)
//                orig = partners;
//
//            if (constraint != null) {
//                if (orig != null && orig.size() > 0) {
//                    for (Partner partner : orig) {
//                        if (partner.getName().toUpperCase()
//                                .contains(constraint.toString().toUpperCase())
//                                || ("" + partner.getId())
//                                .toUpperCase()
//                                .contains(
//                                        constraint.toString()
//                                                .toUpperCase()))
//                            results.add(partner);
//                    }
//                }
                oReturn.values = partners;
//            }
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                //do nothing
            }
            return oReturn;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            partners = (ArrayList<Partner>) results.values;
            notifyDataSetChanged();
        }
    }

}
