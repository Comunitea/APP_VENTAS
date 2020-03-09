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
package com.cafedered.midban.entities;

import com.cafedered.cafedroidlitedao.annotations.Entity;
import com.cafedered.cafedroidlitedao.annotations.Id;
import com.cafedered.cafedroidlitedao.annotations.Property;
import com.cafedered.midban.annotations.Remote;
import com.cafedered.midban.annotations.RemoteProperty;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.service.repositories.ProductRepository;
import com.cafedered.midban.service.repositories.ProductUomRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity(tableName = "sale_order_line")
@Remote(object = "sale.order.line")
public class OrderLine extends BaseRemoteEntity {

    private static final long serialVersionUID = -6151808321374334031L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "product_id")
    @RemoteProperty(name = "product_id")
    private Number productId;

    @Property(columnName = "product_uom_qty", type = Property.SQLType.REAL)
    @RemoteProperty(name = "product_uom_qty")
    private Number productUomQuantity;

    @Property(columnName = "product_uos_qty", type = Property.SQLType.REAL)
    @RemoteProperty(name = "product_uos_qty")
    private Number productUosQuantity;

    @Property(columnName = "product_uos")
    @RemoteProperty(name = "product_uos")
    private Number productUos;

    @Property(columnName = "product_uom")
    @RemoteProperty(name = "product_uom")
    private Number productUom;

    @Property(columnName = "order_id")
    @RemoteProperty(name = "order_id")
    private Number orderId;

    @Property(columnName = "price_unit", type = Property.SQLType.REAL)
    @RemoteProperty(name = "price_unit")
    private Number priceUnit;

    @Property(columnName = "price_subtotal", type = Property.SQLType.REAL)
    @RemoteProperty(name = "price_subtotal")
    private Number priceSubtotal;

    // @Property(columnName = "price_udv", type = Property.SQLType.REAL)
    // @RemoteProperty(name = "price_udv")
    private Number priceUdv;

    @Property(columnName = "discount", type = Property.SQLType.REAL)
    @RemoteProperty(name = "discount")
    private Number discount;

    @Property(columnName = "order_partner_id")
    @RemoteProperty(name = "order_partner_id")
    private Number orderPartnerId;

    @Property(columnName = "name")
    @RemoteProperty(name = "name")
    private String name;

    @Property(columnName = "product_packaging")
    @RemoteProperty(name = "product_packaging")
    private Number productPackaging;

    @RemoteProperty(name = "tax_id")
    @Property(columnName = "tax_id")
    private Number[] taxesId;

    @RemoteProperty(name = "state")
    @Property(columnName = "state")
    private String state;

    private Product product;

    FilterCollection filters = new FilterCollection();

    public Product getProduct() {
        if (product == null)
            try {
                product = ProductRepository.getInstance().getById(
                        productId.longValue());
            } catch (ConfigurationException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            } catch (ServiceException e) {
                if (LoggerUtil.isDebugEnabled())
                    e.printStackTrace();
            }
        return product;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Number getPriceUdv() {
        /* DAVID - VOY A TRAMPEAR ESTE VALOR PARA QUE SE CALCULE */
        Number result = null;
        try {
            if ((getProductUos() != null) && (getPriceUnit() != null)){
                ProductUom productUom = ProductUomRepository.getInstance().getById(getProductUos().longValue());
                // cambio a 3 decimales
                result = new BigDecimal(getPriceUnit().floatValue() * productUom.getFactor_inv().floatValue()).setScale(3, BigDecimal.ROUND_HALF_UP);
            }
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }

        return result;
    }

    // esto está porque tiene que estar para que no falle, pero realmente no se usa nunca
    public void setPriceUdv(Number aPrice){
        priceUdv = aPrice;
    }

    public Number getProductUos() {
        // DAVID - introduzco este valor por defecto ante la multitud de fallos si se devuelve un null
 /*       if (productUos == null) {
            return 0F;
        }
        else
*/            return productUos;
    }

    public void setProductUos(Number productUos) {
        this.productUos = productUos;
    }

    public Number getProductUosQuantity() {
        return productUosQuantity;
    }

    public void setProductUosQuantity(Number productUosQuantity) {
        this.productUosQuantity = productUosQuantity;
        if ((getProductUos() != null) && (getProductUom() != null) && (getProductUos().longValue() == getProductUom().longValue())){
            this.productUomQuantity = productUosQuantity;
        }
        else{
            try {
                ProductUom puom = ProductUomRepository.getInstance().getById(this.productUos.longValue());
                this.productUomQuantity = new BigDecimal(getProductUosQuantity().longValue() * puom.getFactor_inv().floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                this.productUomQuantity = null;
            }

        }
    }

    public void setProductUomQuantity(Number productUomQuantity) {
        this.productUomQuantity = productUomQuantity;
        if ((getProductUos() != null) && (getProductUom() != null) && (getProductUos().longValue() == getProductUom().longValue())){
            this.productUosQuantity = productUomQuantity;
        }
        else{
            try {
                ProductUom puom = ProductUomRepository.getInstance().getById(this.productUos.longValue());
                this.productUosQuantity = new BigDecimal(getProductUomQuantity().longValue() * puom.getFactor().floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                this.productUosQuantity = null;
            }

        }
    }


    public Number[] getTaxesId() {
        return taxesId;
    }

    public void setTaxesId(Number[] taxesId) {
        this.taxesId = taxesId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Number getProductId() {
        return productId;
    }

    public void setProductId(Number productId) {
        this.productId = productId;
    }

    public Number getProductUomQuantity() {
        return productUomQuantity;
    }

    public Number getOrderId() {
        return orderId;
    }

    public void setOrderId(Number orderId) {
        this.orderId = orderId;
    }

    public Number getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Number priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Number getPriceSubtotal() {
        return priceSubtotal;
    }

    public void setPriceSubtotal(Number priceSubtotal) {
        this.priceSubtotal = priceSubtotal;
    }

    public Number getDiscount() {
        return discount;
    }

    public void setDiscount(Number discount) {
        this.discount = discount;
    }

    public Number getOrderPartnerId() {
        return orderPartnerId;
    }

    public void setOrderPartnerId(Number orderPartnerId) {
        this.orderPartnerId = orderPartnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getProductPackaging() {
        return productPackaging;
    }

    public void setProductPackaging(Number productPackaging) {
        this.productPackaging = productPackaging;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        return filters;
    }

    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

    public void cloneWithoutIdentifiers(OrderLine ol) {
        ol.setDiscount(this.getDiscount());
        ol.setName(this.getName() + "cp");
        ol.setOrderPartnerId(this.getOrderPartnerId());
        ol.setProductUomQuantity(this.getProductUomQuantity());
        ol.setProductUosQuantity(this.getProductUosQuantity());
        ol.setProductPackaging(this.getProductPackaging());
        ol.setProductId(this.getProductId());
        ol.setTaxesId(this.getTaxesId());
        ol.setProductUos(this.getProductUos());
        ol.setProductUom(this.getProductUom());
        ol.setPriceUnit(this.getPriceUnit());
        ol.setPriceSubtotal(new BigDecimal(this.getPriceUdv().floatValue() * ol.getProductUomQuantity().floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
    }

    public Number getProductUom() {
        return productUom;
    }

    public void setProductUom(Number productUom) {
        this.productUom = productUom;
    }
}
