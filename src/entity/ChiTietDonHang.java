package entity;

public class ChiTietDonHang {
    private String maCTDH;
    private int    soLuong;
    private double donGia;     // giá tại thời điểm order (snapshot)
    private double thanhTien;  // soLuong * donGia
    private String ghiChu;     // ghi chú pha chế: đường, đá,...
    private String maDonHang;  // FK DonHang
    private String maMon;      // FK Mon
    private String maSize;     // FK Size

    public ChiTietDonHang() {}

    public ChiTietDonHang(String maCTDH, int soLuong, double donGia,
                           double thanhTien, String ghiChu,
                           String maDonHang, String maMon, String maSize) {
        this.maCTDH    = maCTDH;
        this.soLuong   = soLuong;
        this.donGia    = donGia;
        this.thanhTien = thanhTien;
        this.ghiChu    = ghiChu;
        this.maDonHang = maDonHang;
        this.maMon     = maMon;
        this.maSize    = maSize;
    }

    public String getMaCTDH()          { return maCTDH; }
    public void   setMaCTDH(String v)  { this.maCTDH = v; }

    public int  getSoLuong()       { return soLuong; }
    public void setSoLuong(int v)  { this.soLuong = v; }

    public double getDonGia()          { return donGia; }
    public void   setDonGia(double v)  { this.donGia = v; }

    public double getThanhTien()          { return thanhTien; }
    public void   setThanhTien(double v)  { this.thanhTien = v; }

    public String getGhiChu()          { return ghiChu; }
    public void   setGhiChu(String v)  { this.ghiChu = v; }

    public String getMaDonHang()          { return maDonHang; }
    public void   setMaDonHang(String v)  { this.maDonHang = v; }

    public String getMaMon()          { return maMon; }
    public void   setMaMon(String v)  { this.maMon = v; }

    public String getMaSize()          { return maSize; }
    public void   setMaSize(String v)  { this.maSize = v; }

    /** Tính lại thanhTien (chưa tính topping, topping tính riêng) */
    public void tinhThanhTien() {
        this.thanhTien = this.soLuong * this.donGia;
    }

    @Override
    public String toString() {
        return "ChiTietDonHang{" + maCTDH + ", mon=" + maMon
                + ", size=" + maSize + ", sl=" + soLuong
                + ", tien=" + thanhTien + "}";
    }
}
