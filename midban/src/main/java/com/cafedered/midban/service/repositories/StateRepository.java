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

import java.util.HashMap;
import java.util.Map;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.midban.dao.StateDAO;
import com.cafedered.midban.entities.State;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;

public class StateRepository extends BaseRepository<State, StateDAO> {
    private static StateRepository instance = null;

    private static final int MAX_CACHE_CAPACITY = 60;

    Map<Long, State> cachedEntities = new HashMap<Long, State>();

    public static StateRepository getInstance() {
        if (instance == null)
            instance = new StateRepository();
        return instance;
    }

    @Override
    public State getById(Long id) throws ConfigurationException,
            ServiceException {
        if (!cachedEntities.containsKey(id)) {
            if (cachedEntities.keySet().size() == MAX_CACHE_CAPACITY)
                cachedEntities
                        .remove(cachedEntities.keySet().iterator().next());
            cachedEntities.put(id, super.getById(id));
        }
        return cachedEntities.get(id);
    }

    @Override
    public void saveOrUpdate(State entity) throws ServiceException {
        try {
            if (!cachedEntities.containsKey(entity.getId())) {
                dao.saveOrUpdate(entity);
                cachedEntities.put(entity.getId(), entity);
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new ServiceException("Cannot access data.", e);
        }
    }

    private StateRepository() {
        dao = StateDAO.getInstance();
    }
}
