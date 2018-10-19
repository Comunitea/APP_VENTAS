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

import java.util.Collection;
import java.util.List;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.dao.PartnerDAO;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.User;
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

public class PartnerRepository extends BaseRepository<Partner, PartnerDAO> {
    private static PartnerRepository instance = null;

    public static PartnerRepository getInstance() {
        if (instance == null)
            instance = new PartnerRepository();
        return instance;
    }

    private PartnerRepository() {
        dao = PartnerDAO.getInstance();
    }

//    public void synchronizeDeliveryDays(String login, String passwd) throws ServiceException, ConfigurationException {
//        int totalNumOfItems = getCount();
//        int offset = 0;
//        do {
//            List<Partner> partners = getAll(offset, 100);
//            offset = offset + 100;
//            for (Partner partner : partners) {
//                Session openERPSession = null;
//                try {
//                    openERPSession = SessionFactory.getInstance(login, passwd)
//                            .getSession();
//                } catch (Exception e) {
//                    // do nothing, openERPSession will be null
//                }
//                if (openERPSession != null) {
//                    ObjectAdapter adapter;
//                    try {
//                        adapter = openERPSession
//                                .getObjectAdapter("res.partner");
//                        FilterCollection filters = new FilterCollection();
//                        try {
//                            filters.add("id", "=", partner.getId());
//                        } catch (OpeneERPApiException e) {
//                            e.printStackTrace();
//                        }
//                        RowCollection entities;
//                        String[] fieldsRemote = {"delivery_days_ids"};
//                        entities = adapter.searchAndReadObject(filters,
//                                fieldsRemote, "1900-01-01 00:00:00");
//                        for (Row row : entities) {
//                            try {
//                                Object[] dayIds = (Object[]) row
//                                        .get("delivery_days_ids");
//                                if (dayIds != null) {
//                                    for (Object aDay : dayIds) {
//                                        if (partner.getDeliveryDays() == null)
//                                            partner.setDeliveryDays("");
//                                        partner.setDeliveryDays(partner.getDeliveryDays() + aDay.toString() + ";");
//                                    }
//                                    saveOrUpdate(partner);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } while(offset < totalNumOfItems);
//    }

    public List<Partner> getAllForUser(Number userId, Integer numElements,
            Integer offset) throws ServiceException {
        Partner partner = new Partner();
        partner.setUserId(userId);
        try {
            return dao.getByExample(partner, Restriction.AND, true,
                    numElements, offset);
        } catch (DatabaseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Partner> getByExample(Partner entity, Restriction restriction,
                                boolean exactMatching, Integer numElements, Integer offset)
            throws ServiceException {
        try {
            User user =
                    ((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER));
            entity.setUserId(user.getId());
            return dao.getByExample(entity, restriction, exactMatching,
                    numElements, offset);
        } catch (DatabaseException e) {
            throw new ServiceException("Cannot retrieve objects", e);
        }
    }

    public List<Partner> getByExampleUser(Partner partner, Restriction restriction, boolean exactMatching, int offset, int limit) throws ServiceException {
        try {
            return dao.getByExampleUser(partner, restriction, exactMatching, offset, limit);
        } catch (DatabaseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
