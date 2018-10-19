package com.cafedered.midban.service.repositories;

import com.cafedered.midban.dao.PickingTypeDAO;
import com.cafedered.midban.entities.PickingType;


public class PickingTypeRepository extends BaseRepository<PickingType, PickingTypeDAO> {
    private static PickingTypeRepository instance;

    private PickingTypeRepository() {
        dao = PickingTypeDAO.getInstance();
    }

    public static PickingTypeRepository getInstance() {
        if (instance == null)
            instance = new PickingTypeRepository();
        return instance;
    }
}
