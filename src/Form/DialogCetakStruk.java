/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import koneksi.Koneksi;

/**
 * Dialog Cetak Struk Modern untuk Form Siswa BI-KANTIN.
 * Menampilkan rincian transaksi dalam format struk digital,
 * mendukung pencetakan langsung ke printer / PDF dan ekspor file teks.
 * 
 * @author ikyys
 */
public class DialogCetakStruk extends JDialog {

    private static final Logger logger = Logger.getLogger(DialogCetakStruk.class.getName());

    private int pesananId;
    private String namaSiswa = "Siswa";
    private String namaMenu = "-";
    private String kategori = "Makanan";
    private int hargaSatuan = 0;
    private int jumlah = 1;
    private int totalHarga = 0;
    private String statusPesanan = "Menunggu Konfirmasi";
    private String waktuTransaksi;

    private JEditorPane receiptPane;
    private JButton btnCetak;
    private JButton btnSimpanTxt;
    private JButton btnTutup;

    /**
     * Konstruktor utama: Mengambil rincian pesanan dari database berdasarkan pesananId
     * 
     * @param parent Frame induk (FormSiswa)
     * @param pesananId ID pesanan pada tabel pesanan
     */
    public DialogCetakStruk(Frame parent, int pesananId) {
        super(parent, "BI-KANTIN - Struk Pembayaran", true);
        this.pesananId = pesananId;
        this.waktuTransaksi = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        muatDataPesanan();
        initUI();
    }

    /**
     * Konstruktor fleksibel dengan data lengkap
     */
    public DialogCetakStruk(Frame parent, int pesananId, String namaSiswa, String namaMenu, 
                            String kategori, int hargaSatuan, int jumlah, int totalHarga, String statusPesanan) {
        super(parent, "BI-KANTIN - Struk Pembayaran", true);
        this.pesananId = pesananId;
        this.namaSiswa = namaSiswa != null ? namaSiswa : "Siswa";
        this.namaMenu = namaMenu != null ? namaMenu : "-";
        this.kategori = kategori != null ? kategori : "Makanan";
        this.hargaSatuan = hargaSatuan;
        this.jumlah = jumlah;
        this.totalHarga = totalHarga;
        this.statusPesanan = statusPesanan != null ? statusPesanan : "Menunggu Konfirmasi";
        this.waktuTransaksi = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        initUI();
    }

    /**
     * Memuat data pesanan lengkap dari database dengan JOIN ke tabel menu dan users
     */
    private void muatDataPesanan() {
        String sql = "SELECT p.id, p.jumlah, p.total_harga, p.status, "
                   + "m.nama_menu, m.kategori, m.harga, u.username "
                   + "FROM pesanan p "
                   + "JOIN menu m ON p.menu_id = m.id "
                   + "JOIN users u ON p.user_id = u.id "
                   + "WHERE p.id = ?";

        try (Connection c = Koneksi.getKoneksi();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, pesananId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.namaSiswa = rs.getString("username");
                    this.namaMenu = rs.getString("nama_menu");
                    this.kategori = rs.getString("kategori");
                    this.hargaSatuan = rs.getInt("harga");
                    this.jumlah = rs.getInt("jumlah");
                    this.totalHarga = rs.getInt("total_harga");
                    this.statusPesanan = rs.getString("status");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal memuat detail pesanan untuk struk: " + pesananId, e);
        }
    }

    /**
     * Menginisialisasi komponen antarmuka dialog struk dengan tema visual BI-KANTIN
     */
    private void initUI() {
        setSize(470, 680);
        setResizable(false);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(new Color(241, 245, 249)); // Slate 100
        setLayout(new BorderLayout());

        // 1. Header Panel
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(30, 41, 59)); // Slate 800
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel lblHeaderTitle = new JLabel("STRUK PEMBELIAN DIGITAL", SwingConstants.CENTER);
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeaderTitle.setForeground(Color.WHITE);

        JLabel lblHeaderSub = new JLabel("BI-KANTIN -- Bukti Transaksi Resmi Siswa", SwingConstants.CENTER);
        lblHeaderSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHeaderSub.setForeground(new Color(203, 213, 225)); // Slate 300

