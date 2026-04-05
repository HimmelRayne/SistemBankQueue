import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ============================================================
 *  BANK QUEUE SYSTEM - Sistem Antrian Bank
 *  Struktur Data : Queue (Linked List)
 *  GUI           : Java Swing
 *  TTS           : OS native (Windows/Mac/Linux)
 * ============================================================
 */
public class BankQueueGUI extends JFrame {

    private final Queue queue = new Queue();

    private static final Color BG_DARK     = new Color(10,  15,  30);
    private static final Color BG_CARD     = new Color(18,  25,  45);
    private static final Color ACCENT_BLUE = new Color(30, 120, 255);
    private static final Color ACCENT_CYAN = new Color(0,  210, 210);
    private static final Color ACCENT_GOLD = new Color(255,185,   0);
    private static final Color TEXT_WHITE  = new Color(230,235, 255);
    private static final Color TEXT_GRAY   = new Color(130,145, 175);
    private static final Color SUCCESS     = new Color(40, 200, 120);
    private static final Color DANGER      = new Color(255, 70,  70);

    private JPanel panelAntrian;
    private JLabel lblNomor, lblNama, lblJumlah, lblStatus;

    public BankQueueGUI() {
        buildUI();
    }

    private void buildUI() {
        setTitle("Bank Queue System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 660);
        setMinimumSize(new Dimension(780, 560));
        setLocationRelativeTo(null);

        JPanel root = gradientPanel(BG_DARK, new Color(5, 10, 30));
        root.setLayout(new BorderLayout());
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
        setVisible(true);
    }

    // ── Header ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,new Color(20,40,100),getWidth(),0,new Color(8,18,55)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(ACCENT_BLUE); g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(0, 68));
        p.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel left = new JPanel(); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); left.setOpaque(false);
        left.add(Box.createVerticalGlue());
        left.add(lbl("BANK QUEUE SYSTEM", new Font("Courier New", Font.BOLD, 21), ACCENT_CYAN));
        left.add(lbl("Sistem Antrian Digital  •  Linked List Queue", new Font("SansSerif", Font.PLAIN, 12), TEXT_GRAY));
        left.add(Box.createVerticalGlue());

