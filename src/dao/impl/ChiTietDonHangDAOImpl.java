package dao.impl;

import connectDB.DatabaseConnection;
import dao.ChiTietDonHangDAO;
import entity.ChiTietDonHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDonHangDAOImpl implements ChiTietDonHangDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private ChiTietDonHang mapRow(ResultSet rs) throws SQLException {
        return new ChiTietDonHang(
            rs.getString("maCTDH"),
            rs.getInt("soLuong"),
            rs.getDouble("donGia"),
            rs.getDouble("thanhTien"),
            rs.getString("ghiChu"),
            rs.getString("maDonHang"),
            rs.getString("maMon"),
            rs.getString("maSize")
        );
    }

    @Override
    public boolean insert(ChiTietDonHang ct) {
        String sql = "INSERT INTO ChiTietDonHang(maCTDH, soLuong, donGia, thanhTien, ghiChu, maDonHang, maMon, maSize) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, ct.getMaCTDH());
            ps.setInt(2, ct.getSoLuong());
            ps.setDouble(3, ct.getDonGia());
            ps.setDouble(4, ct.getThanhTien());
            ps.setString(5, ct.getGhiChu());
            ps.setString(6, ct.getMaDonHang());
            ps.setString(7, ct.getMaMon());
            ps.setString(8, ct.getMaSize());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangDAOImpl.insert: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(ChiTietDonHang ct) {
        String sql = "UPDATE ChiTietDonHang SET soLuong=?, donGia=?, thanhTien=?, ghiChu=?, maDonHang=?, maMon=?, maSize=? WHERE maCTDH=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getDonGia());
            ps.setDouble(3, ct.getThanhTien());
            ps.setString(4, ct.getGhiChu());
            ps.setString(5, ct.getMaDonHang());
            ps.setString(6, ct.getMaMon());
            ps.setString(7, ct.getMaSize());
            ps.setString(8, ct.getMaCTDH());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangDAOImpl.update: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(String maCTDH) {
        String sql = "DELETE FROM ChiTietDonHang WHERE maCTDH=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maCTDH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangDAOImpl.delete: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ChiTietDonHang findById(String maCTDH) {
        String sql = "SELECT * FROM ChiTietDonHang WHERE maCTDH=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maCTDH);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangDAOImpl.findById: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ChiTietDonHang> findAll() {
        List<ChiTietDonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietDonHang ORDER BY maDonHang, maCTDH";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangDAOImpl.findAll: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<ChiTietDonHang> findByDonHang(String maDonHang) {
        List<ChiTietDonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietDonHang WHERE maDonHang=? ORDER BY maCTDH";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maDonHang);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangDAOImpl.findByDonHang: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean deleteByDonHang(String maDonHang) {
        String sql = "DELETE FROM ChiTietDonHang WHERE maDonHang=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maDonHang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangDAOImpl.deleteByDonHang: " + e.getMessage());
        }
        return false;
    }
}
