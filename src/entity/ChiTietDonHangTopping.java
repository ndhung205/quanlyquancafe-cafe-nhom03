package entity;

public class ChiTietDonHangTopping {
    private String maID;
    private int    soLuong;
    private double giaTopping; // giá snapshot tại thời điểm order
    private String maCTDH;     // FK ChiTietDonHang
    private String maTopping;  // FK Topping

    public ChiTietDonHangTopping() {}

    public ChiTietDonHangTopping(String maID, int soLuong, double giaTopping,
                                  String maCTDH, String maTopping) {
        this.maID       = maID;
        this.soLuong    = soLuong;
        this.giaTopping = giaTopping;
        this.maCTDH     = maCTDH;
        this.maTopping  = maTopping;
    }

    public String getMaID()          { return maID; }
    public void   setMaID(String v)  { this.maID = v; }

    public int  getSoLuong()       { return soLuong; }
    public void setSoLuong(int v)  { this.soLuong = v; }

    public double getGiaTopping()          { return giaTopping; }
    public void   setGiaTopping(double v)  { this.giaTopping = v; }

    public String getMaCTDH()          { return maCTDH; }
    public void   setMaCTDH(String v)  { this.maCTDH = v; }

    public String getMaTopping()          { return maTopping; }
    public void   setMaTopping(String v)  { this.maTopping = v; }

    /** Tổng tiền topping của dòng này */
    public double getTongGiaTopping() {
        return soLuong * giaTopping;
    }

    @Override
    public String toString() {
        return "ChiTietDonHangTopping{" + maID + ", topping=" + maTopping
                + ", sl=" + soLuong + ", gia=" + giaTopping + "}";
    }
}