        lblJumlah = lbl("0 Antrian", new Font("Courier New", Font.BOLD, 16), ACCENT_GOLD);
        lblJumlah.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(left, BorderLayout.WEST);
        p.add(lblJumlah, BorderLayout.EAST);
        return p;
    }

    // ── Body ─────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12,20,8,20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.insets = new Insets(8,8,8,8);

        g.gridx=0; g.gridy=0; g.weightx=0.40; g.weighty=1; p.add(buildLeft(), g);
        g.gridx=1; g.weightx=0.60;             p.add(buildRight(), g);
        return p;
    }

    private JPanel buildLeft() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false);
        p.add(buildCalledCard()); p.add(Box.createVerticalStrut(14));
        JButton[] btns = {
            btn("➕  Ambil Antrian",   ACCENT_BLUE, Color.WHITE, e -> ambilAntrian()),
            btn("📢  Panggil Antrian", ACCENT_CYAN, BG_DARK,    e -> panggilAntrian()),
            btn("🔄  Reset Antrian",   new Color(40,46,70), TEXT_GRAY, e -> resetAntrian())
        };
        for (JButton b : btns) { b.setAlignmentX(Component.CENTER_ALIGNMENT); p.add(b); p.add(Box.createVerticalStrut(10)); }
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel buildCalledCard() {
        JPanel card = roundedCard(BG_CARD, ACCENT_BLUE, 18);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20,16,20,16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;

        g.gridy=0; card.add(lbl("SEDANG DIPANGGIL", new Font("Courier New",Font.BOLD,11), TEXT_GRAY, SwingConstants.CENTER), g);
        lblNomor = lbl("—", new Font("Courier New",Font.BOLD,72), ACCENT_CYAN, SwingConstants.CENTER);
        g.gridy=1; card.add(lblNomor, g);
        lblNama = lbl("Belum ada", new Font("SansSerif",Font.PLAIN,15), TEXT_WHITE, SwingConstants.CENTER);
        g.gridy=2; card.add(lblNama, g);
        return card;
    }

    private JPanel buildRight() {
        JPanel card = roundedCard(BG_CARD, new Color(40,60,120), 18);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16,16,16,16));

        JLabel t = lbl("DAFTAR ANTRIAN", new Font("Courier New",Font.BOLD,13), ACCENT_GOLD);
        t.setBorder(new EmptyBorder(0,0,10,0));
        card.add(t, BorderLayout.NORTH);

        panelAntrian = new JPanel();
        panelAntrian.setLayout(new BoxLayout(panelAntrian, BoxLayout.Y_AXIS));
        panelAntrian.setOpaque(false);

        JScrollPane sc = new JScrollPane(panelAntrian);
        sc.setOpaque(false); sc.getViewport().setOpaque(false);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getVerticalScrollBar().setUnitIncrement(14);
        sc.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        card.add(sc, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        p.setBorder(new EmptyBorder(4,20,10,20));
        lblStatus = lbl("Sistem siap. Silahkan ambil nomor antrian.", new Font("SansSerif",Font.ITALIC,12), TEXT_GRAY);
        JLabel ver = lbl("v1.0  •  Queue (Linked List)  •  Java Swing", new Font("Courier New",Font.PLAIN,11), new Color(50,60,90));
        p.add(lblStatus, BorderLayout.WEST); p.add(ver, BorderLayout.EAST);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    //  AKSI
    // ══════════════════════════════════════════════════════════
    private void ambilAntrian() {
        String nama = JOptionPane.showInputDialog(this, "Masukkan nama Anda:", "Ambil Nomor Antrian", JOptionPane.PLAIN_MESSAGE);
        if (nama == null) return;
        nama = nama.trim();
        if (nama.isEmpty()) { setStatus("❌  Nama tidak boleh kosong.", DANGER); return; }
        int nomor = queue.enqueue(nama);
        setStatus("✅  Nomor " + nomor + " – " + nama + " berhasil didaftarkan.", SUCCESS);
        refresh();
    }

    private void panggilAntrian() {
        if (queue.isEmpty()) {
            setStatus("⚠️  Antrian kosong.", ACCENT_GOLD);
            lblNomor.setText("—"); lblNama.setText("Antrian kosong"); return;
        }
        Node n = queue.dequeue();
        lblNomor.setText(String.valueOf(n.nomorAntrian));
        lblNama.setText(n.nama);
        setStatus("📢  Memanggil nomor " + n.nomorAntrian + " – " + n.nama, ACCENT_CYAN);
        flashNumber();
        speak(n.nomorAntrian, n.nama);
        refresh();
    }

    private void resetAntrian() {
        int ok = JOptionPane.showConfirmDialog(this, "Reset semua antrian?\nTindakan ini tidak bisa dibatalkan.",
                "Konfirmasi Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        queue.reset();
        lblNomor.setText("—"); lblNama.setText("Belum ada");
        refresh();
        setStatus("🔄  Antrian direset.", TEXT_GRAY);
    }

    // ── Refresh daftar antrian ────────────────────────────────
    private void refresh() {
        panelAntrian.removeAll();
        Node[] nodes = queue.getAllNodes();
        if (nodes.length == 0) {
            JLabel e = lbl("Antrian kosong", new Font("SansSerif",Font.ITALIC,13), TEXT_GRAY, SwingConstants.CENTER);
            e.setAlignmentX(Component.CENTER_ALIGNMENT);
            e.setBorder(new EmptyBorder(30,0,0,0));
            panelAntrian.add(e);
        } else {
            for (int i=0;i<nodes.length;i++) { panelAntrian.add(buildRow(i+1, nodes[i])); panelAntrian.add(Box.createVerticalStrut(6)); }
        }
        panelAntrian.revalidate(); panelAntrian.repaint();
        int sz = nodes.length;
        lblJumlah.setText(sz + (sz==1?" Antrian":" Antrian"));
    }

    private JPanel buildRow(int urutan, Node node) {
        boolean first = (urutan==1);
        JPanel row = new JPanel(new BorderLayout(12,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(first ? new Color(30,60,120) : new Color(25,32,55));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                if (first) { g2.setColor(ACCENT_BLUE); g2.setStroke(new BasicStroke(1.2f)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10); }
                g2.dispose();
            }
        };
        row.setOpaque(false); row.setBorder(new EmptyBorder(8,12,8,12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE,54));

        JLabel lNo = lbl(String.format("%03d",node.nomorAntrian), new Font("Courier New",Font.BOLD,20), first?ACCENT_CYAN:ACCENT_BLUE);
        lNo.setPreferredSize(new Dimension(58,0));

        JPanel info = new JPanel(); info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS)); info.setOpaque(false);
        info.add(lbl(node.nama, new Font("SansSerif",Font.BOLD,13), TEXT_WHITE));
        info.add(lbl(first?"▶  Berikutnya dipanggil":"Urutan ke-"+urutan, new Font("SansSerif",Font.PLAIN,11), first?ACCENT_GOLD:TEXT_GRAY));

        row.add(lNo, BorderLayout.WEST); row.add(info, BorderLayout.CENTER);
        return row;
    }

    private void flashNumber() {
        Color[] seq = {ACCENT_GOLD,ACCENT_CYAN,ACCENT_BLUE,ACCENT_GOLD,ACCENT_CYAN};
        final int[] i = {0};
        Timer t = new Timer(90, null);
        t.addActionListener(e -> { lblNomor.setForeground(i[0]<seq.length?seq[i[0]++]:ACCENT_CYAN); if(i[0]>=seq.length) t.stop(); });
        t.start();
    }

    // ── Text-to-Speech (OS native) ────────────────────────────
    private void speak(int nomor, String nama) {
        String text = "Nomor " + nomor + " " + nama + " silahkan menuju loket";
        String os = System.getProperty("os.name","").toLowerCase();
        new Thread(() -> {
            try {
                if (os.contains("win")) {
                    String script = "Add-Type -AssemblyName System.Speech; " +
                            "($s=New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" +
                            text.replace("'","") + "');";
                    new ProcessBuilder("powershell","-Command",script).redirectErrorStream(true).start().waitFor();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("say", text).start().waitFor();
                } else {
                    // Linux: coba espeak, fallback ke print
                    try { new ProcessBuilder("espeak","-v","id",text).start().waitFor(); }
                    catch (Exception ex) { System.out.println("[TTS] " + text); }
                }
            } catch (Exception ex) { System.out.println("[TTS] " + text); }
        }).start();
    }

    private void setStatus(String msg, Color c) { lblStatus.setText(msg); lblStatus.setForeground(c); }

    // ══════════════════════════════════════════════════════════
    //  FACTORY HELPERS
    // ══════════════════════════════════════════════════════════
    private static JLabel lbl(String t, Font f, Color c) { JLabel l=new JLabel(t); l.setFont(f); l.setForeground(c); return l; }
    private static JLabel lbl(String t, Font f, Color c, int align) { JLabel l=lbl(t,f,c); l.setHorizontalAlignment(align); return l; }

    private static JButton btn(String text, Color bg, Color fg, ActionListener al) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()?bg.darker():getModel().isRollover()?bg.brighter():bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif",Font.BOLD,14)); b.setForeground(fg);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
        b.setPreferredSize(new Dimension(220,48));
        b.addActionListener(al); return b;
    }

    private static JPanel roundedCard(Color bg, Color border, int r) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,r,r);
                g2.setColor(border); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,r,r); g2.dispose();
            }
        };
    }

    private static JPanel gradientPanel(Color c1, Color c2) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setPaint(new GradientPaint(0,0,c1,getWidth(),getHeight(),c2));
                g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose();
            }
        };
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(BankQueueGUI::new);
    }
}
