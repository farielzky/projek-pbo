package view;

import model.LayangLayang;
import model.LimasLayangLayang;
import model.PrismaLayangLayang;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class JendelaUtama extends JFrame {

    private static final Color BG          = new Color(245, 244, 240);   // warm off-white
    private static final Color SURFACE     = new Color(255, 255, 255);   // card white
    private static final Color BORDER_CLR  = new Color(220, 218, 212);   // soft border
    private static final Color TEXT_PRI    = new Color(40,  38,  35);    // near-black
    private static final Color TEXT_SEC    = new Color(120, 116, 108);   // muted brown-gray
    private static final Color ACCENT      = new Color(100, 130, 200);   // calm blue
    private static final Color ACCENT_DARK = new Color(70,  100, 175);
    private static final Color COL_LAYANG  = new Color(90,  160, 220);   // sky blue
    private static final Color COL_PRISMA  = new Color(180, 120, 200);   // soft purple
    private static final Color COL_LIMAS   = new Color(90,  185, 140);   // sage green
    private static final Color COL_SYS     = new Color(200, 100,  90);   // muted red
    private static final Color COL_INTR    = new Color(220, 165,  60);   // amber

    private static final Font FONT_HEAD  = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_SUB   = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 11);
    private static final Font FONT_MONO  = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font FONT_SMALL = new Font("Monospaced", Font.PLAIN, 11);

    private PillCheckBox cbLayang, cbPrisma, cbLimas, cbInterrupt;
    private JTextField txtData, txtThread;
    private JButton btnJalankan;
    private JTextPane textPane;
    private SmoothProgressBar progressBar;
    private JLabel lblStatus, lblRuntime, lblThreadCount;
    private JPanel threadMonitorPanel;

    private List<Thread> daftarThread;
    private long startTime;
    private Timer runtimeTimer;
    private int totalTasks;

    private final java.util.Map<String, ThreadCard> threadCards =
            Collections.synchronizedMap(new java.util.LinkedHashMap<>());

    public JendelaUtama() {
        setTitle("Geometry · Multithreading Engine");
        setSize(1000, 680);
        setMinimumSize(new Dimension(880, 580));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        daftarThread = Collections.synchronizedList(new ArrayList<>());
        initUI();
    }

    private void initUI() {
        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLogPanel(), buildRightPanel());
        split.setResizeWeight(0.65);
        split.setBorder(null);
        split.setDividerSize(6);
        split.setBackground(BG);
        add(split, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(SURFACE);
        p.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(18, 24, 18, 24)));

        // Controls row
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        ctrl.setBackground(SURFACE);

        cbLayang = new PillCheckBox("2D  Layang", COL_LAYANG);
        cbPrisma = new PillCheckBox("3D  Prisma", COL_PRISMA);
        cbLimas  = new PillCheckBox("3D  Limas",  COL_LIMAS);
        cbLayang.setSelected(true);

        txtData   = buildField("100");
        txtThread = buildField("5");

        ctrl.add(makeLabel("Bangun:"));
        ctrl.add(cbLayang);
        ctrl.add(cbPrisma);
        ctrl.add(cbLimas);
        ctrl.add(Box.createHorizontalStrut(10));
        ctrl.add(makeLabel("Data:"));
        ctrl.add(txtData);
        ctrl.add(makeLabel("Thread:"));
        ctrl.add(txtThread);
        
        // Pilihan Interupsi
        cbInterrupt = new PillCheckBox("Mode Interupsi", COL_INTR);
        cbInterrupt.setSelected(true);
        ctrl.add(Box.createHorizontalStrut(10));
        ctrl.add(cbInterrupt);
        ctrl.add(Box.createHorizontalStrut(8));

        btnJalankan = buildRunButton();
        ctrl.add(btnJalankan);

        p.add(ctrl, BorderLayout.EAST);
        return p;
    }

    private JPanel buildLogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 16, 0, 8));

        JLabel lbl = sectionLabel("Execution Log");
        p.add(lbl, BorderLayout.NORTH);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(SURFACE);
        textPane.setMargin(new Insets(12, 12, 12, 12));
        textPane.setFont(FONT_MONO);

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(new RoundedBorder(8, BORDER_CLR));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scroll.getVerticalScrollBar().setOpaque(false);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(16, 8, 0, 16));

        p.add(buildProgressCard(), BorderLayout.NORTH);
        p.add(buildThreadMonitor(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildProgressCard() {
        JPanel card = card();
        card.setLayout(new GridLayout(0, 1, 0, 8));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        lblStatus = new JLabel("Idle");
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStatus.setForeground(TEXT_PRI);

        progressBar = new SmoothProgressBar();
        progressBar.setPreferredSize(new Dimension(0, 10));

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(SURFACE);
        lblRuntime = smallMeta("Runtime: —");
        lblThreadCount = smallMeta("Threads: 0");
        row.add(lblRuntime, BorderLayout.WEST);
        row.add(lblThreadCount, BorderLayout.EAST);

        card.add(lblStatus);
        card.add(progressBar);
        card.add(row);
        return card;
    }

    private JPanel buildThreadMonitor() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(BG);
        wrapper.add(sectionLabel("Thread Monitor"), BorderLayout.NORTH);

        threadMonitorPanel = new JPanel();
        threadMonitorPanel.setLayout(new BoxLayout(threadMonitorPanel, BoxLayout.Y_AXIS));
        threadMonitorPanel.setBackground(SURFACE);

        JScrollPane scroll = new JScrollPane(threadMonitorPanel);
        scroll.setBorder(new RoundedBorder(8, BORDER_CLR));
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(SURFACE);
        p.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(8, 24, 8, 24)));
        JLabel hint = new JLabel("Mode Interupsi aktif = 5% kemungkinan thread saling mematikan. Interrupted terlihat amber.");
        hint.setFont(FONT_SUB);
        hint.setForeground(TEXT_SEC);
        p.add(hint, BorderLayout.WEST);
        return p;
    }

    private void mulaiProses() {
        try {
            int jumlahData   = Integer.parseInt(txtData.getText().trim());
            int jumlahThread = Integer.parseInt(txtThread.getText().trim());
            if (jumlahData <= 0 || jumlahThread <= 0) throw new NumberFormatException();
            if (!cbLayang.isSelected() && !cbPrisma.isSelected() && !cbLimas.isSelected()) {
                JOptionPane.showMessageDialog(this, "Pilih minimal 1 bangun geometri.", "Perhatian", JOptionPane.WARNING_MESSAGE);
                return;
            }

            textPane.setText("");
            threadCards.clear();
            threadMonitorPanel.removeAll();
            threadMonitorPanel.revalidate();
            threadMonitorPanel.repaint();

            daftarThread.clear();
            btnJalankan.setEnabled(false);
            
            totalTasks = 0;
            if (cbLayang.isSelected()) totalTasks += jumlahData; 
            if (cbPrisma.isSelected()) totalTasks += jumlahData; 
            if (cbLimas.isSelected())  totalTasks += jumlahData;

            progressBar.reset(totalTasks);
            lblStatus.setText("Running…");
            lblStatus.setForeground(ACCENT);

            tambahLog("[SYSTEM] Memulai komputasi multithreading…", COL_SYS);

            // Tambahkan Header Tabel di sini
            tambahLog("+--------------+-----------+--------+--------+----------+----------+----------+", TEXT_PRI);
            tambahLog(String.format("| %-12s | %-9s | %-6s | %-6s | %-8s | %-8s | %-8s |", "Jenis Thread", "Data ke-i", "D1", "D2", "Luas", "Keliling", "Volume"), TEXT_PRI);
            tambahLog("+--------------+-----------+--------+--------+----------+----------+----------+", TEXT_PRI);
            
            int idx = 1;
            if (cbLayang.isSelected()) for (int i = 1; i <= jumlahThread; i++) preregister("LAYANG", i, COL_LAYANG);
            if (cbPrisma.isSelected()) for (int i = 1; i <= jumlahThread; i++) preregister("PRISMA", i, COL_PRISMA);
            if (cbLimas.isSelected())  for (int i = 1; i <= jumlahThread; i++) preregister("LIMAS",  i, COL_LIMAS);

            startTime = System.currentTimeMillis();
            if (runtimeTimer != null) runtimeTimer.stop();
            runtimeTimer = new Timer(200, e -> updateRuntime());
            runtimeTimer.start();

            if (cbLayang.isSelected()) buatDanJalankanThread("LAYANG", jumlahData, jumlahThread, COL_LAYANG);
            if (cbPrisma.isSelected()) buatDanJalankanThread("PRISMA", jumlahData, jumlahThread, COL_PRISMA);
            if (cbLimas.isSelected())  buatDanJalankanThread("LIMAS",  jumlahData, jumlahThread, COL_LIMAS);

            updateThreadCount();

            new Thread(() -> {
                while (true) {
                    boolean anyAlive = daftarThread.stream().anyMatch(Thread::isAlive);
                    if (!anyAlive) {
                        SwingUtilities.invokeLater(() -> {
                            runtimeTimer.stop();
                            btnJalankan.setEnabled(true);
                            progressBar.complete();
                            lblStatus.setText("Selesai  ✓");
                            lblStatus.setForeground(COL_LIMAS);
                            updateRuntime();
                            // Tambahkan garis penutup tabel di sini
                            tambahLog("+--------------+-----------+--------+--------+----------+----------+----------+", TEXT_PRI);
                            tambahLog("[SYSTEM] Semua thread selesai.", COL_SYS);
                        });
                        break;
                    }
                    try { Thread.sleep(250); } catch (Exception ignored) {}
                    SwingUtilities.invokeLater(this::updateThreadCount);
                }
            }, "monitor-thread").start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input tidak valid. Masukkan angka bulat positif.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preregister(String jenis, int i, Color color) {
        String key = jenis + "-" + i;
        ThreadCard card = new ThreadCard(key, color);
        threadCards.put(key, card);
        threadMonitorPanel.add(card);
        threadMonitorPanel.add(Box.createVerticalStrut(4));
    }

    private void buatDanJalankanThread(String jenis, int data, int jmlThread, Color warna) {
    boolean modeInterupsi = cbInterrupt.isSelected(); 

    // Hitung jatah dasar per thread secara matematis
    int jatahDasar = data / jmlThread;
    int sisaBagi = data % jmlThread;

    for (int i = 1; i <= jmlThread; i++) {
        // Pembagian sisa tugas agar total data pas (tidak kurang/lebih)
        int jatahThreadIni = jatahDasar + (i <= sisaBagi ? 1 : 0);

        Runnable r;
        if (jenis.equals("LAYANG"))      r = new LayangLayang(this, jatahThreadIni, warna, i, daftarThread, modeInterupsi);
        else if (jenis.equals("PRISMA")) r = new PrismaLayangLayang(this, jatahThreadIni, warna, i, daftarThread, modeInterupsi);
        else                             r = new LimasLayangLayang(this, jatahThreadIni, warna, i, daftarThread, modeInterupsi);
        
        Thread t = new Thread(r, jenis + "-" + i);
        daftarThread.add(t);
        t.start();
    }
}

    private void updateRuntime() {
        long ms = System.currentTimeMillis() - startTime;
        lblRuntime.setText(String.format("Runtime: %.1fs", ms / 1000.0));
    }

    private void updateThreadCount() {
        long alive = daftarThread.stream().filter(Thread::isAlive).count();
        lblThreadCount.setText("Threads: " + alive + " / " + daftarThread.size());
    }

    public synchronized void tambahLog(String teks, Color warna) {
        SwingUtilities.invokeLater(() -> {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, warna);
            aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Monospaced");
            aset = sc.addAttribute(aset, StyleConstants.FontSize, 12);
            int len = textPane.getDocument().getLength();
            try {
                textPane.getDocument().insertString(len, teks + "\n", aset);
                textPane.setCaretPosition(textPane.getDocument().getLength());
            } catch (BadLocationException ignored) {}
        });
    }

    public void tambahProgress() {
        SwingUtilities.invokeLater(() -> progressBar.increment());
    }

    public void updateThreadCard(String key, int done, int total, String state) {
        SwingUtilities.invokeLater(() -> {
            ThreadCard card = threadCards.get(key);
            if (card != null) card.update(done, total, state);
        });
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(SURFACE);
        p.setBorder(new RoundedBorder(8, BORDER_CLR));
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(TEXT_SEC);
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        return l;
    }

    private JLabel makeLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_SEC);
        return l;
    }

    private JLabel smallMeta(String t) {
        JLabel l = new JLabel(t);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_SEC);
        return l;
    }

    private JTextField buildField(String val) {
        JTextField f = new JTextField(val, 4) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(FONT_LABEL);
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setBackground(SURFACE);
        f.setForeground(TEXT_PRI);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
                new RoundedBorder(8, BORDER_CLR),
                new EmptyBorder(4, 6, 4, 6)));
        f.setOpaque(false);
        return f;
    }

    private JButton buildRunButton() {
        JButton btn = new JButton("Jalankan") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? ACCENT_DARK
                        : getModel().isRollover() ? ACCENT_DARK.brighter()
                        : ACCENT;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> mulaiProses());
        return btn;
    }

    static class ThreadCard extends JPanel {
        private final JLabel lblName, lblState;
        private final MiniBar bar;
        private final Color accent;

        ThreadCard(String name, Color accent) {
            this.accent = accent;
            setLayout(new BorderLayout(6, 0));
            setBackground(Color.WHITE);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setBorder(new CompoundBorder(
                    new RoundedBorder(6, new Color(230, 228, 224)),
                    new EmptyBorder(7, 10, 7, 10)));
            setOpaque(true);

            JPanel left = new JPanel(new BorderLayout(6, 0));
            left.setBackground(Color.WHITE);
            JLabel dot = new JLabel("●") {
                { setForeground(accent); setFont(new Font("SansSerif", Font.PLAIN, 10)); }
            };
            lblName = new JLabel(name);
            lblName.setFont(new Font("Monospaced", Font.BOLD, 11));
            lblName.setForeground(new Color(40, 38, 35));
            left.add(dot, BorderLayout.WEST);
            left.add(lblName, BorderLayout.CENTER);

            lblState = new JLabel("WAITING");
            lblState.setFont(new Font("SansSerif", Font.BOLD, 10));
            lblState.setForeground(new Color(160, 156, 148));
            lblState.setHorizontalAlignment(SwingConstants.RIGHT);
            lblState.setPreferredSize(new Dimension(75, 14));

            bar = new MiniBar(accent);

            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(Color.WHITE);
            top.add(left, BorderLayout.WEST);
            top.add(lblState, BorderLayout.EAST);

            add(top, BorderLayout.NORTH);
            add(bar, BorderLayout.SOUTH);
        }

        void update(int done, int total, String state) {
            bar.setProgress(total == 0 ? 0 : (double) done / total);
            lblState.setText(state);
            Color stateColor = switch (state) {
                case "RUNNING"   -> accent;
                case "DONE"      -> new Color(90, 185, 140);
                case "INTERRUPTED" -> new Color(220, 165, 60);
                default          -> new Color(160, 156, 148);
            };
            lblState.setForeground(stateColor);
            repaint();
        }
    }

    static class MiniBar extends JPanel {
        private double progress = 0;
        private final Color color;
        MiniBar(Color c) {
            this.color = c;
            setPreferredSize(new Dimension(0, 4));
            setBackground(new Color(240, 238, 234));
            setOpaque(false);
        }
        void setProgress(double p) { this.progress = Math.min(1, Math.max(0, p)); repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(235, 233, 229));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
            if (progress > 0) {
                g2.setColor(color);
                int w = (int)(getWidth() * progress);
                g2.fillRoundRect(0, 0, w, getHeight(), 4, 4);
            }
            g2.dispose();
        }
    }

    static class SmoothProgressBar extends JPanel {
        private int max = 100, current = 0;
        private double visual = 0;
        private Timer anim;

        SmoothProgressBar() {
            setOpaque(false);
            anim = new Timer(16, e -> {
                visual += (current - visual) * 0.12;
                if (Math.abs(current - visual) < 0.3) visual = current;
                repaint();
            });
            anim.start();
        }
        void reset(int newMax) { max = newMax; current = 0; visual = 0; }
        void increment() { current = Math.min(current + 1, max); }
        void complete() { current = max; }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight(), w = getWidth(), r = h / 2;
            g2.setColor(new Color(230, 228, 224));
            g2.fillRoundRect(0, 0, w, h, r * 2, r * 2);
            if (max > 0) {
                double pct = visual / max;
                int fw = (int)(w * pct);
                if (fw > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, ACCENT, fw, 0, ACCENT_DARK);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, Math.max(fw, r * 2), h, r * 2, r * 2);
                }
                String pctStr = String.format("%d%%", (int)(pct * 100));
                g2.setFont(new Font("SansSerif", Font.BOLD, h - 2));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(pctStr)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(pct > 0.5 ? Color.WHITE : TEXT_PRI);
                g2.drawString(pctStr, tx, ty);
            }
            g2.dispose();
        }
    }

    static class PillCheckBox extends JToggleButton {
        private final Color activeColor;
        PillCheckBox(String text, Color color) {
            super(text, false);
            this.activeColor = color;
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(5, 12, 5, 12));
        }
        public boolean isSelected() { return super.isSelected(); }
        public void setSelected(boolean s) { super.setSelected(s); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean on = isSelected();
            g2.setColor(on ? activeColor : new Color(230, 228, 224));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.setColor(on ? Color.WHITE : TEXT_SEC);
            FontMetrics fm = g2.getFontMetrics(getFont());
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setFont(getFont());
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        RoundedBorder(int r, Color c) { this.radius = r; this.color = c; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius / 2, radius / 2, radius / 2, radius / 2); }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new JendelaUtama().setVisible(true));
    }
}