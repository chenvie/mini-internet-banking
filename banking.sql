-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 15, 2018 at 12:53 PM
-- Server version: 10.1.21-MariaDB
-- PHP Version: 5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `banking`
--

-- --------------------------------------------------------

--
-- Table structure for table `cabang_bank`
--

CREATE TABLE `cabang_bank` (
  `kode_cabang` varchar(10) NOT NULL,
  `nama_cabang` varchar(50) NOT NULL,
  `alamat_cabang` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cabang_bank`
--

INSERT INTO `cabang_bank` (`kode_cabang`, `nama_cabang`, `alamat_cabang`) VALUES
('asd1', 'KCU Jogja', 'Sudirman'),
('asd2', 'KCU Temanggung', 'Temanggung');

-- --------------------------------------------------------

--
-- Table structure for table `nasabah`
--

CREATE TABLE `nasabah` (
  `id_nasabah` int(10) NOT NULL,
  `email` varchar(40) NOT NULL,
  `username` varchar(20) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `no_ktp` varchar(20) NOT NULL,
  `tgl_lahir` date NOT NULL,
  `alamat` varchar(100) NOT NULL,
  `kode_rahasia` varchar(6) NOT NULL,
  `no_rek` varchar(16) NOT NULL,
  `jml_saldo` int(11) NOT NULL,
  `kode_cabang` varchar(10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `nasabah`
--

INSERT INTO `nasabah` (`id_nasabah`, `email`, `username`, `nama_lengkap`, `password`, `no_ktp`, `tgl_lahir`, `alamat`, `kode_rahasia`, `no_rek`, `jml_saldo`, `kode_cabang`, `created`) VALUES
(1, 'reinald.a.k@gmail.com', 'reinaldd', 'reinalda ar', '123456', '3323031211960005', '2018-08-06', 'temanggung', '654321', '2141516', 2000000, 'asd1', '2018-08-15 03:04:48'),
(2, 'boni@gmail.com', 'bonii', 'bonifasius', '812018', '123513163', '2018-02-05', 'magelang', '123', '2491204', 3000000, 'asd1', '2018-08-15 08:10:51'),
(3, 'asd@gmail.com', 'coba', 'coba', 'coba', '123', '2018-08-02', 'coba', '123', '1919191919', 1500000, 'asd2', '2018-08-15 03:04:48'),
(4, 'dany@gmail.com', 'cocobaba', 'dany', '123', '123', '2018-08-02', 'yogya', '123', '123213213', 1000000, 'asd1', '2018-08-15 03:04:48'),
(6, 'cipe@gmail.com', 'cipe', 'asd asd', '123', '123', '2018-01-01', 'disana', 'asda', '037001', 4500000, 'asd1', '2018-08-15 09:19:19'),
(7, 'sam@gmail.com', 'sam', 'sam w', 'qwe', '123', '2018-08-02', 'kudus', '123', '037000', 450000, 'asd1', '2018-08-15 04:34:29'),
(8, 'billy@gmail.com', 'bil', 'billy b', 'qwe', '12312', '2018-08-02', 'taman siswa', '123', '037002', 450000, 'asd1', '2018-08-15 05:07:06'),
(9, 'argo@gmail.com', 'argo', 'argo uchiha', 'qwe', '123', '2018-08-01', 'godean', '123', '037008', 450000, 'asd1', '2018-08-15 05:25:15'),
(10, 'kadinugraha@gmail.com', '', 'kristian adi', 'qwe', '123', '2018-08-05', 'godean', '123', '037009', 450000, 'asd1', '2018-08-15 05:47:30'),
(11, 'kw@gmail.com', 'katon10', 'katon wijana', '123', '123', '2018-08-08', 'godean', '123', '037010', 450000, 'asd1', '2018-08-15 05:49:58'),
(12, 'hb@gmail.com', 'halim12', 'halim budi', 'qwe', '123', '2018-08-09', 'maguwo', 'qwe', '037011', 450000, 'asd1', '2018-08-15 05:50:53'),
(13, 'ivan@gmail.com', 'ivan12', 'ivan', 'qwe', '123', '2018-08-03', 'klitren', 'qwe', '037012', 450000, 'asd1', '2018-08-15 05:52:22');

-- --------------------------------------------------------

--
-- Table structure for table `pulsa`
--

CREATE TABLE `pulsa` (
  `kode_pembelian` varchar(10) NOT NULL,
  `no_hp` varchar(40) NOT NULL,
  `provider` enum('Indosat','XL','Telkomsel','Smartfren') NOT NULL,
  `nominal` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pulsa`
--

INSERT INTO `pulsa` (`kode_pembelian`, `no_hp`, `provider`, `nominal`) VALUES
('20001', '08978902350', 'Telkomsel', 50000);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `kode_transaksi` varchar(20) NOT NULL,
  `id_nasabah` int(10) NOT NULL,
  `tgl_trans` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `jenis` varchar(2) NOT NULL,
  `status` varchar(8) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`kode_transaksi`, `id_nasabah`, `tgl_trans`, `jenis`, `status`) VALUES
