package com.cafedered.midban.service.repositories;

import com.cafedered.cafedroidlitedao.extractor.Restriction;
import com.cafedered.midban.dao.PricelistPricesDAO;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.PricelistPrices;
import com.cafedered.midban.entities.Product;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;

import java.util.ArrayList;
import java.util.List;

public class PricelistPricesRepository extends BaseRepository<PricelistPrices, PricelistPricesDAO> {
    private static PricelistPricesRepository instance = null;

    public static PricelistPricesRepository getInstance() {
        if (instance == null)
            instance = new PricelistPricesRepository();
        return instance;
    }

    private PricelistPricesRepository() {
        dao = PricelistPricesDAO.getInstance();
    }

    public static List<Product>  getProductsOfPriceList(Long pricelistId) {
        ArrayList<Product> result = new ArrayList<Product>();
        try {
            PricelistPrices example = new PricelistPrices();

            example.setPricelistId(pricelistId);
            List<PricelistPrices> prices = PricelistPricesRepository.getInstance().getByExample(example, Restriction.AND,
                    true, 0, 100000000);
            for (PricelistPrices price: prices) {
                Product product = ProductRepository.getInstance().getById(price.getProductId().longValue());
                if (product != null) {
                    result.add(product);
                }
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Product>  getProductsOfPartner(Long partner){
        Partner p = null;
        try {

            p = PartnerRepository.getInstance().getById(partner);

            // voy a mirar si es una dirección de entrega, en ese caso la tarifa a usar será la del parentId
            if ((p != null) && (p.getType() != null) && (p.getType().equals("delivery")) && (p.getParentId().longValue() > 0)){
                p = PartnerRepository.getInstance().getById(p.getParentId().longValue());
            }

            if (p.getPricelistId() != null){
                return getProductsOfPriceList(p.getPricelistId().longValue());
            }

        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return new ArrayList<Product>();
    }

}
