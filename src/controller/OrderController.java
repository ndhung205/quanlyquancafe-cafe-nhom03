package controller;

import dao.BanDAO;
import dao.ChiTietDonHangDAO;
import dao.ChiTietDonHangToppingDAO;
import dao.DonHangDAO;
import dao.impl.BanDAOImpl;
import dao.impl.ChiTietDonHangDAOImpl;
import dao.impl.ChiTietDonHangToppingDAOImpl;
import dao.impl.DonHangDAOImpl;
import dto.CartItem;
import entity.Ban;
import entity.ChiTietDonHang;
import entity.ChiTietDonHangTopping;
import entity.DonHang;
import enums.LoaiDon;
import enums.TrangThaiBan;
import enums.TrangThaiDonHang;
import exception.AppException;
import utils.IDGenerator;
import utils.SessionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý nghiệp vụ gọi món, lưu order, load order cũ.
 */
public class OrderController {

    private final DonHangDAO donHangDAO;
    private final ChiTietDonHangDAO ctDonHangDAO;
    private final ChiTietDonHangToppingDAO ctToppingDAO;
    private final BanDAO banDAO;
    private final MenuController menuController;

    public OrderController() {
        this.donHangDAO = new DonHangDAOImpl();
        this.ctDonHangDAO = new ChiTietDonHangDAOImpl();
        this.ctToppingDAO = new ChiTietDonHangToppingDAOImpl();
        this.banDAO = new BanDAOImpl();
        this.menuController = new MenuController();
    }

    /**
     * Load order đang phục vụ của một đơn hàng thành List CartItem.
     */
    public List<CartItem> loadCart(String maDonHang) {
        List<CartItem> cart = new ArrayList<>();
        if (maDonHang == null)
            return cart;

        List<ChiTietDonHang> ctdhList = ctDonHangDAO.findByDonHang(maDonHang);
        for (ChiTietDonHang ct : ctdhList) {
            CartItem item = new CartItem(
                    menuController.getMonById(ct.getMaMon()),
                    menuController.getSizeById(ct.getMaSize()),
                    ct.getSoLuong(),
                    ct.getDonGia(),
                    ct.getGhiChu());

            // Load toppings cho món này
            List<ChiTietDonHangTopping> topList = ctToppingDAO.findByCTDH(ct.getMaCTDH());
            for (ChiTietDonHangTopping top : topList) {
                item.addTopping(menuController.getToppingById(top.getMaTopping()), top.getSoLuong());
                // Chỉnh sửa lại giá trị topping trong dto cho chính xác theo db lưu
                item.getToppings().get(item.getToppings().size() - 1).giaTopping = top.getGiaTopping();
            }
            cart.add(item);
        }
        return cart;
    }

