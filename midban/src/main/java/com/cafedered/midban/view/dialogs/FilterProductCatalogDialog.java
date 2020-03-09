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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.ProductCategory;
import com.cafedered.midban.entities.ProductTemplate;
import com.cafedered.midban.service.repositories.ProductCategoryRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.service.repositories.ProductTemplateRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.adapter.CustomArrayAdapter;
import com.cafedered.midban.view.adapter.ProductCatalogItemAdapter;
import com.cafedered.midban.view.fragments.ProductCatalogFragment;

public class FilterProductCatalogDialog extends Dialog {

    private String temperature;
    private String category;
    private List<Product> currentProducts;
    private ProductCatalogItemAdapter adapter;
    private Product productExample;
    CustomArrayAdapter<String> adapterFilterCategory;
    Set setCategories;
    boolean spinnerCreated = false;

    private static FilterProductCatalogDialog instance = null;

    public static FilterProductCatalogDialog getInstance(Context context,
            final ProductCatalogFragment fragment, ListView list, boolean first) {
        if (first)
            instance = null;
        if (instance == null)
            instance = new FilterProductCatalogDialog(context, fragment, list);
        return instance;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FilterProductCatalogDialog(Context context,
            final ProductCatalogFragment fragment, final ListView list) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_product_catalog_filters);
        final Spinner categories = (Spinner) findViewById(R.id.dialog_product_catalog_filters_spinner_category);
        final TextView selectedCategory = (TextView) findViewById(R.id.dialog_product_catalog_filters_tv_category);
        setCategories = new LinkedHashSet<String>();
        setCategories.add(getContext()
                .getResources()
                .getString(
                        R.string.dialog_product_catalog_filters_spinner_category));
        setCategories.addAll(ProductCategoryRepository.getInstance()
                .getFirstLevelCategories());
        adapterFilterCategory = new CustomArrayAdapter<String>(context,
                setCategories);
        categories.setAdapter(adapterFilterCategory);

        categories
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                            android.view.View v, int position, long id) {
                        if (spinnerCreated) {
                            String categoryName = categories.getSelectedItem().toString().trim();
                            if (!categoryName
                                    .equals(getContext()
                                            .getResources()
                                            .getString(
                                                    R.string.dialog_product_catalog_filters_spinner_category)))
                            selectedCategory.setText(categoryName);
                            ProductCategory template = new ProductCategory();
                            template.setName(categoryName);
                            try {
                                List<ProductCategory> list = ProductCategoryRepository.getInstance().getByExample(template, Restriction.AND, false, 0, 10000);
                                if (list != null && list.size() > 0)
                                    template = list.get(0);
                                ProductCategory templateId = new ProductCategory();
                                templateId.setParentId(template.getId());
                                setCategories.clear();
                                try {
                                    setCategories.add(getContext()
                                            .getResources()
                                            .getString(
                                                    R.string.dialog_product_catalog_filters_spinner_category));
                                    for (ProductCategory aCategory : ProductCategoryRepository.getInstance().getByExample(templateId, Restriction.AND, true, 0, 100000))
                                        setCategories.add(aCategory.getName());
                                } catch (ServiceException e) {
                                    e.printStackTrace();
                                }
                                adapterFilterCategory = new CustomArrayAdapter<String>(FilterProductCatalogDialog.this.getContext(),
                                        setCategories);
                                categories.setAdapter(adapterFilterCategory);
                                spinnerCreated = false;
                            } catch (ServiceException e) {
                                e.printStackTrace();
                            }
                        }
                        else spinnerCreated = true;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        final EditText temperatureText = (EditText) findViewById(R.id.dialog_product_catalog_filters_editext_temperature);

        Button apply = (Button) findViewById(R.id.dialog_product_catalog_filters_button_apply);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                category = selectedCategory.getText().toString();
                temperature = temperatureText.getText().toString();
//                subcategory = subcategories.getSelectedItem().toString();
                productExample = new Product();
                if (category.length() != 0 && !category
                        .contains(getContext()
                                .getResources()
                                .getString(
                                        R.string.dialog_product_catalog_filters_spinner_category))) {

                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setName(category);
                    try {
                        ProductTemplate productTemplate = new ProductTemplate();
                        productTemplate.setCategId(ProductCategoryRepository
                                .getInstance()
                                .getByExample(productCategory, Restriction.AND,
                                        false, 0, 100000).get(0).getId());
                        List<ProductTemplate> templatesList = ProductTemplateRepository
                                .getInstance().getByExample(productTemplate,
                                        Restriction.AND, true, 0, 100);
                        if (templatesList.size() > 0) {
                            productExample.setProductTmplId(templatesList
                                    .get(0).getId());
                        }
                    } catch (ServiceException e) {
                        // Unreachable
                    }
                }

                if (!temperature.isEmpty())
                    productExample.setTemperature(Double
                            .parseDouble(temperature));
                try {
                    currentProducts = ProductRepository.getInstance()
                            .getByExample(productExample, Restriction.AND,
                                    true, 0, 10);
                    adapter = new ProductCatalogItemAdapter(fragment,
                            currentProducts);
                    list.setAdapter(adapter);
                    list.setOnScrollListener(new InfiniteScrollListener(15) {
                        @Override
                        public void loadMore(int page, int totalItemsCount) {
                            try {
                                currentProducts.addAll(ProductRepository.getInstance()
                                        .getByExample(productExample, Restriction.AND,
                                                true, page, page * 15));

                            } catch (ServiceException e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                    instance.cancel();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
            }
        });

        TextView clearFilters = (TextView) findViewById(R.id.dialog_product_catalog_filters_clear);
        clearFilters.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setCategories.clear();
                setCategories.add(getContext()
                        .getResources()
                        .getString(
                                R.string.dialog_product_catalog_filters_spinner_category));
                setCategories.addAll(ProductCategoryRepository.getInstance()
                        .getFirstLevelCategories());
                adapterFilterCategory = new CustomArrayAdapter<String>(FilterProductCatalogDialog.this.getContext(),
                        setCategories);
                categories.setAdapter(adapterFilterCategory);
//                categories.setSelection(0);
                selectedCategory.setText("");
                temperatureText.setText("");
            }
        });
    }

    public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
        private int bufferItemCount = 10;
        private int currentPage = 0;
        private int itemCount = 0;
        private boolean isLoading = true;

        public InfiniteScrollListener(int bufferItemCount) {
            this.bufferItemCount = bufferItemCount;
        }

        public abstract void loadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Do Nothing
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount < itemCount) {
                this.itemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.isLoading = true; }
            }

            if (isLoading && (totalItemCount > itemCount)) {
                isLoading = false;
                itemCount = totalItemCount;
                currentPage++;
            }

            if (!isLoading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
                loadMore(currentPage + 1, totalItemCount);
                isLoading = true;
            }
        }
    }

    public class FilterProductCatalogDialogScrollListener implements
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
                        currentProducts.addAll(ProductRepository.getInstance()
                                .getByExample(productExample, Restriction.AND,
                                        true, totalItemCount, 5));

                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
