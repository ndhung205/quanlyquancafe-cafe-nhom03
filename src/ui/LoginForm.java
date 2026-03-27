package ui;

import com.formdev.flatlaf.FlatClientProperties;
import controller.AuthController;
import controller.ShiftController;
import entity.CaLamViec;
import entity.NhanVien;
import enums.VaiTro;
import exception.AppException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JFrame {

    private final AuthController authController = new AuthController();
    private final ShiftController shiftController = new ShiftController();

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginForm() {
        setTitle("COFFEE 11:01 - Đăng Nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        // Nền toàn frame: tối navy
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(new Color(26, 26, 46));
        setContentPane(bg);

        // Card trung tâm
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(45, 45, 45, 45));
        card.setPreferredSize(new Dimension(380, 440));


        // Logo
        JLabel lblIcon = new JLabel("\u2615", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("COFFEE 11:01", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(26, 26, 46));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("H\u1EC7 th\u1ED1ng qu\u1EA3n l\u00FD b\u00E1n h\u00E0ng", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(160, 160, 160));
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        // Username field
        JLabel lblUser = new JLabel("T\u00EAn \u0111\u0103ng nh\u1EADp");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(100, 100, 100));
        lblUser.setAlignmentX(LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nh\u1EADp MSNV ho\u1EB7c username");


        // Password field
        JLabel lblPass = new JLabel("M\u1EADt kh\u1EA9u");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(100, 100, 100));
        lblPass.setAlignmentX(LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nh\u1EADp m\u1EADt kh\u1EA9u");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        // Login button
        JButton btnLogin = new JButton("\u0110\u0102NG NH\u1EACP");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusable(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(btnLogin);

        // Ghép card
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(8));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(30));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(28));
        card.add(btnLogin);

        bg.add(card);
    }

    private void handleLogin() {
        try {
            NhanVien nv = authController.login(txtUsername.getText(), new String(txtPassword.getPassword()));

            // Kiểm tra ca hiện tại
            CaLamViec caHienTai = shiftController.kiemTraCaHienTai();

            if (caHienTai != null) {
                // Đã có ca đang mở → vào thẳng MainFrame
                openMainFrame();
            } else if (VaiTro.NHAN_VIEN.equals(nv.getVaiTro())) {
                // NHAN_VIEN bắt buộc mở ca
                boolean daChapNhanMoCa = showShiftOpenDialog();
                if (!daChapNhanMoCa) {
                    authController.logout();
                    return; // Ở lại LoginForm
                }
                openMainFrame();
            } else {
                // QUAN_LY: hỏi có muốn mở ca không
                int choice = JOptionPane.showOptionDialog(this,
                    "B\u1EA1n c\u00F3 mu\u1ED1n m\u1EDF ca l\u00E0m vi\u1EC7c kh\u00F4ng?\n(B\u1ECF qua n\u1EBFu ch\u1EC9 c\u1EA7n xem b\u00E1o c\u00E1o/qu\u1EA3n tr\u1ECB)",
                    "M\u1EDF ca l\u00E0m vi\u1EC7c",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"M\u1EDF Ca", "B\u1ECF Qua"},
                    "B\u1ECF Qua"
                );
                if (choice == 0) {
                    showShiftOpenDialog();
                }
                openMainFrame();
            }

        } catch (AppException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "L\u1ED7i \u0111\u0103ng nh\u1EADp", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "L\u1ED7i k\u1EBFt n\u1ED1i c\u01A1 s\u1EDF d\u1EEF li\u1EC7u!", "L\u1ED7i h\u1EC7 th\u1ED1ng", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hiện dialog mở ca. Trả về true nếu ca được mở thành công.
     */
    private boolean showShiftOpenDialog() {
        ui.dialog.ShiftOpenDialog dlg = new ui.dialog.ShiftOpenDialog(this, shiftController);
        dlg.setVisible(true); // Modal, chặn ở đây
        return dlg.isShiftOpened();
    }

    private void openMainFrame() {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
            this.dispose();
        });
    }

    // ── Entry point ──────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception e) {
            System.err.println("FlatLaf init failed");
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
