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

import com.cafedered.midban.dao.ConfigurationDAO;
import com.cafedered.midban.entities.Configuration;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;

public class ConfigurationRepository extends
		BaseRepository<Configuration, ConfigurationDAO> {
	private static ConfigurationRepository instance;
	
	private ConfigurationRepository() {
		dao = ConfigurationDAO.getInstance();
	}
	
	public static ConfigurationRepository getInstance() {
		if (instance == null)
			instance = new ConfigurationRepository();
		return instance;
	}

	public Configuration getConfiguration() throws ConfigurationException, ServiceException {
		return getById(1L);
	}
}
