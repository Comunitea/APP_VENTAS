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
import com.cafedered.midban.dao.RouteDetailDAO;
import com.cafedered.midban.entities.CustomerList;
import com.cafedered.midban.entities.RouteDetail;
import com.cafedered.midban.entities.Synchronization;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpeneERPApiException;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.debortoliwines.openerp.api.Session;

import org.apache.xmlrpc.XmlRpcException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Filter;

public class RouteDetailRepository extends BaseRepository<RouteDetail, RouteDetailDAO> {
    private static RouteDetailRepository instance = null;

    public static RouteDetailRepository getInstance() {
        if (instance == null)
            instance = new RouteDetailRepository();
        return instance;
    }

    public void synchronizeRoutes(String login, String passwd)  throws ConfigurationException {
         Session openERPSession = null;
        Long initDate = new Date().getTime();
         try {
         openERPSession = SessionFactory.getInstance(login, passwd).getSession();
         } catch (Exception e) {
            // do nothing, openERPSession will be null
         }
         if (openERPSession != null) {
             ObjectAdapter adapter;
             ObjectAdapter adapterCustomerList;
             try {
                 adapter = openERPSession.getObjectAdapter("route.detail");
                 adapterCustomerList = openERPSession.getObjectAdapter("customer.list");
             } catch (XmlRpcException e) {
                 throw new ConfigurationException(e);
             } catch (OpeneERPApiException e) {
                 throw new ConfigurationException(e);
             }
             FilterCollection filters = new RouteDetail().getRemoteFilters();
             RowCollection entities;
             String[] fieldsRemote = {"id", "date", "route_id", "customer_ids"};
             String maxDateOld = SynchronizationRepository.getInstance().getMaxDateFor(
                     RouteDetail.class);
//             String maxDateOld = "2015-01-01 00:00:00";
             String maxDate = DateUtil.convertToUTC(maxDateOld);
             try {
                 entities = adapter.searchAndReadObject(filters, fieldsRemote,
                         maxDate);
                 List<RouteDetail> toSave = new ArrayList<RouteDetail>();
                 int result = 0;
                 for (Row row : entities) {
                     RouteDetail detail = new RouteDetail();
                     Object[] customerIds = (Object[]) row
                             .get("customer_ids");
                     String customers = "";
                     if (customerIds != null) {
                         for (Object id : customerIds) {
                             customers += ";" + id;
                         }
                         FilterCollection filtersCustomerList = new FilterCollection();
                         filtersCustomerList.add("id", "in", customerIds);
                         RowCollection entitiesCustomerList;
                         String[] fieldsRemoteCustomerList = {"id", "detail_id", "customer_id", "result"};
                         String maxDateOldCustomerList = maxDate;
                         String maxDateCustomerList = DateUtil.convertToUTC(maxDateOldCustomerList);
                         entitiesCustomerList = adapterCustomerList.searchAndReadObject(filtersCustomerList, fieldsRemoteCustomerList,
                                 maxDateCustomerList);
                         for (Row rowCustomerList : entitiesCustomerList) {
                             CustomerList cl = new CustomerList();
                             cl.setId(Long.parseLong("" + rowCustomerList.getID()));
                             cl.setDetailId((Integer) ((Object[])rowCustomerList.get("detail_id"))[0]);
                             cl.setCustomerId((Integer) ((Object[])rowCustomerList.get("customer_id"))[0]);
                             CustomerListRepository.getInstance().saveOrUpdate(cl);
                         }
                         customers += ";";
                         detail.setCustomers(customers);
                     }
                     detail.setId(Long.valueOf(row.getID()));
                     detail.setRouteId(Long.valueOf((Integer) ((Object[]) row.get("route_id"))[0]));
                     detail.setDate(DateUtil.toFormattedString(
                             (Date) row.get("date"),
                             "yyyy-MM-dd HH:mm:ss"));
                     toSave.add(detail);
                     result++;
                 }
                 for (RouteDetail route : toSave)
                     saveOrUpdate(route);
                 try {
                     Date endDataSynchro = new Date();
                     Long duration = endDataSynchro.getTime() - initDate;
                     SynchronizationRepository.getInstance().saveOrUpdate(
                             Synchronization.create(RouteDetail.class.getName(),
                                     DateUtil.toFormattedString(endDataSynchro), result,
                                     duration, ""));
                 } catch (ServiceException e) {
                     throw new ConfigurationException(e);
                 }
             } catch (XmlRpcException e) {
                 throw new ConfigurationException(e);
             } catch (OpeneERPApiException e) {
                 throw new ConfigurationException(e);
             } catch (ServiceException e) {
                 throw new ConfigurationException(e);
             }
         }
    }

    private RouteDetailRepository() {
        dao = RouteDetailDAO.getInstance();
    }

    public RouteDetail getByRouteId(long routeId, Date date) throws DatabaseException {
        return dao.getByRouteId(routeId, date);
    }
}
