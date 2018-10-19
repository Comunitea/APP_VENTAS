package com.cafedered.midban.dao;

import com.cafedered.midban.entities.AccountPaymentTerm;


public class AccountPaymentTermDAO extends BaseDAO<AccountPaymentTerm> {

    private static AccountPaymentTermDAO instance;

    public static AccountPaymentTermDAO getInstance() {
        if (instance == null)
            instance = new AccountPaymentTermDAO();
        return instance;
    }
}
