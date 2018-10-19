package com.cafedered.midban.service.repositories;

import com.cafedered.midban.dao.WarehouseDAO;
import com.cafedered.midban.dao.WeekDayDAO;
import com.cafedered.midban.entities.Warehouse;
import com.cafedered.midban.entities.WeekDay;

/**
 * Created by nacho on 29/11/15.
 */
public class WarehouseRepository extends BaseRepository<Warehouse, WarehouseDAO> {
    private static WarehouseRepository instance = null;

    public static WarehouseRepository getInstance() {
        if (instance == null)
            instance = new WarehouseRepository();
        return instance;
    }

    private WarehouseRepository() {
        dao = WarehouseDAO.getInstance();
    }
}
