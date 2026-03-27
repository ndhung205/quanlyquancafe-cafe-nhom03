package dao.impl;

import connectDB.DatabaseConnection;
import dao.DonHangDAO;
import entity.DonHang;
import enums.LoaiDon;
import enums.TrangThaiDonHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonHangDAOImpl implements DonHangDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private DonHang mapRow(ResultSet rs) throws SQLException {
        return new DonHang(
            rs.getString("maDonHang"),
            rs.getTimestamp("thoiGianMo") != null ? rs.getTimestamp("thoiGianMo").toLocalDateTime() : null,
            rs.getTimestamp("thoiGianChot") != null ? rs.getTimestamp("thoiGianChot").toLocalDateTime() : null,
            rs.getDouble("tongTienTamTinh"),
            rs.getString("ghiChu"),
            TrangThaiDonHang.valueOf(rs.getString("trangThai")),
            LoaiDon.valueOf(rs.getString("loaiDon")),
            rs.getString("maBan"),
            rs.getString("maDatBan"),
            rs.getString("maCa"),
            rs.getString("maNV")
        );
    }

    @Override
    public boolean insert(DonHang donHang) {
        String sql = "INSERT INTO DonHang(maDonHang, thoiGianMo, thoiGianChot, tongTienTamTinh, ghiChu, trangThai, loaiDon, maBan, maDatBan, maCa, maNV) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, donHang.getMaDonHang());
            ps.setTimestamp(2, donHang.getThoiGianMo() != null ? Timestamp.valueOf(donHang.getThoiGianMo()) : null);
            ps.setTimestamp(3, donHang.getThoiGianChot() != null ? Timestamp.valueOf(donHang.getThoiGianChot()) : null);
            ps.setDouble(4, donHang.getTongTienTamTinh());
            ps.setString(5, donHang.getGhiChu());
            ps.setString(6, donHang.getTrangThai().name());
            ps.setString(7, donHang.getLoaiDon().name());
            ps.setString(8, donHang.getMaBan());
            ps.setString(9, donHang.getMaDatBan());
            ps.setString(10, donHang.getMaCa());
            ps.setString(11, donHang.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.insert: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(DonHang donHang) {
        String sql = "UPDATE DonHang SET thoiGianMo=?, thoiGianChot=?, tongTienTamTinh=?, ghiChu=?, trangThai=?, loaiDon=?, maBan=?, maDatBan=?, maCa=?, maNV=? WHERE maDonHang=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setTimestamp(1, donHang.getThoiGianMo() != null ? Timestamp.valueOf(donHang.getThoiGianMo()) : null);
            ps.setTimestamp(2, donHang.getThoiGianChot() != null ? Timestamp.valueOf(donHang.getThoiGianChot()) : null);
            ps.setDouble(3, donHang.getTongTienTamTinh());
            ps.setString(4, donHang.getGhiChu());
            ps.setString(5, donHang.getTrangThai().name());
            ps.setString(6, donHang.getLoaiDon().name());
            ps.setString(7, donHang.getMaBan());
            ps.setString(8, donHang.getMaDatBan());
            ps.setString(9, donHang.getMaCa());
            ps.setString(10, donHang.getMaNV());
            ps.setString(11, donHang.getMaDonHang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.update: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(String maDonHang) {
        String sql = "DELETE FROM DonHang WHERE maDonHang=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maDonHang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.delete: " + e.getMessage());
        }
        return false;
    }

    @Override
    public DonHang findById(String maDonHang) {
        String sql = "SELECT * FROM DonHang WHERE maDonHang=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maDonHang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.findById: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<DonHang> findAll() {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM DonHang ORDER BY thoiGianMo DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.findAll: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<DonHang> findByBan(String maBan) {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM DonHang WHERE maBan=? AND trangThai='DANG_PHUC_VU' ORDER BY thoiGianMo DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maBan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.findByBan: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<DonHang> findByCa(String maCa) {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM DonHang WHERE maCa=? ORDER BY thoiGianMo DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maCa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.findByCa: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateTrangThai(String maDonHang, TrangThaiDonHang trangThai) {
        String sql = "UPDATE DonHang SET trangThai=? WHERE maDonHang=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, trangThai.name());
            ps.setString(2, maDonHang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.updateTrangThai: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<DonHang> findDanhSachMangVeDangPhucVu() {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM DonHang WHERE loaiDon='MANG_VE' AND trangThai='DANG_PHUC_VU' ORDER BY thoiGianMo DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("DonHangDAOImpl.findDanhSachMangVeDangPhucVu: " + e.getMessage());
        }
        return list;
    }
}
