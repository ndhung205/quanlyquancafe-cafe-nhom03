package controller;

import dao.CaLamViecDAO;
import dao.DonHangDAO;
import dao.HoaDonDAO;
import dao.KhuVucDAO;
import dao.impl.CaLamViecDAOImpl;
import dao.impl.DonHangDAOImpl;
import dao.impl.HoaDonDAOImpl;
import dao.impl.KhuVucDAOImpl;
import entity.CaLamViec;
import entity.DonHang;
import entity.HoaDon;
import entity.KhuVuc;
import enums.TrangThaiCa;
import exception.AppException;
import utils.IDGenerator;
import utils.SessionManager;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Xử lý nghiệp vụ ca làm việc: mở ca, đóng ca, kiểm tra ca hiện tại.
 */
public class ShiftController {

    private final CaLamViecDAO caDAO;
    private final KhuVucDAO khuVucDAO;
    private final DonHangDAO donHangDAO;
    private final HoaDonDAO hoaDonDAO;

    public ShiftController() {
        this.caDAO = new CaLamViecDAOImpl();
        this.khuVucDAO = new KhuVucDAOImpl();
        this.donHangDAO = new DonHangDAOImpl();
        this.hoaDonDAO = new HoaDonDAOImpl();
    }

    /**
     * Lấy danh sách khu vực đang hoạt động (cho combo chọn khi mở ca).
     */
    public java.util.List<KhuVuc> getDanhSachKhuVuc() {
        return khuVucDAO.findActive();
    }

    /**
     * Kiểm tra nhân viên hiện tại có ca DANG_LAM hay không.
     * Nếu có, tự động cập nhật vào SessionManager.
     * @return CaLamViec đang mở, hoặc null nếu chưa có
     */
    public CaLamViec kiemTraCaHienTai() {
        if (!SessionManager.isLoggedIn()) return null;
        CaLamViec ca = caDAO.findCaHienTai(SessionManager.getMaNVHienTai());
        SessionManager.setCurrentCa(ca);
        return ca;
    }

    /**
     * Mở ca làm việc mới.
     * @param tienDauCa số tiền mặt đầu ca (petty cash)
     */
    public CaLamViec moCa(double tienDauCa, String maKhuVuc) {
        if (!SessionManager.isLoggedIn()) {
            throw new AppException("Chưa đăng nhập!");
        }
        if (maKhuVuc == null || maKhuVuc.trim().isEmpty()) {
            throw new AppException("Vui lòng chọn khu vực làm việc!");
        }

        String maNV = SessionManager.getMaNVHienTai();

        // Kiểm tra có ca cũ chưa đóng
        CaLamViec caHienTai = caDAO.findCaHienTai(maNV);
        if (caHienTai != null) {
            throw new AppException("Bạn đang có ca chưa đóng (Mã: " + caHienTai.getMaCa() + ").");
        }

        String maCa = IDGenerator.newMaCaLamViec();
        CaLamViec caMoi = new CaLamViec(
            maCa,
            LocalDate.now(),
            LocalTime.now(),
            null,           // gioKetThuc = null
            tienDauCa,      // tongDoanhThu ban đầu = tiền đầu ca
            TrangThaiCa.DANG_LAM,
            maNV,
            maKhuVuc
        );

        boolean ok = caDAO.insert(caMoi);
        if (!ok) {
            throw new AppException("Lỗi hệ thống khi mở ca!");
        }

        SessionManager.setCurrentCa(caMoi);
        return caMoi;
    }

    /**
     * Đóng ca hiện tại.
     * @param tienThucTe tổng tiền mặt thực đếm trong két
     */
    public void dongCa(double tienThucTe) {
        CaLamViec ca = SessionManager.getCurrentCa();
        if (ca == null || !TrangThaiCa.DANG_LAM.equals(ca.getTrangThai())) {
            throw new AppException("Không có ca nào đang mở để đóng.");
        }

        caDAO.updateTongDoanhThu(ca.getMaCa(), tienThucTe);
        boolean ok = caDAO.dongCa(ca.getMaCa());
        if (!ok) {
            throw new AppException("Lỗi hệ thống khi đóng ca!");
        }

        SessionManager.setCurrentCa(null);
    }

    // ── Báo cáo cuối ca ──────────────────────────────────────────────────

    /**
     * Đếm số đơn hàng hoàn thành trong ca hiện tại.
     */
    public int getSoDonHoanThanh(String maCa) {
        java.util.List<DonHang> dsDon = donHangDAO.findByCa(maCa);
        int count = 0;
        for (DonHang dh : dsDon) {
            if (enums.TrangThaiDonHang.DA_HOAN_THANH.equals(dh.getTrangThai())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tính tổng doanh thu thực tế (từ hoá đơn đã thanh toán trong ca).
     */
    public double getTongDoanhThuCa(String maCa) {
        java.util.List<HoaDon> dsHD = hoaDonDAO.findByCa(maCa);
        double total = 0;
        for (HoaDon hd : dsHD) {
            if (enums.TrangThaiHoaDon.DA_THANH_TOAN.equals(hd.getTrangThai())) {
                total += hd.getTongTienPhaiTra();
            }
        }
        return total;
    }

    /**
     * Đếm tổng số đơn (bao gồm cả đang phục vụ, huỷ...) trong ca.
     */
    public int getTongSoDon(String maCa) {
        return donHangDAO.findByCa(maCa).size();
    }
}
