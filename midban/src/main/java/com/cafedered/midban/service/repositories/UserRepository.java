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

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.dao.UserDAO;
import com.cafedered.midban.entities.Route;
import com.cafedered.midban.entities.User;
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

import java.util.ArrayList;
import java.util.List;

public class UserRepository extends BaseRepository<User, UserDAO> {
	private static UserRepository instance;

	private UserRepository() {
		dao = UserDAO.getInstance();
	}

	public static UserRepository getInstance() {
		if (instance == null)
			instance = new UserRepository();
		return instance;
	}

    public boolean authenticateUserInDB(String username, String pass)
            throws ServiceException {
		User user = User.create(username, pass, null);
		try {
			List<User> users = getByExample(user, Restriction.AND, true, 0, 100000);
			if (users != null && users.size() > 0) {
				User actualUser = users.get(0);
				// comprobación de si el usuario pertenece a la compañía
				if (!(MidbanApplication.activeCompany == actualUser.getCompanyId().intValue())){
					return false;
				}
			}
            return users.size() >= 1;
		} catch (ServiceException e) {
            throw e;
		}
	}

	public void synchronizeUsers(String username, String pass) throws ConfigurationException {
		Session openERPSession = null;
		try {
			openERPSession = SessionFactory.getInstance(username, pass).getSession();
		} catch (Exception e) {
			// do nothing, openERPSession will be null
		}
		if (openERPSession != null) {
			ObjectAdapter adapter;
			try {
				adapter = openERPSession.getObjectAdapter("res.users");
			} catch (XmlRpcException e) {
				throw new ConfigurationException(e);
			} catch (OpeneERPApiException e) {
				throw new ConfigurationException(e);
			}
			FilterCollection filters = new User().getRemoteFilters();
			RowCollection entities;
			String[] fieldsRemote = {"id", "login", "company_id"};
			try {
				entities = adapter.searchAndReadObject(filters, fieldsRemote,
						"1900-01-01 00:00:00");
				List<User> toSave = new ArrayList<User>();
				if (entities.size() > 0)
					for (User user : getAll(0, 1000000))
						delete(user.getId());
				for (Row row : entities) {
					User detail = new User();
					Object[] routeIds = null;
					String routes = "";
					if (routeIds != null) {
						for (Object id : routeIds) {
							routes += ";" + id;
						}
						routes += ";";
						detail.setRouteIds(routes);
					}
					detail.setId(Long.valueOf(row.getID()));
					detail.setLogin((String)row.get("login"));
					detail.setCompanyId(((Integer)((Object[])row.get("company_id"))[0]).longValue());


					User example = new User();
					example.setLogin(detail.getLogin());
					List<User> users = getByExample(example, Restriction.AND, true, 0, 1);
					if (users != null && users.size() > 0) {
						User previous = users.get(0);
						if (previous != null) {
							detail.setPasswd(previous.getPasswd());
//							detail.setId(previous.getId());
						}
					}
					toSave.add(detail);
				}
				for (User user : toSave)
					saveOrUpdate(user);
			} catch (XmlRpcException e) {
				throw new ConfigurationException(e);
			} catch (OpeneERPApiException e) {
				throw new ConfigurationException(e);
			} catch (ServiceException e) {
				throw new ConfigurationException(e);
			}
		}
	}

	public boolean isUserLoggedLastTwoHours() {
		return dao.isUserLoggedLastTwoHours();
	}

	public User getLastUserLogged() {
		return dao.getUserLoggedLastTwoHours();
	}
}
