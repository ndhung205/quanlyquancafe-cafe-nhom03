package ui.dialog;

import controller.ShiftController;
import exception.AppException;
import utils.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Dialog bắt buộc mở ca trước khi vào hệ thống.
 * Modal: chặn cửa sổ cha cho đến khi xử lý xong.
 */
public class ShiftOpenDialog extends JDialog {

    private final ShiftController shiftController;
    private boolean shiftOpened = false;

    public ShiftOpenDialog(JFrame parent, ShiftController shiftController) {
        super(parent, "M\u1EDF Ca L\u00E0m Vi\u1EC7c", true);
        this.shiftController = shiftController;

        setSize(420, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Bấm X = từ chối mở ca
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shiftOpened = false;
                dispose();
            }
        });

        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Icon + Title
        JLabel lblIcon = new JLabel("\uD83D\uDD70", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("M\u1EDF Ca L\u00E0m Vi\u1EC7c", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(39, 174, 96));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        // Info
        String tenNV = SessionManager.getCurrentUser().getTenNV();
        String ngay = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        JLabel lblInfo = new JLabel("Nh\u00E2n vi\u00EAn: " + tenNV + "  |  Ng\u00E0y: " + ngay);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(new Color(120, 120, 120));
        lblInfo.setAlignmentX(CENTER_ALIGNMENT);

        // Khu vực combo
        JLabel lblKhuVuc = new JLabel("Khu v\u1EF1c l\u00E0m vi\u1EC7c");
        lblKhuVuc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblKhuVuc.setForeground(new Color(80, 80, 80));
        lblKhuVuc.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<String> cboKhuVuc = new JComboBox<>();
        java.util.List<entity.KhuVuc> dsKhuVuc = shiftController.getDanhSachKhuVuc();
        java.util.Map<String, String> mapKV = new java.util.LinkedHashMap<>();
        for (entity.KhuVuc kv : dsKhuVuc) {
            cboKhuVuc.addItem(kv.getTenKhuVuc());
            mapKV.put(kv.getTenKhuVuc(), kv.getMaKhuVuc());
        }
        cboKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboKhuVuc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cboKhuVuc.setAlignmentX(LEFT_ALIGNMENT);

        // Money input
        JLabel lblTien = new JLabel("S\u1ED1 ti\u1EC1n \u0111\u1EA7u ca (VN\u0110)");
        lblTien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTien.setForeground(new Color(80, 80, 80));
        lblTien.setAlignmentX(LEFT_ALIGNMENT);

        JTextField txtTien = new JTextField("0");
        txtTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtTien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        txtTien.setHorizontalAlignment(JTextField.CENTER);

        JLabel lblNote = new JLabel("Ki\u1EC3m \u0111\u1EBFm ti\u1EC1n m\u1EB7t trong k\u00E9t tr\u01B0\u1EDBc khi b\u1EAFt \u0111\u1EA7u.");
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNote.setForeground(new Color(160, 160, 160));
        lblNote.setAlignmentX(LEFT_ALIGNMENT);

        // Button
        JButton btnStart = new JButton("B\u1EAET \u0110\u1EA6U CA");
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnStart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnStart.setAlignmentX(CENTER_ALIGNMENT);
        btnStart.setBackground(new Color(39, 174, 96));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusable(false);
        btnStart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnStart.addActionListener(e -> {
            try {
                double tien = Double.parseDouble(txtTien.getText().trim());
                String tenKV = (String) cboKhuVuc.getSelectedItem();
                String maKV = mapKV.get(tenKV);
                shiftController.moCa(tien, maKV);
                shiftOpened = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui l\u00F2ng nh\u1EADp s\u1ED1 h\u1EE3p l\u1EC7!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
            } catch (AppException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "L\u1ED7i", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Layout
        main.add(lblIcon);
        main.add(Box.createVerticalStrut(6));
        main.add(lblTitle);
        main.add(Box.createVerticalStrut(4));
        main.add(lblInfo);
        main.add(Box.createVerticalStrut(20));
        main.add(lblKhuVuc);
        main.add(Box.createVerticalStrut(4));
        main.add(cboKhuVuc);
        main.add(Box.createVerticalStrut(16));
        main.add(lblTien);
        main.add(Box.createVerticalStrut(4));
        main.add(txtTien);
        main.add(Box.createVerticalStrut(4));
        main.add(lblNote);
        main.add(Box.createVerticalStrut(20));
        main.add(btnStart);

        setContentPane(main);
    }

    /**
     * @return true nếu ca đã được mở thành công
     */
    public boolean isShiftOpened() {
        return shiftOpened;
    }
}
