package dao.impl;

import connectDB.DatabaseConnection;
import dao.ChiTietDonHangToppingDAO;
import entity.ChiTietDonHangTopping;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDonHangToppingDAOImpl implements ChiTietDonHangToppingDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private ChiTietDonHangTopping mapRow(ResultSet rs) throws SQLException {
        return new ChiTietDonHangTopping(
            rs.getString("maID"),
            rs.getInt("soLuong"),
            rs.getDouble("giaTopping"),
            rs.getString("maCTDH"),
            rs.getString("maTopping")
        );
    }

    @Override
    public boolean insert(ChiTietDonHangTopping ct) {
        String sql = "INSERT INTO ChiTietDonHangTopping(maID, soLuong, giaTopping, maCTDH, maTopping) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, ct.getMaID());
            ps.setInt(2, ct.getSoLuong());
            ps.setDouble(3, ct.getGiaTopping());
            ps.setString(4, ct.getMaCTDH());
            ps.setString(5, ct.getMaTopping());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangToppingDAOImpl.insert: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(ChiTietDonHangTopping ct) {
        String sql = "UPDATE ChiTietDonHangTopping SET soLuong=?, giaTopping=?, maCTDH=?, maTopping=? WHERE maID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getGiaTopping());
            ps.setString(3, ct.getMaCTDH());
            ps.setString(4, ct.getMaTopping());
            ps.setString(5, ct.getMaID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangToppingDAOImpl.update: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(String maID) {
        String sql = "DELETE FROM ChiTietDonHangTopping WHERE maID=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangToppingDAOImpl.delete: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ChiTietDonHangTopping findById(String maID) {
        String sql = "SELECT * FROM ChiTietDonHangTopping WHERE maID=?";
        try (PreparedStatement ps = getConnprepareStatement(sql)) {
            ps.setString(1, maID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangToppingDAOImpl.findById: " + e.getMessage());
        }
        return null;
    }

    private PreparedStatement getConnprepareStatement(String sql) throws SQLException {
		return getConn().prepareStatement(sql);
	}

	@Override
    public List<ChiTietDonHangTopping> findAll() {
        List<ChiTietDonHangTopping> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietDonHangTopping ORDER BY maCTDH, maID";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangToppingDAOImpl.findAll: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<ChiTietDonHangTopping> findByCTDH(String maCTDH) {
        List<ChiTietDonHangTopping> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietDonHangTopping WHERE maCTDH=? ORDER BY maID";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, maCTDH);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("ChiTietDonHangToppingDAOImpl.findByCTDH: " + e.getMessage());
        }
        return list;
    }
}