('10001', 1, '2018-08-13 08:06:41', 'DB', 'Berhasil'),
('10002', 2, '2018-08-13 08:06:41', 'DB', 'Berhasil'),
('10003', 2, '2018-08-13 08:06:41', 'DB', 'Gagal'),
('20001', 1, '2018-08-13 08:06:41', 'DB', 'Berhasil');

-- --------------------------------------------------------

--
-- Table structure for table `transfer`
--

CREATE TABLE `transfer` (
  `kode_transfer` varchar(100) NOT NULL,
  `rek_transfer` varchar(40) NOT NULL,
  `nominal` int(50) NOT NULL,
  `keterangan` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transfer`
--

INSERT INTO `transfer` (`kode_transfer`, `rek_transfer`, `nominal`, `keterangan`) VALUES
('10001', '214534', 50000, 'coba'),
('10002', '87654', 100000, 'coba 2'),
('10003', '765421', 30000, 'coba 3');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cabang_bank`
--
ALTER TABLE `cabang_bank`
  ADD PRIMARY KEY (`kode_cabang`),
  ADD KEY `kode_cabang` (`kode_cabang`);

--
-- Indexes for table `nasabah`
--
ALTER TABLE `nasabah`
  ADD PRIMARY KEY (`id_nasabah`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username_2` (`username`),
  ADD KEY `username` (`username`),
  ADD KEY `id_nasabah` (`id_nasabah`),
  ADD KEY `kode_cabang` (`kode_cabang`);

--
-- Indexes for table `pulsa`
--
ALTER TABLE `pulsa`
  ADD PRIMARY KEY (`kode_pembelian`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`kode_transaksi`),
  ADD KEY `kode_transfer` (`kode_transaksi`),
  ADD KEY `id_nasabah` (`id_nasabah`),
  ADD KEY `id_nasabah_2` (`id_nasabah`);

--
-- Indexes for table `transfer`
--
ALTER TABLE `transfer`
  ADD PRIMARY KEY (`kode_transfer`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `nasabah`
--
ALTER TABLE `nasabah`
  MODIFY `id_nasabah` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `nasabah`
--
ALTER TABLE `nasabah`
  ADD CONSTRAINT `nasabah_ibfk_1` FOREIGN KEY (`kode_cabang`) REFERENCES `cabang_bank` (`kode_cabang`);

--
-- Constraints for table `pulsa`
--
ALTER TABLE `pulsa`
  ADD CONSTRAINT `pulsa_ibfk_1` FOREIGN KEY (`kode_pembelian`) REFERENCES `transaksi` (`kode_transaksi`);

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_nasabah`) REFERENCES `nasabah` (`id_nasabah`);

--
-- Constraints for table `transfer`
--
ALTER TABLE `transfer`
  ADD CONSTRAINT `transfer_ibfk_1` FOREIGN KEY (`kode_transfer`) REFERENCES `transaksi` (`kode_transaksi`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
