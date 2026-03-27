package controller;

import dao.BanDAO;
import dao.DonHangDAO;
import dao.KhuVucDAO;
import dao.impl.BanDAOImpl;
import dao.impl.DonHangDAOImpl;
import dao.impl.KhuVucDAOImpl;
import entity.Ban;
import entity.DonHang;
import entity.KhuVuc;
import entity.ChiTietDonHang;
import enums.TrangThaiBan;
import enums.TrangThaiDonHang;

import java.util.List;
import dao.ChiTietDonHangDAO;
import dao.impl.ChiTietDonHangDAOImpl;

/**
 * Xử lý nghiệp vụ bàn: lấy danh sách, đổi trạng thái, tìm đơn hàng đang mở.
 */
public class TableController {

    private final BanDAO banDAO;
    private final KhuVucDAO khuVucDAO;
    private final DonHangDAO donHangDAO;
    private final ChiTietDonHangDAO ctdhDAO;

    public TableController() {
        this.banDAO = new BanDAOImpl();
        this.khuVucDAO = new KhuVucDAOImpl();
        this.donHangDAO = new DonHangDAOImpl();
        this.ctdhDAO = new ChiTietDonHangDAOImpl();
    }

    public List<KhuVuc> getDanhSachKhuVuc() {
        return khuVucDAO.findActive();
    }

    /**
     * Lấy tất cả bàn (mọi khu vực).
     */
    public List<Ban> getAllBan() {
        return banDAO.findAll();
    }

    /**
     * Lấy bàn theo khu vực. Nếu maKhuVuc null/empty thì trả tất cả.
     */
    public List<Ban> getBanByKhuVuc(String maKhuVuc) {
        if (maKhuVuc == null || maKhuVuc.isEmpty()) {
            return banDAO.findAll();
        }
        return banDAO.findByKhuVuc(maKhuVuc);
    }

    /**
     * Cập nhật trạng thái bàn.
     */
    public void capNhatTrangThai(String maBan, TrangThaiBan trangThai) {
        banDAO.updateTrangThai(maBan, trangThai);
    }

    /**
     * Tìm đơn hàng DANG_PHUC_VU của bàn (đơn đang mở).
     * 
     * @return DonHang hoặc null nếu bàn trống
     */
    public DonHang getDonHangDangMo(String maBan) {
        List<DonHang> ds = donHangDAO.findByBan(maBan);
        for (DonHang dh : ds) {
            if (TrangThaiDonHang.DANG_PHUC_VU.equals(dh.getTrangThai())
                    || TrangThaiDonHang.CHO_THANH_TOAN.equals(dh.getTrangThai())) {
                return dh;
            }
        }
        return null;
    }

    public List<Ban> getBanTrong() {
        return banDAO.findByTrangThai(TrangThaiBan.TRONG);
    }

    public List<Ban> getBanDangCoKhach() {
        return banDAO.findByTrangThai(TrangThaiBan.CO_KHACH);
    }

    /**
     * Chuyển đơn hàng (từ bàn nguồn đang có khách sang bàn đích trống)
     */
    public void chuyenBan(String maHD, String maBanNguon, String maBanDich) {
        DonHang dh = donHangDAO.findById(maHD);
        if (dh == null)
            return;

        // Cập nhật mã bàn cho đơn hàng
        dh.setMaBan(maBanDich);
        donHangDAO.update(dh);

        // Cập nhật trạng thái bàn
        banDAO.updateTrangThai(maBanNguon, TrangThaiBan.TRONG);
        banDAO.updateTrangThai(maBanDich, TrangThaiBan.CO_KHACH);
    }

    /**
     * Gộp 2 bàn có khách (Gộp A -> B). Đơn hàng A sẽ gộp chi tiết vào B rồi bị hủy.
     */
    public void gopBan(String maDonNguon, String maDonDich, String maBanNguon, String maBanDich) {
        DonHang dhNguon = donHangDAO.findById(maDonNguon);
        DonHang dhDich = donHangDAO.findById(maDonDich);
        if (dhNguon == null || dhDich == null)
            return;

        // Đổi maDonHang của tất cả ctdh nguồn sang maDonDich
        List<ChiTietDonHang> ctdhNguon = ctdhDAO.findByDonHang(maDonNguon);
        for (ChiTietDonHang ct : ctdhNguon) {
            ct.setMaDonHang(maDonDich);
            ctdhDAO.update(ct);
        }

        // Cập nhật tổng tiền đơn đích
        dhDich.setTongTienTamTinh(dhDich.getTongTienTamTinh() + dhNguon.getTongTienTamTinh());
        donHangDAO.update(dhDich);

        // Xóa đơn nguồn (Hoặc chuyển thành DA_HUY)
        donHangDAO.updateTrangThai(maDonNguon, TrangThaiDonHang.DA_HUY);

        // Cập nhật trạng thái bàn nguồn
        banDAO.updateTrangThai(maBanNguon, TrangThaiBan.TRONG);
    }
}
