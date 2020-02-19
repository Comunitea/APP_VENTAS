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

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.dao.InvoiceDAO;
import com.cafedered.midban.entities.AccountJournal;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.entities.Voucher;
import com.cafedered.midban.pdf.creators.InvoicePdfCreator;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.ReflectionUtils;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpenERPCommand;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.Session;

public class InvoiceRepository extends BaseRepository<Invoice, InvoiceDAO> {

    private static InvoiceRepository instance = null;

    public static InvoiceRepository getInstance() {
        if (instance == null)
            instance = new InvoiceRepository();
        return instance;
    }

    private InvoiceRepository() {
        dao = InvoiceDAO.getInstance();
    }

    public List<Invoice> getInvoicesWithFilters(String state, String dateFrom,
            String dateTo, Float amountLessThan, Float amountMoreThan,
            Long partnerId) {
        return dao.getInvoicesWithFilters(state, dateFrom, dateTo,
                amountLessThan, amountMoreThan, partnerId);
    }

    @SuppressWarnings("unchecked")
    public void sendInvoiceAsPdf(Invoice invoice) throws Exception {
        User user = (User) MidbanApplication
                .getValueFromContext(ContextAttributes.LOGGED_USER);
        Session openERPSession = SessionFactory.getInstance(user.getLogin(),
                user.getPasswd()).getSession();
        OpenERPCommand command = new OpenERPCommand(openERPSession);
        Integer[] id = { invoice.getId().intValue() };
        HashMap<String, Object> invoiceSent = (HashMap<String, Object>) command
                .callObjectFunction(
                        invoice.getClass().getAnnotation(Remote.class).object(),
                        "action_invoice_sent", id);
        if (LoggerUtil.isDebugEnabled())
            Log.i(this.getClass().getName(), "Pasamos action_invoice_sent -> "
                    + invoiceSent);
        String[] strings = new String[] { "composition_mode", "res_id",
                "template_id", "model", "use_template" };
        Object[] context = { strings, invoiceSent.get("context") };
        HashMap<String, Object> mailWizard = (HashMap<String, Object>) command
                .callObjectFunction(invoiceSent.get("res_model").toString(),
                        "default_get", context);
        if (LoggerUtil.isDebugEnabled())
            Log.i(this.getClass().getName(), "Pasamos default_get -> "
                    + mailWizard);
        Object[] contextMailWizard = { mailWizard };
        Integer idAsistenteEnvio = (Integer) command.callObjectFunction(
                invoiceSent.get("res_model").toString(), "create",
                contextMailWizard);
        if (LoggerUtil.isDebugEnabled())
            Log.i(this.getClass().getName(), "Pasamos create -> "
                    + idAsistenteEnvio);
        Integer[] idAsistenteEnvioArray = { idAsistenteEnvio };
        command.callObjectFunction(invoiceSent.get("res_model").toString(),
                "send_mail", idAsistenteEnvioArray);
        if (LoggerUtil.isDebugEnabled())
            Log.i(this.getClass().getName(), "Pasamos send_mail");
    }

    public File getInvoiceAsPdf(Invoice invoice) throws ConfigurationException,
            ServiceException {
        return InvoicePdfCreator.generateFile(getById(invoice.getId()));
    }

    public void makePayment(User user, Invoice invoice, double amount,
            AccountJournal type, String checkNumber, String checkDueDate,
            String account) throws ServiceException {
        Integer voucherId = VoucherRepository.getInstance().getNextIdNumber();
        Voucher voucher = new Voucher();
        voucher.setName(invoice.getNumber());
        voucher.setPeriodId(invoice.getPeriodId());
        voucher.setAmount(amount);
        voucher.setDate(DateUtil.toFormattedString(new Date(), "yyyy-MM-dd"));
        try {
            voucher.setDueDate(DateUtil.toFormattedString(
                    DateUtil.parseDate(checkDueDate, "dd.MM.yyyy"),
                    "yyyy-MM-dd"));
        } catch (ParseException e) {
            // do nothing, this happens when checkDueDate is null (cash paid).
        }
        voucher.setJournalId(type.getId().intValue());
        voucher.setPartnerId(invoice.getPartner().getCommercialPartnerId());
        voucher.setAccountId(invoice.getAccountId());
        voucher.setState("posted");
        voucher.setType("receipt");
        try {
            voucherId = synchronizePayment(user, voucher, invoice);
        } catch (Exception e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            voucher.setPendingSynchronization(1);
            throw new ServiceException(
                    "No se ha podido enviar el pago al servidor. Sincronice manualmente.",
                    e);
        }
        voucher.setId(voucherId.longValue());
        VoucherRepository.getInstance().saveOrUpdate(voucher);
        try {
            getRemoteObjects(invoice, user.getLogin(), user.getPasswd(), false);
        } catch (Exception e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
            throw new ServiceException(
                    "Se ha enviado el pago, pero no se han sincronizado las facturas. Sincronice manualmente.",
                    e);
        }
    }

