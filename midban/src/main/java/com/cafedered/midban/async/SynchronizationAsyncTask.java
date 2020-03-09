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
package com.cafedered.midban.async;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.R;
import com.cafedered.midban.entities.Account;
import com.cafedered.midban.entities.AccountJournal;
import com.cafedered.midban.entities.AccountMoveLine;
import com.cafedered.midban.entities.AccountPaymentTerm;
import com.cafedered.midban.entities.Company;
import com.cafedered.midban.entities.Contact;
import com.cafedered.midban.entities.Invoice;
import com.cafedered.midban.entities.InvoiceLine;
import com.cafedered.midban.entities.Order;
import com.cafedered.midban.entities.OrderLine;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.PartnerCategory;
import com.cafedered.midban.entities.PartnerProduct;
import com.cafedered.midban.entities.PaymentMode;
import com.cafedered.midban.entities.PricelistPrices;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.ProductCategory;
import com.cafedered.midban.entities.ProductPackaging;
import com.cafedered.midban.entities.ProductPartnerPrice;
import com.cafedered.midban.entities.ProductTemplate;
import com.cafedered.midban.entities.ProductUl;
import com.cafedered.midban.entities.ProductUom;
import com.cafedered.midban.entities.ProductUomCateg;
import com.cafedered.midban.entities.RouteDetail;
import com.cafedered.midban.entities.Shop;
import com.cafedered.midban.entities.StockMove;
import com.cafedered.midban.entities.StockPickingOut;
import com.cafedered.midban.entities.Synchronization;
import com.cafedered.midban.entities.SynchronizationSummary;
import com.cafedered.midban.entities.Tax;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.entities.Voucher;
import com.cafedered.midban.entities.VoucherLine;
import com.cafedered.midban.entities.WeekDay;
import com.cafedered.midban.entities.Warehouse;
import com.cafedered.midban.service.repositories.AccountJournalRepository;
import com.cafedered.midban.service.repositories.AccountMoveLineRepository;
import com.cafedered.midban.service.repositories.AccountPaymentTermRepository;
import com.cafedered.midban.service.repositories.AccountRepository;
import com.cafedered.midban.service.repositories.CompanyRepository;
import com.cafedered.midban.service.repositories.ConfigurationRepository;
import com.cafedered.midban.service.repositories.ContactRepository;
import com.cafedered.midban.service.repositories.InvoiceLineRepository;
import com.cafedered.midban.service.repositories.InvoiceRepository;
import com.cafedered.midban.service.repositories.OrderLineRepository;
import com.cafedered.midban.service.repositories.OrderRepository;
import com.cafedered.midban.service.repositories.PartnerCategoryRepository;
import com.cafedered.midban.service.repositories.PartnerProductRepository;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.PaymentModeRepository;
import com.cafedered.midban.service.repositories.PaymentTypeRepository;
import com.cafedered.midban.service.repositories.PricelistPricesRepository;
import com.cafedered.midban.service.repositories.ProductCategoryRepository;
import com.cafedered.midban.service.repositories.ProductPackagingRepository;
import com.cafedered.midban.service.repositories.ProductPartnerPriceRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.service.repositories.ProductTemplateRepository;
import com.cafedered.midban.service.repositories.ProductUlRepository;
import com.cafedered.midban.service.repositories.ProductUomCategRepository;
import com.cafedered.midban.service.repositories.ProductUomRepository;
import com.cafedered.midban.service.repositories.RouteDetailRepository;
import com.cafedered.midban.service.repositories.RouteRepository;
import com.cafedered.midban.service.repositories.ShopRepository;
import com.cafedered.midban.service.repositories.StockMoveRepository;
import com.cafedered.midban.service.repositories.StockPickingOutRepository;
import com.cafedered.midban.service.repositories.SynchronizationRepository;
import com.cafedered.midban.service.repositories.SynchronizationSummaryRepository;
import com.cafedered.midban.service.repositories.TaxRepository;
import com.cafedered.midban.service.repositories.UserRepository;
import com.cafedered.midban.service.repositories.VoucherLineRepository;
import com.cafedered.midban.service.repositories.VoucherRepository;
import com.cafedered.midban.service.repositories.WarehouseRepository;
import com.cafedered.midban.service.repositories.WeekDayRepository;
import com.cafedered.midban.utils.DateUtil;
import com.cafedered.midban.utils.GMailSender;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.MessagesForUser;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.utils.exceptions.SynchronizationErrorException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpenERPCommand;
import com.debortoliwines.openerp.api.OpeneERPApiException;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.debortoliwines.openerp.api.Session;

