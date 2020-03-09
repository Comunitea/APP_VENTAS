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
import com.cafedered.midban.service.repositories.ProductTemplateRepository;
import com.cafedered.midban.service.repositories.ProductUlRepository;
import com.cafedered.midban.service.repositories.ProductUomRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.OpeneERPApiException;

import java.math.BigDecimal;

@Entity(tableName = "product_product")
@Remote(object = "product.product")
public class Product extends BaseRemoteEntity {

    private static final long serialVersionUID = 2325116999521804028L;

    @Id(autoIncrement = false, column = "id")
    @RemoteProperty(name = "id")
    private Long id;

    @Property(columnName = "image_medium")
    @RemoteProperty(name = "image_medium")
    private byte[] imageMedium;

    @Property(columnName = "image_small")
    @RemoteProperty(name = "image_small")
    private byte[] imageSmall;

    @Property(columnName = "name_template")
    @RemoteProperty(name = "name_template")
    private String nameTemplate;

    @Property(columnName = "product_tmpl_id")
    @RemoteProperty(name = "product_tmpl_id",
            entityRef = ProductTemplate.class,
            repositoryRef = ProductTemplateRepository.class,
            orderedProperties = { "id", "name" })
    private Number productTmplId;

    @Property(columnName = "ul")
    @RemoteProperty(name = "ul",
            entityRef = ProductUl.class,
            repositoryRef = ProductUlRepository.class,
            orderedProperties = { "id", "name" })
    private Number ul;

    @Property(columnName = "color")
    @RemoteProperty(name = "color")
    private Number color;

    @Property(columnName = "lst_price")
    @RemoteProperty(name = "lst_price")
    private Number lstPrice;

    @Property(columnName = "partner_ref")
    @RemoteProperty(name = "partner_ref")
    private String partnerRef;
    //
    // @Property(columnName = "price", type = Property.SQLType.REAL)
    // @RemoteProperty(name = "price")
    // private Number price;

    @Property(columnName = "price_extra")
    @RemoteProperty(name = "price_extra")
    private Number priceExtra;

    @Property(columnName = "sale_ok")
    @RemoteProperty(name = "sale_ok")
    private Boolean saleOk;

    @Property(columnName = "purchase_ok")
    @RemoteProperty(name = "purchase_ok")
    private Boolean purchaseOk;

    @Property(columnName = "type")
    @RemoteProperty(name = "type")
    private String type;

    @Property(columnName = "uom_id")
    @RemoteProperty(name = "uom_id")
    private Number uomId;

    @Property(columnName = "uos_id")
    @RemoteProperty(name = "uos_id")
    private Number uosId;

    @Property(columnName = "list_price")
    @RemoteProperty(name = "list_price")
    private Number listPrice;

    @Property(columnName = "default_code")
    @RemoteProperty(name = "default_code")
    private String defaultCode;

    @Property(columnName = "ean13")
    @RemoteProperty(name = "ean13")
    private String ean13;

    @Property(columnName = "dun14")
    @RemoteProperty(name = "dun14")
    private String dun14;

    @Property(columnName = "temperature", type = Property.SQLType.REAL)
    @RemoteProperty(name = "temperature")
    private Number temperature;

    @Property(columnName = "cost_method")
    @RemoteProperty(name = "cost_method")
    private String costMethod;

    @Property(columnName = "standard_price")
    @RemoteProperty(name = "standard_price")
    private Number standardPrice;

    @Property(columnName = "produce_delay")
    @RemoteProperty(name = "produce_delay")
    private Number produceDelay;

    @Property(columnName = "active")
    @RemoteProperty(name = "active")
    private Boolean active;

    @Property(columnName = "uom_po_id")
    @RemoteProperty(name = "uom_po_id")
    private Number uomPoId;

    @Property(columnName = "qty_available", type = Property.SQLType.REAL)
    @RemoteProperty(name = "qty_available")
    private Number qtyAvailable;

