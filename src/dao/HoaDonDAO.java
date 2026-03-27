package dao;

import dao.base.BaseDAO;
import entity.HoaDon;
import java.time.LocalDate;
import java.util.List;

public interface HoaDonDAO extends BaseDAO<HoaDon, String> {
    HoaDon findByDonHang(String maDonHang);
    List<HoaDon> findByCa(String maCa);
    List<HoaDon> findByNgay(LocalDate ngay);
}
