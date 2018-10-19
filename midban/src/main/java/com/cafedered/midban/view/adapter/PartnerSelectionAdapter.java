package com.cafedered.midban.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nacho on 26/11/15.
 */
public class PartnerSelectionAdapter extends ArrayAdapter<Partner> {
    private LayoutInflater layoutInflater;
    List<Partner> mCustomers;

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((Partner)resultValue).getRef() + " - " + ((Partner)resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                List<Partner> suggestions = new ArrayList<Partner>();
                Partner example = new Partner();
                example.setUserId(((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER)).getId());
                if (constraint != null)
                    example.setRef(constraint.toString());
                if (constraint != null)
                    example.setName(constraint.toString());
                try {
                    if (constraint != null && constraint.length() > 1)
                        suggestions = PartnerRepository.getInstance().getByExampleUser(example, Restriction.OR, false, 100, 0);
                    else suggestions = new ArrayList<Partner>();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
//                for (Partner customer : mCustomers) {
//                    // Note: change the "contains" to "startsWith" if you only want starting matches
//                    if (customer.getName().toLowerCase().contains(constraint.toString().toLowerCase())
//                            || customer.getRef().toLowerCase().contains(constraint.toString().toLowerCase())) {
//                        suggestions.add(customer);
//                    }
//                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<Partner>) results.values);
            } else {
                // no filter, add entire original list back in
                addAll(mCustomers);
            }
            notifyDataSetChanged();
        }
    };

    public PartnerSelectionAdapter(Context context, int textViewResourceId, List<Partner> customers) {
        super(context, textViewResourceId, customers);
        // copy all the customers into a master list
        mCustomers = new ArrayList<Partner>(customers.size());
        mCustomers.addAll(customers);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(android.R.layout.select_dialog_item, null);
        }

        Partner customer = getItem(position);

        TextView name = (TextView) view.findViewById(android.R.id.text1);
        name.setText(customer.getRef() + " - " + customer.getName());

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