        pnlHeader.add(lblHeaderTitle, BorderLayout.NORTH);
        pnlHeader.add(lblHeaderSub, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. Center Panel: Kertas Struk
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(new Color(241, 245, 249));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel pnlCardStruk = new JPanel(new BorderLayout());
        pnlCardStruk.setBackground(Color.WHITE);
        pnlCardStruk.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        receiptPane = new JEditorPane();
        receiptPane.setContentType("text/html");
        receiptPane.setEditable(false);
        receiptPane.setBackground(Color.WHITE);
        receiptPane.setText(generateHtmlReceipt());
        receiptPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(receiptPane);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        pnlCardStruk.add(scrollPane, BorderLayout.CENTER);

        pnlCenter.add(pnlCardStruk, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // 3. Bottom Panel: Tombol Aksi
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        pnlBottom.setBackground(new Color(248, 250, 252));
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        btnCetak = new JButton("Cetak / PDF");
        styleDialogButton(btnCetak, new Color(22, 163, 74)); // Emerald Green
        btnCetak.addActionListener(e -> aksiCetak());

        btnSimpanTxt = new JButton("Simpan File");
        styleDialogButton(btnSimpanTxt, new Color(79, 70, 229)); // Indigo 600
        btnSimpanTxt.addActionListener(e -> aksiSimpanFile());

        btnTutup = new JButton("Tutup");
        styleDialogButton(btnTutup, new Color(100, 116, 139)); // Slate Gray
        btnTutup.addActionListener(e -> dispose());

        pnlBottom.add(btnCetak);
        pnlBottom.add(btnSimpanTxt);
        pnlBottom.add(btnTutup);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void styleDialogButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(125, 36));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    /**
     * Memformat angka ke format Rupiah
     */
    private String formatRp(int nominal) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
        return nf.format(nominal);
    }

    /**
     * Menghasilkan badge HTML berwarna sesuai status pesanan
     */
    private String getStatusHtmlBadge(String status) {
        if (status == null) status = "Menunggu Konfirmasi";
        String lower = status.toLowerCase();

        String bgColor;
        String textColor;

        if (lower.contains("selesai") || lower.contains("siap")) {
            bgColor = "#dcfce7"; // Light emerald
            textColor = "#15803d"; // Dark emerald
        } else if (lower.contains("proses")) {
            bgColor = "#e0e7ff"; // Light indigo
            textColor = "#4338ca"; // Indigo
        } else if (lower.contains("batal")) {
            bgColor = "#fee2e2"; // Light red
            textColor = "#b91c1c"; // Dark red
        } else {
            bgColor = "#fef3c7"; // Light amber
            textColor = "#b45309"; // Amber
        }

        return String.format(
            "<span style=\"background-color: %s; color: %s; padding: 2px 8px; font-weight: bold; font-size: 8pt;\">%s</span>",
            bgColor, textColor, status.toUpperCase()
        );
    }

    /**
     * Menghasilkan template HTML struk yang ramah render Swing
     */
    public String generateHtmlReceipt() {
        return "<html>"
             + "<head>"
             + "<style>"
             + "body { font-family: 'Segoe UI', Tahoma, sans-serif; margin: 10px; background-color: #ffffff; color: #1e293b; }"
             + ".header-title { font-size: 15pt; font-weight: bold; color: #0f172a; text-align: center; margin: 0; }"
             + ".header-sub { font-size: 8pt; color: #64748b; text-align: center; margin: 2px 0; }"
             + ".divider { border-top: 1px dashed #94a3b8; margin: 8px 0; }"
             + ".meta-label { font-size: 8pt; color: #64748b; }"
             + ".meta-val { font-size: 8pt; color: #1e293b; font-weight: bold; }"
             + ".table-head { font-size: 8pt; font-weight: bold; color: #334155; background-color: #f1f5f9; padding: 4px; }"
             + ".item-name { font-size: 9pt; font-weight: bold; color: #0f172a; }"
             + ".item-sub { font-size: 8pt; color: #64748b; font-style: italic; }"
             + ".total-label { font-size: 11pt; font-weight: bold; color: #0f172a; }"
             + ".total-val { font-size: 13pt; font-weight: bold; color: #16a34a; text-align: right; }"
             + ".footer-text { font-size: 8pt; color: #64748b; text-align: center; margin: 2px 0; }"
             + "</style>"
             + "</head>"
             + "<body>"
             + "<div class='header-title'>BI-KANTIN</div>"
             + "<div class='header-sub'>Kantin Digital Terpadu Sekolah</div>"
             + "<div class='header-sub'>Jl. Pendidikan No. 1 &bull; Telp. (021) 123456</div>"
             + "<div class='divider'></div>"
             + "<table width='100%' cellpadding='2' cellspacing='0'>"
             + "  <tr>"
             + "    <td class='meta-label' width='35%'>No. Pesanan</td>"
             + "    <td width='5%'>:</td>"
             + "    <td class='meta-val'>#BK-" + String.format("%04d", pesananId) + "</td>"
             + "  </tr>"
             + "  <tr>"
             + "    <td class='meta-label'>Tanggal/Waktu</td>"
             + "    <td>:</td>"
             + "    <td class='meta-val' style='font-weight: normal; color: #334155;'>" + waktuTransaksi + " WIB</td>"
             + "  </tr>"
             + "  <tr>"
             + "    <td class='meta-label'>Nama Siswa</td>"
             + "    <td>:</td>"
             + "    <td class='meta-val' style='color: #2563eb;'>" + namaSiswa + "</td>"
             + "  </tr>"
             + "  <tr>"
             + "    <td class='meta-label'>Status Pesanan</td>"
             + "    <td>:</td>"
             + "    <td>" + getStatusHtmlBadge(statusPesanan) + "</td>"
             + "  </tr>"
             + "</table>"
             + "<div class='divider'></div>"
             + "<table width='100%' cellpadding='3' cellspacing='0'>"
             + "  <tr class='table-head'>"
             + "    <td align='left'>Menu</td>"
             + "    <td align='center'>Qty</td>"
             + "    <td align='right'>Harga</td>"
             + "    <td align='right'>Subtotal</td>"
             + "  </tr>"
             + "  <tr>"
             + "    <td class='item-name'>" + namaMenu + "</td>"
             + "    <td align='center' style='font-size: 9pt;'>" + jumlah + "</td>"
             + "    <td align='right' style='font-size: 8pt; color: #475569;'>Rp " + formatRp(hargaSatuan) + "</td>"
             + "    <td align='right' style='font-size: 9pt; font-weight: bold; color: #0f172a;'>Rp " + formatRp(totalHarga) + "</td>"
             + "  </tr>"
             + "  <tr>"
             + "    <td colspan='4' class='item-sub'>Kategori: " + kategori + "</td>"
             + "  </tr>"
             + "</table>"
             + "<div class='divider'></div>"
             + "<table width='100%' cellpadding='3' cellspacing='0'>"
             + "  <tr>"
             + "    <td class='total-label'>TOTAL BAYAR</td>"
             + "    <td class='total-val'>Rp " + formatRp(totalHarga) + "</td>"
             + "  </tr>"
             + "</table>"
             + "<div class='divider'></div>"
             + "<div class='footer-text'>Harap tunjukkan struk ini saat mengambil pesanan di kantin.</div>"
             + "<div class='footer-text' style='font-weight: bold; color: #0f172a; margin-top: 5px;'>TERIMA KASIH!</div>"
             + "<div class='footer-text' style='font-family: monospace; letter-spacing: 2px; color: #94a3b8; font-size: 7pt; margin-top: 8px;'>*BIKANTIN-ORD-" + pesananId + "*</div>"
             + "</body>"
             + "</html>";
    }

