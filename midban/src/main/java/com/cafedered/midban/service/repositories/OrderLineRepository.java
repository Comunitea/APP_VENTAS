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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.dao.OrderLineDAO;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.utils.LoggerUtil;

public class OrderLineRepository extends
        BaseRepository<OrderLine, OrderLineDAO> {

    private static OrderLineRepository instance = null;

    private static Map<Number, List<OrderLine>> cacheByOrder = new HashMap<Number, List<OrderLine>>(
            100);

    public static OrderLineRepository getInstance() {
        if (instance == null)
            instance = new OrderLineRepository();
        return instance;
    }

    private OrderLineRepository() {
        dao = OrderLineDAO.getInstance();
    }

    public OrderLine getLastOrderLineForProductAndPartner(Long productId,
            Long partnerId) {
        return dao.getLastOrderLineForProductAndPartner(productId, partnerId);
    }

    public List<OrderLine> getLinesByOrderId(Long id) {
//        if (!cacheByOrder.containsKey(id)) {
            OrderLine line = new OrderLine();
            line.setOrderId((Number) id);
            try {
                List<OrderLine> result = dao.getByExample(line,
                        Restriction.AND, true, 0, 100000);
                Iterator<OrderLine> itLines = result.iterator();
                while (itLines.hasNext()) {
                    String status = itLines.next().getState();
                    if (status == null || status.length() == 0 || status.trim().length() == 0)
                        itLines.remove();
                }
//                if (cacheByOrder.size() == 99)
//                    cacheByOrder
//                            .remove(cacheByOrder.keySet().iterator().next());
//                cacheByOrder.put(id, result);
                return result;
            } catch (DatabaseException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
                return new ArrayList<OrderLine>();
            }
//        } else {
//            return cacheByOrder.get(id);
//        }
    }
}
