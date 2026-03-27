package dao.impl;

import connectDB.DatabaseConnection;
import dao.StatisticDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticDAOImpl implements StatisticDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Map<String, Double> getDoanhThu7NgayQua() {
        // LinkedHashMap giữ thứ tự chèn. Mình muốn ngày cũ nhất đến ngày mới nhất.
        Map<String, Double> result = new LinkedHashMap<>();
        
        // SQL Server query: nhóm theo ngày của 7 ngày gần nhất, sx tăng dần
        // Dùng FORMAT() hoặc CONVERT để format ngày dd/MM. 
        // Lấy hóa đơn Đã thanh toán
        String sql = """
            WITH CTE_Dates AS (
                SELECT CAST(GETDATE() - 6 AS DATE) AS d
                UNION ALL
                SELECT DATEADD(day, 1, d) FROM CTE_Dates WHERE d < CAST(GETDATE() AS DATE)
            )
            SELECT FORMAT(CTE_Dates.d, 'dd/MM') as Ngay, ISNULL(SUM(hd.tongTienPhaiTra), 0) as DoanhThu
            FROM CTE_Dates
            LEFT JOIN HoaDon hd ON CAST(hd.thoiGianThanhToan AS DATE) = CTE_Dates.d AND hd.trangThai = 'DA_THANH_TOAN'
            GROUP BY CTE_Dates.d
            ORDER BY CTE_Dates.d ASC
        """;
        
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                result.put(rs.getString("Ngay"), rs.getDouble("DoanhThu"));
            }
        } catch (SQLException e) {
            System.err.println("StatisticDAO.getDoanhThu7NgayQua: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Integer> getTopMonBanChay(int top) {
        Map<String, Integer> result = new LinkedHashMap<>();
        
        String sql = """
            SELECT TOP (?) m.tenMon, SUM(ct.soLuong) as TongSL
            FROM ChiTietDonHang ct
            JOIN DonHang dh ON ct.maDonHang = dh.maDonHang
            JOIN Mon m ON ct.maMon = m.maMon
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY m.tenMon
            ORDER BY TongSL DESC
        """;
        
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, top);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("tenMon"), rs.getInt("TongSL"));
                }
            }
        } catch (SQLException e) {
            System.err.println("StatisticDAO.getTopMonBanChay: " + e.getMessage());
        }
        
        return result;
    }
}
