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

import com.cafedered.midban.dao.AccountJournalDAO;
import com.cafedered.midban.dao.AccountMoveLineDAO;
import com.cafedered.midban.entities.AccountJournal;
import com.cafedered.midban.entities.AccountMoveLine;

public class AccountJournalRepository extends BaseRepository<AccountJournal, AccountJournalDAO> {
    private static AccountJournalRepository instance = null;

    public static AccountJournalRepository getInstance() {
        if (instance == null)
            instance = new AccountJournalRepository();
        return instance;
    }

    private AccountJournalRepository() {
        dao = AccountJournalDAO.getInstance();
    }
}