    /**
     * Menghasilkan teks struk polos terformat (ASCII) untuk penyimpanan file
     */
    public String generatePlainTextReceipt() {
        return "==================================================\n"
             + "                   BI-KANTIN                      \n"
             + "             Kantin Digital Sekolah               \n"
             + "       Jl. Pendidikan No. 1, Jakarta              \n"
             + "           Telp: (021) 123-4567                   \n"
             + "==================================================\n"
             + "No. Pesanan   : #BK-" + String.format("%04d", pesananId) + "\n"
             + "Tanggal/Waktu : " + waktuTransaksi + " WIB\n"
             + "Nama Siswa    : " + namaSiswa + "\n"
             + "Status        : " + statusPesanan + "\n"
             + "--------------------------------------------------\n"
             + "Menu          : " + namaMenu + "\n"
             + "Kategori      : " + kategori + "\n"
             + "Jumlah Beli   : " + jumlah + " porsi\n"
             + "Harga Satuan  : Rp " + formatRp(hargaSatuan) + "\n"
             + "--------------------------------------------------\n"
             + "TOTAL BAYAR   : Rp " + formatRp(totalHarga) + "\n"
             + "==================================================\n"
             + " Catatan:\n"
             + " - Harap simpan dan tunjukkan struk ini ke kantin\n"
             + "   saat melakukan pengambilan makanan/minuman.\n"
             + " - Terima kasih telah berbelanja di BI-KANTIN!\n"
             + "==================================================\n";
    }

    /**
     * Membuka dialog print sistem untuk mencetak struk langsung ke printer atau Save as PDF
     */
    private void aksiCetak() {
        try {
            boolean complete = receiptPane.print(null, null, true, null, null, true);
            if (complete) {
                JOptionPane.showMessageDialog(this, 
                    "Pencetakan berhasil dikirim ke printer/PDF!", 
                    "Cetak Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, 
                "Terjadi kendala saat mencetak: " + e.getMessage() + "\nAnda dapat menggunakan tombol 'Simpan File' sebagai alternatif.", 
                "Gagal Cetak", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Menyimpan struk ke dalam file teks (.txt) melalui JFileChooser
     */
    private void aksiSimpanFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Struk Pesanan");
        fileChooser.setSelectedFile(new File("Struk_BIKANTIN_BK-" + String.format("%04d", pesananId) + ".txt"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".txt");
            }

            try (FileWriter fw = new FileWriter(fileToSave)) {
                fw.write(generatePlainTextReceipt());
                JOptionPane.showMessageDialog(this, 
                    "Struk berhasil disimpan di:\n" + fileToSave.getAbsolutePath(), 
                    "Simpan Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Gagal menyimpan file: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