    /**
     * Lưu order xuống DB.
     * 
     * @param donHangHienTai đơn hàng đang mở (có thể null nếu là tạo mới).
     * @param ban            bàn được đặt (nếu là MANG_VE thì ban=null hoặc có mã
     *                       MANG_VE tuỳ logic).
     * @param cart           danh sách các món trong giỏ.
     */
    public DonHang saveOrder(DonHang donHangHienTai, Ban ban, List<CartItem> cart) {
        if (!SessionManager.isCaDangMo()) {
            throw new AppException("Vui lòng mở ca làm việc trước khi gọi món!");
        }

        boolean isMangVe = (ban != null && "MANG_VE".equals(ban.getMaBan()));
        boolean isNew = (donHangHienTai == null);

        double tongTien = 0;
        for (CartItem item : cart) {
            tongTien += item.getThanhTien();
        }

        DonHang dh = donHangHienTai;
        if (isNew) {
            String maDH = IDGenerator.newMaDonHang();
            dh = new DonHang(
                    maDH,
                    LocalDateTime.now(),
                    null,
                    tongTien,
                    "",
                    TrangThaiDonHang.DANG_PHUC_VU,
                    isMangVe ? LoaiDon.MANG_VE : LoaiDon.TAI_BAN,
                    isMangVe ? null : ban.getMaBan(),
                    null,
                    SessionManager.getCurrentCa().getMaCa(),
                    SessionManager.getMaNVHienTai());
            donHangDAO.insert(dh);

            // Cập nhật trạng thái bàn = CÓ KHÁCH (nếu không phải mang về)
            if (!isMangVe && ban != null) {
                banDAO.updateTrangThai(ban.getMaBan(), TrangThaiBan.CO_KHACH);
            }
        } else {
            // Update tổng tiền
            dh.setTongTienTamTinh(tongTien);
            donHangDAO.update(dh);

            // Xóa hết chi tiết cũ để thêm lại từ đầu
            // Phải lấy danh sách CTDH cũ ra để xoá CTDHTopping trước (tránh FK constraint)
            List<ChiTietDonHang> oldList = ctDonHangDAO.findByDonHang(dh.getMaDonHang());
            for (ChiTietDonHang oldCT : oldList) {
                // Lấy topping con
                List<ChiTietDonHangTopping> tops = ctToppingDAO.findByCTDH(oldCT.getMaCTDH());
                for (ChiTietDonHangTopping t : tops) {
                    ctToppingDAO.delete(t.getMaID());
                }
            }
            // Sau khi xoá topping xong mới xoá CTDH
            ctDonHangDAO.deleteByDonHang(dh.getMaDonHang());
        }

        // Insert giỏ hàng mới
        for (CartItem item : cart) {
            ChiTietDonHang ct = new ChiTietDonHang(
                    IDGenerator.newMaChiTietDonHang(),
                    item.getSoLuong(),
                    item.getDonGiaSize(),
                    item.getThanhTien(), // Lưu ý: thanhTien này bao gồm cả phần Topping chưa? DB schema ghi: soLuong *
                                         // donGia (không chứa topping, topping tự getTongGiaTopping riêng).
                    item.getGhiChu(),
                    dh.getMaDonHang(),
                    item.getMon().getMaMon(),
                    item.getSize().getMaSize());
            // Sửa lại công thức theo đúng mô tả entity ChiTietDonHang:
            ct.tinhThanhTien(); // donGia * soLuong
            ctDonHangDAO.insert(ct);

            // Insert toppings
            for (CartItem.CartTopping top : item.getToppings()) {
                ChiTietDonHangTopping ctt = new ChiTietDonHangTopping(
                        IDGenerator.newMaCTDHTopping(),
                        top.soLuong,
                        top.giaTopping,
                        ct.getMaCTDH(),
                        top.topping.getMaTopping());
                ctToppingDAO.insert(ctt);
            }
        }
        return dh;
    }

    /**
     * Hủy đơn hàng đang chưa thanh toán.
     */
    public void huyDonHang(String maDonHang) {
        DonHang dh = donHangDAO.findById(maDonHang);
        if (dh != null) {
            // Cập nhật trạng thái Đơn
            donHangDAO.updateTrangThai(maDonHang, TrangThaiDonHang.DA_HUY);

            // Xóa chi tiết đơn hàng (và CTDHTopping kèm theo)
            List<ChiTietDonHang> oldList = ctDonHangDAO.findByDonHang(maDonHang);
            for (ChiTietDonHang oldCT : oldList) {
                // Xoá topping
                List<ChiTietDonHangTopping> tops = ctToppingDAO.findByCTDH(oldCT.getMaCTDH());
                for (ChiTietDonHangTopping t : tops) {
                    ctToppingDAO.delete(t.getMaID());
                }
            }
            // Xoá CTDH
            ctDonHangDAO.deleteByDonHang(maDonHang);

            // Bàn về TRỐNG
            String maBan = dh.getMaBan();
            if (maBan != null && !maBan.isEmpty() && !"MANG_VE".equals(maBan)) {
                banDAO.updateTrangThai(maBan, TrangThaiBan.TRONG);
            }
        }
    }

    /**
     * Lấy danh sách các đơn MANG VỀ đang phục vụ.
     */
    public List<DonHang> getOpenTakeawayOrders() {
        return donHangDAO.findDanhSachMangVeDangPhucVu();
    }

    /**
     * Lấy tóm tắt các món trong đơn hàng (ví dụ: "Cà phê sữa, Trà đào...")
     */
    public String getOrderSummary(String maDonHang) {
        List<ChiTietDonHang> ctdhList = ctDonHangDAO.findByDonHang(maDonHang);
        if (ctdhList.isEmpty()) return "(Ch\u01b0a c\u00f3 m\u00f3n)";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctdhList.size(); i++) {
            entity.Mon m = menuController.getMonById(ctdhList.get(i).getMaMon());
            if (m != null) {
                sb.append(m.getTenMon());
                if (i < ctdhList.size() - 1) sb.append(", ");
            }
            if (sb.length() > 50) { // Gi\u1edbi h\u1ea1n \u0111\u1ed9 d\u00e0i chu\u1ed7i t\u00f3m t\u1eaft
                sb.append("...");
                break;
            }
        }
        return sb.toString();
    }
}
