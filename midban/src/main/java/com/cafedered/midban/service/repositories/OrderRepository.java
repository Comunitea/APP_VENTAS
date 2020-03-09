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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.dao.OrderDAO;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.decorators.LastSaleCustomObject;
import com.cafedered.midban.entities.decorators.PartnerFavouritesDecorator;
import com.cafedered.midban.pdf.creators.OrderPdfCreator;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;

@SuppressLint("DefaultLocale")
public class OrderRepository extends BaseRepository<Order, OrderDAO> {

    private static OrderRepository instance = null;
    private static Order currentOrder;
    private static List<HashMap<String, Object>> unsynchronizedOrders = new ArrayList<HashMap<String, Object>>();

    public Integer getNumberOfOrdersForPartner(Long partnerId) {
        Order order = new Order();
        order.setPartnerId(partnerId);
        try {
           return dao.getByExample(order, Restriction.AND, true, 0, 10000000).size();
        } catch (DatabaseException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    public void updateStatusOfOrder(Long id, int i) throws DatabaseException, ConfigurationException {
        dao.updateSynchronizationStatus(id, i);
    }

    public static List<HashMap<String, Object>> getUnsynchronizedOrders() {
        return unsynchronizedOrders;
    }

    public enum DateFilters {
        LAST_ORDER(0), LAST_30_DAYS(-30), LAST_90_DAYS(-90), LAST_YEAR(-365);

        private int datesBack;

        private DateFilters(int datesBack) { // 0 means last order;
            this.datesBack = datesBack;
        }

        public int getDatesBack() {
            return datesBack;
        }
    }

    public static OrderRepository getInstance() {
        if (instance == null)
            instance = new OrderRepository();
        return instance;
    }

    private OrderRepository() {
        dao = OrderDAO.getInstance();
    }

    public static void setCurrentOrder(Order order) {
        currentOrder = order;
    }

    public static Order getCurrentOrder() {
        if (currentOrder == null) {
            currentOrder = new Order();
            currentOrder.setId(getInstance().getNextIdNumber().longValue());
        }
        return currentOrder;
    }

    public boolean isOrderInitialized() {
        return currentOrder != null;
    }

    public static void clearCurrentOrder() {
        currentOrder = null;
    }

    public List<Product> getProductFavouritesForPartner(Long idPartner) {
        return dao.getProductsOfPartnerWithDateFilters(
                idPartner, DateFilters.LAST_90_DAYS.getDatesBack());
    }

    @SuppressLint("DefaultLocale")
    public List<PartnerFavouritesDecorator> getProductsOfOrdersForPartner(
            Long id, DateFilters lastOrder, String name, String code,
            Long idProduct) {
        Map<Long, Number[]> productIds = dao.getOrdersOfPartnerWithDateFilters(
                id, lastOrder.getDatesBack());
        List<PartnerFavouritesDecorator> result = new ArrayList<PartnerFavouritesDecorator>();
        for (Long productId : productIds.keySet()) {
            if (null == idProduct || productId.equals(idProduct)) {
                try {
                    Product product = ProductRepository.getInstance().getById(
                            productId);
                    if ((name.isEmpty() || (!name.isEmpty()
                            && product.getNameTemplate() != null && product
                            .getNameTemplate().toLowerCase()
                            .contains(name.toLowerCase())))
                            || (code.isEmpty() || (!code.isEmpty()
                                    && product.getCode() != null && product
                                    .getCode().toLowerCase()
                                    .contains(code.toLowerCase())))) {
                        PartnerFavouritesDecorator toResult = new PartnerFavouritesDecorator(
                                product);
                        toResult.setUomQty(productIds.get(productId)[0]);
                        result.add(toResult);
                    }
                } catch (ConfigurationException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<LastSaleCustomObject> getProductLastSalesForPartner(
            Product product, Partner partner) {
        return dao.getProductLastSalesForPartner(partner.getId(),
                product.getId());
    }

    public List<Order> getOrdersWithFilters(String state, String dateFrom,
            String dateTo, Float amountLessThan, Float amountMoreThan,
            Long partnerId, boolean orderByName) {
        return dao.getOrdersWithFilters(state, dateFrom, dateTo,
                amountLessThan, amountMoreThan, partnerId, orderByName);
    }

    public File getOrderAsPdf(Order order) throws ConfigurationException,
            ServiceException {
        return OrderPdfCreator.generateFile(getById(order.getId()));
    }
}
