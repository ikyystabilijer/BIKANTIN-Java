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
 * Form Admin untuk Kelola Menu (CRUD) dan Manajemen Pesanan Masuk (Ubah Status).
 * 
 * @author ikyys
 */
public class FormAdmin extends javax.swing.JFrame {
    
    private static final Logger logger = Logger.getLogger(FormAdmin.class.getName());
    private int selectedMenuId = -1;
    private int selectedPesananId = -1;

    /**
     * Creates new form FormAdmin
     */
    public FormAdmin() {
        initComponents();
        setTitle("BI-KANTIN - Dashboard Admin");
        setLocationRelativeTo(null);
        applyCustomStyles();
        tampilMenu();
        tampilPesanan();
        resetMenuInput();
    }

    /**
     * Mengatur tema warna dan estetika tampilan FormAdmin
     */
    private void applyCustomStyles() {
        // Background Form
        getContentPane().setBackground(new java.awt.Color(248, 250, 252));

        // Judul Utama
        jLabel5.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        jLabel5.setForeground(new java.awt.Color(15, 23, 42)); // Slate 900

        // Sub-judul Section
        java.awt.Font sectionFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14);
        java.awt.Color sectionColor = new java.awt.Color(30, 41, 59); // Slate 800
        jLabel8.setFont(sectionFont);
        jLabel8.setForeground(sectionColor);
        jLabel7.setFont(sectionFont);
        jLabel7.setForeground(sectionColor);

        // Label Input Form
        java.awt.Font labelFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        java.awt.Color labelColor = new java.awt.Color(71, 85, 105); // Slate 600
        jLabel1.setFont(labelFont);
        jLabel1.setForeground(labelColor);
        jLabel2.setFont(labelFont);
        jLabel2.setForeground(labelColor);
        jLabel3.setFont(labelFont);
        jLabel3.setForeground(labelColor);
        jLabel4.setFont(labelFont);
        jLabel4.setForeground(labelColor);

