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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ServiceException;

import java.util.ArrayList;
import java.util.List;

public class ProductAutocompleteAdapter extends BaseAdapter implements Filterable {

    Context _context;
    List<Product> products;
    IProductSelection selection;

    public ProductAutocompleteAdapter(Context context, List<Product> _products, IProductSelection selection) {
        _context = context;
        products = _products;
        orig = products;
        filter = new ProductFilter();
        this.selection = selection;
    }

    @Override
    public int getCount() {
        if (products != null)
            return products.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int arg0) {
        return products.get(arg0).getCode() + " - "
                + products.get(arg0).getNameTemplate();
    }

    @Override
    public long getItemId(int arg0) {
        return products.get(arg0).getId();
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        final ProductAutoCompleteView productView;
        if (arg1 == null)
            productView = new ProductAutoCompleteView(_context,
                    products.get(arg0));
        else {
            productView = (ProductAutoCompleteView) arg1;
            if (arg0 < products.size()) {
                productView.setID(products.get(arg0).getId());
                productView.setName(products.get(arg0).getCode() + " - "
                        + products.get(arg0).getNameTemplate());
            }
        }
        productView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection.onSelectedProduct(products.get(arg0));
            }
        });
        return productView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private ProductFilter filter;
    List<Product> orig;

    @SuppressLint("DefaultLocale")
    private class ProductFilter extends Filter {

        public ProductFilter() {

        }

        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Product example = new Product();
            if (constraint != null)
                example.setCode(constraint.toString());
            if (constraint != null)
                example.setNameTemplate(constraint.toString());
            try {
                if (constraint != null && constraint.length() > 0)
                    products = ProductRepository.getInstance().getByExample(example, Restriction.OR, false, 0, 100);
                else products = new ArrayList<Product>();
            } catch (ServiceException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
            FilterResults oReturn = new FilterResults();
            oReturn.values = products;
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
            products = (ArrayList<Product>) results.values;
            notifyDataSetChanged();
        }
    }

}
