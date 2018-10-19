package com.cafedered.midban.service.repositories;

import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpenERPCommand;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.Session;

public class ReservationRepository {

    private static ReservationRepository instance;

    public static ReservationRepository getInstance() {
        if (instance == null)
            instance = new ReservationRepository();
        return instance;
    }

    public boolean createReservation(Long partnerId, Long productId,
            Integer productUom, Double productUomQty, Double priceUnit,
            String login, String passwd)
            throws ServiceException {
        try {
            Session openERPSession;
            openERPSession = SessionFactory.getInstance(login, passwd)
                    .getSession();
            ObjectAdapter adapter = openERPSession
                    .getObjectAdapter("stock.reservation");
            String[] fieldsRemote = { "name", "product_id", "product_uom_qty",
                    "product_uom", "partner_id", "partner_id2", "price_unit" };
            Row newRow = adapter.getNewRow(fieldsRemote);
            newRow.put("name", "/");
            newRow.put("product_id", productId);
            newRow.put("partner_id", partnerId);
            newRow.put("partner_id2", partnerId);
            newRow.put("product_uom", productUom);
            newRow.put("product_uom_qty", productUomQty);
            newRow.put("price_unit", priceUnit);
            adapter.createObject(newRow);
            OpenERPCommand command = new OpenERPCommand(SessionFactory
                    .getInstance(login, passwd).getSession());
            command.executeWorkflow("stock.reservation", "confirm_reserve",
                    newRow.getID());
            return true;
        } catch (Exception e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            return false;
        }
    }
}
