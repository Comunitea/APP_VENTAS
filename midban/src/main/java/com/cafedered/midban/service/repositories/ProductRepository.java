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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;

import com.cafedered.cafedroidlitedao.exceptions.DatabaseException;
import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.dao.ProductDAO;
import com.cafedered.midban.entities.Company;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.PartnerProduct;
import com.cafedered.midban.entities.PricelistPrices;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.entities.ProductPartnerPrice;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.SessionFactory;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.OpenERPCommand;
import com.debortoliwines.openerp.api.OpeneERPApiException;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.debortoliwines.openerp.api.Session;

public class ProductRepository extends BaseRepository<Product, ProductDAO> {

    private static ProductRepository instance = null;

    public static ProductRepository getInstance() {
        if (instance == null)
            instance = new ProductRepository();
        return instance;
    }

    private ProductRepository() {
        dao = ProductDAO.getInstance();
    }

    public BigDecimal getCalculatedPrice(Product instance, Partner partner, String tariff,
                                         String login, String passwd) {
        Long initTime = new Date().getTime();

        // si tengo tarifa me voy directamente a ella
        if ((instance != null) && (tariff != null) && (!"".equals(tariff))) {
            PricelistPrices pl = new PricelistPrices();
            pl.setPricelistId(Long.parseLong(tariff));
            pl.setProductId(instance.getId().longValue());
            try {
                List<PricelistPrices> list = PricelistPricesRepository.getInstance().getByExample(pl, Restriction.AND, true, 0, 1);
                if (list.size() == 1){
                  return new BigDecimal(list.get(0).getPrice().doubleValue()).setScale(3, RoundingMode.HALF_UP);
                }
            } catch (ServiceException e1) {
                e1.printStackTrace();
            }
        }

        // sino voy a mirar en la tarifa del partner
        if (partner != null && instance != null) {
            ProductPartnerPrice example = new ProductPartnerPrice();
            example.setDateCalculated(null);
            example.setPartnerId(partner.getPricelistId());
            example.setProductId(instance.getId());
            try {
                List<ProductPartnerPrice> list = ProductPartnerPriceRepository.getInstance().getByExample(example, Restriction.AND, true, 0, 1);
                if (list != null && list.size() > 0) {
                    example = list.get(0);
                    Date exampleDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(example.getDateCalculated());
                    Long now = new Date().getTime();
                    if ((now - exampleDate.getTime()) < 60000)
                        // cambio a 3 decimales
                        return new BigDecimal(example.getPrice().doubleValue()).setScale(3, RoundingMode.HALF_UP);
                }
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        // sino pues la de la empresa
        tariff = MidbanApplication.priceListIdActualCompany();
        if ((instance != null) && (tariff != null) && (!"".equals(tariff))) {
            PricelistPrices pl = new PricelistPrices();
            pl.setPricelistId(Long.parseLong(tariff));
            pl.setProductId(instance.getId().longValue());
            try {
                List<PricelistPrices> list = PricelistPricesRepository.getInstance().getByExample(pl, Restriction.AND, true, 0, 1);
                if (list.size() == 1){
                    return new BigDecimal(list.get(0).getPrice().doubleValue()).setScale(3, RoundingMode.HALF_UP);
                }
            } catch (ServiceException e1) {
                e1.printStackTrace();
            }
        }
        System.out.println("Time bdd: " + (new Date().getTime() - initTime) + " ms.");

        BigDecimal result = null;

/*
VOY A COMENTAR TODA LA PARTE QUE VA A BUSCAR EL PRECIO A SERVIDOR, AHORA YA NO FUNCIONA
        initTime = new Date().getTime();
        // if online, we try to get prices in specific.customer.pvp
        Session openERPSession = null;
        try {
            openERPSession = SessionFactory.getInstance(login, passwd)
                    .getSession();
        } catch (Exception e) {
            // do nothing, openERPSession will be null
        }
        if (openERPSession != null) {
            ObjectAdapter adapter;
            try {
                if (partner != null) {
                    adapter = openERPSession
                            .getObjectAdapter("sale.specific.price");
//                    'customer_id', ' product_id', pricelist_id' y 'discount'
                    FilterCollection filters = new FilterCollection();
                    try {
                        filters.add("customer_id", "=", partner.getId());
                        filters.add("product_id", "=", instance.getId());
                        filters.add("pricelist_id", "=", partner.getPricelistId());
                    } catch (OpeneERPApiException e) {
                        e.printStackTrace();
                    }
                    RowCollection entities;
                    String[] fieldsRemote = {"discount"};
                    entities = adapter.searchAndReadObject(filters,
                            fieldsRemote, "1900-01-01 00:00:00");
                    for (Row row : entities) {
                        try {
                            Integer discount = (Integer) row
                                    .get("discount");
                            try {
                                ObjectAdapter adapterPrices = openERPSession
                                        .getObjectAdapter("table.pricelist.prices");
                                FilterCollection filtersPrecioTarifa = new FilterCollection();
                                try {
                                    if (partner != null && partner.getPricelistId() != null)
                                        filtersPrecioTarifa.add("pricelist_id", "=",
                                                partner.getPricelistId());
                                    else {
                                        Company actualCompany = CompanyRepository.getInstance().getById(new Long(MidbanApplication.activeCompany));
                                        if (actualCompany == null) {
                                            filtersPrecioTarifa.add("pricelist_id", "=", "1");
                                        }
                                        else{
                                            if ((actualCompany.getSalesAppProductPricelist() != null) && (!"".equals(actualCompany.getSalesAppProductPricelist().toString()))){
                                                filtersPrecioTarifa.add("pricelist_id", "=", actualCompany.getSalesAppProductPricelist().toString());
                                            }
                                            else{
                                                filtersPrecioTarifa.add("pricelist_id", "=", "1");
                                            }
                                        }
                                    }
                                    filtersPrecioTarifa.add("product_id", "=", instance.getId());
                                } catch (OpeneERPApiException e) {
                                    e.printStackTrace();
                                }
                                RowCollection prices;
                                String[] fieldsRemoteTarifa = {"price"};
                                prices = adapterPrices.searchAndReadObject(filters,
                                        fieldsRemoteTarifa, "1900-01-01 00:00:00");
                                for (Row aPrice : prices) {
                                    result = BigDecimal.valueOf((Double) aPrice
                                            .get("price"));
                                    if (result != null) {
                                        ProductPartnerPrice toSave = new ProductPartnerPrice();
                                        if (partner != null)
                                            toSave.setPartnerId(partner.getPricelistId());
                                        toSave.setProductId(instance.getId());
                                        toSave.setPrice(result.doubleValue());
                                        if  (discount != null) {
                                            toSave.setPrice(result.doubleValue() * discount / 100);
                                            toSave.setPartnerId(partner.getId());
                                        }
                                        // cambio a 3 decimales
                                        result = BigDecimal.valueOf((toSave.getPrice().floatValue())).setScale(3, RoundingMode.HALF_UP);
                                        ProductPartnerPriceRepository.getInstance()
                                                .saveForProductAndPartner(toSave);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // if not specific price available, we try to get non-specific price
            if (result == null) {
                try {
                    ObjectAdapter adapterPrices = openERPSession
                            .getObjectAdapter("table.pricelist.prices");
                    FilterCollection filters = new FilterCollection();
                    try {
                        if (partner != null && partner.getPricelistId() != null)
                            filters.add("pricelist_id", "=",
                                    partner.getPricelistId());
                        else{
                            Company actualCompany = CompanyRepository.getInstance().getById(new Long(MidbanApplication.activeCompany));
                            if (actualCompany == null) {
                                filters.add("pricelist_id", "=", "1");
                            }
                            else{
                                if ((actualCompany.getSalesAppProductPricelist() != null) && (!"".equals(actualCompany.getSalesAppProductPricelist().toString()))){
                                    filters.add("pricelist_id", "=", actualCompany.getSalesAppProductPricelist().toString());
                                }
                                else{
                                    filters.add("pricelist_id", "=", "1");
                                }
                            }
                        }
                        filters.add("product_id", "=", instance.getId());
                    } catch (OpeneERPApiException e) {
                        e.printStackTrace();
                    }
                    RowCollection prices;
                    String[] fieldsRemote = {"price"};
                    prices = adapterPrices.searchAndReadObject(filters,
                            fieldsRemote, "1900-01-01 00:00:00");
                    for (Row aPrice : prices) {
                        result = BigDecimal.valueOf((Double) aPrice
                                .get("price"));
                        if (result != null) {
                            ProductPartnerPrice toSave = new ProductPartnerPrice();
                            if (partner != null)
                                toSave.setPartnerId(partner.getPricelistId());
                            toSave.setProductId(instance.getId());
                            toSave.setPrice(result.doubleValue());
                            ProductPartnerPriceRepository.getInstance()
                                    .saveForProductAndPartner(toSave);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Time network: " + (new Date().getTime() - initTime) + " ms.");


// TODA ESTA PARTE AHORA TAMPOCO TIENE YA SENTIDO
        initTime = new Date().getTime();
        if (result == null) {
            ProductPartnerPrice savedPrice = new ProductPartnerPrice();
            savedPrice.setDateCalculated(null);
            if (partner != null)
                savedPrice.setPartnerId(partner.getPricelistId());
            savedPrice.setProductId(instance.getId());
            List<ProductPartnerPrice> list = null;
            try {
                list = ProductPartnerPriceRepository.getInstance()
                        .getByExample(savedPrice, Restriction.AND, true,
                                0, 100000);
            } catch (ServiceException e) {
                // do nothing
            }
            if (list != null && list.size() > 0) {
                if (partner == null) {
                    if (list.get(0).getPartnerId() == null || list.get(0).getPartnerId().longValue() == 0L)
                        savedPrice = list.get(0);
                    if (savedPrice != null && savedPrice.getPrice() != null)
                        result = BigDecimal.valueOf(savedPrice.getPrice()
                                .doubleValue());
                } else {
                    savedPrice = list.get(0);
                    if (savedPrice != null && savedPrice.getPrice() != null)
                        result = BigDecimal.valueOf(savedPrice.getPrice()
                                .doubleValue());
                }
            } else {
                savedPrice = new ProductPartnerPrice();
                savedPrice.setDateCalculated(null);
                savedPrice.setProductId(instance.getId());
                List<ProductPartnerPrice> results = null;
                try {
                    results = ProductPartnerPriceRepository.getInstance()
                            .getByExample(savedPrice, Restriction.AND, true,
                                    0, 100000);
                } catch (ServiceException e) {
                    // do nothing
                }
                if (results != null && results.size() > 0) {
                    savedPrice = results.get(0);
                    if (savedPrice != null && savedPrice.getPrice() != null)
                        result = BigDecimal.valueOf(savedPrice.getPrice()
                                .doubleValue());
                }
            }
            ProductPartnerPrice newPrice = new ProductPartnerPrice();
            newPrice.setProductId(instance.getId());
            if (partner != null)
                newPrice.setPartnerId(partner.getPricelistId());
            newPrice.setPrice(result);
            try {
                ProductPartnerPriceRepository.getInstance().saveForProductAndPartner(newPrice);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }

*/
        System.out.println("Time BDD result: " + (new Date().getTime() - initTime) + " ms.");
        if (result != null) {
            // cambio a 3 decimales
            return result.setScale(3, RoundingMode.HALF_UP);
        }
        else
            return BigDecimal.ZERO;
    }

    public BigDecimal getDefaultPriceForProduct(Product instance, Partner partner) {
        ProductPartnerPrice savedPrice = new ProductPartnerPrice();
        savedPrice.setDateCalculated(null);
        BigDecimal result = null;
        if (partner != null)
            savedPrice.setPartnerId(partner.getPricelistId());
        savedPrice.setProductId(instance.getId());
        List<ProductPartnerPrice> list = null;
        try {
            list = ProductPartnerPriceRepository.getInstance()
                    .getByExample(savedPrice, Restriction.AND, true,
                            0, 100000);
        } catch (ServiceException e) {
            // do nothing
        }
        if (list != null && list.size() > 0) {
            if (partner == null) {
                if (list.get(0).getPartnerId() == null || list.get(0).getPartnerId().longValue() == 0L)
                    savedPrice = list.get(0);
                if (savedPrice != null && savedPrice.getPrice() != null)
                    result = BigDecimal.valueOf(savedPrice.getPrice()
                            .doubleValue());
            } else {
                savedPrice = list.get(0);
                if (savedPrice != null && savedPrice.getPrice() != null)
                    result = BigDecimal.valueOf(savedPrice.getPrice()
                            .doubleValue());
            }
        } else {
            savedPrice = new ProductPartnerPrice();
            savedPrice.setDateCalculated(null);
            savedPrice.setProductId(instance.getId());
            List<ProductPartnerPrice> results = null;
            try {
                results = ProductPartnerPriceRepository.getInstance()
                        .getByExample(savedPrice, Restriction.AND, true,
                                0, 100000);
            } catch (ServiceException e) {
                // do nothing
            }
            if (results != null && results.size() > 0) {
                savedPrice = results.get(0);
                if (savedPrice != null && savedPrice.getPrice() != null)
                    result = BigDecimal.valueOf(savedPrice.getPrice()
                            .doubleValue());
            }
        }

        if (result != null)
            // cambio a 3 decimales
            return result.setScale(3, RoundingMode.HALF_UP);
        else
            return BigDecimal.ZERO;
    }



    public List<Product> getAllForPartner(Long id, Integer offset,
                                          Integer numElements, boolean ordenarPorCategoria, boolean ordenarAlfabeticamente) {
        User user = (User) MidbanApplication
                .getValueFromContext(ContextAttributes.LOGGED_USER);
//        if (id != null) {
//            try {
//                List<Product> result = new ArrayList<Product>();
//                PartnerProduct example = new PartnerProduct();
//                example.setPartnerId(id);
//                for (PartnerProduct aProduct : PartnerProductRepository.getInstance().getByExample(example,
//                        Restriction.AND, true, 0, 100000000)) {
//                    Product product = getById(aProduct.getProductId());
//                    if (product != null && product.getSaleOk())
//                        result.add(product);
//                }
//                return result;
//            } catch (Exception e) {
//                if (LoggerUtil.isDebugEnabled()) {
//                    e.printStackTrace();
//                    try {
//                        return getByExample(new Product(), Restriction.OR, true, numElements, offset);
//                    } catch (ServiceException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        } else {
            try {
                Partner partner = null;
                try {
                    partner = PartnerRepository.getInstance().getById(id);
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                }
                if (partner != null){
                    return getByExample(new Product(), Restriction.OR, false, numElements, offset, ordenarPorCategoria, ordenarAlfabeticamente, partner.getPricelistId().toString());
                }
                else{
                    return getByExample(new Product(), Restriction.OR, false, numElements, offset, ordenarPorCategoria, ordenarAlfabeticamente, "");
                }
            } catch (ServiceException e1) {
                e1.printStackTrace();
            }
//        }
        return new ArrayList<Product>();
    }

    public void synchronizeSubstitutes(String login, String passwd)
            throws XmlRpcException, OpeneERPApiException, ServiceException,
            ConfigurationException {
        // TODO sincronizar sustitutos cuando la función sea más adecuada
        // for (Product p : getAll()) {
        // Session openERPSession = null;
        // try {
        // openERPSession = SessionFactory.getInstance(login, passwd)
        // .getSession();
        // } catch (Exception e) {
        // // do nothing, openERPSession will be null
        // }
        // if (openERPSession != null) {
        // ObjectAdapter adapter;
        // adapter = openERPSession.getObjectAdapter("product.product");
        // FilterCollection filters = new FilterCollection();
        // filters.add("id", "=", p.getId());
        // RowCollection entities;
        // String[] fieldsRemote = { "products_substitute_ids" };
        // entities = adapter.searchAndReadObject(filters, fieldsRemote,
        // "1900-01-01 00:00:00");
        // for (Row row : entities) {
        // Object[] productIds = (Object[]) row
        // .get("products_substitute_ids");
        // String substitutes = "";
        // if (productIds != null) {
        // for (Object id : productIds) {
        // substitutes += ";" + id;
        // }
        // substitutes += ";";
        // p.setSubstituteProducts(substitutes);
        // saveOrUpdate(p);
        // }
        // }
        // }
        // }
    }

    @Override
    public List<Product> getByExample(Product entity, Restriction restriction,
                                      boolean exactMatching, Integer numElements, Integer offset)
            throws ServiceException {
        try {
            User user =
                    ((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER));
            List<Product> products = dao.getByExample(entity, restriction, exactMatching,
                    numElements, offset);
            List<Product> result = new ArrayList<Product>();
            for (Product product : products) {
                if (product.getSaleOk())
                    result.add(product);
            }
            return result;
        } catch (DatabaseException e) {
            throw new ServiceException("Cannot retrieve objects", e);
        }
    }

    @Override
    public List<Product> getAll(Integer numElements, Integer offset) throws ConfigurationException, ServiceException {
        return getAll(numElements, offset, true, false, "");
    }

    public List<Product> getAll(Integer numElements, Integer offset, boolean ordenarPorCategoria, boolean ordenarAlfabeticamente, String tarifaPorLaQueFiltrar)
            throws ConfigurationException, ServiceException {
        try {
            User user =
                ((User) MidbanApplication.getValueFromContext(ContextAttributes.LOGGED_USER));
            Product entity = new Product();
            entity.setSaleOk(true);
            entity.setActive(true);
            return dao.getByExample(entity, Restriction.OR, true,
                    numElements, offset, ordenarPorCategoria, ordenarAlfabeticamente, tarifaPorLaQueFiltrar);
        } catch (DatabaseException e) {
            throw new ServiceException("Cannot retrieve all objects", e);
        }
    }

    public List<Product> getByExample(Product productSearch, Restriction restriction, boolean exactMatching, int offset, int limit, boolean ordenarPorCategoria, boolean ordenarAlfabeticamente, String tarifaPorLaQueFiltrar) throws ServiceException {
        try {
            return dao.getByExample(productSearch, restriction, exactMatching, limit, offset, ordenarPorCategoria, ordenarAlfabeticamente, tarifaPorLaQueFiltrar);
        } catch (DatabaseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }


}
