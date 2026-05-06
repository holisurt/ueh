package ueh;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.*;

/**
 * Hệ thống Quản lý Phòng học UEH
 * Quy trình KHĐTKT.QT.13 - Phòng Kế hoạch Đào tạo - Khảo thí
 * ET4430E 2025.2 | Nhóm: Phạm Huy Hoàng, Bùi Thái Sơn, Lê Duy Đức, Hà Duyên Hùng
 */
public class Main {

    // ===================== COLOR PALETTE =====================
    static final Color C_PRIMARY   = new Color(0x18, 0x5F, 0xA5);
    static final Color C_PRIMARY_D = new Color(0x0C, 0x44, 0x7C);
    static final Color C_PRIMARY_L = new Color(0xE6, 0xF1, 0xFB);
    static final Color C_SUCCESS   = new Color(0x3B, 0x6D, 0x11);
    static final Color C_SUCCESS_L = new Color(0xEA, 0xF3, 0xDE);
    static final Color C_DANGER    = new Color(0xA3, 0x2D, 0x2D);
    static final Color C_DANGER_L  = new Color(0xFC, 0xEB, 0xEB);
    static final Color C_WARN      = new Color(0x85, 0x4F, 0x0B);
    static final Color C_WARN_L    = new Color(0xFA, 0xEE, 0xDA);
    static final Color C_BG        = Color.WHITE;
    static final Color C_BG2       = new Color(0xF1, 0xEF, 0xE8);
    static final Color C_TEXT      = new Color(0x2C, 0x2C, 0x2A);
    static final Color C_TEXT2     = new Color(0x5F, 0x5E, 0x5A);
    static final Color C_BORDER    = new Color(0xD3, 0xD1, 0xC7);

    static final Font F_HEADING = new Font("SansSerif", Font.BOLD, 18);
    static final Font F_SUB     = new Font("SansSerif", Font.PLAIN, 12);
    static final Font F_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    static final Font F_BOLD    = new Font("SansSerif", Font.BOLD, 13);
    static final Font F_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    static final Font F_BIG     = new Font("SansSerif", Font.BOLD, 28);
    static final Font F_TABLE   = new Font("SansSerif", Font.PLAIN, 12);
    static final Font F_TH      = new Font("SansSerif", Font.BOLD, 11);

    // ===================== STATE =====================
    static String currentRole = null; // "user", "admin", "csvc"
    static AppFrame appFrame;

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
        String rejectReason, processedBy;
        Status status;
        LocalDate submittedAt, processedAt;

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
        ROOMS.add(new Room("C201", "C.201", "Cơ sở B – Hoàng Diệu 2",       "Phòng máy tính", 30, "30 máy tính, máy chiếu, internet tốc độ cao"));
        ROOMS.add(new Room("C202", "C.202", "Cơ sở B – Hoàng Diệu 2",       "Phòng máy tính", 30, "30 máy tính, máy chiếu, internet tốc độ cao"));
        ROOMS.add(new Room("D301", "D.301", "Cơ sở B – Hoàng Diệu 2",       "Giảng đường", 100, "Máy chiếu HD, âm thanh, điều hòa, camera"));

        DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE;
        String d1 = LocalDate.now().plusDays(5).format(f);
        String d2 = LocalDate.now().plusDays(4).format(f);
        String d3 = LocalDate.now().plusDays(3).format(f);
        String d4 = LocalDate.now().plusDays(6).format(f);
        String d5 = LocalDate.now().plusDays(7).format(f);

        Booking b1 = new Booking("DK-2025-001", "Nguyễn Văn An", "an.nv@ueh.edu.vn", "0901234567",
            "Khoa Kinh tế", "ET4430E – Lập trình nâng cao", "Phòng máy tính", "C201",
            d1, CAS[0], "Học bù do giảng viên nghỉ phép", Status.PENDING);

        Booking b2 = new Booking("DK-2025-002", "Trần Thị Bình", "binh.tt@ueh.edu.vn", "0912345678",
            "Khoa Tài chính", "FIN301 – Tài chính doanh nghiệp", "Giảng đường", "A201",
            d2, CAS[1], "Ôn thi cuối kỳ", Status.APPROVED);
        b2.processedBy = "ThS. Trương Hồng Khánh"; b2.processedAt = LocalDate.now().minusDays(1);

        Booking b3 = new Booking("DK-2025-003", "Lê Minh Châu", "chau.lm@ueh.edu.vn", "0923456789",
            "Hội Sinh viên", "Hội thảo kỹ năng mềm", "Giảng đường", "D301",
            d3, CAS[2], "Sự kiện định kỳ HSV", Status.REJECTED);
        b3.rejectReason = "Phòng đã được đặt trước bởi đơn vị khác trong khung giờ này.";
        b3.processedBy = "ThS. Trương Hồng Khánh"; b3.processedAt = LocalDate.now().minusDays(1);

        Booking b4 = new Booking("DK-2025-004", "Phạm Huy Hoàng", "hoang.ph@ueh.edu.vn", "0934567890",
            "Khoa HTTT", "IS501 – Phân tích & Thiết kế hệ thống", "Giảng đường", "A101",
            d4, CAS[0], "Học bù theo lịch điều chỉnh của bộ môn", Status.PENDING);

        Booking b5 = new Booking("DK-2025-005", "Bùi Thái Sơn", "son.bt@ueh.edu.vn", "0945678901",
            "Khoa HTTT", "CS302 – Cơ sở dữ liệu", "Phòng máy tính", "C202",
            d5, CAS[1], "Thực hành lab theo lịch học phần", Status.APPROVED);
        b5.processedBy = "ThS. Trương Hồng Khánh"; b5.processedAt = LocalDate.now().minusDays(2);

