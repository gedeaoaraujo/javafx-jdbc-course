package model.services;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

import java.util.List;

public class SellerService {

    private SellerDao dao = DaoFactory.createSellerDao();

    public List<Seller> findAll() {
        return dao.findAll();
    }

    public void saveOrUpdate(Seller object){
        if(object.getId() == null){
            dao.insert(object);
        }else{
            dao.update(object);
        }
    }

    public void remove(Seller object){
        dao.deleteById(object.getId());
    }
}