public class SynchronizationAsyncTask extends AsyncTask<User, String, Boolean> {

    private ProgressBar progress;
    private TextView messagesArea;
    private Context context;
    private Integer currentProgress;
    public static int MAX_PROGRESS = 27;
    private String[] lastPublishedProgress;

    private static SynchronizationAsyncTask instance;

    public static SynchronizationAsyncTask getInstance(ProgressBar bar, TextView messages,
                                                       Context context) {
        if (instance == null || instance.getStatus().equals(Status.FINISHED))
            instance = new SynchronizationAsyncTask(bar, messages, context);
        else {
            instance.setProgress(bar);
            instance.setMessagesArea(messages);
            instance.setContext(context);
            if (instance.getCurrentProgress() != null && instance.getCurrentProgress() != -1 && instance.getCurrentProgress() != MAX_PROGRESS) {
                instance.publishProgress(instance.getLastPublishedProgress());
                instance.getProgress().setVisibility(View.VISIBLE);
                instance.getProgress().setMax(MAX_PROGRESS);
                instance.getProgress().setProgress(instance.getCurrentProgress());
            }
        }
        return instance;
    }

    public SynchronizationAsyncTask(ProgressBar bar, TextView messages,
                                    Context context) {
        super();
        this.progress = bar;
        this.progress.setMax(MAX_PROGRESS);
        this.currentProgress = -1;
        this.messagesArea = messages;
        this.context = context;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }


    @SuppressLint("WrongThread")
    @SuppressWarnings("finally")
    @Override
    protected Boolean doInBackground(User... params) {
        User user = params[0];
        Long initTime = new Date().getTime();
        String error = "";
        try {
            lastPublishedProgress = new String[] { "Sincronización en curso...",
                    "" + currentProgress++ };
            publishProgress(lastPublishedProgress);
            // try {
            // publishProgress(new String[] {
            // "Sincronizando Clientes (Provincias)...",
            // "" + currentProgress++ });
            // StateRepository.getInstance().getRemoteObjects(new State(),
            // user.getLogin(), user.getPasswd());
            //
            // } catch (ConfigurationException e) {
            // throw new SynchronizationErrorException(e,
            // "Error al sincronizar provincias de cliente.");
            // }

/* debug sincro de clientes
            try {
                lastPublishedProgress =new String[] { "Sincronizando Clientes...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                PartnerRepository.getInstance().getRemoteObjects(new Partner(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Clientes.");
            }
*/

            try {
                lastPublishedProgress =new String[] { "Sincronizando tiendas...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ShopRepository.getInstance().getRemoteObjects(new Shop(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar tiendas.");
            }

            try {
                lastPublishedProgress =new String[] { "Sincronizando precios...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                PricelistPricesRepository.getInstance().getRemoteObjects(new PricelistPrices(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar precios.");
            }

            try {
                lastPublishedProgress = new String[] { "Sincronizando métodos de pago...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                PaymentModeRepository.getInstance().getRemoteObjects(new PaymentMode(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar métodos de pago.");
            }

            try {
                lastPublishedProgress = new String[] { "Sincronizando plazos de pago...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                AccountPaymentTermRepository.getInstance().getRemoteObjects(new AccountPaymentTerm(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar plazos de pago.");
            }

            try {
                lastPublishedProgress = new String[] { "Sincronizando almacenes...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                WarehouseRepository.getInstance().getRemoteObjects(new Warehouse(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar almacenes.");
            }
            /* DAVID - NO SINCRONIZA POR FALLO
            try {
                lastPublishedProgress = new String[] {
                        "Sincronizando Weekdays (weekday)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                WeekDayRepository.getInstance().getRemoteObjects(new WeekDay(),
                        user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Weekdays.");
            }
            */
            try {
                lastPublishedProgress = new String[] { "Sincronizando impuestos...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                TaxRepository.getInstance().getRemoteObjects(new Tax(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar impuestos.");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando categorías de cliente...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                PartnerCategoryRepository.getInstance().getRemoteObjects(new PartnerCategory(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar categorías de cliente.");
            }
            lastPublishedProgress = new String[] { "Sincronizando clientes pendientes...",
                    "" + currentProgress++ };
            publishProgress(lastPublishedProgress);
            Partner partner = new Partner();
            partner.setPendingSynchronization(1);
            PartnerRepository.getInstance().updateRemoteObjects(
                    PartnerRepository.getInstance().getByExample(partner,
                            Restriction.AND, true, 0, 100000), user.getLogin(),
                    user.getPasswd());

            try {
                lastPublishedProgress =new String[] { "Sincronizando clientes...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                PartnerRepository.getInstance().getRemoteObjects(new Partner(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar clientes.");
            }

            try {
                lastPublishedProgress = new String[] { "Sincronizando métodos de pago...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                PaymentTypeRepository.getInstance().getRemoteObjects(new AccountJournal(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar métodos de pago.");
            }

            try {
                lastPublishedProgress = new String[] { "Sincronizando cuentas contables...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                AccountRepository.getInstance().getRemoteObjects(new Account(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar cuentas contables.");
            }
            try {
                lastPublishedProgress =new String[] { "Sincronizando cobros cliente...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                AccountMoveLine line = new AccountMoveLine();
                Account example = new Account();
                example.setType("receivable");
                List<Integer> accountIds = new ArrayList<Integer>();
                for (Account anAccount : AccountRepository.getInstance().getByExample(example, Restriction.AND, true, 0, 10000000))
                    accountIds.add(anAccount.getId().intValue());
                Set idsPartners = PartnerRepository.getInstance().getAllDistinctSomeProperty("id");
                List<Integer> idsQuery = new ArrayList<Integer>();
                for (Object idPartner : idsPartners) {
                    idsQuery.add(((Long) idPartner).intValue());
                }
                System.out.println("Account id: " + example.getId());
                line.getFilters().add("account_id", "in", accountIds.toArray());
                line.getFilters().add("partner_id", "in", idsQuery.toArray());
                AccountMoveLineRepository.getInstance().getRemoteObjects(line,
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar cobros cliente...");
            }

//            try {
            lastPublishedProgress = new String[] { "Sincronizando productos de cliente...",
                    "" + currentProgress++ };
            publishProgress(lastPublishedProgress);
//                for (Partner aPartner : PartnerRepository.getInstance().getAll(0, 100000000)) {
//                    OpenERPCommand command = new OpenERPCommand(SessionFactory
//                            .getInstance(user.getLogin(), user.getPasswd())
//                            .getSession());
//                    Object[] productIds = (Object[]) command.callObjectFunction(
//                            "res.partner",
//                            "search_products_to_sell", new Object[] { aPartner.getId().intValue() });
//                    List<Product> result = new ArrayList<Product>();
//                    for (Object aProductId : productIds) {
//                        PartnerProduct partnerProduct = new PartnerProduct();
//                        partnerProduct.setPartnerId(aPartner.getId());
//                        for (PartnerProduct existing : PartnerProductRepository.getInstance().getByExample(partnerProduct, Restriction.AND, true, 0, 10000000)) {
//                            PartnerProductRepository.getInstance().delete(existing.getId());
//                        }
//                        partnerProduct.setProductId(((Integer) aProductId).longValue());
//                        PartnerProductRepository.getInstance().saveOrUpdate(partnerProduct);
//                    }
//                }

//            } catch (ConfigurationException e) {
//                throw new SynchronizationErrorException(e,
//                        "Error al sincronizar Productos de Cliente.");
//            }
//            try {
//                publishProgress(new String[] { "Sincronizando Días de entrega (Clientes)...",
//                        "" + currentProgress++ });
//                PartnerRepository.getInstance().synchronizeDeliveryDays(user.getLogin(), user.getPasswd());
//            } catch (ConfigurationException e) {
//                throw new SynchronizationErrorException(e,
//                        "Error al sincronizar Días de entrega.");
//            } catch (ServiceException e) {
//                throw new SynchronizationErrorException(e,
//                        "Error al sincronizar Días de entrega.");
//            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando usuarios...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                UserRepository.getInstance().synchronizeUsers(user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar usuarios.");
            }

            /* DAVID - NO SINCRONIZA POR FALLO
            try {
                lastPublishedProgress = new String[] { "Sincronizando Contactos...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ContactRepository.getInstance().getRemoteObjects(new Contact(),
                        user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Contactos.");
            }
            */
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductRepository.getInstance().getRemoteObjects(new Product(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos.");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos sustitutivos...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductRepository.getInstance().synchronizeSubstitutes(
                        user.getLogin(), user.getPasswd());

            } catch (Exception e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos sustitutivos.");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos (product_ul)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductUlRepository.getInstance().getRemoteObjects(new ProductUl(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos (product_ul).");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos (product_uom_categ)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductUomCategRepository.getInstance().getRemoteObjects(new ProductUomCateg(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos (product_uom_categ.");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos (product_uom)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductUomRepository.getInstance().getRemoteObjects(new ProductUom(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos (product_uom).");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos (product_template)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductTemplateRepository.getInstance().getRemoteObjects(new ProductTemplate(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos (product_template).");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos (product_category)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductCategoryRepository.getInstance().getRemoteObjects(new ProductCategory(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos (product_category).");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando productos (product_packaging)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                ProductPackagingRepository.getInstance().getRemoteObjects(new ProductPackaging(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar productos (product_packaging).");
            }


            /* DAVID - NO SINCRONIZA POR FALLO Y ADEMAS NO ES NECESARIO AHORA
            try {
                lastPublishedProgress = new String[] { "Sincronizando Pagos (Voucher)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                VoucherRepository.getInstance().getRemoteObjects(new Voucher(),
                        user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Pagos (Voucher).");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando Pagos (VoucherLine)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                VoucherLineRepository.getInstance().getRemoteObjects(
                        new VoucherLine(), user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Pagos (VoucherLine).");
            }
            */
            lastPublishedProgress = new String[] { "Sincronizando pedidos pendientes...",
                    "" + currentProgress++ };
            publishProgress(lastPublishedProgress);
//            Order order = new Order();
//            order.setPendingSynchronization(1);
//            for (Order o : OrderRepository.getInstance().getByExample(order,
//                    Restriction.AND, true, 0, 100000)) {
//                confirmarPedido(user, o);
//            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando pedidos...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                OrderRepository.getInstance().getRemoteObjects(new Order(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar pedidos.");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando pedidos (líneas)...",
                        "" + currentProgress++};
                publishProgress(lastPublishedProgress);
                List<Integer> orderIds = new ArrayList<Integer>();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -180); // últimos seis meses
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Order o : OrderRepository.getInstance().getOrdersWithFilters(null, formatter.format(calendar.getTime()), null, null, null, null, true)) {
                    orderIds.add(o.getId().intValue());
                }
                OrderLine orderLine = new OrderLine();
                orderLine.getRemoteFilters().add("order_id", "in", orderIds.toArray(new Integer[]{}));
                // no voy a hacer deletes porque aquí está modificando el filtro que se envía a servidor para obtener
                // datos y me está dejando sin los ids de todas las líneas de pedidos que no ha tenido cambios desde
                // la última sincronización, entonces el algoritmo de búsqueda de eliminaciones piensa que esas líneas
                // de pedidos fueron borradas y las quita de local
				// cambié un poco el método y ahora voy a hacer deletes para probar unos días - Pedro Gómez - 06/03/2020
                OrderLineRepository.getInstance().getRemoteObjects(orderLine,
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar pedidos (líneas).");
            }

            /* DAVID - NO SINCRONIZA POR FALLO Y ADEMAS NO ES NECESARIO AHORA
            try {
                lastPublishedProgress = new String[] { "Sincronizando Facturas (Invoice)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                InvoiceRepository.getInstance().getRemoteObjects(new Invoice(),
                        user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Facturas (Invoice Line).");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando Facturas (Invoice Line)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                List<Integer> invoiceIds = new ArrayList<Integer>();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -90);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Invoice o : InvoiceRepository.getInstance().getInvoicesWithFilters(null, formatter.format(calendar.getTime()), null, null, null, null)) {
                    invoiceIds.add(o.getId().intValue());
                }
                InvoiceLine invoiceLine = new InvoiceLine();
                invoiceLine.getRemoteFilters().add("invoice_id", "in", invoiceIds.toArray(new Integer[]{}));
                InvoiceLineRepository.getInstance().getRemoteObjects(
                        invoiceLine, user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Facturas (Invoice Line).");
            }
*/
            try {
                lastPublishedProgress = new String[] { "Sincronizando empresas...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                CompanyRepository.getInstance().getRemoteObjects(new Company(),
                        user.getLogin(), user.getPasswd(), true);

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar empresas.");
            }

            /* DAVID - TARDA MUCHISIMO EN SINCRONIZAR ADEMÁS AHORA NO ES NECESARIO
            try {
                lastPublishedProgress = new String[] { "Sincronizando Albaranes...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                StockPickingOutRepository.getInstance().getRemoteObjects(
                        new StockPickingOut(), user.getLogin(),
                        user.getPasswd());
            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Albaranes.");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando Albaranes (Stock Move)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                StockMoveRepository.getInstance().getRemoteObjects(
                        new StockMove(), user.getLogin(), user.getPasswd());
            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Albaranes  (Stock Move).");
            }
            */

            /* DAVID - AHORA NO ES NECESARIO

            try {
                lastPublishedProgress = new String[] { "Sincronizando Rutas (Route detail)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                RouteDetailRepository.getInstance().synchronizeRoutes(user.getLogin(), user.getPasswd());

            } catch (ConfigurationException e) {
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Rutas (Route detail).");
            }
            try {
                lastPublishedProgress = new String[] { "Sincronizando Rutas (Route)...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                RouteRepository.getInstance().synchronizeRoutes(user.getLogin(), user.getPasswd());
                lastPublishedProgress = new String[] { "Sincronización Completa.",
                        "" + currentProgress };
                publishProgress(lastPublishedProgress);
            } catch (ConfigurationException e) {
//                currentProgress = MAX_PROGRESS;
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar Rutas (Route).");
            }
            */
            try {
                lastPublishedProgress = new String[] { "Sincronizando precios...",
                        "" + currentProgress++ };
                publishProgress(lastPublishedProgress);
                // Esta sincronicación no tengo claro que sirva para algo. Revisar cuando tenga tiempo - Pedro Gómez - 06/03/2020
                Long initDate = new Date().getTime();
                Session openERPSession = null;
                openERPSession = SessionFactory.getInstance(user.getLogin(), user.getPasswd())
                        .getSession();

                ObjectAdapter adapterPrices = openERPSession
                        .getObjectAdapter("table.pricelist.prices");
                FilterCollection filters = new FilterCollection();
                RowCollection prices;
                String[] fieldsRemote = {"product_id", "pricelist_id", "price"};
                String maxDateOld = DateUtil.convertToUTC(SynchronizationRepository.getInstance().getMaxDateFor(ProductPartnerPrice.class));
                prices = adapterPrices.searchAndReadObject(
                        filters, fieldsRemote, maxDateOld);
                int numItems = 0;

                // DAVID - https://bitbucket.org/noroestesoluciones/odoo-app/issues/7/pantalla-de-b-squeda-la-columna-de-la
                for (Row aPrice : prices) {
                    // cambio a 3 decimales
                    BigDecimal result = BigDecimal.valueOf((Double) aPrice.get("price")).setScale(3, RoundingMode.HALF_UP);
                    if (result != null) {
                        ProductPartnerPrice toSave = new ProductPartnerPrice();
                        if (aPrice.get("pricelist_id") != null)
                            toSave.setPartnerId((Integer) ((Object[]) aPrice.get("pricelist_id"))[0]);
                        if (aPrice.get("product_id") != null) {
                            toSave.setProductId((Integer) ((Object[]) aPrice.get("product_id"))[0]);
                            toSave.setPrice(result.doubleValue());
                            ProductPartnerPriceRepository.getInstance().saveOrUpdate(toSave);
                        }
                        numItems++;
                    }
                }
                try {
                    Date endDataSynchro = new Date();
                    Long duration = endDataSynchro.getTime() - initDate;
                    SynchronizationRepository.getInstance().saveOrUpdate(
                            Synchronization.create(ProductPartnerPrice.class.getName(),
                                    DateUtil.toFormattedString(endDataSynchro), numItems,
                                    duration, error));
                } catch (ServiceException e) {
                    throw new ConfigurationException(e);
                }
            } catch (Exception e) {
                currentProgress = MAX_PROGRESS;
                throw new SynchronizationErrorException(e,
                        "Error al sincronizar precios.");
            }
        } catch (SynchronizationErrorException e) {
            error = e.getDescriptiveErrorMessage();
            if (LoggerUtil.isDebugEnabled()) {
                e.printStackTrace();
                appendLog(e.getMessage() + "\n" + e.getCause());
            }
            final GMailSender sender = new GMailSender();
            if (!LoggerUtil.isDebugEnabled()) {
                sender.set_subject("Error en SINCRONIZACION APP MIDBAN");
                sender.set_to(new String[]{"abdaleon@gmail.com"});
            } else {
                sender.set_subject("Error en SINCRONIZACION APP MIDBAN - debug local CafedeRed");
                sender.set_to(new String[]{"abdaleon@gmail.com"});
            }
            String stackTrace = e.getMessage();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            stackTrace = sw.toString();
            sender.set_body("Se ha producido un error en SINCRONIZACION MIDBAN.\n Traza:\n " + stackTrace);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        sender.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
            currentProgress = MAX_PROGRESS;
        } catch (Exception e) {
            error = "Error indeterminado.";
            if (LoggerUtil.isDebugEnabled()) {
                e.printStackTrace();
                appendLog(e.getMessage() + "\n" + e.getCause());
            }
            final GMailSender sender = new GMailSender();
            if (!LoggerUtil.isDebugEnabled()) {
                sender.set_subject("Error en SINCRONIZACION APP MIDBAN");
                sender.set_to(new String[]{"abdaleon@gmail.com"});
            } else {
                sender.set_subject("Error en SINCRONIZACION APP MIDBAN - debug local CafedeRed");
                sender.set_to(new String[]{"abdaleon@gmail.com"});
            }
            String stackTrace = e.getMessage();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            stackTrace = sw.toString();
            sender.set_body("Se ha producido un error en SINCRONIZACION MIDBAN.\n Traza:\n " + stackTrace);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        sender.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
            currentProgress = MAX_PROGRESS;
        } finally {
            final Long endTime = new Date().getTime();
            final SynchronizationSummary summary = new SynchronizationSummary();
            summary.setDate(DateUtil.toFormattedString(new Date(),
                    "yyyyMMddHHmmss"));
            if (error.isEmpty()) {
                summary.setResult("Ok");
                lastPublishedProgress = new String[] { "Sincronización completa.",
                        "" + currentProgress };
                publishProgress(lastPublishedProgress);
            }
            else {
                summary.setResult(error);
                lastPublishedProgress = new String[] { "Error en la sincronización.",
                        "" + currentProgress };
                publishProgress(lastPublishedProgress);
            }
            summary.setTimeInSeconds((endTime - initTime) / 1000);
            try {
                SynchronizationSummaryRepository.getInstance().saveOrUpdate(
                        summary);
            } catch (final ServiceException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
            currentProgress = MAX_PROGRESS;
            return error.isEmpty();
        }
    }

    @Override
    protected void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        progress.setProgress(Integer.parseInt(values[1]));
        messagesArea.setText(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        progress.setVisibility(View.INVISIBLE);
        if (result)
            MessagesForUser.showMessage((Activity) context,
                    R.string.fragment_synchronization_synchro_ok,
                    Toast.LENGTH_LONG, Level.INFO);
        else
            MessagesForUser.showMessage((Activity) context,
                    R.string.fragment_synchronization_synchro_ko,
                    Toast.LENGTH_LONG, Level.SEVERE);
    }

    public void appendLog(String text) {
        File logFile = new File("sdcard/midban.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO DELETE THIS METHOD!!!!!!
                e.printStackTrace();
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                    true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }

    public ProgressBar getProgress() {
        return this.progress;
    }

    public void setProgress(ProgressBar progress) {
        this.progress = progress;
    }

    public void setMessagesArea(TextView messagesArea) {
        this.messagesArea = messagesArea;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String[] getLastPublishedProgress() {
        return lastPublishedProgress;
    }

    public void setLastPublishedProgress(String[] lastPublishedProgress) {
        this.lastPublishedProgress = lastPublishedProgress;
    }

    private void confirmarPedido(User user, final Order confirmOrder) {
        new AsyncTask<User, Void, Boolean>() {

//            ProgressDialog progress;
//
//            protected void onPreExecute() {
//                progress = ProgressDialog.show(OrderNewDispositionFragment.this.getActivity(), "Sincronizando...",
//                        "Enviando pedido", true);
//            }

            @Override
            protected Boolean doInBackground(User... user) {
                try {
                    final boolean confirmacionPendiente = confirmOrder.getPendingSynchronization() != null && confirmOrder.getPendingSynchronization() == 1;
                    confirmOrder
                            .setPendingSynchronization(1);
                    Integer result;
                    if (confirmOrder.getId().intValue() >= OrderRepository.getInstance().getLastSynchronizedIdNumber() && confirmacionPendiente) {
                        List<Object> linesOrderOdoo = new ArrayList<Object>();
                        fillLineParamsWithOrderLines(linesOrderOdoo, confirmOrder.getLines(), false);
                        HashMap<String, Object> orderOdoo = new HashMap<String, Object>();
                        orderOdoo.put("partner_id", confirmOrder.getPartnerId().intValue());
                        orderOdoo.put("order_line", linesOrderOdoo.toArray());
                        orderOdoo.put("warehouse_id", ConfigurationRepository.getInstance().getConfiguration().getWarehouseId().intValue());
                        if (confirmOrder.getClientOrderRef() != null)
                            orderOdoo.put("client_order_ref", confirmOrder.getClientOrderRef());
                        if (confirmOrder.getShopId() != null)
                            orderOdoo.put("shop_id", confirmOrder.getShopId().intValue());
                        if (confirmOrder.getPricelistId() != null)
                            orderOdoo.put("pricelist_id", confirmOrder.getPricelistId().intValue());
                        if (confirmOrder.getAmountTax() != null)
                            orderOdoo.put("amount_tax", confirmOrder.getAmountTax().doubleValue());
                        if (confirmOrder.getMargin() != null)
                            orderOdoo.put("margin", confirmOrder.getMargin().doubleValue());
                        if (confirmOrder.getAmountUntaxed() != null)
                            orderOdoo.put("amount_untaxed", confirmOrder.getAmountUntaxed().doubleValue());
                        if (confirmOrder.getAmountTotal() != null)
                            orderOdoo.put("amount_total", confirmOrder.getAmountTotal().doubleValue());
                        if (confirmOrder.getPartnerShippingId() != null)
                            orderOdoo.put("partner_shipping_id", confirmOrder.getPartnerShippingId().intValue());
                        if (confirmOrder.getCreateDate() != null)
                            orderOdoo.put("create_date", confirmOrder.getCreateDate());
                        orderOdoo.put("channel", "tablet");
                        if (confirmOrder.getNote() != null && confirmOrder.getNote().length() > 0)
                            orderOdoo.put("note", confirmOrder.getNote());
                        if (confirmOrder.getName() != null)
                            orderOdoo.put("name", confirmOrder.getName());
                        if (confirmOrder.getPartnerInvoiceId() != null)
                            orderOdoo.put("partner_invoice_id", confirmOrder.getPartnerInvoiceId().intValue());
                        if (confirmOrder.getState() != null)
                            orderOdoo.put("state", confirmOrder.getState());
                        if (confirmOrder.getDateOrder() != null)
                            orderOdoo.put("date_order", confirmOrder.getDateOrder());
                        if (confirmOrder.getRequestedDate() != null)
                            orderOdoo.put("requested_date", confirmOrder.getRequestedDate());
                        OpenERPCommand command = new OpenERPCommand(SessionFactory
                                .getInstance(user[0].getLogin(),
                                        user[0].getPasswd()).getSession());
                        result = (Integer) command.createObject("sale.order", orderOdoo);
                    } else {
                        //Estamos guardando la edición del pedido
//                        OrderRepository.getInstance().updateStatusOfOrder(confirmOrder.getId(), 1);
                        try {
                            OpenERPCommand command = new OpenERPCommand(SessionFactory
                                    .getInstance(user[0].getLogin(),
                                            user[0].getPasswd()).getSession());
                            command.callObjectFunction("sale.order",
                                    "cancel_sale_to_draft", new Integer[]{confirmOrder.getId().intValue()});
                        } catch (Exception e) {
                            if (!e.getMessage().contains("allow_none")) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        List<Object> linesOrderOdoo = new ArrayList<Object>();
                        fillLineParamsWithOrderLines(linesOrderOdoo, confirmOrder.getLines(), true);
                        HashMap<String, Object> orderOdoo = new HashMap<String, Object>();
                        orderOdoo.put("id", confirmOrder.getId().intValue());
                        orderOdoo.put("partner_id", confirmOrder.getPartnerId().intValue());
                        orderOdoo.put("order_line", linesOrderOdoo.toArray());
                        orderOdoo.put("warehouse_id", ConfigurationRepository.getInstance().getConfiguration().getWarehouseId().intValue());
                        if (confirmOrder.getClientOrderRef() != null)
                            orderOdoo.put("client_order_ref", confirmOrder.getClientOrderRef());
                        if (confirmOrder.getShopId() != null)
                            orderOdoo.put("shop_id", confirmOrder.getShopId().intValue());
                        if (confirmOrder.getPricelistId() != null)
                            orderOdoo.put("pricelist_id", confirmOrder.getPricelistId().intValue());
                        if (confirmOrder.getAmountTax() != null)
                            orderOdoo.put("amount_tax", confirmOrder.getAmountTax().doubleValue());
                        if (confirmOrder.getMargin() != null)
                            orderOdoo.put("margin", confirmOrder.getMargin().doubleValue());
                        if (confirmOrder.getAmountUntaxed() != null)
                            orderOdoo.put("amount_untaxed", confirmOrder.getAmountUntaxed().doubleValue());
                        if (confirmOrder.getAmountTotal() != null)
                            orderOdoo.put("amount_total", confirmOrder.getAmountTotal().doubleValue());
                        if (confirmOrder.getPartnerShippingId() != null)
                            orderOdoo.put("partner_shipping_id", confirmOrder.getPartnerShippingId().intValue());
                        if (confirmOrder.getCreateDate() != null)
                            orderOdoo.put("create_date", confirmOrder.getCreateDate());
                        orderOdoo.put("channel", "tablet");
                        if (confirmOrder.getNote() != null)
                            orderOdoo.put("note", confirmOrder.getNote());
                        if (confirmOrder.getName() != null)
                            orderOdoo.put("name", confirmOrder.getName());
                        if (confirmOrder.getPartnerInvoiceId() != null)
                            orderOdoo.put("partner_invoice_id", confirmOrder.getPartnerInvoiceId().intValue());
                        if (confirmOrder.getState() != null)
                            orderOdoo.put("state", confirmOrder.getState());
                        if (confirmOrder.getDateOrder() != null)
                            orderOdoo.put("date_order", confirmOrder.getDateOrder());
                        if (confirmOrder.getRequestedDate() != null)
                            orderOdoo.put("requested_date", confirmOrder.getRequestedDate());
                        OpenERPCommand command = new OpenERPCommand(SessionFactory
                                .getInstance(user[0].getLogin(),
                                        user[0].getPasswd()).getSession());
                        result = command.writeObject("sale.order", confirmOrder.getId().intValue(), orderOdoo) ? confirmOrder.getId().intValue() : 0;
                    }
                    if (result != 0) {
                        OpenERPCommand commandConfirm = new OpenERPCommand(SessionFactory
                                .getInstance(user[0].getLogin(),
                                        user[0].getPasswd()).getSession());
                        commandConfirm.callObjectFunction("sale.order",
                                "action_button_confirm", new Object[]{new Integer[]{result}});
                    }
                    confirmOrder
                            .setPendingSynchronization(0);
                    OrderRepository.getInstance().saveOrUpdate(
                            confirmOrder);
                    OrderRepository.getInstance().getRemoteObjects(new Order(), user[0].getLogin(),
                            user[0].getPasswd(), false);
                    OrderLineRepository.getInstance().getRemoteObjects(new OrderLine(), user[0].getLogin(),
                            user[0].getPasswd(), false);
//                    doRemoveOfOldLines(confirmOrder.getLines());
                } catch (ServiceException e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    try {
                        OrderRepository.getInstance().saveOrUpdate(
                                confirmOrder);
                    } catch (ServiceException e1) {
                        if (LoggerUtil.isDebugEnabled())
                            e1.printStackTrace();
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    if (LoggerUtil.isDebugEnabled())
                        e.printStackTrace();
                    try {
                        OrderRepository.getInstance().saveOrUpdate(
                                confirmOrder);
                    } catch (ServiceException e1) {
                        if (LoggerUtil.isDebugEnabled())
                            e1.printStackTrace();
                        return false;
                    }
                    return true;
                }
                return true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
    }

    private void fillLineParamsWithOrderLines(List<Object> params, List<OrderLine> lines, boolean updating) {
        for (int i = 0; i < lines.size(); i++) {
            Object[] dictionary = new Object[3];
            if (!updating || lines.get(i).getId() == null) {
                dictionary[0] = 0;
                dictionary[1] = 0;
            } else {
                if (lines.get(i).getId() != null) {
                    dictionary[0] = 1;
                    dictionary[1] = lines.get(i).getId().intValue();
                }
            }
            Map lineValues = new HashMap();
            lineValues.put("product_id", lines.get(i).getProductId().intValue());
            if (lines.get(i).getName() != null)
                lineValues.put("description", lines.get(i).getName());
            lineValues.put("product_uos_qty", lines.get(i).getProductUosQuantity().intValue());
            lineValues.put("product_uos", lines.get(i).getProductUos().intValue());
            lineValues.put("price_unit", lines.get(i).getPriceUnit().doubleValue());
            Product product = null;
            try {
                product = ProductRepository.getInstance().getById(lines.get(i).getProductId().longValue());
                lineValues.put("tax_id", new Number[]{product.getProductTemplate().getTaxesId()});
            } catch (ConfigurationException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            lineValues.put("discount", lines.get(i).getDiscount().doubleValue());
            dictionary[2] = lineValues;
            params.add(dictionary);
        }
    }
}
