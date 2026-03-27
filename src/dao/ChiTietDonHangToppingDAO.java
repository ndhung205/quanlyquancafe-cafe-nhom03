package dao;

import dao.base.BaseDAO;
import entity.ChiTietDonHangTopping;
import java.util.List;

public interface ChiTietDonHangToppingDAO extends BaseDAO<ChiTietDonHangTopping, String> {
    List<ChiTietDonHangTopping> findByCTDH(String maCTDH);
}
