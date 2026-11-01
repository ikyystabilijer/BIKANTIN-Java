-- Database: db_bikantin
CREATE DATABASE IF NOT EXISTS db_bikantin;
USE db_bikantin;

-- 1. Tabel Users (Untuk Admin dan Siswa)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role ENUM('admin', 'siswa') NOT NULL
);

-- 2. Tabel Menu Makanan/Minuman
CREATE TABLE IF NOT EXISTS menu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_menu VARCHAR(100) NOT NULL,
    harga INT NOT NULL,
    kategori VARCHAR(50) NOT NULL
);

-- 3. Tabel Pesanan (Relasi ke Users dan Menu)
CREATE TABLE IF NOT EXISTS pesanan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    menu_id INT NOT NULL,
    jumlah INT NOT NULL,
    total_harga INT NOT NULL,
    status VARCHAR(100) DEFAULT 'Menunggu Konfirmasi'
);

-- Data Default untuk Uji Coba
INSERT INTO users (username, password, role) VALUES 
('admin_kantin', 'admin123', 'admin'),
('budi_siswa', '123', 'siswa');

INSERT INTO menu (nama_menu, harga, kategori) VALUES 
('Nasi Goreng Spesial', 15000, 'Makanan'),
('Es Teh Manis', 4000, 'Minuman');
