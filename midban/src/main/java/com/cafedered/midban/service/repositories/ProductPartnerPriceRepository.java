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
package com.cafedered.midban.service.repositories;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.midban.dao.ProductPartnerPriceDAO;
import com.cafedered.midban.entities.ProductPartnerPrice;
import com.cafedered.midban.utils.exceptions.ServiceException;

public class ProductPartnerPriceRepository extends
        BaseRepository<ProductPartnerPrice, ProductPartnerPriceDAO> {
    private static ProductPartnerPriceRepository instance = null;

    public static ProductPartnerPriceRepository getInstance() {
        if (instance == null)
            instance = new ProductPartnerPriceRepository();
        return instance;
    }

    private ProductPartnerPriceRepository() {
        dao = ProductPartnerPriceDAO.getInstance();
    }

    public void saveForProductAndPartner(ProductPartnerPrice toSave)
            throws ServiceException {
        try {
        dao.saveForProductAndPartner(toSave);
        } catch (DatabaseException e) {
            throw new ServiceException("No se ha podido guardar", e);
        }
    }
}
