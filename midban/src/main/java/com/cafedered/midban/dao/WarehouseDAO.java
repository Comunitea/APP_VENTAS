package com.cafedered.midban.dao;

import com.cafedered.midban.entities.WeekDay;
import com.cafedered.midban.entities.Warehouse;

/**
 * Created by nacho on 29/11/15.
 */
public class WarehouseDAO extends BaseDAO<Warehouse> {

    private static WarehouseDAO instance;

    public static WarehouseDAO getInstance() {
        if (instance == null)
            instance = new WarehouseDAO();
        return instance;
    }
}