        // Text input & ComboBox
        java.awt.Font inputFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12);
        txtNamaMenu.setFont(inputFont);
        txtHarga.setFont(inputFont);
        cbKategori.setFont(inputFont);
        cbStatus.setFont(inputFont);

        // Tombol Aksi
        styleButton(btnSimpanMenu, new java.awt.Color(22, 163, 74));  // Emerald Green
        styleButton(btnEditMenu, new java.awt.Color(37, 99, 235));    // Royal Blue
        styleButton(btnHapusMenu, new java.awt.Color(220, 38, 38));   // Crimson Red
        styleButton(btnReset, new java.awt.Color(100, 116, 139));     // Slate Gray
        styleButton(btnUbahStatus, new java.awt.Color(124, 58, 237)); // Modern Violet
        styleButton(btnRefresh, new java.awt.Color(13, 148, 136));    // Teal
        styleButton(btnLogout, new java.awt.Color(225, 29, 72));      // Rose Red

        // Inisialisasi tampilan tabel
        styleTable(tblMenu, jScrollPane1);
        styleTable(tblPesanan, jScrollPane2);
    }

    private void styleButton(javax.swing.JButton btn, java.awt.Color bgColor) {
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void styleTable(javax.swing.JTable table, javax.swing.JScrollPane scrollPane) {
        table.setRowHeight(28);
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
    }

    /**
     * Menampilkan daftar menu makanan dan minuman pada tblMenu
     */
    public void tampilMenu() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Nama Menu", "Harga", "Kategori"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT id, nama_menu, harga, kategori FROM menu ORDER BY id DESC";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama_menu"),
                    rs.getInt("harga"),
                    rs.getString("kategori")
                });
            }

            tblMenu.setModel(model);
            styleTable(tblMenu, jScrollPane1);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal memuat data menu", e);
            JOptionPane.showMessageDialog(this, "Gagal memuat menu: " + e.getMessage());
        }
    }

    /**
     * Menampilkan daftar pesanan masuk dari seluruh siswa pada tblPesanan
     */
    public void tampilPesanan() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Nama Siswa", "Menu", "Jumlah", "Total", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT p.id, u.username, m.nama_menu, p.jumlah, p.total_harga, p.status "
                   + "FROM pesanan p "
                   + "JOIN users u ON p.user_id = u.id "
                   + "JOIN menu m ON p.menu_id = m.id "
                   + "ORDER BY p.id DESC";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("nama_menu"),
                    rs.getInt("jumlah"),
                    rs.getInt("total_harga"),
                    rs.getString("status")
                });
            }

            tblPesanan.setModel(model);
            styleTable(tblPesanan, jScrollPane2);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal memuat data pesanan", e);
            JOptionPane.showMessageDialog(this, "Gagal memuat pesanan: " + e.getMessage());
        }
    }

    /**
     * Mengosongkan form input menu
     */
    private void resetMenuInput() {
        selectedMenuId = -1;
        txtNamaMenu.setText("");
        txtHarga.setText("");
        cbKategori.setSelectedIndex(0);
        tblMenu.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNamaMenu = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbKategori = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cbStatus = new javax.swing.JComboBox<>();
        btnSimpanMenu = new javax.swing.JButton();
        btnEditMenu = new javax.swing.JButton();
        btnHapusMenu = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnUbahStatus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMenu = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPesanan = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BI-KANTIN - Dashboard Admin");

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel5.setText("PANEL ADMIN - KANTIN ONLINE (BI-KANTIN)");

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        btnRefresh.setText("Refresh Data");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        jLabel8.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        jLabel8.setText("Kelola Menu:");

        jLabel7.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        jLabel7.setText("Daftar Pesanan Masuk (Siswa):");

        jLabel3.setText("Nama Menu:");

        jLabel1.setText("Harga (Rp):");

        jLabel2.setText("Kategori:");

        cbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Makanan", "Minuman", "Snack" }));

        jLabel4.setText("Ubah Status Pesanan:");

        cbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Menunggu Konfirmasi", "Sedang Dimasak", "Siap Diambil", "Selesai", "Lunas" }));

        btnSimpanMenu.setText("Simpan Menu");
        btnSimpanMenu.addActionListener(this::btnSimpanMenuActionPerformed);

        btnEditMenu.setText("Edit Menu");
        btnEditMenu.addActionListener(this::btnEditMenuActionPerformed);

        btnHapusMenu.setText("Hapus Menu");
        btnHapusMenu.addActionListener(this::btnHapusMenuActionPerformed);

        btnReset.setText("Reset Form");
        btnReset.addActionListener(this::btnResetActionPerformed);

        btnUbahStatus.setText("Ubah Status");
        btnUbahStatus.addActionListener(this::btnUbahStatusActionPerformed);

        tblMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama Menu", "Harga", "Kategori"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMenuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMenu);

        tblPesanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama Siswa", "Menu", "Jumlah", "Total", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPesanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPesananMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPesanan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtNamaMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addComponent(cbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)
                                    .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpanMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEditMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnHapusMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnUbahStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(btnLogout)
                    .addComponent(btnRefresh))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNamaMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSimpanMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEditMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHapusMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUbahStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanMenuActionPerformed
        String nama = txtNamaMenu.getText().trim();
        String hargaText = txtHarga.getText().trim();
        String kategori = cbKategori.getSelectedItem().toString();

        if (nama.isEmpty() || hargaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama menu dan harga wajib diisi!");
            return;
        }

        try {
            int harga = Integer.parseInt(hargaText);
            if (harga <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!");
                return;
            }

            String sql = "INSERT INTO menu (nama_menu, harga, kategori) VALUES (?, ?, ?)";

            try (Connection c = Koneksi.getKoneksi();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, nama);
                ps.setInt(2, harga);
                ps.setString(3, kategori);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Menu baru berhasil disimpan!");
            }

            resetMenuInput();
            tampilMenu();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka bulat!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan menu: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSimpanMenuActionPerformed

    private void btnEditMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditMenuActionPerformed
        if (selectedMenuId == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik menu pada tabel yang ingin diedit terlebih dahulu!");
            return;
        }

        String nama = txtNamaMenu.getText().trim();
        String hargaText = txtHarga.getText().trim();
        String kategori = cbKategori.getSelectedItem().toString();

        if (nama.isEmpty() || hargaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama menu dan harga wajib diisi!");
            return;
        }

        try {
            int harga = Integer.parseInt(hargaText);
            if (harga <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!");
                return;
            }

            String sql = "UPDATE menu SET nama_menu=?, harga=?, kategori=? WHERE id=?";

            try (Connection c = Koneksi.getKoneksi();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, nama);
                ps.setInt(2, harga);
                ps.setString(3, kategori);
                ps.setInt(4, selectedMenuId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Menu berhasil diperbarui!");
            }

            resetMenuInput();
            tampilMenu();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka bulat!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui menu: " + e.getMessage());
        }
    }//GEN-LAST:event_btnEditMenuActionPerformed

    private void btnHapusMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusMenuActionPerformed
        if (selectedMenuId == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik menu pada tabel yang ingin dihapus terlebih dahulu!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus menu '" + txtNamaMenu.getText() + "'?",
            "Konfirmasi Hapus Menu",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM menu WHERE id=?";

            try (Connection c = Koneksi.getKoneksi();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, selectedMenuId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Menu berhasil dihapus!");
                resetMenuInput();
                tampilMenu();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus menu: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnHapusMenuActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetMenuInput();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnUbahStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahStatusActionPerformed
        int row = tblPesanan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik salah satu pesanan pada tabel pesanan terlebih dahulu!");
            return;
        }

        int idPesanan = (Integer) tblPesanan.getValueAt(row, 0);
        String statusDipilih = cbStatus.getSelectedItem().toString();

        String sql = "UPDATE pesanan SET status=? WHERE id=?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, statusDipilih);
            ps.setInt(2, idPesanan);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Status pesanan ID " + idPesanan + " berhasil diubah menjadi: " + statusDipilih);
            tampilPesanan();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + e.getMessage());
        }
    }//GEN-LAST:event_btnUbahStatusActionPerformed

    private void tblMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMenuMouseClicked
        int row = tblMenu.getSelectedRow();
        if (row != -1) {
            selectedMenuId = (Integer) tblMenu.getValueAt(row, 0);
            txtNamaMenu.setText(tblMenu.getValueAt(row, 1).toString());
            txtHarga.setText(tblMenu.getValueAt(row, 2).toString());
            cbKategori.setSelectedItem(tblMenu.getValueAt(row, 3).toString());
        }
    }//GEN-LAST:event_tblMenuMouseClicked

    private void tblPesananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPesananMouseClicked
        int row = tblPesanan.getSelectedRow();
        if (row != -1) {
            selectedPesananId = (Integer) tblPesanan.getValueAt(row, 0);
            String status = tblPesanan.getValueAt(row, 5).toString();
            cbStatus.setSelectedItem(status);
        }
    }//GEN-LAST:event_tblPesananMouseClicked

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        tampilMenu();
        tampilPesanan();
        JOptionPane.showMessageDialog(this, "Data menu dan pesanan berhasil diperbarui!");
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
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
        java.awt.EventQueue.invokeLater(() -> new FormAdmin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditMenu;
    private javax.swing.JButton btnHapusMenu;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSimpanMenu;
    private javax.swing.JButton btnUbahStatus;
    private javax.swing.JComboBox<String> cbKategori;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblMenu;
    private javax.swing.JTable tblPesanan;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtNamaMenu;
    // End of variables declaration//GEN-END:variables
}
