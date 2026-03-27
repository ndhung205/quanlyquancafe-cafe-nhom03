package controller;

import dao.BanDAO;
import dao.DonHangDAO;
import dao.HoaDonDAO;
import dao.impl.BanDAOImpl;
import dao.impl.DonHangDAOImpl;
import dao.impl.HoaDonDAOImpl;
import entity.DonHang;
import entity.HoaDon;
import dto.CartItem;
import enums.HinhThucThanhToan;
import enums.TrangThaiBan;
import enums.TrangThaiDonHang;
import enums.TrangThaiHoaDon;
import exception.AppException;
import utils.IDGenerator;
import utils.SessionManager;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Xử lý nghiệp vụ thanh toán: xuất hoá đơn, chốt đơn, giải phóng bàn.
 */
public class PaymentController {

    private final HoaDonDAO hoaDonDAO;
    private final DonHangDAO donHangDAO;
    private final BanDAO banDAO;
    private final InventoryController inventory;

    public PaymentController() {
        this.hoaDonDAO = new HoaDonDAOImpl();
        this.donHangDAO = new DonHangDAOImpl();
        this.banDAO = new BanDAOImpl();
        this.inventory = new InventoryController();
    }

    /**
     * Thực hiện thanh toán cho một đơn hàng.
     * @param donHang Đơn hàng hiện tại
     * @param tongTienPhaiTra Số tiền khách cần thanh toán sau khi trừ/cộng (nếu có logic nâng cao)
     * @param hinhThuc Hình thức (Tiền mặt / Chuyển khoản)
     * @return HoaDon vừa được tạo.
     */
    public HoaDon thanhToan(DonHang donHang, List<CartItem> cart, double tongTienPhaiTra, HinhThucThanhToan hinhThuc) {
        if (!SessionManager.isCaDangMo()) {
            throw new AppException("Vui l\u00F2ng m\u1EDF ca l\u00E0m vi\u1EC7c tr\u01B0\u1EDBc khi thanh to\u00E1n!");
        }

        if (donHang == null || !TrangThaiDonHang.DANG_PHUC_VU.equals(donHang.getTrangThai())) {
            throw new AppException("\u0110\u01A1n h\u00E0ng kh\u00F4ng h\u1EE3p l\u1EC7 ho\u1EB7c kh\u00F4ng \u1EDF tr\u1EA1ng th\u00E1i \u0111ang ph\u1EE5c v\u1EE5!");
        }

        // T\u1EA1o ho\u00E1 \u0111\u01A1n
        String maHD = IDGenerator.newMaHoaDon();
        HoaDon hd = new HoaDon(
            maHD,
            LocalDateTime.now(), // thoiGianXuat
            LocalDateTime.now(), // thoiGianThanhToan
            tongTienPhaiTra,
            TrangThaiHoaDon.DA_THANH_TOAN,
            hinhThuc,
            donHang.getMaDonHang(),
            SessionManager.getMaNVHienTai()
        );

        boolean ok = hoaDonDAO.insert(hd);
        if (!ok) {
            throw new AppException("L\u1ED7i h\u1EC7 th\u1ED1ng khi l\u01B0u h\u00F3a \u0111\u01A1n!");
        }

        // C\u1EADp nh\u1EADt \u0110\u01A1n H\u00E0ng -> DA_HOAN_THANH
        donHang.setThoiGianChot(LocalDateTime.now());
        donHangDAO.update(donHang); // update t.gian chot. Nhung chi c\u1EA7n updateTrangThai
        donHangDAO.updateTrangThai(donHang.getMaDonHang(), TrangThaiDonHang.DA_HOAN_THANH);

        // Gi\u1EA3i ph\u00F3ng B\u00E0n (Chuy\u1EC3n v\u1EC1 TRONG), tr\u1EEBF m\u00E3 mang v\u1EC1
        String maBan = donHang.getMaBan();
        if (maBan != null && !maBan.isEmpty() && !"MANG_VE".equals(maBan)) {
            banDAO.updateTrangThai(maBan, TrangThaiBan.TRONG);
        }

        // Trừ hệ số tồn kho các nguyên liệu
        inventory.deductStock(cart);

        return hd;
    }
}
