package dao;

import dao.base.BaseDAO;
import entity.DonHang;
import enums.TrangThaiDonHang;
import java.util.List;

public interface DonHangDAO extends BaseDAO<DonHang, String> {
    List<DonHang> findByBan(String maBan);
    List<DonHang> findByCa(String maCa);
    boolean updateTrangThai(String maDonHang, TrangThaiDonHang trangThai);
    List<DonHang> findDanhSachMangVeDangPhucVu();
}
