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

import com.cafedered.midban.dao.RouteDAO;
import com.cafedered.midban.dao.RouteDetailDAO;
import com.cafedered.midban.entities.Route;
import com.cafedered.midban.entities.RouteDetail;
import com.cafedered.midban.entities.Synchronization;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RouteRepository extends BaseRepository<Route, RouteDAO> {
    private static RouteRepository instance = null;

    public static RouteRepository getInstance() {
        if (instance == null)
            instance = new RouteRepository();
        return instance;
    }

    public void synchronizeRoutes(String login, String passwd)  throws ConfigurationException {
        Long initDate = new Date().getTime();
        Session openERPSession = null;
        try {
            openERPSession = SessionFactory.getInstance(login, passwd).getSession();
        } catch (Exception e) {
            // do nothing, openERPSession will be null
        }
        if (openERPSession != null) {
            ObjectAdapter adapter;
            try {
                adapter = openERPSession.getObjectAdapter("route");
            } catch (XmlRpcException e) {
                throw new ConfigurationException(e);
            } catch (OpeneERPApiException e) {
                throw new ConfigurationException(e);
            }
            FilterCollection filters = new Route().getRemoteFilters();
            RowCollection entities;
            String[] fieldsRemote = {"id", "name", "code", "comercial_id", "partner_ids", "day_id"};
            try {
                 String maxDateOld = SynchronizationRepository.getInstance().getMaxDateFor(
                     Route.class);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Calendar last9Days = Calendar.getInstance();
//                last9Days.add(Calendar.YEAR, -5);
//                String maxDateOld = formatter.format(last9Days.getTime());
                String maxDate = DateUtil.convertToUTC(maxDateOld);
                entities = adapter.searchAndReadObject(filters, fieldsRemote, maxDate);
                if(LoggerUtil.isDebugEnabled())
                    System.out.println("Obtiene " + entities.size() + " rutas.");
                List<Route> toSave = new ArrayList<Route>();
                int result = 0;
                for (Row row : entities) {
                    Route detail = new Route();
//                    Object[] partnerIds = (Object[]) row
//                            .get("partner_ids");
//                    String customers = "";
//                    if (partnerIds != null) {
//                        for (Object id : partnerIds) {
//                            customers += ";" + getPartnerRouteInfo(id, login, passwd);
//                        }
//                        customers += ";";
//                        detail.setPartners(customers);
//                    }
                    detail.setId(Long.valueOf(row.getID()));
                    if (row.get("comercial_id") != null)
                        detail.setComercialId(Long.valueOf((Integer) ((Object[]) row.get("comercial_id"))[0]));
                    detail.setName((String)row.get("name"));
                    detail.setCode((String) row.get("code"));
                    if (row.get("day_id") != null)
                        detail.setDayId(Long.valueOf((Integer) ((Object[]) row.get("day_id"))[0]));
                    toSave.add(detail);
                    result++;
                }
                for (Route route : toSave)
                    saveOrUpdate(route);
                try {
                    Date endDataSynchro = new Date();
                    Long duration = endDataSynchro.getTime() - initDate;
                    SynchronizationRepository.getInstance().saveOrUpdate(
                            Synchronization.create(Route.class.getName(),
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

    private String getPartnerRouteInfo(Object relationId, String login, String passwd) throws ConfigurationException {
        Session openERPSession = null;
        try {
            openERPSession = SessionFactory.getInstance(login, passwd).getSession();
        } catch (Exception e) {
            // do nothing, openERPSession will be null
        }
        if (openERPSession != null) {
            ObjectAdapter adapter;
            try {
                adapter = openERPSession.getObjectAdapter("partner.route.info");
            } catch (XmlRpcException e) {
                throw new ConfigurationException(e);
            } catch (OpeneERPApiException e) {
                throw new ConfigurationException(e);
            }
            FilterCollection filters = new FilterCollection();
            RowCollection entities;
            String[] fieldsRemote = {"id", "partner_id"};
            try {
                filters.add("id", "=", relationId);
            } catch (OpeneERPApiException e) {
                throw new ConfigurationException(e);
            }
            try {
                entities = adapter.searchAndReadObject(filters, fieldsRemote,
                        "1900-01-01 00:00:00");
                Row row = entities.get(0);
                return row.get("partner_id") != null ? ((Object[])row.get("partner_id"))[0].toString() : null;
            } catch (XmlRpcException e) {
                throw new ConfigurationException(e);
            } catch (OpeneERPApiException e) {
                throw new ConfigurationException(e);
            }
        }
        return null;
    }

    private RouteRepository() {
        dao = RouteDAO.getInstance();
    }
}
