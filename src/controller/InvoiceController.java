package controller;

import dao.HoaDonDAO;
import dao.impl.HoaDonDAOImpl;
import entity.HoaDon;
import java.util.List;

public class InvoiceController {

    private final HoaDonDAO hoaDonDAO;

    public InvoiceController() {
        this.hoaDonDAO = new HoaDonDAOImpl();
    }

    public List<HoaDon> getAllHoaDon() {
        return hoaDonDAO.findAll(); // Mặc định DAO sắp xếp theo thời gian xuất giảm dần
    }
}
