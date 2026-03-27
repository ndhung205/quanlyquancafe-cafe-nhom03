package dao;

import dao.base.BaseDAO;
import entity.ChiTietDonHang;
import java.util.List;

public interface ChiTietDonHangDAO extends BaseDAO<ChiTietDonHang, String> {
    List<ChiTietDonHang> findByDonHang(String maDonHang);
    boolean deleteByDonHang(String maDonHang);
}
