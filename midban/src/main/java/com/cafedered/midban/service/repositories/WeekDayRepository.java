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

import com.cafedered.midban.dao.CompanyDAO;
import com.cafedered.midban.dao.WeekDayDAO;
import com.cafedered.midban.entities.Company;
import com.cafedered.midban.entities.WeekDay;

public class WeekDayRepository extends BaseRepository<WeekDay, WeekDayDAO> {
    private static WeekDayRepository instance = null;

    public static WeekDayRepository getInstance() {
        if (instance == null)
            instance = new WeekDayRepository();
        return instance;
    }

    private WeekDayRepository() {
        dao = WeekDayDAO.getInstance();
    }
}