    private int synchronizePayment(User user, Voucher voucher, Invoice invoice)
            throws Exception {
        Session openERPSession = SessionFactory.getInstance(user.getLogin(),
                user.getPasswd()).getSession();

        ObjectAdapter voucherAdapter = openERPSession.getObjectAdapter(voucher
                .getClass().getAnnotation(Remote.class).object());
        Map<String, String> fields = getRemoteFields(voucher);
        String[] fieldNames = new String[fields.keySet().size()];
        fieldNames = fields.keySet().toArray(fieldNames);
        Row newVoucher = voucherAdapter.getNewRow(fieldNames);
        for (String field : fieldNames) {
            newVoucher.put(
                    field,
                    ReflectionUtils.getValue(
                            voucher,
                            ReflectionUtils.getField(Voucher.class,
                                    fields.get(field))));
        }
        voucherAdapter.createObject(newVoucher);

        OpenERPCommand command = new OpenERPCommand(openERPSession);
        Object[] id = { newVoucher.getID() };
        Map params[] = new Map[1];
        params[0] = new HashMap();
        params[0].put("invoice_id", invoice.getId().intValue());
        Object[] obj = { id,
                voucher.getPartner().getCommercialPartnerId().intValue(),
                voucher.getJournalId().intValue(),
                voucher.getAmount().doubleValue(),
                Integer.valueOf("1").intValue(), voucher.getType(),
                voucher.getDate(), params[0] };
        HashMap<String, Object> recalculatedLines = (HashMap<String, Object>) command
                .callObjectFunction(
                        voucher.getClass().getAnnotation(Remote.class).object(),
                        "recompute_voucher_lines", obj);
        Object[] lineCR = (Object[]) ((HashMap) recalculatedLines.get("value"))
                .get("line_cr_ids");
        for (Object o : lineCR) {
            ((HashMap) o).put("voucher_id", newVoucher.getID());
            OpenERPCommand commandAddLineCr = new OpenERPCommand(openERPSession);
            commandAddLineCr
                    .createObject("account.voucher.line", ((HashMap) o));
        }
        ((HashMap<String, Object>) recalculatedLines.get("value"))
                .remove("line_cr_ids");
        Object[] lineDR = (Object[]) ((HashMap) recalculatedLines.get("value"))
                .get("line_dr_ids");
        for (Object o : lineDR) {
            ((HashMap) o).put("voucher_id", newVoucher.getID());
            OpenERPCommand commandAddLineDr = new OpenERPCommand(openERPSession);
            commandAddLineDr
                    .createObject("account.voucher.line", ((HashMap) o));
        }
        ((HashMap<String, Object>) recalculatedLines.get("value"))
                .remove("line_dr_ids");

        command.writeObject("account.voucher", newVoucher.getID(),
                recalculatedLines);

        OpenERPCommand workflowCommand = new OpenERPCommand(openERPSession);
        workflowCommand.executeWorkflow(
                voucher.getClass().getAnnotation(Remote.class).object(),
                "proforma_voucher", newVoucher.getID());

        return newVoucher.getID();
    }

