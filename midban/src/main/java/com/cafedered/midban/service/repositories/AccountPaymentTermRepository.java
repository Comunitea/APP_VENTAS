package com.cafedered.midban.service.repositories;

import com.cafedered.midban.dao.AccountPaymentTermDAO;
import com.cafedered.midban.entities.AccountPaymentTerm;


public class AccountPaymentTermRepository extends BaseRepository<AccountPaymentTerm, AccountPaymentTermDAO> {
    private static AccountPaymentTermRepository instance = null;

    public static AccountPaymentTermRepository getInstance() {
        if (instance == null)
            instance = new AccountPaymentTermRepository();
        return instance;
    }

    private AccountPaymentTermRepository() {
        dao = AccountPaymentTermDAO.getInstance();
    }

}
