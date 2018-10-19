package com.cafedered.midban.dao;

import com.cafedered.midban.entities.PaymentMode;


public class PaymentModeDAO extends BaseDAO<PaymentMode> {

    private static PaymentModeDAO instance;

    public static PaymentModeDAO getInstance() {
        if (instance == null)
            instance = new PaymentModeDAO();
        return instance;
    }
}
