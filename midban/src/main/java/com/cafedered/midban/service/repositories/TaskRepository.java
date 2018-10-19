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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.midban.dao.TaskDAO;
import com.cafedered.midban.entities.Task;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;

public class TaskRepository extends BaseRepository<Task, TaskDAO> {
    private static TaskRepository instance = null;

    public static TaskRepository getInstance() {
        if (instance == null)
            instance = new TaskRepository();
        return instance;
    }

    private TaskRepository() {
        dao = TaskDAO.getInstance();
    }

    public Map<Date, List<Task>> getTasksInFuture() throws ServiceException {
        Map<Date, List<Task>> result = new HashMap<Date, List<Task>>();
        try {
            List<Task> tasks = dao.getAll(0, 100000);
            for (Task task : tasks) {
                Date date = DateUtil
                        .parseDate(task.getInitDate(), "yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (date.compareTo(calendar.getTime()) >= 0) {
                    if (result.get(date) == null)
                        result.put(date, new ArrayList<Task>());
                    result.get(date).add(task);
                }
            }
        } catch (ConfigurationException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (DatabaseException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (ParseException e) {
            // unreachable
        }
        return result;
    }
}
