package com.cafedered.midban.dao;

import com.cafedered.midban.entities.PricelistPrices;

public class PricelistPricesDAO extends BaseDAO<PricelistPrices> {

    private static PricelistPricesDAO instance;

    public static PricelistPricesDAO getInstance() {
        if (instance == null)
            instance = new PricelistPricesDAO();
        return instance;
    }


}
