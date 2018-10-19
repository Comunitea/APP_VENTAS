package com.cafedered.midban.service.repositories;

import com.cafedered.midban.dao.PaymentModeDAO;
import com.cafedered.midban.entities.PaymentMode;


public class PaymentModeRepository extends BaseRepository<PaymentMode, PaymentModeDAO> {
    private static PaymentModeRepository instance = null;

    public static PaymentModeRepository getInstance() {
        if (instance == null)
            instance = new PaymentModeRepository();
        return instance;
    }

    private PaymentModeRepository() {
        dao = PaymentModeDAO.getInstance();
    }
}

