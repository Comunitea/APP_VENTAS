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
package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.debortoliwines.openerp.api.OpenERPXmlRpcProxy.RPCProtocol;

@SuppressWarnings("serial")
@Entity(tableName = "configuration")
public class Configuration extends BaseEntity {
    @Id(column = "id", autoIncrement = false)
    private Long id;
    @Property(columnName = "protocol")
    private String protocol;
    @Property(columnName = "url_openerp")
    private String urlOpenErp;
    @Property(columnName = "port_openerp")
    private Number portOpenErp;
    @Property(columnName = "db_openerp")
    private String dbOpenErp;
    @Property(columnName = "default_username")
    private String username;
	@Property(columnName = "warehouse_id")
	Number warehouseId;
//	@Property(columnName = "email_logs")
//	private String emailLogs;

	public Configuration() {
		super();
	}

	public static Configuration create(String urlOpenErp, Integer portOpenErp,
			String dbOpenErp, String username) {
		return new Configuration(urlOpenErp, portOpenErp, dbOpenErp, username);
    }

	protected Configuration(String urlOpenErp, Integer portOpenErp,
			String dbOpenErp, String username) {
        super();
		this.urlOpenErp = urlOpenErp;
		this.portOpenErp = portOpenErp;
		this.dbOpenErp = dbOpenErp;
        this.username = username;
//		this.emailLogs = emailLogs;
    }

//	public String getEmailLogs() {
//		return emailLogs;
//	}
//
//	public void setEmailLogs(String emailLogs) {
//		this.emailLogs = emailLogs;
//	}


	public Number getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Number warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getUrlOpenErp() {
		return urlOpenErp;
	}

	public void setUrlOpenErp(String urlOpenErp) {
		this.urlOpenErp = urlOpenErp;
	}

    public Number getPortOpenErp() {
		return portOpenErp;
	}

    public void setPortOpenErp(Number portOpenErp) {
		this.portOpenErp = portOpenErp;
	}

	public String getDbOpenErp() {
		return dbOpenErp;
	}

	public void setDbOpenErp(String dbOpenErp) {
		this.dbOpenErp = dbOpenErp;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Configuration [id=" + id + ", URL_OPENERP=" + urlOpenErp
				+ ", PORT_OPENERP=" + portOpenErp + ", DB_OPENERP="
				+ dbOpenErp + ", username=" + username + "]";
	}

    public RPCProtocol getProtocolRCP() {
        if (getProtocol().equals("http"))
            return RPCProtocol.RPC_HTTP;
        else
            return RPCProtocol.RPC_HTTPS;
    }

}