    @Property(columnName = "incoming_qty", type = Property.SQLType.REAL)
    @RemoteProperty(name = "incoming_qty")
    private Number incomingQty;

    @Property(columnName = "outgoing_qty", type = Property.SQLType.REAL)
    @RemoteProperty(name = "outgoing_qty")
    private Number outgoingQty;

    @Property(columnName = "virtual_available")
    @RemoteProperty(name = "virtual_available")
    private Number virtualAvailable;

    @Property(columnName = "track_production")
    @RemoteProperty(name = "track_production")
    private Boolean trackProduction;

    @Property(columnName = "track_incoming")
    @RemoteProperty(name = "track_incoming")
    private Boolean trackIncoming;

    @Property(columnName = "track_outgoing")
    @RemoteProperty(name = "track_outgoing")
    private Boolean trackOutgoing;

    @Property(columnName = "state")
    @RemoteProperty(name = "state")
    private String state;

    @Property(columnName = "loc_rack")
    @RemoteProperty(name = "loc_rack")
    private String locRack;

    @Property(columnName = "loc_row")
    @RemoteProperty(name = "loc_row")
    private String locRow;

    @Property(columnName = "loc_case")
    @RemoteProperty(name = "loc_case")
    private String locCase;

    @Property(columnName = "volume", type = Property.SQLType.REAL)
    @RemoteProperty(name = "volume")
    private Number volume;

    @Property(columnName = "weight", type = Property.SQLType.REAL)
    @RemoteProperty(name = "weight")
    private Number weight;

    @Property(columnName = "weight_net", type = Property.SQLType.REAL)
    @RemoteProperty(name = "weight_net")
    private Number weightNet;

    @Property(columnName = "life_time")
    @RemoteProperty(name = "life_time")
    private Number lifeTime;

    @Property(columnName = "use_time")
    @RemoteProperty(name = "use_time")
    private Number useTime;

    @Property(columnName = "removal_time")
    @RemoteProperty(name = "removal_time")
    private Number removalTime;

    @Property(columnName = "alert_time")
    @RemoteProperty(name = "alert_time")
    private Number alertTime;

    @Property(columnName = "warranty")
    @RemoteProperty(name = "warranty")
    private Number warranty;

    @Property(columnName = "sale_delay")
    @RemoteProperty(name = "sale_delay")
    private Number saleDelay;

    @Property(columnName = "box_units")
    @RemoteProperty(name = "box_units")
    private Number boxUnits;

    @Property(columnName = "pallet_boxes_pallet")
    @RemoteProperty(name = "pallet_boxes_pallet")
    private Number boxesPerPallet;

    @Property(columnName = "pallet_gross_weight")
    @RemoteProperty(name = "pallet_gross_weight")
    private Number palletGrossWeight;

    @Property(columnName = "pallet_total_height")
    @RemoteProperty(name = "pallet_total_height")
    private Number palletTotalHeight;

    @Property(columnName = "pallet_ul")
    @RemoteProperty(name = "pallet_ul")
    private Number typeOfPallet;


    @Property(columnName = "substitute_products")
    private String substituteProducts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImageMedium() {
        return imageMedium;
    }

    public void setImageMedium(byte[] imageMedium) {
        this.imageMedium = imageMedium;
    }

    public byte[] getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(byte[] imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public void setNameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }

    public Number getProductTmplId() {
        return productTmplId;
    }

    public void setProductTmplId(Number productTmplId) {
        this.productTmplId = productTmplId;
    }

    public String getCode() {
        return defaultCode;
    }

    public void setCode(String code) {
        this.defaultCode = code;
    }

    public Number getColor() {
        return color;
    }

    public void setColor(Number color) {
        this.color = color;
    }

    public Number getLstPrice() {
        return lstPrice;
    }

    public void setLstPrice(Number lstPrice) {
        this.lstPrice = lstPrice;
    }

