/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Form;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.Koneksi;

/**
 * Form Siswa untuk memilih menu, memesan makanan/minuman,
 * dan memantau status pesanan secara real-time.
 * 
 * @author ikyys
 */
public class FormSiswa extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FormSiswa.class.getName());

    private int userId = 2;
    private String username = "Siswa";
    private javax.swing.Timer autoRefreshTimer;

    /**
     * Item pembungkus untuk data menu di JComboBox
     */
    public static class MenuItem {
        private final int id;
        private final String nama;
        private final int harga;
        private final String kategori;

        public MenuItem(int id, String nama, int harga, String kategori) {
            this.id = id;
            this.nama = nama;
            this.harga = harga;
            this.kategori = kategori;
        }

        public int getId() {
            return id;
        }

        public String getNama() {
            return nama;
        }

        public int getHarga() {
            return harga;
        }

        public String getKategori() {
            return kategori;
        }

        @Override
        public String toString() {
            return nama + " (" + kategori + ") - Rp " + harga;
        }
    }

    /**
     * Creates new form FormSiswa (Default constructor)
     */
    public FormSiswa() {
        this(2, "Siswa");
    }

    /**
     * Constructor dengan data login siswa
     * 
     * @param userId ID siswa dari tabel users
     * @param username Nama siswa dari tabel users
     */
    public FormSiswa(int userId, String username) {
        this.userId = userId;
        this.username = username;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("BI-KANTIN - Dashboard Siswa (" + username + ")");
        lblUser.setText("Selamat Datang, " + username + "!");
        applyCustomStyles();

        // Double-click baris tabel untuk langsung mencetak struk
        tblPesanan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tblPesanan.getSelectedRow() != -1) {
                    btnCetakStrukActionPerformed(null);
                }
            }
        });

        loadMenu();
        tampilPesananSiswa();

        // Timer untuk update status secara real-time (setiap 3 detik)
        autoRefreshTimer = new javax.swing.Timer(3000, e -> tampilPesananSiswa());
        autoRefreshTimer.start();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
                    autoRefreshTimer.stop();
                }
            }
        });
    }

    /**
     * Mengatur tema warna dan estetika tampilan FormSiswa
     */
    private void applyCustomStyles() {
        // Background Form
        getContentPane().setBackground(new java.awt.Color(248, 250, 252));

        // Judul Utama & Greeting
        lblJudul.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblJudul.setForeground(new java.awt.Color(15, 23, 42)); // Slate 900

        lblUser.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblUser.setForeground(new java.awt.Color(37, 99, 235)); // Royal Blue

        // Label Form
        java.awt.Font labelFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        java.awt.Color labelColor = new java.awt.Color(71, 85, 105); // Slate 600
        jLabel2.setFont(labelFont);
        jLabel2.setForeground(labelColor);
        jLabel3.setFont(labelFont);
        jLabel3.setForeground(labelColor);
        jLabel4.setFont(labelFont);
        jLabel4.setForeground(labelColor);
        jLabel5.setFont(labelFont);
        jLabel5.setForeground(labelColor);

        jLabel6.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jLabel6.setForeground(new java.awt.Color(30, 41, 59));

        // Input & Controls
        cbMenu.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        cbMenu.setBackground(java.awt.Color.WHITE);

        spnJumlah.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));

        txtHarga.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        txtHarga.setBackground(new java.awt.Color(241, 245, 249));

        // Total bayar field dibuat lebih eye-catching (hijau segar)
        txtTotal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
        txtTotal.setForeground(new java.awt.Color(22, 163, 74));
        txtTotal.setBackground(new java.awt.Color(240, 253, 244));

        // Status banner terkini
        lblStatusTerkini.setOpaque(true);
        lblStatusTerkini.setBackground(new java.awt.Color(238, 242, 255));
        lblStatusTerkini.setForeground(new java.awt.Color(67, 56, 202));
        lblStatusTerkini.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblStatusTerkini.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new java.awt.Color(199, 210, 254), 1, true),
            javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        // Tombol
        styleButton(btnPesan, new java.awt.Color(22, 163, 74), 14);  // Emerald Green
        styleButton(btnCetakStruk, new java.awt.Color(79, 70, 229), 12); // Royal Indigo (Cetak Struk)
        styleButton(btnRefresh, new java.awt.Color(13, 148, 136), 12); // Teal
        styleButton(btnLogout, new java.awt.Color(225, 29, 72), 12);    // Rose Red

        // Inisialisasi tabel
        styleTable(tblPesanan, jScrollPane1);
    }

    private void styleButton(javax.swing.JButton btn, java.awt.Color bgColor, int fontSize) {
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, fontSize));
        btn.setBackground(bgColor);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void styleTable(javax.swing.JTable table, javax.swing.JScrollPane scrollPane) {
        table.setRowHeight(30);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        table.setSelectionBackground(new java.awt.Color(219, 234, 254));
        table.setSelectionForeground(new java.awt.Color(15, 23, 42));
        table.setGridColor(new java.awt.Color(241, 245, 249));

        if (table.getTableHeader() != null) {
            table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            table.getTableHeader().setBackground(new java.awt.Color(226, 232, 240));
            table.getTableHeader().setForeground(new java.awt.Color(30, 41, 59));
            table.getTableHeader().setReorderingAllowed(false);
        }

        if (scrollPane != null) {
            scrollPane.getViewport().setBackground(java.awt.Color.WHITE);
            scrollPane.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 232, 240), 1));
        }

        // Render warna status dan perataan kolom tabel pesanan
        if (table.getColumnCount() >= 5) {
            // Kolom ID (Center)
            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(0).setPreferredWidth(45);

            // Kolom Jumlah (Center)
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(2).setPreferredWidth(55);

            // Kolom Total Harga (Right & Rupiah format)
            table.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                        javax.swing.JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                    if (value instanceof Number) {
                        setText("Rp " + String.format("%,d", ((Number) value).longValue()).replace(',', '.'));
                    }
                    return this;
                }
            });

            // Kolom Status (Badge Berwarna Modern)
            table.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                        javax.swing.JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    javax.swing.JLabel lbl = (javax.swing.JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                    lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    String val = value != null ? value.toString() : "";
                    String lower = val.toLowerCase();

                    if (!isSelected) {
                        lbl.setOpaque(true);
                        if (lower.contains("selesai") || lower.contains("siap")) {
                            lbl.setBackground(new java.awt.Color(220, 252, 231)); // Emerald-100
                            lbl.setForeground(new java.awt.Color(21, 128, 61));   // Emerald-700
                            lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
                        } else if (lower.contains("proses")) {
                            lbl.setBackground(new java.awt.Color(224, 231, 255)); // Indigo-100
                            lbl.setForeground(new java.awt.Color(67, 56, 202));   // Indigo-700
                            lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
                        } else if (lower.contains("batal")) {
                            lbl.setBackground(new java.awt.Color(254, 226, 226)); // Red-100
                            lbl.setForeground(new java.awt.Color(185, 28, 28));   // Red-700
                            lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
                        } else {
                            // Menunggu Konfirmasi / Pending
                            lbl.setBackground(new java.awt.Color(254, 243, 199)); // Amber-100
                            lbl.setForeground(new java.awt.Color(180, 83, 9));    // Amber-700
                            lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
                        }
                    }
                    return lbl;
                }
            });
        }
    }

    /**
     * Mengambil daftar menu dari database untuk JComboBox
     */
    public void loadMenu() {
        cbMenu.removeAllItems();
        String sql = "SELECT id, nama_menu, harga, kategori FROM menu ORDER BY nama_menu ASC";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MenuItem item = new MenuItem(
                    rs.getInt("id"),
                    rs.getString("nama_menu"),
                    rs.getInt("harga"),
                    rs.getString("kategori")
                );
                cbMenu.addItem(item);
            }

            updateHargaDanTotal();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal memuat menu", e);
        }
    }

    /**
     * Menghitung total harga berdasarkan menu dan jumlah yang dipilih
     */
    private void updateHargaDanTotal() {
        MenuItem selected = (MenuItem) cbMenu.getSelectedItem();
        if (selected != null) {
            int harga = selected.getHarga();
            int jumlah = ((Number) spnJumlah.getValue()).intValue();
            int total = harga * jumlah;

            txtHarga.setText(String.valueOf(harga));
            txtTotal.setText(String.valueOf(total));
        } else {
            txtHarga.setText("0");
            txtTotal.setText("0");
        }
    }

    /**
     * Menampilkan riwayat dan status pesanan milik siswa yang sedang login
     */
    public void tampilPesananSiswa() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Menu", "Jumlah", "Total", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT p.id, m.nama_menu, p.jumlah, p.total_harga, p.status "
                   + "FROM pesanan p "
                   + "JOIN menu m ON p.menu_id = m.id "
                   + "WHERE p.user_id = ? "
                   + "ORDER BY p.id DESC";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String menu = rs.getString("nama_menu");
                    int jumlah = rs.getInt("jumlah");
                    int total = rs.getInt("total_harga");
                    String status = rs.getString("status");

                    if (first) {
                        lblStatusTerkini.setText("Status Pesanan Terkini: " + status + " (" + menu + ")");
                        updateBannerColor(status);
                        first = false;
                    }

                    model.addRow(new Object[]{id, menu, jumlah, total, status});
                }

                if (first) {
                    lblStatusTerkini.setText("Status Pesanan Terkini: Belum ada pesanan");
                    updateBannerColor(null);
                }
            }

            tblPesanan.setModel(model);
            styleTable(tblPesanan, jScrollPane1);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal memuat pesanan siswa", e);
        }
    }

    /**
     * Memperbarui warna banner status pesanan terkini secara dinamis
     */
    private void updateBannerColor(String status) {
        if (status == null) {
            lblStatusTerkini.setBackground(new java.awt.Color(241, 245, 249));
            lblStatusTerkini.setForeground(new java.awt.Color(100, 116, 139));
            lblStatusTerkini.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(203, 213, 225), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            return;
        }

        String lower = status.toLowerCase();
        if (lower.contains("selesai") || lower.contains("siap")) {
            lblStatusTerkini.setBackground(new java.awt.Color(236, 253, 245));
            lblStatusTerkini.setForeground(new java.awt.Color(5, 150, 105));
            lblStatusTerkini.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(167, 243, 208), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        } else if (lower.contains("proses")) {
            lblStatusTerkini.setBackground(new java.awt.Color(238, 242, 255));
            lblStatusTerkini.setForeground(new java.awt.Color(67, 56, 202));
            lblStatusTerkini.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(199, 210, 254), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        } else if (lower.contains("batal")) {
            lblStatusTerkini.setBackground(new java.awt.Color(254, 242, 242));
            lblStatusTerkini.setForeground(new java.awt.Color(220, 38, 38));
            lblStatusTerkini.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(254, 202, 202), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        } else {
            lblStatusTerkini.setBackground(new java.awt.Color(254, 252, 232));
            lblStatusTerkini.setForeground(new java.awt.Color(161, 98, 7));
            lblStatusTerkini.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(254, 240, 138), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblJudul = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cbMenu = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        spnJumlah = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        btnPesan = new javax.swing.JButton();
        lblStatusTerkini = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnCetakStruk = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPesanan = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BI-KANTIN - Dashboard Siswa");

        lblJudul.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        lblJudul.setText("DASHBOARD PEMESANAN SISWA - BI-KANTIN");

        lblUser.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblUser.setText("Selamat Datang, Siswa!");

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        jLabel2.setText("Pilih Menu:");

        cbMenu.addActionListener(this::cbMenuActionPerformed);

        jLabel3.setText("Harga Satuan (Rp):");

        txtHarga.setEditable(false);

        jLabel4.setText("Jumlah Beli:");

        spnJumlah.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));
        spnJumlah.addChangeListener(this::spnJumlahStateChanged);

        jLabel5.setText("Total Bayar (Rp):");

        txtTotal.setEditable(false);

        btnPesan.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        btnPesan.setText("Kirim Pesanan");
        btnPesan.addActionListener(this::btnPesanActionPerformed);

        lblStatusTerkini.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        lblStatusTerkini.setForeground(new java.awt.Color(0, 51, 153));
        lblStatusTerkini.setText("Status Pesanan Terkini: Belum ada pesanan");

        jLabel6.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel6.setText("Riwayat & Tracking Pesanan Saya:");

        btnCetakStruk.setText("Cetak Struk");
        btnCetakStruk.addActionListener(this::btnCetakStrukActionPerformed);

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        tblPesanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Menu", "Jumlah", "Total", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblPesanan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(cbMenu, 0, 230, Short.MAX_VALUE)
                            .addComponent(jLabel3)
                            .addComponent(txtHarga)
                            .addComponent(jLabel4)
                            .addComponent(spnJumlah)
                            .addComponent(jLabel5)
                            .addComponent(txtTotal)
                            .addComponent(btnPesan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCetakStruk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefresh))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                            .addComponent(lblStatusTerkini, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(25, 25, 25))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblJudul)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblJudul)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUser)
                    .addComponent(btnLogout))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblStatusTerkini))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnPesan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(btnCetakStruk)
                            .addComponent(btnRefresh))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMenuActionPerformed
        updateHargaDanTotal();
    }//GEN-LAST:event_cbMenuActionPerformed

    private void spnJumlahStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnJumlahStateChanged
        updateHargaDanTotal();
    }//GEN-LAST:event_spnJumlahStateChanged

    private void btnPesanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesanActionPerformed
        MenuItem item = (MenuItem) cbMenu.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih menu terlebih dahulu!");
            return;
        }

        int jumlah = ((Number) spnJumlah.getValue()).intValue();
        if (jumlah <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah pesanan minimal 1!");
            return;
        }

        int totalHarga = item.getHarga() * jumlah;

        String sql = "INSERT INTO pesanan (user_id, menu_id, jumlah, total_harga, status) "
                   + "VALUES (?, ?, ?, ?, 'Menunggu Konfirmasi')";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, item.getId());
            ps.setInt(3, jumlah);
            ps.setInt(4, totalHarga);

            ps.executeUpdate();

            int newPesananId = -1;
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    newPesananId = gk.getInt(1);
                }
            }

            spnJumlah.setValue(1);
            tampilPesananSiswa();

            String totalFormatted = String.format("%,d", totalHarga).replace(',', '.');
            int opsiCetak = JOptionPane.showConfirmDialog(this,
                    "Pesanan berhasil dikirim!\n\n"
                    + "Menu   : " + item.getNama() + "\n"
                    + "Jumlah : " + jumlah + "\n"
                    + "Total  : Rp " + totalFormatted + "\n\n"
                    + "Apakah Anda ingin mencetak struk sekarang?",
                    "Pesanan Berhasil",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (opsiCetak == JOptionPane.YES_OPTION) {
                if (newPesananId != -1) {
                    bukaDialogStruk(newPesananId);
                } else if (tblPesanan.getRowCount() > 0) {
                    int topId = Integer.parseInt(tblPesanan.getValueAt(0, 0).toString());
                    bukaDialogStruk(topId);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengirim pesanan: " + e.getMessage());
        }
    }//GEN-LAST:event_btnPesanActionPerformed

    private void btnCetakStrukActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblPesanan.getSelectedRow();
        if (selectedRow == -1) {
            if (tblPesanan.getRowCount() > 0) {
                // Pilih baris pesanan paling atas (terbaru) secara otomatis
                selectedRow = 0;
                tblPesanan.setRowSelectionInterval(0, 0);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Belum ada riwayat pesanan yang dapat dicetak struknya.\nSilakan lakukan pemesanan terlebih dahulu.",
                    "Perhatian", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            int pesananId = Integer.parseInt(tblPesanan.getValueAt(selectedRow, 0).toString());
            bukaDialogStruk(pesananId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Data ID pesanan tidak valid: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Membuka modal dialog cetak struk pembayaran siswa
     * 
     * @param pesananId ID pesanan yang akan dicetak struknya
     */
    public void bukaDialogStruk(int pesananId) {
        DialogCetakStruk dialog = new DialogCetakStruk(this, pesananId);
        dialog.setVisible(true);
    }

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadMenu();
        tampilPesananSiswa();
        JOptionPane.showMessageDialog(this, "Data status pesanan diperbarui!");
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
            autoRefreshTimer.stop();
        }
        this.dispose();
        new FormLogin().setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FormSiswa().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCetakStruk;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPesan;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<Object> cbMenu;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblStatusTerkini;
    private javax.swing.JLabel lblUser;
    private javax.swing.JSpinner spnJumlah;
    private javax.swing.JTable tblPesanan;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
