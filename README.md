# 🍽️ BI-KANTIN - Sistem Manajemen & Pemesanan Kantin Digital

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![NetBeans](https://img.shields.io/badge/NetBeans%20IDE-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white)](https://netbeans.apache.org/)
[![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)](#)

**BI-KANTIN** adalah aplikasi desktop berbasis **Java Swing** dan **MySQL** yang dirancang untuk mempermudah dan mendigitalisasi proses pemesanan makanan/minuman di kantin sekolah. 

Sistem ini memiliki dua peran pengguna (*Multi-Role*): **Admin Kantin** untuk mengelola menu dan pesanan masuk, serta **Siswa** untuk memesan makanan dan memantau status pesanan secara *real-time*.

---

## 🚀 Fitur Utama

### 👨‍🎓 Dashboard Siswa
- **Katalog Menu Interaktif:** Menampilkan pilihan makanan/minuman beserta harganya secara dinamis dari database.
- **Kalkulasi Otomatis:** Perhitungan total bayar otomatis saat menentukan jumlah porsi/item yang dipesan.
- **Status Pesanan Real-Time:** Notifikasi status pesanan terkini yang diperbarui otomatis setiap 3 detik (*auto-refresh* menggunakan `javax.swing.Timer`) dengan warna banner dinamis.
- **Riwayat Pesanan Pribadi:** Menampilkan riwayat pesanan dengan badge status warna-warni (*Emerald, Indigo, Amber, Red*).
- **Cetak Struk Digital & PDF:** Fitur cetak struk pesanan langsung ke printer fisik / Simpan sebagai PDF dan ekspor file `.txt` dengan tema warna modern (bisa diakses via tombol, double-click tabel, atau pop-up instan setelah memesan).

### 👨‍🍳 Dashboard Admin (Kantin)
- **Manajemen Menu (CRUD):**
  - Menambah menu baru (Nama, Harga, Kategori: *Makanan*, *Minuman*, *Snack*).
  - Mengubah (Edit) detail menu yang sudah ada.
  - Menghapus menu.
- **Tracking & Manajemen Pesanan Masuk:**
  - Melihat antrean seluruh pesanan yang masuk dari semua siswa.
  - Memperbarui status pesanan secara bertahap:
    - ⏳ *Menunggu Konfirmasi*
    - 🍳 *Sedang Dimasak*
    - 📦 *Siap Diambil*
    - ✅ *Selesai*
    - 💵 *Lunas*

### 🔐 Autentikasi & Keamanan
- Sistem **Login Terpadu** yang otomatis mengarahkan pengguna ke dashboard yang sesuai berdasarkan *role* (`admin` atau `siswa`).

---

## 🛠️ Teknologi & Prasyarat

- **Bahasa Pemrograman:** Java (JDK 8 atau lebih baru)
- **GUI Framework:** Java Swing (Nimbus Look and Feel)
- **Database:** MySQL / MariaDB (via XAMPP atau Laragon)
- **Driver Database:** `mysql-connector-j-26.7.0.jar` (terlampir di folder `lib/`)
- **IDE Rekomendasi:** Apache NetBeans IDE 12+

---

## 📦 Skema & Akun Bawaan (Default Accounts)

Database menggunakan nama: **`db_bikantin`**

### Akun Uji Coba (Demo):
| Role | Username | Password | Keterangan |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin_kantin` | `admin123` | Akses penuh dashboard kelola menu & pesanan |
| **Siswa** | `budi_siswa` | `123` | Akses menu pemesanan & tracking |

---

## ⚙️ Panduan Instalasi & Menjalankan

Ikuti langkah-langkah berikut untuk menjalankan proyek di komputer lokal:

### 1. Clone Repositori
```bash
git clone https://github.com/ikyystabilijer/BIKANTIN-Java.git
cd Bikantin
```

### 2. Konfigurasi Database (MySQL)
1. Nyalakan layanan **MySQL** melalui XAMPP / Laragon.
2. Buka **phpMyAdmin** (`http://localhost/phpmyadmin`).
3. Buat database baru bernama `db_bikantin` atau langsung impor file SQL:
   - Pilih tab **Import** ➔ Pilih file **`db_bikantin.sql`** yang ada di folder proyek ➔ Klik **Go / Kirim**.
4. Periksa file konfigurasi koneksi di `src/koneksi/Koneksi.java`:
   ```java
   String url = "jdbc:mysql://localhost:3306/db_bikantin";
   String user = "root";
   String password = ""; // sesuaikan jika MySQL kamu memiliki password
   ```

### 3. Buka di NetBeans IDE
1. Buka **Apache NetBeans**.
2. Klik menu **File** ➔ **Open Project...**
3. Arahkan ke folder hasil clone **`Bikantin`** dan klik **Open Project**.
4. Pastikan library `mysql-connector-j-26.7.0.jar` sudah terhubung pada bagian **Libraries**.

### 4. Jalankan Aplikasi
- Klik kanan pada project **Bikantin** ➔ Pilih **Run** (atau tekan `Shift + F6` pada file `src/Main.java`).
- Silakan login menggunakan akun uji coba di atas.

---

## 📁 Struktur Direktori

```text
Bikantin/
├── db_bikantin.sql          # File dump database MySQL
├── dist/                    # Folder build / runnable JAR
├── lib/                     # Library JDBC Connector MySQL
│   └── mysql-connector-j-26.7.0.jar
├── nbproject/               # Konfigurasi project NetBeans
└── src/
    ├── Main.java            # Entry point aplikasi (Inisialisasi Look & Feel dan FormLogin)
    ├── Form/
    │   ├── FormLogin.java        # Halaman Login Multi-User
    │   ├── FormAdmin.java        # Dashboard Admin (Kelola Menu & Status Pesanan)
    │   ├── FormSiswa.java        # Dashboard Siswa (Pemesanan & Tracking Real-time)
    │   └── DialogCetakStruk.java # Modal Struk Pembayaran & Dialog Cetak Printer/PDF
    └── koneksi/
        └── Koneksi.java     # Singleton class koneksi database JDBC
```

---

## 👤 Pembuat / Author

- **ikyys** - [*GitHub Profile*](https://github.com/ikyys)

---

## 📄 Lisensi

Proyek ini dibuat untuk tujuan pembelajaran dan pengembangan sistem informasi. Bebas digunakan dan dimodifikasi untuk keperluan edukasi.
