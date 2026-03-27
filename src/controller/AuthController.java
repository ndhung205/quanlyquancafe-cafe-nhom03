package controller;

import dao.NhanVienDAO;
import dao.impl.NhanVienDAOImpl;
import entity.NhanVien;
import enums.TrangThaiNhanVien;
import exception.AppException;
import utils.PasswordUtils;
import utils.SessionManager;

/**
 * Xử lý nghiệp vụ xác thực: đăng nhập, đăng xuất.
 */
public class AuthController {

    private final NhanVienDAO nhanVienDAO;

    public AuthController() {
        this.nhanVienDAO = new NhanVienDAOImpl();
    }

    /**
     * Xác thực đăng nhập.
     * @return NhanVien nếu thành công
     * @throws AppException nếu sai thông tin hoặc tài khoản bị khoá
     */
    public NhanVien login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new AppException("Vui lòng nhập tên đăng nhập!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AppException("Vui lòng nhập mật khẩu!");
        }

        NhanVien nv = nhanVienDAO.findByUsername(username.trim());
        if (nv == null) {
            throw new AppException("Sai tên đăng nhập hoặc mật khẩu!");
        }

        if (!PasswordUtils.verify(password, nv.getPasswordHash())) {
            throw new AppException("Sai tên đăng nhập hoặc mật khẩu!");
        }

        if (TrangThaiNhanVien.DA_NGHI.equals(nv.getTrangThai())) {
            throw new AppException("T\u00E0i kho\u1EA3n \u0111\u00E3 b\u1ECB v\u00F4 hi\u1EC7 u ho\u00E1.");
        }

        // Lưu vào session
        SessionManager.setCurrentUser(nv);
        return nv;
    }

    /**
     * Đăng xuất: xoá toàn bộ session.
     */
    public void logout() {
        SessionManager.clear();
    }
}
