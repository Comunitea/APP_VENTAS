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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.entities.ProductPartnerPrice;

public class ProductPartnerPriceDAO extends BaseDAO<ProductPartnerPrice> {

	private static ProductPartnerPriceDAO instance;
	
	public static ProductPartnerPriceDAO getInstance() {
		if (instance == null)
			instance = new ProductPartnerPriceDAO();
		return instance;
	}

    public void saveForProductAndPartner(ProductPartnerPrice toSave)
            throws DatabaseException {
        ProductPartnerPrice queryObject = new ProductPartnerPrice();
        queryObject.setDateCalculated(null);
        queryObject.setPartnerId(toSave.getPartnerId());
        queryObject.setProductId(toSave.getProductId());
        List<ProductPartnerPrice> list = null;
        try {
            list = instance.getByExample(queryObject, Restriction.AND, true,
                    0, 100000);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0)
            toSave.setId(list.get(0).getId());
        toSave.setDateCalculated(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        instance.saveOrUpdate(toSave);

    }
}