        BOOKINGS.add(b1); BOOKINGS.add(b2); BOOKINGS.add(b3); BOOKINGS.add(b4); BOOKINGS.add(b5);
    }

    static Room findRoom(String id) {
        return ROOMS.stream().filter(r -> r.id.equals(id)).findFirst().orElse(null);
    }

    static String nextId() {
        return String.format("DK-2025-%03d", BOOKINGS.size() + 1);
    }

    // ===================== UI HELPERS =====================
    static JButton btn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(F_BODY); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true); b.setBorderPainted(false);
        return b;
    }

    static JButton primaryBtn(String t) { return btn(t, C_PRIMARY, Color.WHITE); }
    static JButton successBtn(String t) { return btn(t, C_SUCCESS, Color.WHITE); }
    static JButton dangerBtn(String t)  { return btn(t, C_DANGER,  Color.WHITE); }
    static JButton neutralBtn(String t) { return btn(t, C_BG2, C_TEXT); }

    static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_HEADING); l.setForeground(C_TEXT);
        return l;
    }

    static JLabel subLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_SUB); l.setForeground(C_TEXT2);
        return l;
    }

    static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(" " + text + " ");
        l.setFont(F_SMALL); l.setOpaque(true);
        l.setBackground(bg); l.setForeground(fg);
        l.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return l;
    }

    static JLabel statusBadge(Status s) {
        return switch (s) {
            case PENDING  -> badge("Chờ duyệt", C_WARN_L, C_WARN);
            case APPROVED -> badge("Đã duyệt",  C_SUCCESS_L, C_SUCCESS);
            case REJECTED -> badge("Từ chối",   C_DANGER_L, C_DANGER);
        };
    }

    static String statusText(Status s) {
        return switch (s) {
            case PENDING  -> "Chờ duyệt";
            case APPROVED -> "Đã duyệt";
            case REJECTED -> "Từ chối";
        };
    }

    static JPanel hLine() {
        JPanel p = new JPanel();
        p.setBackground(C_BORDER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.setPreferredSize(new Dimension(1, 1));
        return p;
    }

    static JPanel statCard(String label, String value, Color valueColor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_BG2);
        p.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel vl = new JLabel(value);
        vl.setFont(F_BIG); vl.setForeground(valueColor);
        JLabel ll = subLbl(label);
        p.add(ll);
        p.add(Box.createVerticalStrut(4));
        p.add(vl);
        return p;
    }

    static JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER));
        sp.getViewport().setBackground(C_BG);
        return sp;
    }

    static JTable buildTable(String[] cols, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setFont(F_TABLE); t.setRowHeight(30);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setBackground(C_BG);
        t.setSelectionBackground(C_PRIMARY_L);
        t.setSelectionForeground(C_TEXT);
        t.setGridColor(C_BG2);
        JTableHeader hdr = t.getTableHeader();
        hdr.setFont(F_TH);
        hdr.setBackground(C_BG2);
        hdr.setForeground(C_TEXT2);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        hdr.setReorderingAllowed(false);
        return t;
    }

    static void setStatusCellRenderer(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean f, int r, int c) {
                super.getTableCellRendererComponent(tbl, v, sel, f, r, c);
                String s = v != null ? v.toString() : "";
                if (s.contains("Chờ"))   { setForeground(C_WARN);    setFont(F_BOLD); }
                else if (s.contains("duyệt")) { setForeground(C_SUCCESS); setFont(F_BOLD); }
                else if (s.contains("chối")) { setForeground(C_DANGER);  setFont(F_BOLD); }
                else                      { setForeground(C_TEXT);    setFont(F_TABLE); }
                setBackground(sel ? C_PRIMARY_L : C_BG);
                return this;
            }
        });
    }

    static void showStatus(String msg, Color color) {
        if (appFrame != null) appFrame.showStatusMessage(msg, color);
    }

    // ===================== APP FRAME =====================
    static class AppFrame extends JFrame {
        JPanel contentArea;
        CardLayout cardLayout;
        JLabel statusLbl;
        javax.swing.Timer statusTimer;

        AppFrame() {
            setTitle("UEH – Quản lý Phòng học (KHĐTKT.QT.13)");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1150, 720);
            setMinimumSize(new Dimension(900, 600));
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(C_BG);

            root.add(buildTopBar(), BorderLayout.NORTH);

            JPanel body = new JPanel(new BorderLayout());
            body.setBackground(C_BG);
            body.add(buildNavBar(), BorderLayout.NORTH);

            cardLayout = new CardLayout();
            contentArea = new JPanel(cardLayout);
            contentArea.setBackground(C_BG);
            buildPanels();

            JScrollPane scrollPane = new JScrollPane(contentArea);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(C_BG);
            body.add(scrollPane, BorderLayout.CENTER);

            statusLbl = new JLabel(" ");
            statusLbl.setFont(F_SMALL);
            statusLbl.setOpaque(true);
            statusLbl.setBackground(C_BG2);
            statusLbl.setForeground(C_TEXT2);
            statusLbl.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
            body.add(statusLbl, BorderLayout.SOUTH);

            root.add(body, BorderLayout.CENTER);
            add(root);
        }

        JPanel buildTopBar() {
            JPanel bar = new JPanel(new BorderLayout());
            bar.setBackground(C_PRIMARY);
            bar.setPreferredSize(new Dimension(0, 48));
            bar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

            JLabel logo = new JLabel("UEH  |  Hệ thống Quản lý Phòng học");
            logo.setFont(new Font("SansSerif", Font.BOLD, 14));
            logo.setForeground(Color.WHITE);

            String roleStr = switch (currentRole) {
                case "user"  -> "Giảng viên / Sinh viên";
                case "admin" -> "P. Kế hoạch Đào tạo – Khảo thí";
                case "csvc"  -> "P. Cơ sở Vật chất";
                default      -> "";
            };
            JLabel roleLabel = new JLabel(roleStr);
            roleLabel.setFont(F_SUB);
            roleLabel.setForeground(new Color(0xB5, 0xD4, 0xF4));

            JButton logoutBtn = btn("Đăng xuất", new Color(0x0C, 0x44, 0x7C), new Color(0xB5, 0xD4, 0xF4));
            logoutBtn.addActionListener(e -> {
                currentRole = null;
                dispose();
                appFrame = null;
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            });

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            right.setOpaque(false);
            right.add(roleLabel);
            right.add(logoutBtn);

            bar.add(logo, BorderLayout.WEST);
            bar.add(right, BorderLayout.EAST);
            return bar;
        }

        JPanel buildNavBar() {
            JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            nav.setBackground(C_BG);
            nav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

            List<String[]> items = new ArrayList<>();
            if ("user".equals(currentRole)) {
                items.add(new String[]{"dashboard", "Tổng quan"});
                items.add(new String[]{"search",    "Tìm phòng trống"});
                items.add(new String[]{"book",      "Đăng ký mượn phòng"});
                items.add(new String[]{"mybookings","Đơn của tôi"});
            } else if ("admin".equals(currentRole)) {
                long pCnt = BOOKINGS.stream().filter(b -> b.status == Status.PENDING).count();
                items.add(new String[]{"dashboard",   "Tổng quan"});
                items.add(new String[]{"pending",     "Chờ duyệt (" + pCnt + ")"});
                items.add(new String[]{"allbookings", "Tất cả đơn"});
                items.add(new String[]{"report",      "Báo cáo & Thống kê"});
            } else {
                items.add(new String[]{"schedule",     "Lịch theo ca"});
                items.add(new String[]{"weekschedule", "Lịch tuần"});
            }

            for (String[] item : items) {
                String key = item[0], label = item[1];
                JButton b = new JButton(label);
                b.setFont(F_BODY);
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
                b.setForeground(C_TEXT2);
                b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                b.addActionListener(e -> navigate(key));
                b.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { b.setForeground(C_PRIMARY); }
                    @Override public void mouseExited(MouseEvent e)  { b.setForeground(C_TEXT2); }
                });
                nav.add(b);
            }
            return nav;
        }

        void buildPanels() {
            if ("user".equals(currentRole)) {
                contentArea.add(new UserDashPanel(),    "dashboard");
                contentArea.add(new SearchPanel(),      "search");
                contentArea.add(new BookFormPanel(null),"book");
                contentArea.add(new MyBookingsPanel(),  "mybookings");
            } else if ("admin".equals(currentRole)) {
                contentArea.add(new AdminDashPanel(),   "dashboard");
                contentArea.add(new PendingPanel(),     "pending");
                contentArea.add(new AllBookingsPanel(), "allbookings");
                contentArea.add(new ReportPanel(),      "report");
            } else {
                contentArea.add(new SchedulePanel(),     "schedule");
                contentArea.add(new WeekSchedulePanel(), "weekschedule");
            }
        }

        void navigate(String key) {
            contentArea.removeAll();
            buildPanels();
            rebuildNav();
            cardLayout.show(contentArea, key);
            contentArea.revalidate();
            contentArea.repaint();
        }

        void navigateToBook(Booking prefill) {
            contentArea.removeAll();
            if ("user".equals(currentRole)) {
                contentArea.add(new UserDashPanel(),         "dashboard");
                contentArea.add(new SearchPanel(),           "search");
                contentArea.add(new BookFormPanel(prefill),  "book");
                contentArea.add(new MyBookingsPanel(),       "mybookings");
            }
            rebuildNav();
            cardLayout.show(contentArea, "book");
            contentArea.revalidate();
            contentArea.repaint();
        }

        void rebuildNav() {
            // Replace the nav bar (it's in BorderLayout.NORTH of body)
            Container body = (Container) getContentPane().getComponent(0);
            // The body is the panel added to CENTER; find its NORTH component
            // Simplest: just refresh the frame layout
            // For simplicity, we just update the status
        }

        void showStatusMessage(String msg, Color color) {
            statusLbl.setText(msg);
            statusLbl.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            statusLbl.setForeground(darker(color));
            if (statusTimer != null) statusTimer.stop();
            statusTimer = new javax.swing.Timer(5000, e -> {
                statusLbl.setText(" ");
                statusLbl.setBackground(C_BG2);
                statusLbl.setForeground(C_TEXT2);
            });
            statusTimer.setRepeats(false);
            statusTimer.start();
        }

        static Color darker(Color c) {
            return new Color(Math.max(0, c.getRed() - 60), Math.max(0, c.getGreen() - 60), Math.max(0, c.getBlue() - 60));
        }

        void showDefault() {
            String def = "csvc".equals(currentRole) ? "schedule" : "dashboard";
            cardLayout.show(contentArea, def);
        }
    }

    // ===================== BASE PANEL =====================
    static abstract class BasePanel extends JPanel {
        BasePanel() {
            setLayout(new BorderLayout(0, 0));
            setBackground(C_BG);
            setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        }

        JPanel vBox(Component... components) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setOpaque(false);
            for (Component c : components) {
                p.add(c);
            }
            return p;
        }

        JPanel row(Component... components) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            p.setOpaque(false);
            for (Component c : components) p.add(c);
            return p;
        }
    }

    // ===================== LOGIN FRAME =====================
    static class LoginFrame extends JFrame {
        LoginFrame() {
            setTitle("Đăng nhập – Hệ thống Quản lý Phòng học UEH");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(750, 520);
            setResizable(false);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(C_BG2);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            root.setBorder(BorderFactory.createEmptyBorder(40, 50, 30, 50));

            // Logo + Title
            JPanel header = new JPanel();
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
            header.setOpaque(false);

            JLabel logoBg = new JLabel("  UEH  ");
            logoBg.setFont(new Font("SansSerif", Font.BOLD, 14));
            logoBg.setBackground(C_PRIMARY); logoBg.setForeground(Color.WHITE);
            logoBg.setOpaque(true);
            logoBg.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
            logoBg.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel titleLbl = heading("Hệ thống Quản lý Phòng học");
            titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLbl.setBorder(BorderFactory.createEmptyBorder(14, 0, 4, 0));

            JLabel subLbl = subLbl("Quy trình KHĐTKT.QT.13 · Đại học Kinh tế TP. Hồ Chí Minh");
            subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel chooseLbl = subLbl("Chọn vai trò để đăng nhập vào hệ thống");
            chooseLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            chooseLbl.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

            header.add(logoBg);
            header.add(titleLbl);
            header.add(subLbl);
            header.add(chooseLbl);

            // Role cards
            JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
            cards.setOpaque(false);
            cards.setBorder(BorderFactory.createEmptyBorder(20, 0, 24, 0));

            Object[][] roles = {
                {"user",  "GV / SV",   "Giảng viên & Sinh viên",      "Tìm phòng, đăng ký và theo dõi đơn mượn phòng",  C_PRIMARY, C_PRIMARY_L},
                {"admin", "KHĐT-KT",   "Phòng Kế hoạch Đào tạo",       "Tiếp nhận, xét duyệt đơn, xuất báo cáo",          C_SUCCESS, C_SUCCESS_L},
                {"csvc",  "P. CSVC",   "Phòng Cơ sở Vật chất",         "Xem lịch mở cửa phòng và chuẩn bị thiết bị",      C_WARN, C_WARN_L},
            };

            for (Object[] r : roles) {
                String role = (String) r[0];
                Color primary = (Color) r[4];
                Color light   = (Color) r[5];

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBackground(C_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BORDER, 1),
                    BorderFactory.createEmptyBorder(20, 18, 20, 18)));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel abbr = new JLabel(" " + r[1] + " ");
                abbr.setFont(F_SMALL); abbr.setOpaque(true);
                abbr.setBackground(light); abbr.setForeground(primary);
                abbr.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

                JLabel nameLbl = new JLabel("<html><b>" + r[2] + "</b></html>");
                nameLbl.setFont(F_BODY); nameLbl.setForeground(C_TEXT);
                nameLbl.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 0));

                JLabel descLbl = new JLabel("<html><div style='width:160px;color:#5F5E5A'>" + r[3] + "</div></html>");
                descLbl.setFont(F_SMALL);

                card.add(abbr); card.add(nameLbl); card.add(descLbl);

                card.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        currentRole = role;
                        dispose();
                        appFrame = new AppFrame();
                        appFrame.setVisible(true);
                        appFrame.showDefault();
                    }
                    @Override public void mouseEntered(MouseEvent e) { card.setBackground(C_BG2); }
                    @Override public void mouseExited(MouseEvent e)  { card.setBackground(C_BG);  }
                });

                cards.add(card);
            }

            JLabel footer = subLbl("ET4430E 2025.2  ·  Nhóm: Phạm Huy Hoàng · Bùi Thái Sơn · Lê Duy Đức · Hà Duyên Hùng");
            footer.setHorizontalAlignment(JLabel.CENTER);
            footer.setAlignmentX(Component.CENTER_ALIGNMENT);

            root.add(header, BorderLayout.NORTH);
            root.add(cards, BorderLayout.CENTER);
            root.add(footer, BorderLayout.SOUTH);
            add(root);
        }
    }

    // ===================== USER DASHBOARD =====================
    static class UserDashPanel extends BasePanel {
        UserDashPanel() {
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JLabel title = heading("Chào mừng đến hệ thống quản lý phòng học UEH");
            title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
            JLabel sub = subLbl("Đặt phòng học nhanh chóng theo quy trình KHĐTKT.QT.13");
            sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

            // Quick action cards
            JPanel quickCards = new JPanel(new GridLayout(1, 2, 14, 0));
            quickCards.setOpaque(false);
            quickCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            quickCards.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

            String[][] actions = {
                {"Tìm phòng trống", "Tra cứu theo ngày, ca, cơ sở và loại phòng", "search", "#185FA5", "#E6F1FB"},
                {"Đăng ký mượn phòng", "Gửi yêu cầu trực tuyến – chỉ mất 2 phút", "book", "#0F6E56", "#E1F5EE"},
            };
            for (String[] a : actions) {
                Color bg = Color.decode(a[3]);
                Color bgL = Color.decode(a[4]);
                JPanel c = new JPanel(new BorderLayout());
                c.setBackground(bgL);
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bg, 1),
                    BorderFactory.createEmptyBorder(16, 18, 16, 18)));
                c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel t = new JLabel(a[0]);
                t.setFont(F_BOLD); t.setForeground(bg);
                JLabel d = new JLabel(a[1]);
                d.setFont(F_SMALL); d.setForeground(bg);
                c.add(t, BorderLayout.NORTH);
                c.add(d, BorderLayout.CENTER);

                String view = a[2];
                c.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { appFrame.navigate(view); }
                });
                quickCards.add(c);
            }

            // Rules card
            JPanel rulesCard = new JPanel();
            rulesCard.setLayout(new BoxLayout(rulesCard, BoxLayout.Y_AXIS));
            rulesCard.setBackground(C_BG);
            rulesCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
            rulesCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

            JLabel rTitle = new JLabel("Lưu ý quan trọng – Quy trình KHĐTKT.QT.13");
            rTitle.setFont(F_BOLD); rTitle.setForeground(C_TEXT);
            rulesCard.add(rTitle);
            rulesCard.add(Box.createVerticalStrut(10));

            String[] rules = {
                "• Đăng ký trước ít nhất 3 ngày làm việc (Bước 1)",
                "• Bắt buộc dùng email @ueh.edu.vn để xác thực (FR-01)",
                "• Trường hợp khẩn cấp: liên hệ trực tiếp P.KHĐT-KT qua email & điện thoại",
                "• Hồ sơ được lưu trữ trong 01 năm (Bước 5 – Lưu hồ sơ)"
            };
            for (String r : rules) {
                JLabel rl = subLbl(r);
                rl.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                rulesCard.add(rl);
            }

            content.add(title); content.add(sub);
            content.add(quickCards);
            content.add(rulesCard);

            add(content, BorderLayout.NORTH);
        }
    }

    // ===================== SEARCH PANEL =====================
    static class SearchPanel extends BasePanel {
        JTextField dateField;
        JComboBox<String> caBox, loaiBox, campusBox;
        JPanel resultsPanel;

        SearchPanel() {
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(heading("Tìm kiếm phòng trống (FR-02)"), BorderLayout.WEST);
            top.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

            // Filter bar
            JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            filterBar.setBackground(C_BG);
            filterBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

            dateField = new JTextField(LocalDate.now().plusDays(3).toString(), 11);
            dateField.setFont(F_BODY);

            caBox = new JComboBox<>(new String[]{
                "-- Tất cả ca --", CAS[0], CAS[1], CAS[2]});
            loaiBox = new JComboBox<>(new String[]{
                "-- Tất cả loại --", "Giảng đường", "Phòng máy tính"});
            campusBox = new JComboBox<>(new String[]{
                "-- Tất cả cơ sở --", "Cơ sở A – Nguyễn Đình Chiểu", "Cơ sở B – Hoàng Diệu 2"});

            for (JComboBox<?> cb : new JComboBox[]{caBox, loaiBox, campusBox}) {
                cb.setFont(F_BODY);
            }

            JButton searchBtn = primaryBtn("Tìm kiếm");
            searchBtn.addActionListener(e -> refreshResults());

            filterBar.add(new JLabel("Ngày (yyyy-MM-dd):"));
            filterBar.add(dateField);
            filterBar.add(new JLabel("Ca học:"));
            filterBar.add(caBox);
            filterBar.add(new JLabel("Loại phòng:"));
            filterBar.add(loaiBox);
            filterBar.add(new JLabel("Cơ sở:"));
            filterBar.add(campusBox);
            filterBar.add(searchBtn);

            resultsPanel = new JPanel(new BorderLayout());
            resultsPanel.setBackground(C_BG);
            resultsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

            JPanel north = new JPanel(new BorderLayout(0, 0));
            north.setOpaque(false);
            north.add(top, BorderLayout.NORTH);
            north.add(filterBar, BorderLayout.CENTER);

            add(north, BorderLayout.NORTH);
            add(resultsPanel, BorderLayout.CENTER);

            refreshResults();
        }

        void refreshResults() {
            resultsPanel.removeAll();
            String date = dateField.getText().trim();
            String ca = caBox.getSelectedIndex() == 0 ? "" : (String) caBox.getSelectedItem();
            String loai = loaiBox.getSelectedIndex() == 0 ? "" : (String) loaiBox.getSelectedItem();
            String campus = campusBox.getSelectedIndex() == 0 ? "" : (String) campusBox.getSelectedItem();

            // Rooms booked in this slot
            Set<String> booked = BOOKINGS.stream()
                .filter(b -> b.status == Status.APPROVED
                          && b.ngaySuDung.equals(date)
                          && (ca.isEmpty() || b.caSuDung.equals(ca)))
                .map(b -> b.roomId)
                .collect(Collectors.toSet());

            List<Room> filtered = ROOMS.stream()
                .filter(r -> (loai.isEmpty() || r.type.equals(loai))
                          && (campus.isEmpty() || r.campus.contains(campus.contains("A") ? "Nguyễn Đình Chiểu" : "Hoàng Diệu")))
                .collect(Collectors.toList());

            String[] cols = {"Phòng", "Cơ sở", "Loại phòng", "Sức chứa", "Trang thiết bị", "Trạng thái"};
            Object[][] data = filtered.stream().map(r -> new Object[]{
                r.name, r.campus, r.type, r.capacity + " người",
                r.facilities, booked.contains(r.id) ? "Đã được đặt" : "Còn trống"
            }).toArray(Object[][]::new);

            JTable table = buildTable(cols, data);

            // Color status column
            table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean f, int r, int c) {
                    super.getTableCellRendererComponent(t, v, sel, f, r, c);
                    boolean isEmpty = "Còn trống".equals(v);
                    setForeground(isEmpty ? C_SUCCESS : C_DANGER);
                    setFont(F_BOLD);
                    setBackground(sel ? C_PRIMARY_L : C_BG);
                    return this;
                }
            });

            // Double-click to open booking form
            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = table.getSelectedRow();
                        if (row < 0) return;
                        Room r = filtered.get(row);
                        if (booked.contains(r.id)) {
                            JOptionPane.showMessageDialog(appFrame,
                                "Phòng " + r.name + " đã được đặt trong khung giờ đã chọn.\nVui lòng chọn phòng hoặc ca khác.",
                                "Phòng đã được đặt", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        Booking draft = new Booking("", "", "", "", "", "", r.type, r.id,
                            date, ca.isEmpty() ? CAS[0] : ca, "", Status.PENDING);
                        appFrame.navigateToBook(draft);
                    }
                }
            });

            JLabel countLbl = subLbl("Tìm thấy " + filtered.size() + " phòng"
                + (booked.size() > 0 ? " · " + booked.size() + " phòng đã được đặt trong khung giờ này" : "")
                + "  (nhấn đúp vào dòng để đặt phòng)");
            countLbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));

            resultsPanel.add(countLbl, BorderLayout.NORTH);
            resultsPanel.add(styledScroll(table), BorderLayout.CENTER);
            resultsPanel.revalidate();
            resultsPanel.repaint();
        }
    }

    // ===================== BOOKING FORM =====================
    static class BookFormPanel extends BasePanel {
        final Booking prefill;

        BookFormPanel(Booking prefill) {
            this.prefill = prefill;
            buildUI();
        }

        void buildUI() {
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JLabel title = heading("Đăng ký mượn phòng học (FR-03)");
            JLabel sub = subLbl("Biểu mẫu KHĐTKT.QT.13.01  ·  Yêu cầu đăng ký trước ít nhất 3 ngày làm việc");
            sub.setBorder(BorderFactory.createEmptyBorder(4, 0, 16, 0));

            // Form panel
            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(C_BG);
            form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));

            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.HORIZONTAL;
            g.insets = new Insets(6, 6, 6, 10);

            // Input fields
            JTextField fHoTen   = tf("Nguyễn Văn A");
            JTextField fEmail   = tf("hoten@ueh.edu.vn");
            JTextField fDt      = tf("09xxxxxxxx");
            JTextField fDonVi   = tf("Khoa / Viện / Ban / Bộ môn...");
            JTextField fNgay    = tf(prefill != null && !prefill.ngaySuDung.isEmpty()
                                    ? prefill.ngaySuDung
                                    : LocalDate.now().plusDays(4).toString());
            JTextField fNd      = tf("VD: ET4430E – Lập trình nâng cao");
            JTextArea  fLyDo    = new JTextArea(3, 30);
            fLyDo.setFont(F_BODY); fLyDo.setLineWrap(true); fLyDo.setWrapStyleWord(true);
            fLyDo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

            String selCa = prefill != null && !prefill.caSuDung.isEmpty() ? prefill.caSuDung : CAS[0];
            JComboBox<String> fCa = new JComboBox<>(CAS);
            fCa.setFont(F_BODY);
            for (int i = 0; i < CAS.length; i++) {
                if (CAS[i].equals(selCa)) { fCa.setSelectedIndex(i); break; }
            }

            JComboBox<String> fLoai = new JComboBox<>(new String[]{"Giảng đường", "Phòng máy tính"});
            fLoai.setFont(F_BODY);
            if (prefill != null && !prefill.loaiPhong.isEmpty()) fLoai.setSelectedItem(prefill.loaiPhong);

            JComboBox<Room> fRoom = new JComboBox<>();
            fRoom.setFont(F_BODY);

            Runnable populateRooms = () -> {
                fRoom.removeAllItems();
                String sel = (String) fLoai.getSelectedItem();
                ROOMS.stream().filter(r -> r.type.equals(sel)).forEach(fRoom::addItem);
                if (prefill != null && !prefill.roomId.isEmpty()) {
                    for (int i = 0; i < fRoom.getItemCount(); i++) {
                        if (fRoom.getItemAt(i).id.equals(prefill.roomId)) {
                            fRoom.setSelectedIndex(i); break;
                        }
                    }
                }
            };
            fLoai.addActionListener(e -> populateRooms.run());
            populateRooms.run();

            // Layout rows: label, field, label, field
            Object[][] rows = {
                {"Họ và tên *",                fHoTen,  "Email UEH (@ueh.edu.vn) *", fEmail},
                {"Điện thoại di động *",        fDt,     "Đơn vị công tác *",         fDonVi},
                {"Ngày sử dụng (yyyy-MM-dd) *", fNgay,   "Ca sử dụng *",              fCa   },
                {"Loại phòng *",                fLoai,   "Phòng *",                   fRoom },
            };

            for (int row = 0; row < rows.length; row++) {
                g.gridy = row;

                g.gridx = 0; g.weightx = 0;
                JLabel lbl1 = new JLabel(rows[row][0].toString());
                lbl1.setFont(F_SUB); lbl1.setForeground(C_TEXT2);
                form.add(lbl1, g);

                g.gridx = 1; g.weightx = 0.45;
                form.add((Component) rows[row][1], g);

                g.gridx = 2; g.weightx = 0;
                JLabel lbl2 = new JLabel(rows[row][2].toString());
                lbl2.setFont(F_SUB); lbl2.setForeground(C_TEXT2);
                form.add(lbl2, g);

                g.gridx = 3; g.weightx = 0.45;
                form.add((Component) rows[row][3], g);
            }

            // Full-width rows
            g.gridy = rows.length;
            g.gridx = 0; g.weightx = 0; g.gridwidth = 1;
            JLabel lNd = new JLabel("Tên lớp học phần / Nội dung *");
            lNd.setFont(F_SUB); lNd.setForeground(C_TEXT2);
            form.add(lNd, g);
            g.gridx = 1; g.weightx = 1; g.gridwidth = 3;
            form.add(fNd, g);

            g.gridy = rows.length + 1;
            g.gridx = 0; g.weightx = 0; g.gridwidth = 1;
            JLabel lLy = new JLabel("Lý do mượn phòng *");
            lLy.setFont(F_SUB); lLy.setForeground(C_TEXT2);
            form.add(lLy, g);
            g.gridx = 1; g.weightx = 1; g.gridwidth = 3;
            form.add(new JScrollPane(fLyDo), g);

            // Buttons
            g.gridy = rows.length + 2;
            g.gridx = 0; g.gridwidth = 4; g.insets = new Insets(14, 6, 0, 0);
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            btnRow.setOpaque(false);
            JButton submitBtn = primaryBtn("Gửi đăng ký");
            JButton cancelBtn = neutralBtn("Hủy");
            cancelBtn.addActionListener(e -> appFrame.navigate("dashboard"));
            btnRow.add(submitBtn); btnRow.add(cancelBtn);
            form.add(btnRow, g);

            submitBtn.addActionListener(e -> {
                String ht = fHoTen.getText().trim();
                String em = fEmail.getText().trim();
                String dt = fDt.getText().trim();
                String dv = fDonVi.getText().trim();
                String ng = fNgay.getText().trim();
                String nd = fNd.getText().trim();
                String ly = fLyDo.getText().trim();
                String ca = (String) fCa.getSelectedItem();
                Room   rm = (Room) fRoom.getSelectedItem();

                // Validate required
                if (ht.isEmpty() || em.isEmpty() || dt.isEmpty() || dv.isEmpty()
                        || ng.isEmpty() || nd.isEmpty() || ly.isEmpty() || rm == null) {
                    JOptionPane.showMessageDialog(appFrame,
                        "Vui lòng điền đầy đủ tất cả các trường bắt buộc (*)",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE); return;
                }
                // Validate email
                if (!em.endsWith("@ueh.edu.vn")) {
                    JOptionPane.showMessageDialog(appFrame,
                        "Email phải có định dạng @ueh.edu.vn (FR-01 – Xác thực SSO)",
                        "Email không hợp lệ", JOptionPane.WARNING_MESSAGE); return;
                }
                // Validate date format and 3-day rule
                try {
                    LocalDate date = LocalDate.parse(ng);
                    long diff = ChronoUnit.DAYS.between(LocalDate.now(), date);
                    if (diff < 3) {
                        JOptionPane.showMessageDialog(appFrame,
                            "Yêu cầu đăng ký trước ít nhất 3 ngày làm việc.\n" +
                            "Ngày yêu cầu: " + ng + " (còn " + diff + " ngày)\n" +
                            "Nếu trường hợp khẩn cấp, liên hệ trực tiếp P.KHĐT-KT.",
                            "Không đủ thời hạn đăng ký", JOptionPane.WARNING_MESSAGE); return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(appFrame,
                        "Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng: yyyy-MM-dd",
                        "Lỗi định dạng ngày", JOptionPane.ERROR_MESSAGE); return;
                }
                // Check conflict
                Room finalRm = rm;
                String finalNg = ng;
                Optional<Booking> conflict = BOOKINGS.stream()
                    .filter(b -> b.status == Status.APPROVED
                              && b.roomId.equals(finalRm.id)
                              && b.ngaySuDung.equals(finalNg)
                              && b.caSuDung.equals(ca))
                    .findFirst();
                if (conflict.isPresent()) {
                    JOptionPane.showMessageDialog(appFrame,
                        "Trùng lịch! Phòng " + rm.name + " đã được duyệt cho đơn " + conflict.get().id +
                        "\nNgày: " + ng + " · Ca: " + ca +
                        "\nVui lòng chọn phòng, ngày hoặc ca khác.",
                        "Phát hiện trùng lịch (FR-04)", JOptionPane.ERROR_MESSAGE); return;
                }

                String newId = nextId();
                String loaiS = (String) fLoai.getSelectedItem();
                Booking b = new Booking(newId, ht, em, dt, dv, nd, loaiS, rm.id, ng, ca, ly, Status.PENDING);
                BOOKINGS.add(b);

                showStatus("✓  Đơn " + newId + " đã gửi thành công! Email xác nhận đã gửi tới " + em, C_SUCCESS);
                JOptionPane.showMessageDialog(appFrame,
                    "Đơn đăng ký đã gửi thành công!\n\n" +
                    "Mã đơn: " + newId + "\n" +
                    "Phòng: " + rm.name + " · " + ng + " · " + ca.split(" ")[0] + "\n\n" +
                    "Phòng KHĐT-KT sẽ xử lý và gửi kết quả qua email:\n" + em,
                    "Đăng ký thành công", JOptionPane.INFORMATION_MESSAGE);
                appFrame.navigate("mybookings");
            });

            content.add(title); content.add(sub); content.add(form);
            add(new JScrollPane(content) {{ setBorder(null); }});
        }

        static JTextField tf(String placeholder) {
            JTextField f = new JTextField(20);
            f.setFont(F_BODY);
            f.setForeground(C_TEXT2);
            f.setText(placeholder);
            f.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(C_TEXT); }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(C_TEXT2); }
                }
            });
            return f;
        }
    }

    // ===================== MY BOOKINGS =====================
    static class MyBookingsPanel extends BasePanel {
        MyBookingsPanel() {
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            top.add(heading("Đơn đăng ký của tôi"), BorderLayout.WEST);
            JButton newBtn = primaryBtn("+ Đăng ký mới");
            newBtn.addActionListener(e -> appFrame.navigate("book"));
            top.add(newBtn, BorderLayout.EAST);

            String[] cols = {"Mã đơn", "Nội dung / Lớp học", "Người đăng ký", "Phòng", "Ngày", "Ca", "Trạng thái", "Ghi chú từ chối"};
            Object[][] data = BOOKINGS.stream().map(b -> {
                Room r = findRoom(b.roomId);
                return new Object[]{
                    b.id, b.noiDung, b.hoTen,
                    r != null ? r.name : b.roomId,
                    b.ngaySuDung,
                    b.caSuDung.split(" ")[0],
                    statusText(b.status),
                    b.rejectReason != null ? b.rejectReason : ""
                };
            }).toArray(Object[][]::new);

            JTable table = buildTable(cols, data);
            setStatusCellRenderer(table, 6);

            add(top, BorderLayout.NORTH);
            add(styledScroll(table), BorderLayout.CENTER);
        }
    }

    // ===================== ADMIN DASHBOARD =====================
    static class AdminDashPanel extends BasePanel {
        AdminDashPanel() {
            long total = BOOKINGS.size();
            long pend  = BOOKINGS.stream().filter(b -> b.status == Status.PENDING).count();
            long appr  = BOOKINGS.stream().filter(b -> b.status == Status.APPROVED).count();
            long rej   = BOOKINGS.stream().filter(b -> b.status == Status.REJECTED).count();

            JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
            statsRow.setOpaque(false);
            statsRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            statsRow.add(statCard("Tổng đơn",   String.valueOf(total), C_TEXT));
            statsRow.add(statCard("Chờ duyệt",  String.valueOf(pend),  C_WARN));
            statsRow.add(statCard("Đã duyệt",   String.valueOf(appr),  C_SUCCESS));
            statsRow.add(statCard("Từ chối",    String.valueOf(rej),   C_DANGER));

            List<Booking> pendList = BOOKINGS.stream()
                .filter(b -> b.status == Status.PENDING).collect(Collectors.toList());

            JLabel pendTitle = new JLabel("Đơn đang chờ duyệt");
            pendTitle.setFont(F_BOLD); pendTitle.setForeground(C_TEXT);
            pendTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            JPanel center = new JPanel(new BorderLayout(0, 6));
            center.setOpaque(false);
            center.add(pendTitle, BorderLayout.NORTH);

            if (pendList.isEmpty()) {
                center.add(subLbl("Không có đơn nào đang chờ duyệt."), BorderLayout.CENTER);
            } else {
                String[] cols = {"Mã đơn", "Người đăng ký", "Đơn vị", "Phòng", "Ngày", "Ca học"};
                Object[][] data = pendList.stream().map(b -> {
                    Room r = findRoom(b.roomId);
                    return new Object[]{
                        b.id, b.hoTen, b.donVi,
                        r != null ? r.name : b.roomId,
                        b.ngaySuDung, b.caSuDung.split(" ")[0]
                    };
                }).toArray(Object[][]::new);

                JTable table = buildTable(cols, data);
                table.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) appFrame.navigate("pending");
                    }
                });

                JButton viewPendBtn = primaryBtn("Xử lý tất cả đơn chờ duyệt →");
                viewPendBtn.addActionListener(e -> appFrame.navigate("pending"));

                center.add(styledScroll(table), BorderLayout.CENTER);
                center.add(viewPendBtn, BorderLayout.SOUTH);
            }

            JPanel content = new JPanel(new BorderLayout(0, 0));
            content.setOpaque(false);
            content.add(statsRow, BorderLayout.NORTH);
            content.add(center, BorderLayout.CENTER);

            add(heading("Tổng quan hệ thống"), BorderLayout.NORTH);
            add(content, BorderLayout.CENTER);
        }
    }

    // ===================== PENDING PANEL =====================
    static class PendingPanel extends BasePanel {
        PendingPanel() {
            List<Booking> pendList = BOOKINGS.stream()
                .filter(b -> b.status == Status.PENDING).collect(Collectors.toList());

            JLabel title = heading("Xét duyệt đơn – FR-04  (" + pendList.size() + " đơn chờ)");
            title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(C_BG);

            if (pendList.isEmpty()) {
                JLabel empty = subLbl("Không có đơn nào đang chờ duyệt. Tất cả đơn đã được xử lý.");
                empty.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
                listPanel.add(empty);
            }

            for (Booking b : pendList) {
                Room r = findRoom(b.roomId);
                JPanel card = buildBookingCard(b, r);
                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(12));
            }

            JScrollPane sp = new JScrollPane(listPanel);
            sp.setBorder(null);
            sp.getViewport().setBackground(C_BG);

            add(title, BorderLayout.NORTH);
            add(sp, BorderLayout.CENTER);
        }

        JPanel buildBookingCard(Booking b, Room r) {
            JPanel card = new JPanel(new BorderLayout(0, 10));
            card.setBackground(C_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

            // Card header
            JPanel hdr = new JPanel(new BorderLayout());
            hdr.setOpaque(false);
            JLabel idLbl = new JLabel(b.id + "  ·  " + b.noiDung);
            idLbl.setFont(F_BOLD);
            JLabel dateLbl = subLbl("Gửi: " + b.submittedAt.toString());
            hdr.add(idLbl, BorderLayout.WEST);
            hdr.add(dateLbl, BorderLayout.EAST);

            // Info grid
            JPanel info = new JPanel(new GridLayout(2, 4, 10, 4));
            info.setOpaque(false);
            String[][] fields = {
                {"Người đăng ký", b.hoTen}, {"Đơn vị", b.donVi},
                {"Email", b.email},          {"SĐT", b.dienThoai},
                {"Phòng", r != null ? r.name + " · " + r.campus : b.roomId},
                {"Ngày", b.ngaySuDung}, {"Ca", b.caSuDung}, {"Loại", b.loaiPhong}
            };
            for (String[] kv : fields) {
                JLabel kl = subLbl(kv[0] + ": ");
                JLabel vl = new JLabel(kv[1]);
                vl.setFont(F_SMALL);
                JPanel kp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                kp.setOpaque(false);
                kp.add(kl); kp.add(vl);
                info.add(kp);
            }

            JLabel lyDoLbl = new JLabel("Lý do: " + b.lyDo);
            lyDoLbl.setFont(F_SMALL); lyDoLbl.setForeground(C_TEXT2);
            lyDoLbl.setOpaque(true); lyDoLbl.setBackground(C_BG2);
            lyDoLbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

            // Action buttons
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            actions.setOpaque(false);
            JButton approveBtn = successBtn("✓  Duyệt đơn");
            JButton rejectBtn  = dangerBtn("✕  Từ chối");

            approveBtn.addActionListener(e -> {
                // Check conflict
                Optional<Booking> conflict = BOOKINGS.stream()
                    .filter(x -> !x.id.equals(b.id) && x.status == Status.APPROVED
                              && x.roomId.equals(b.roomId)
                              && x.ngaySuDung.equals(b.ngaySuDung)
                              && x.caSuDung.equals(b.caSuDung))
                    .findFirst();
                if (conflict.isPresent()) {
                    JOptionPane.showMessageDialog(appFrame,
                        "Phát hiện trùng lịch!\nPhòng " + b.roomId + " đã được duyệt cho đơn " + conflict.get().id,
                        "Trùng lịch – FR-04", JOptionPane.ERROR_MESSAGE); return;
                }
                int confirm = JOptionPane.showConfirmDialog(appFrame,
                    "Xác nhận duyệt đơn " + b.id + "?\n" +
                    "Phòng: " + (r != null ? r.name : b.roomId) + " · " + b.ngaySuDung + " · " + b.caSuDung.split(" ")[0] + "\n\n" +
                    "Hệ thống sẽ tự động gửi email thông báo (FR-05)\nvà đồng bộ lịch lên P.CSVC (FR-06).",
                    "Xác nhận duyệt", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    b.status = Status.APPROVED;
                    b.processedBy = "ThS. Trương Hồng Khánh";
                    b.processedAt = LocalDate.now();
                    showStatus("✓  Đã duyệt đơn " + b.id + " · Email gửi tới " + b.email + " · Lịch đồng bộ CSVC (FR-06)", C_SUCCESS);
                    appFrame.navigate("pending");
                }
            });

            rejectBtn.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(appFrame,
                    "Nhập lý do từ chối để gửi email thông báo tới " + b.email + ":",
                    "Từ chối đơn " + b.id, JOptionPane.QUESTION_MESSAGE);
                if (reason != null && !reason.trim().isEmpty()) {
                    b.status = Status.REJECTED;
                    b.rejectReason = reason.trim();
                    b.processedBy = "ThS. Trương Hồng Khánh";
                    b.processedAt = LocalDate.now();
                    showStatus("✗  Đã từ chối đơn " + b.id + " · Email thông báo gửi tới " + b.email, C_WARN);
                    appFrame.navigate("pending");
                }
            });

            actions.add(approveBtn); actions.add(rejectBtn);

            JPanel mid = new JPanel(new BorderLayout(0, 8));
            mid.setOpaque(false);
            mid.add(info, BorderLayout.NORTH);
            mid.add(lyDoLbl, BorderLayout.CENTER);

            card.add(hdr, BorderLayout.NORTH);
            card.add(mid, BorderLayout.CENTER);
            card.add(actions, BorderLayout.SOUTH);

            return card;
        }
    }

    // ===================== ALL BOOKINGS =====================
    static class AllBookingsPanel extends BasePanel {
        AllBookingsPanel() {
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            top.add(heading("Tất cả đơn đăng ký  (" + BOOKINGS.size() + ")"), BorderLayout.WEST);

            String[] cols = {"Mã đơn", "Người đăng ký", "Đơn vị", "Nội dung", "Phòng", "Ngày", "Ca", "Trạng thái", "Duyệt bởi"};
            Object[][] data = BOOKINGS.stream().map(b -> {
                Room r = findRoom(b.roomId);
                return new Object[]{
                    b.id, b.hoTen, b.donVi, b.noiDung,
                    r != null ? r.name : b.roomId,
                    b.ngaySuDung, b.caSuDung.split(" ")[0],
                    statusText(b.status),
                    b.processedBy != null ? b.processedBy : "—"
                };
            }).toArray(Object[][]::new);

            JTable table = buildTable(cols, data);
            setStatusCellRenderer(table, 7);

            add(top, BorderLayout.NORTH);
            add(styledScroll(table), BorderLayout.CENTER);
        }
    }

    // ===================== REPORT PANEL =====================
    static class ReportPanel extends BasePanel {
        ReportPanel() {
            long total = BOOKINGS.size();
            long pend  = BOOKINGS.stream().filter(b -> b.status == Status.PENDING).count();
            long appr  = BOOKINGS.stream().filter(b -> b.status == Status.APPROVED).count();
            long rej   = BOOKINGS.stream().filter(b -> b.status == Status.REJECTED).count();

            JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
            statsRow.setOpaque(false);
            statsRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            statsRow.add(statCard("Tổng đơn",  String.valueOf(total), C_TEXT));
            statsRow.add(statCard("Chờ duyệt", String.valueOf(pend),  C_WARN));
            statsRow.add(statCard("Đã duyệt",  String.valueOf(appr),  C_SUCCESS));
            statsRow.add(statCard("Từ chối",   String.valueOf(rej),   C_DANGER));

            JLabel roomTitle = new JLabel("Thống kê lượt đặt theo phòng (FR-07)");
            roomTitle.setFont(F_BOLD);
            roomTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            Map<String, Long> byRoom = BOOKINGS.stream()
                .collect(Collectors.groupingBy(b -> b.roomId, Collectors.counting()));

            String[] cols = {"Phòng", "Cơ sở", "Loại", "Tổng lượt đặt", "Tỷ lệ (%)"};
            Object[][] data = byRoom.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> {
                    Room r = findRoom(e.getKey());
                    long pct = total > 0 ? Math.round(e.getValue() * 100.0 / total) : 0;
                    return new Object[]{
                        r != null ? r.name : e.getKey(),
                        r != null ? r.campus : "—",
                        r != null ? r.type : "—",
                        e.getValue(),
                        pct + "%"
                    };
                }).toArray(Object[][]::new);

            JTable table = buildTable(cols, data);

            JButton exportBtn = primaryBtn("Xuất báo cáo Excel (FR-07)");
            exportBtn.addActionListener(e -> {
                showStatus("✓  Đã xuất báo cáo Excel: BaoCao_SuDungPhong_" + LocalDate.now() + ".xlsx  (FR-07)", C_SUCCESS);
                JOptionPane.showMessageDialog(appFrame,
                    "Báo cáo xuất thành công!\n\nFile: BaoCao_SuDungPhong_" + LocalDate.now() + ".xlsx\n" +
                    "Nội dung: Thống kê công suất sử dụng phòng học\n" +
                    "Số bản ghi: " + BOOKINGS.size() + " đơn đăng ký",
                    "Xuất báo cáo (FR-07)", JOptionPane.INFORMATION_MESSAGE);
            });
            exportBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

            JPanel north = new JPanel(new BorderLayout(0, 0));
            north.setOpaque(false);
            north.add(heading("Báo cáo công suất sử dụng (FR-07)"), BorderLayout.NORTH);
            north.add(statsRow, BorderLayout.CENTER);

            JPanel center = new JPanel(new BorderLayout(0, 8));
            center.setOpaque(false);
            center.add(roomTitle, BorderLayout.NORTH);
            center.add(styledScroll(table), BorderLayout.CENTER);
            center.add(exportBtn, BorderLayout.SOUTH);

            add(north, BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);
        }
    }

    // ===================== SCHEDULE PANEL (CSVC) =====================
    static class SchedulePanel extends BasePanel {
        SchedulePanel() {
            List<Booking> approved = BOOKINGS.stream()
                .filter(b -> b.status == Status.APPROVED).collect(Collectors.toList());

            JLabel title = heading("Lịch mở cửa theo ca (FR-06)");
            JLabel sub = subLbl("Các phòng đã được duyệt · P.CSVC chuẩn bị mở cửa và thiết bị theo từng ca");
            sub.setBorder(BorderFactory.createEmptyBorder(4, 0, 16, 0));

            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(C_BG);

            for (String ca : CAS) {
                List<Booking> caItems = approved.stream()
                    .filter(b -> b.caSuDung.equals(ca)).collect(Collectors.toList());

                // Ca header
                JLabel caHdr = new JLabel("  " + ca + "  ");
                caHdr.setFont(F_BOLD); caHdr.setOpaque(true);
                caHdr.setBackground(C_PRIMARY_L); caHdr.setForeground(C_PRIMARY_D);
                caHdr.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
                caHdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                listPanel.add(caHdr);

                if (caItems.isEmpty()) {
                    JLabel empty = subLbl("    Không có phòng nào cần mở cửa trong ca này.");
                    empty.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
                    listPanel.add(empty);
                } else {
                    String[] cols = {"Phòng", "Cơ sở", "Ngày sử dụng", "Nội dung / Lớp học", "Giảng viên / Đơn vị", "Thiết bị cần chuẩn bị"};
                    Object[][] data = caItems.stream().map(b -> {
                        Room r = findRoom(b.roomId);
                        return new Object[]{
                            r != null ? r.name : b.roomId,
                            r != null ? r.campus : "—",
                            b.ngaySuDung, b.noiDung, b.hoTen + " · " + b.donVi,
                            r != null ? r.facilities : "—"
                        };
                    }).toArray(Object[][]::new);

                    JTable table = buildTable(cols, data);
                    JScrollPane sp = styledScroll(table);
                    sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
                    sp.setPreferredSize(new Dimension(0, 120));
                    sp.setBorder(BorderFactory.createEmptyBorder(4, 0, 12, 0));
                    listPanel.add(sp);
                }
                listPanel.add(Box.createVerticalStrut(8));
            }

            JScrollPane mainScroll = new JScrollPane(listPanel);
            mainScroll.setBorder(null);
            mainScroll.getViewport().setBackground(C_BG);

            JPanel northPanel = new JPanel(new BorderLayout());
            northPanel.setOpaque(false);
            northPanel.add(title, BorderLayout.NORTH);
            northPanel.add(sub, BorderLayout.CENTER);

            add(northPanel, BorderLayout.NORTH);
            add(mainScroll, BorderLayout.CENTER);
        }
    }

    // ===================== WEEK SCHEDULE PANEL (CSVC) =====================
    static class WeekSchedulePanel extends BasePanel {
        WeekSchedulePanel() {
            List<Booking> approved = BOOKINGS.stream()
                .filter(b -> b.status == Status.APPROVED).collect(Collectors.toList());

            DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("dd/MM");

            // Build columns
            String[] cols = new String[8];
            cols[0] = "Ca học";
            for (int i = 0; i < 7; i++) {
                LocalDate d = LocalDate.now().plusDays(i);
                String dow = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("vi"));
                cols[i + 1] = dow + " " + d.format(dayFmt);
            }

            Object[][] data = new Object[CAS.length][8];
            for (int ci = 0; ci < CAS.length; ci++) {
                data[ci][0] = CAS[ci].split(" ")[0];
                for (int di = 0; di < 7; di++) {
                    String ds = LocalDate.now().plusDays(di).toString();
                    int finalCi = ci;
                    String rooms = approved.stream()
                        .filter(b -> b.ngaySuDung.equals(ds) && b.caSuDung.equals(CAS[finalCi]))
                        .map(b -> b.roomId)
                        .collect(Collectors.joining(", "));
                    data[ci][di + 1] = rooms.isEmpty() ? "—" : rooms;
                }
            }

            JTable table = buildTable(cols, data);
            table.setRowHeight(36);

            // Color cells with bookings
            for (int col = 1; col < 8; col++) {
                final int c = col;
                table.getColumnModel().getColumn(c).setCellRenderer(new DefaultTableCellRenderer() {
                    @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean f, int r, int col) {
                        super.getTableCellRendererComponent(t, v, sel, f, r, col);
                        boolean hasBooking = v != null && !"—".equals(v.toString());
                        setBackground(sel ? C_PRIMARY_L : (hasBooking ? C_SUCCESS_L : C_BG));
                        setForeground(hasBooking ? C_SUCCESS : C_TEXT2);
                        setFont(hasBooking ? F_BOLD : F_TABLE);
                        setHorizontalAlignment(JLabel.CENTER);
                        return this;
                    }
                });
            }

            JLabel title = heading("Lịch tuần – Phòng đã được duyệt");
            title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

            add(title, BorderLayout.NORTH);
            add(styledScroll(table), BorderLayout.CENTER);
        }
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Improve font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}