    public BigDecimal getAmountDebt(Long partnerId) {
        BigDecimal result = BigDecimal.ZERO;
        for (Invoice invoice : dao.getInvoicesWithFilters("open", null, null,
                null, null, partnerId)) {
            result = result.add(new BigDecimal(invoice.getAmountTotal().floatValue()).setScale(2, RoundingMode.HALF_UP));
        }
        return result.setScale(2, RoundingMode.HALF_UP);
    }
    // private int synchronizePayment(User user, Voucher voucher, Invoice
    // invoice)
    // throws Exception {
    // Session openERPSession = SessionFactory.getInstance(user.getLogin(),
    // user.getPasswd()).getSession();
    //
    // ObjectAdapter voucherAdapter = openERPSession.getObjectAdapter(voucher
    // .getClass().getAnnotation(Remote.class).object());
    // Map<String, String> fields = getRemoteFields(voucher);
    // String[] fieldNames = new String[fields.keySet().size()];
    // fieldNames = fields.keySet().toArray(fieldNames);
    // Row newVoucher = voucherAdapter.getNewRow(fieldNames);
    // for (String field : fieldNames) {
    // newVoucher.put(
    // field,
    // ReflectionUtils.getValue(
    // voucher,
    // ReflectionUtils.getField(Voucher.class,
    // fields.get(field))));
    // }
    // voucherAdapter.createObject(newVoucher);
    //
    // OpenERPCommand command = new OpenERPCommand(openERPSession);
    // Object[] id = { voucher.getPartner().getCommercialPartnerId(),
    // voucher.getAmount(), invoice.getId().intValue(), true,
    // voucher.getType(), voucher.getJournalId(), true,
    // voucher.getDate(), voucher.getPeriodId() };
    // Map params[] = new Map[1];
    // params[0] = new HashMap();
    // params[0].put("invoice_id", invoice.getId().intValue());
    // Object[] obj = { id,
    // voucher.getPartner().getCommercialPartnerId().intValue(),
    // voucher.getJournalId().intValue(),
    // voucher.getAmount().doubleValue(),
    // Integer.valueOf("1").intValue(), voucher.getType(),
    // voucher.getDate(), params[0] };
    // // HashMap<String, Object> recalculatedLines = (HashMap<String, Object>)
    // // command
    // // .callObjectFunction(
    // // voucher.getClass().getAnnotation(Remote.class).object(),
    // // "recompute_voucher_lines", obj);
    // // Object[] lineCR = (Object[]) ((HashMap<String, Object>)
    // // recalculatedLines
    // // .get("value")).get("line_cr_ids");
    // // for (Object o : lineCR) {
    // // if (o instanceof HashMap<?, ?>) {
    // // ((HashMap) o).put("voucher_id", o);
    // // OpenERPCommand commandAddLineCr = new OpenERPCommand(
    // // openERPSession);
    // // commandAddLineCr.createObject("account.voucher.line",
    // // ((HashMap) o));
    // // }
    // // }
    // // ((HashMap<String, Object>) recalculatedLines.get("value"))
    // // .remove("line_cr_ids");
    // // Object[] lineDR = (Object[]) ((HashMap<String, Object>)
    // // recalculatedLines
    // // .get("value")).get("line_dr_ids");
    // // for (Object o : lineDR) {
    // // if (o instanceof HashMap<?, ?>) {
    // // ((HashMap) o).put("voucher_id", o);
    // // OpenERPCommand commandAddLineDr = new OpenERPCommand(
    // // openERPSession);
    // // commandAddLineDr.createObject("account.voucher.line",
    // // ((HashMap) o));
    // // }
    // // }
    // // ((HashMap<String, Object>) recalculatedLines.get("value"))
    // // .remove("line_dr_ids");
    //
    // // command.writeObject("account.voucher", newVoucher.getID(),
    // // recalculatedLines);
    //
    // OpenERPCommand workflowCommand = new OpenERPCommand(openERPSession);
    // workflowCommand.executeWorkflow(
    // voucher.getClass().getAnnotation(Remote.class).object(),
    // "proforma_voucher", newVoucher.getID());
    // // VoucherLine voucherLine = new VoucherLine();
    // // voucherLine.setAccountId(voucher.getAccountId());
    // // voucherLine.setAmount(voucher.getAmount());
    // // voucherLine.setDueDate(voucher.getDueDate());
    // // voucherLine.setPartnerId(voucher.getPartnerId());
    // // voucherLine.setVoucherId(newVoucher.getID());
    // // voucherLine.setName(voucher.getName());
    // // voucherLine.setType("cr");
    // // ObjectAdapter voucherLineAdapter = openERPSession
    // // .getObjectAdapter(voucherLine.getClass()
    // // .getAnnotation(Remote.class).object());
    // // Map<String, String> fieldsMapLines = getRemoteFields(voucherLine);
    // // String[] lineFields = new String[fieldsMapLines.keySet().size()];
    // // lineFields = fieldsMapLines.keySet().toArray(lineFields);
    // // Row newVoucherLine = voucherLineAdapter.getNewRow(lineFields);
    // // for (String aLineField : lineFields)
    // // newVoucherLine.put(aLineField, ReflectionUtils.getValue(
    // // voucherLine,
    // // ReflectionUtils.getField(VoucherLine.class,
    // // fieldsMapLines.get(aLineField))));
    // // voucherLineAdapter.createObject(newVoucherLine);
    // return newVoucher.getID();
    // }
}
