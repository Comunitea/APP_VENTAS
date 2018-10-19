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
package com.cafedered.midban.entities.decorators;

import com.cafedered.midban.entities.Product;

public class PartnerFavouritesDecorator implements Comparable<PartnerFavouritesDecorator> {

    private Number uomQty;
    private String productPackaging;
    private Product product;
    private boolean checked;

    public PartnerFavouritesDecorator(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Number getUomQty() {
        return uomQty;
    }

    public void setUomQty(Number uomQty) {
        this.uomQty = uomQty;
    }

    public String getProductPackaging() {
        return productPackaging;
    }

    public void setProductPackaging(String productPackaging) {
        this.productPackaging = productPackaging;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int compareTo(PartnerFavouritesDecorator another) {
        if (this.product != null &&
                this.product.getProductTemplate() != null &&
                this.product.getProductTemplate().getProductCategory() != null &&
                another.product != null &&
                another.product.getProductTemplate() != null &&
                another.product.getProductTemplate().getProductCategory() != null) {
            String[] categories = this.product.getProductTemplate()
                    .getProductCategory().getCompleteName().split("/");
            String[] anotherCategories = another.getProduct().getProductTemplate()
                    .getProductCategory().getCompleteName().split("/");
            if (categories.length > 1 && anotherCategories.length > 1)
                return categories[categories.length-2].compareTo(anotherCategories[anotherCategories.length - 2]);
            else
                return categories[categories.length-1].compareTo(anotherCategories[anotherCategories.length - 1]);
        }
        return this.getProduct().getNameTemplate().compareTo(another.getProduct().getNameTemplate());
    }
}
