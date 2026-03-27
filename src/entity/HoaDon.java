package entity;

import enums.HinhThucThanhToan;
import enums.TrangThaiHoaDon;
import java.time.LocalDateTime;

public class HoaDon {
    private String            maHD;
    private LocalDateTime     thoiGianXuat;      // thời điểm tạo hóa đơn
    private LocalDateTime     thoiGianThanhToan;  // nullable - khi khách TT xong
    private double            tongTienPhaiTra;
    private TrangThaiHoaDon   trangThai;
    private HinhThucThanhToan hinhThucThanhToan;  // nullable
    private String            maDonHang;          // FK DonHang (1-1)
    private String            maNV;               // FK NhanVien (thu ngân thực hiện TT)

    public HoaDon() {}

    public HoaDon(String maHD, LocalDateTime thoiGianXuat,
                  LocalDateTime thoiGianThanhToan, double tongTienPhaiTra,
                  TrangThaiHoaDon trangThai, HinhThucThanhToan hinhThucThanhToan,
                  String maDonHang, String maNV) {
        this.maHD               = maHD;
        this.thoiGianXuat       = thoiGianXuat;
        this.thoiGianThanhToan  = thoiGianThanhToan;
        this.tongTienPhaiTra    = tongTienPhaiTra;
        this.trangThai          = trangThai;
        this.hinhThucThanhToan  = hinhThucThanhToan;
        this.maDonHang          = maDonHang;
        this.maNV               = maNV;
    }

    public String getMaHD()          { return maHD; }
    public void   setMaHD(String v)  { this.maHD = v; }

    public LocalDateTime getThoiGianXuat()               { return thoiGianXuat; }
    public void          setThoiGianXuat(LocalDateTime v){ this.thoiGianXuat = v; }

    public LocalDateTime getThoiGianThanhToan()               { return thoiGianThanhToan; }
    public void          setThoiGianThanhToan(LocalDateTime v){ this.thoiGianThanhToan = v; }

    public double getTongTienPhaiTra()          { return tongTienPhaiTra; }
    public void   setTongTienPhaiTra(double v)  { this.tongTienPhaiTra = v; }

    public TrangThaiHoaDon getTrangThai()               { return trangThai; }
    public void            setTrangThai(TrangThaiHoaDon v){ this.trangThai = v; }

    public HinhThucThanhToan getHinhThucThanhToan()               { return hinhThucThanhToan; }
    public void              setHinhThucThanhToan(HinhThucThanhToan v){ this.hinhThucThanhToan = v; }

    public String getMaDonHang()          { return maDonHang; }
    public void   setMaDonHang(String v)  { this.maDonHang = v; }

    public String getMaNV()          { return maNV; }
    public void   setMaNV(String v)  { this.maNV = v; }

    public boolean isDaThanhToan() {
        return TrangThaiHoaDon.DA_THANH_TOAN.equals(trangThai);
    }

    @Override
    public String toString() {
        return "HoaDon{" + maHD + ", don=" + maDonHang
                + ", tien=" + tongTienPhaiTra
                + ", " + trangThai + ", " + hinhThucThanhToan + "}";
    }
}