    public String getPartnerRef() {
        return partnerRef;
    }

    public void setPartnerRef(String partnerRef) {
        this.partnerRef = partnerRef;
    }

    // public Number getPrice() {
    // return price;
    // }
    //
    // public void setPrice(Number price) {
    // this.price = price;
    // }

    public Number getPriceExtra() {
        return priceExtra;
    }

    public void setPriceExtra(Number priceExtra) {
        this.priceExtra = priceExtra;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Number getUomId() {
        return uomId;
    }

    public void setUomId(Number uomId) {
        this.uomId = uomId;
    }

    public Number getListPrice() {
        return listPrice;
    }

    public void setListPrice(Number listPrice) {
        this.listPrice = listPrice;
    }

    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }

    public String getEan13() {
        return ean13;
    }

    public void setEan13(String ean13) {
        this.ean13 = ean13;
    }

    public String getDun14() {
        return dun14;
    }

    public void setDun14(String dun14) {
        this.dun14 = dun14;
    }

    public Number getTemperature() {
        return temperature;
    }

    public void setTemperature(Number temperature) {
        this.temperature = temperature;
    }

    public String getCostMethod() {
        return costMethod;
    }

    public void setCostMethod(String costMethod) {
        this.costMethod = costMethod;
    }

    public Number getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Number standardPrice) {
        this.standardPrice = standardPrice;
    }

    public Number getProduceDelay() {
        return produceDelay;
    }

    public void setProduceDelay(Number produceDelay) {
        this.produceDelay = produceDelay;
    }

    public Number getUomPoId() {
        return uomPoId;
    }

    public void setUomPoId(Number uomPoId) {
        this.uomPoId = uomPoId;
    }

    public Number getQtyAvailable() {
        return qtyAvailable;
    }

    public void setQtyAvailable(Number qtyAvailable) {
        this.qtyAvailable = qtyAvailable;
    }

    public Number getIncomingQty() {
        return incomingQty;
    }

    public void setIncomingQty(Number incomingQty) {
        this.incomingQty = incomingQty;
    }

    public Number getOutgoingQty() {
        return outgoingQty;
    }

    public void setOutgoingQty(Number outgoingQty) {
        this.outgoingQty = outgoingQty;
    }

    public Number getVirtualAvailable() {
        if (virtualAvailable != null)
            return (Number) new BigDecimal(virtualAvailable.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
        return virtualAvailable;
    }

    public void setVirtualAvailable(Number virtualAvailable) {
        this.virtualAvailable = virtualAvailable;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocRack() {
        return locRack;
    }

    public void setLocRack(String locRack) {
        this.locRack = locRack;
    }

    public String getLocRow() {
        return locRow;
    }

    public void setLocRow(String locRow) {
        this.locRow = locRow;
    }

    public String getLocCase() {
        return locCase;
    }

    public void setLocCase(String locCase) {
        this.locCase = locCase;
    }

    public Number getVolume() {
        return volume;
    }

    public void setVolume(Number volume) {
        this.volume = volume;
    }

    public Number getWeight() {
        return weight;
    }

    public void setWeight(Number weight) {
        this.weight = weight;
    }

    public Number getWeightNet() {
        return weightNet;
    }

    public void setWeightNet(Number weightNet) {
        this.weightNet = weightNet;
    }

    public Number getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Number lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Number getUseTime() {
        return useTime;
    }

    public void setUseTime(Number useTime) {
        this.useTime = useTime;
    }

    public Number getRemovalTime() {
        return removalTime;
    }

    public void setRemovalTime(Number removalTime) {
        this.removalTime = removalTime;
    }

    public Number getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Number alertTime) {
        this.alertTime = alertTime;
    }

    public Number getWarranty() {
        return warranty;
    }

    public void setWarranty(Number warranty) {
        this.warranty = warranty;
    }

    public Number getSaleDelay() {
        return saleDelay;
    }

    public void setSaleDelay(Number saleDelay) {
        this.saleDelay = saleDelay;
    }

    public Boolean getSaleOk() {
        return saleOk;
    }

    public void setSaleOk(Boolean saleOk) {
        this.saleOk = saleOk;
    }

    public Boolean getPurchaseOk() {
        return purchaseOk;
    }

    public void setPurchaseOk(Boolean purchaseOk) {
        this.purchaseOk = purchaseOk;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getTrackProduction() {
        return trackProduction;
    }

    public void setTrackProduction(Boolean trackProduction) {
        this.trackProduction = trackProduction;
    }

    public Boolean getTrackIncoming() {
        return trackIncoming;
    }

    public void setTrackIncoming(Boolean trackIncoming) {
        this.trackIncoming = trackIncoming;
    }

    public Boolean getTrackOutgoing() {
        return trackOutgoing;
    }

    public void setTrackOutgoing(Boolean trackOutgoing) {
        this.trackOutgoing = trackOutgoing;
    }

    public String getSubstituteProducts() {
        return substituteProducts;
    }

    public void setSubstituteProducts(String substituteProducts) {
        this.substituteProducts = substituteProducts;
    }

    public Number getUl() {
        return ul;
    }

    public void setUl(Number ul) {
        this.ul = ul;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public FilterCollection getRemoteFilters() {
        FilterCollection filters = new FilterCollection();
        try {
            filters.add("sale_ok", "=", true);
        } catch (OpeneERPApiException e) {
            e.printStackTrace();
        }
        return filters;
    }

    @Override
    public Integer getPendingSynchronization() {
        return null;
    }

    @Override
    public void setPendingSynchronization(Integer pendingSynchronization) {

    }

    public ProductTemplate getProductTemplate() {
        try {
            return ProductTemplateRepository.getInstance().getById(
                    this.productTmplId.longValue());
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;//TODO no enviar
    }

    public ProductUl getProductUl() {
        try {
            return ProductUlRepository.getInstance().getById(
                    this.ul.longValue());
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    public ProductUom getUom() {
        try {
            return ProductUomRepository.getInstance().getById(
                    this.uomId.longValue());
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    public ProductUom getUos() {
        try {
            if ((this.uosId != null) && (this.uosId.longValue() != 0.0)){
                return ProductUomRepository.getInstance().getById(
                        this.uosId.longValue());
            }
            else
                return getUom();

        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
        return null;
    }

    // FIXME implement this when we know data of minPrice for product
    // public Float getMinPrice() {
    // return price.floatValue() - (0.1F * price.floatValue());
    // }

    // FIXME implement this when we know data of maxDiscount for product
    public Float getMaxDiscount() {
        return 20F;
    }

    public Number getUosId() {
        return uosId;
    }

    public void setUosId(Number uosId) {
        this.uosId = uosId;
    }

    public Number getBoxUnits() {
        return boxUnits;
    }

    public void setBoxUnits(Number boxUnits) {
        this.boxUnits = boxUnits;
    }

    public Number getBoxesPerPallet() {
        return boxesPerPallet;
    }

    public void setBoxesPerPallet(Number boxesPerPallet) {
        this.boxesPerPallet = boxesPerPallet;
    }

    public Number getPalletGrossWeight() {
        return palletGrossWeight;
    }

    public void setPalletGrossWeight(Number palletGrossWeight) {
        this.palletGrossWeight = palletGrossWeight;
    }

    public Number getPalletTotalHeight() {
        return palletTotalHeight;
    }

    public void setPalletTotalHeight(Number palletTotalHeight) {
        this.palletTotalHeight = palletTotalHeight;
    }

    public Number getTypeOfPallet() {
        return typeOfPallet;
    }

    public void setTypeOfPallet(Number typeOfPallet) {
        this.typeOfPallet = typeOfPallet;
    }
}
