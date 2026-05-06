import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class Main {

    // ===================== COLOR PALETTE =====================
    static final Color C_PRIMARY   = new Color(0x18, 0x5F, 0xA5);
    static final Color C_PRIMARY_L = new Color(0xE6, 0xF1, 0xFB);
    static final Color C_SUCCESS   = new Color(0x3B, 0x6D, 0x11);
    static final Color C_BG        = Color.WHITE;
    static final Color C_BG2       = new Color(0xF1, 0xEF, 0xE8);
    static final Color C_TEXT      = new Color(0x2C, 0x2C, 0x2A);
    static final Color C_TEXT2     = new Color(0x5F, 0x5E, 0x5A);
    static final Color C_BORDER    = new Color(0xD3, 0xD1, 0xC7);

    static final Font F_HEADING = new Font("SansSerif", Font.BOLD, 18);
    static final Font F_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    static final Font F_BOLD    = new Font("SansSerif", Font.BOLD, 13);
    static final Font F_TABLE   = new Font("SansSerif", Font.PLAIN, 12);
    static final Font F_TH      = new Font("SansSerif", Font.BOLD, 11);

    // ===================== STATE =====================
    static MainFrame mainFrame;

    // ===================== MODELS =====================
    static class Room {
        final String id, name, campus, type, facilities;
        final int capacity;

        Room(String id, String name, String campus, String type, int cap, String fac) {
            this.id = id; this.name = name; this.campus = campus;
            this.type = type; this.capacity = cap; this.facilities = fac;
        }

        @Override
        public String toString() {
            return name + "  (" + campus + " · " + capacity + " chỗ)";
        }
    }

    enum Status { PENDING, APPROVED, REJECTED }

    static class Booking {
        String id, hoTen, email, dienThoai, donVi;
        String noiDung, loaiPhong, roomId;
        String ngaySuDung, caSuDung, lyDo;
        Status status;
        LocalDate submittedAt;

        Booking(String id, String hoTen, String email, String dienThoai, String donVi,
                String noiDung, String loaiPhong, String roomId,
                String ngaySuDung, String caSuDung, String lyDo, Status status) {
            this.id = id; this.hoTen = hoTen; this.email = email;
            this.dienThoai = dienThoai; this.donVi = donVi;
            this.noiDung = noiDung; this.loaiPhong = loaiPhong;
            this.roomId = roomId; this.ngaySuDung = ngaySuDung;
            this.caSuDung = caSuDung; this.lyDo = lyDo;
            this.status = status;
            this.submittedAt = LocalDate.now();
        }
    }

    // ===================== DATA STORE =====================
    static final List<Room> ROOMS = new ArrayList<>();
    static final List<Booking> BOOKINGS = new ArrayList<>();

    static final String[] CAS = {
        "Sáng (07:00 - 11:30)",
        "Chiều (12:30 - 17:00)",
        "Tối (17:30 - 21:00)"
    };

    static {
        initData();
    }

    static void initData() {
        ROOMS.add(new Room("A101", "A.101", "Cơ sở A – Nguyễn Đình Chiểu", "Giảng đường",  60, "Máy chiếu, bảng trắng, micro"));
        ROOMS.add(new Room("A201", "A.201", "Cơ sở A – Nguyễn Đình Chiểu", "Giảng đường",  80, "Máy chiếu, âm thanh, điều hòa"));
        ROOMS.add(new Room("B101", "B.101", "Cơ sở A – Nguyễn Đình Chiểu", "Giảng đường",  40, "Máy chiếu, bảng phấn"));
        ROOMS.add(new Room("C201", "C.201", "Cơ sở B – Hoàng Diệu 2",       "Phòng máy tính", 30, "30 máy tính, máy chiếu, internet"));
        ROOMS.add(new Room("C202", "C.202", "Cơ sở B – Hoàng Diệu 2",       "Phòng máy tính", 30, "30 máy tính, máy chiếu, internet"));
        ROOMS.add(new Room("D301", "D.301", "Cơ sở B – Hoàng Diệu 2",       "Giảng đường", 100, "Máy chiếu HD, âm thanh, điều hòa, camera"));

        DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE;
        BOOKINGS.add(new Booking("DK-2025-001", "Nguyễn Văn An", "an.nv@ueh.edu.vn", "0901234567",
            "Khoa Kinh tế", "ET4430E – Lập trình nâng cao", "Phòng máy tính", "C201",
            LocalDate.now().plusDays(5).format(f), CAS[0], "Học bù do giảng viên nghỉ phép", Status.PENDING));

        BOOKINGS.add(new Booking("DK-2025-002", "Trần Thị Bình", "binh.tt@ueh.edu.vn", "0912345678",
            "Khoa Tài chính", "FIN301 – Tài chính doanh nghiệp", "Giảng đường", "A201",
            LocalDate.now().plusDays(4).format(f), CAS[1], "Ôn thi cuối kỳ", Status.PENDING));

        BOOKINGS.add(new Booking("DK-2025-003", "Lê Minh Châu", "chau.lm@ueh.edu.vn", "0923456789",
            "Hội Sinh viên", "Hội thảo kỹ năng mềm", "Giảng đường", "D301",
            LocalDate.now().plusDays(3).format(f), CAS[2], "Sự kiện định kỳ HSV", Status.PENDING));
    }

    static Room findRoom(String id) {
        return ROOMS.stream().filter(r -> r.id.equals(id)).findFirst().orElse(null);
    }

    static String nextId() {
        return String.format("DK-2025-%03d", BOOKINGS.size() + 1);
    }

    // ===================== UI HELPERS =====================
    static JButton primaryBtn(String t) {
        JButton b = new JButton(t);
        b.setBackground(C_PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFont(F_BODY);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_HEADING);
        l.setForeground(C_TEXT);
        return l;
    }

    static JTable buildTable(String[] cols, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setFont(F_TABLE);
        t.setRowHeight(30);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setBackground(C_BG);
        t.setSelectionBackground(C_PRIMARY_L);
        t.setSelectionForeground(C_TEXT);
        JTableHeader hdr = t.getTableHeader();
        hdr.setFont(F_TH);
        hdr.setBackground(C_BG2);
        hdr.setForeground(C_TEXT2);
        hdr.setReorderingAllowed(false);
        return t;
    }

    static JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER));
        sp.getViewport().setBackground(C_BG);
        return sp;
    }

    // ===================== MAIN FRAME - Send Form & Display List =====================
    static class MainFrame extends JFrame {
        JLabel statusLbl;
        javax.swing.Timer statusTimer;
        JPanel tablePanel;

        MainFrame() {
            setTitle("UEH – Quản lý Phòng học");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1200, 800);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(C_BG);
            root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            // Title
            JLabel title = heading("Đăng ký & Quản lý Phòng học");
            root.add(title, BorderLayout.NORTH);

            // Main content with form and table
            JPanel content = new JPanel(new BorderLayout(0, 16));
            content.setOpaque(false);

            // FORM SECTION
            content.add(buildFormPanel(), BorderLayout.NORTH);

            // TABLE SECTION
            tablePanel = new JPanel(new BorderLayout());
            tablePanel.setOpaque(false);
            content.add(tablePanel, BorderLayout.CENTER);
            refreshTable();

            root.add(content, BorderLayout.CENTER);

            // Status bar
            statusLbl = new JLabel(" ");
            statusLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
            statusLbl.setOpaque(true);
            statusLbl.setBackground(C_BG2);
            statusLbl.setForeground(C_TEXT2);
            statusLbl.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            root.add(statusLbl, BorderLayout.SOUTH);

            add(root);
        }

        JPanel buildFormPanel() {
            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBackground(C_BG);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

            JLabel formTitle = new JLabel("1. Gửi Biểu mẫu Đăng ký Phòng");
            formTitle.setFont(F_BOLD);
            formTitle.setForeground(C_TEXT);

            // Form fields
            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.HORIZONTAL;
            g.insets = new Insets(6, 6, 6, 10);

            JTextField fHoTen   = new JTextField("Nguyễn Văn A", 15);
            JTextField fEmail   = new JTextField("hoten@ueh.edu.vn", 15);
            JTextField fDt      = new JTextField("09xxxxxxxx", 15);
            JTextField fDonVi   = new JTextField("Khoa / Viện...", 15);
            JTextField fNgay    = new JTextField(LocalDate.now().plusDays(4).toString(), 15);
            JTextField fNd      = new JTextField("VD: ET4430E – Lập trình nâng cao", 25);
            JComboBox<String> fCa = new JComboBox<>(CAS);
            JComboBox<String> fLoai = new JComboBox<>(new String[]{"Giảng đường", "Phòng máy tính"});
            JComboBox<Room> fRoom = new JComboBox<>();
            JTextArea  fLyDo    = new JTextArea(2, 40);
            fLyDo.setLineWrap(true);
            fLyDo.setWrapStyleWord(true);

            updateRoomList(fLoai, fRoom);
            fLoai.addActionListener(e -> updateRoomList(fLoai, fRoom));

            // Layout form
            int row = 0;
            g.gridy = row++;
            g.gridx = 0; form.add(new JLabel("Họ và tên *"), g);
            g.gridx = 1; form.add(fHoTen, g);
            g.gridx = 2; form.add(new JLabel("Email @ueh.edu.vn *"), g);
            g.gridx = 3; form.add(fEmail, g);

            g.gridy = row++;
            g.gridx = 0; form.add(new JLabel("Điện thoại *"), g);
            g.gridx = 1; form.add(fDt, g);
            g.gridx = 2; form.add(new JLabel("Đơn vị *"), g);
            g.gridx = 3; form.add(fDonVi, g);

            g.gridy = row++;
            g.gridx = 0; form.add(new JLabel("Ngày sử dụng *"), g);
            g.gridx = 1; form.add(fNgay, g);
            g.gridx = 2; form.add(new JLabel("Ca sử dụng *"), g);
            g.gridx = 3; form.add(fCa, g);

            g.gridy = row++;
            g.gridx = 0; form.add(new JLabel("Loại phòng *"), g);
            g.gridx = 1; form.add(fLoai, g);
            g.gridx = 2; form.add(new JLabel("Phòng *"), g);
            g.gridx = 3; form.add(fRoom, g);

            g.gridy = row++;
            g.gridx = 0; form.add(new JLabel("Nội dung lớp học *"), g);
            g.gridx = 1; g.gridwidth = 3; form.add(fNd, g); g.gridwidth = 1;

            g.gridy = row++;
            g.gridx = 0; form.add(new JLabel("Lý do mượn *"), g);
            g.gridx = 1; g.gridwidth = 3; form.add(new JScrollPane(fLyDo), g); g.gridwidth = 1;

            // Submit button
            JButton submitBtn = primaryBtn("Gửi Đăng ký");
            g.gridy = row++;
            g.gridx = 0; g.gridwidth = 4;
            form.add(submitBtn, g);

            submitBtn.addActionListener(e -> handleSubmitForm(fHoTen, fEmail, fDt, fDonVi, fNgay, fNd, fLyDo, fCa, fRoom, fLoai));

            panel.add(formTitle, BorderLayout.NORTH);
            panel.add(form, BorderLayout.CENTER);
            return panel;
        }

        void updateRoomList(JComboBox<String> fLoai, JComboBox<Room> fRoom) {
            fRoom.removeAllItems();
            String sel = (String) fLoai.getSelectedItem();
            ROOMS.stream().filter(r -> r.type.equals(sel)).forEach(fRoom::addItem);
        }

        void handleSubmitForm(JTextField fHoTen, JTextField fEmail, JTextField fDt, JTextField fDonVi,
                             JTextField fNgay, JTextField fNd, JTextArea fLyDo,
                             JComboBox<String> fCa, JComboBox<Room> fRoom, JComboBox<String> fLoai) {
            String ht = fHoTen.getText().trim();
            String em = fEmail.getText().trim();
            String dt = fDt.getText().trim();
            String dv = fDonVi.getText().trim();
            String ng = fNgay.getText().trim();
            String nd = fNd.getText().trim();
            String ly = fLyDo.getText().trim();
            String ca = (String) fCa.getSelectedItem();
            Room   rm = (Room) fRoom.getSelectedItem();

            // Validate
            if (ht.isEmpty() || em.isEmpty() || dt.isEmpty() || dv.isEmpty()
                    || ng.isEmpty() || nd.isEmpty() || ly.isEmpty() || rm == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ tất cả các trường bắt buộc (*)", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!em.endsWith("@ueh.edu.vn")) {
                JOptionPane.showMessageDialog(this, "Email phải có định dạng @ueh.edu.vn", "Email không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                LocalDate date = LocalDate.parse(ng);
                long diff = ChronoUnit.DAYS.between(LocalDate.now(), date);
                if (diff < 3) {
                    JOptionPane.showMessageDialog(this, "Yêu cầu đăng ký trước ít nhất 3 ngày làm việc.\nNgày yêu cầu: " + ng + " (còn " + diff + " ngày)", "Không đủ thời hạn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng nhập: yyyy-MM-dd", "Lỗi định dạng ngày", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create and add booking
            String newId = nextId();
            String loaiS = (String) fLoai.getSelectedItem();
            Booking b = new Booking(newId, ht, em, dt, dv, nd, loaiS, rm.id, ng, ca, ly, Status.PENDING);
            BOOKINGS.add(b);

            showStatusMessage("✓ Đơn " + newId + " đã gửi thành công! Email xác nhận gửi tới " + em, C_SUCCESS);
            JOptionPane.showMessageDialog(this,
                "Đơn đăng ký đã gửi thành công!\nMã đơn: " + newId + "\nPhòng: " + rm.name + " · " + ng + " · " + ca.split(" ")[0],
                "Đăng ký thành công", JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            fHoTen.setText("Nguyễn Văn A");
            fEmail.setText("hoten@ueh.edu.vn");
            fDt.setText("09xxxxxxxx");
            fDonVi.setText("Khoa / Viện...");
            fNgay.setText(LocalDate.now().plusDays(4).toString());
            fNd.setText("VD: ET4430E – Lập trình nâng cao");
            fLyDo.setText("");
            fCa.setSelectedIndex(0);
            fLoai.setSelectedIndex(0);

            refreshTable();
        }

        void refreshTable() {
            tablePanel.removeAll();

            JLabel tableTitle = new JLabel("2. Danh sách Biểu mẫu Đã Nhận");
            tableTitle.setFont(F_BOLD);
            tableTitle.setForeground(C_TEXT);
            tableTitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

            String[] cols = {"Mã đơn", "Người đăng ký", "Email", "Phòng", "Ngày", "Ca", "Nội dung", "Ngày gửi", "Trạng thái"};
            Object[][] data = BOOKINGS.stream().map(b -> {
                Room r = findRoom(b.roomId);
                return new Object[]{
                    b.id, b.hoTen, b.email,
                    r != null ? r.name : b.roomId,
                    b.ngaySuDung,
                    b.caSuDung.split(" ")[0],
                    b.noiDung,
                    b.submittedAt,
                    b.status.toString()
                };
            }).toArray(Object[][]::new);

            JTable table = buildTable(cols, data);
            tablePanel.add(tableTitle, BorderLayout.NORTH);
            tablePanel.add(styledScroll(table), BorderLayout.CENTER);
            tablePanel.revalidate();
            tablePanel.repaint();
        }

        void showStatusMessage(String msg, Color color) {
            statusLbl.setText(msg);
            statusLbl.setForeground(darker(color));
            if (statusTimer != null) statusTimer.stop();
            statusTimer = new javax.swing.Timer(5000, e -> {
                statusLbl.setText(" ");
                statusLbl.setForeground(C_TEXT2);
            });
            statusTimer.setRepeats(false);
            statusTimer.start();
        }

        static Color darker(Color c) {
            return new Color(Math.max(0, c.getRed() - 60), Math.max(0, c.getGreen() - 60), Math.max(0, c.getBlue() - 60));
        }
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
