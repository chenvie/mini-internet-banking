-- phpMyAdmin SQL Dump
-- version 4.8.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 05, 2018 at 10:40 AM
-- Server version: 10.1.32-MariaDB
-- PHP Version: 7.2.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `banking`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_nasabah` (IN `nama` VARCHAR(100), IN `email` VARCHAR(40), IN `pwd` VARCHAR(100), IN `no_ktp` VARCHAR(20), IN `tgl_lahir` DATE, IN `alamat` VARCHAR(100), IN `kode_rahasia` VARCHAR(6))  NO SQL
BEGIN

DECLARE norek VARCHAR(6);
DECLARE uname varchar(20);
DECLARE selesai bool DEFAULT TRUE;
DECLARE kc varchar(4) DEFAULT "asd1";

/*Generate nomor rekening baru*/
SELECT 
    no_rek into norek
FROM 
   nasabah
   ORDER by id_nasabah DESC LIMIT 1;

SET norek = CAST(norek as int);
SET norek = norek+1;
SET norek = CAST(norek as char(6));
SET norek = CONCAT(0,norek);

/*Generate username baru*/

SET uname = LEFT(nama,LOCATE(" ",nama) - 1);
IF uname != ""
THEN
SET uname = concat(uname, RIGHT(norek,2));
ELSE
SET uname = concat(nama, RIGHT(norek,2));
END IF;

/*Membuat entry nasabah baru*/
INSERT INTO nasabah 
SET
email=email, username=uname, nama_lengkap=nama, password=pwd, no_ktp=no_ktp, tgl_lahir=tgl_lahir, alamat=alamat, kode_rahasia=kode_rahasia, no_rek=norek, kode_cabang=kc;

SELECT uname;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getHistory` (IN `id_nasabah` INT(10), IN `tgl_awal` DATE, IN `tgl_akhir` DATE)  BEGIN
select DISTINCT t.kode_transaksi,t.tgl_trans, IF (substr(t.kode_transaksi,1,1) = '1',CONCAT('Transfer ke ',r.rek_transfer),'Pembelian Pulsa') as tujuan, IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan, IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal, t.status from transaksi t, transfer r, pulsa p, nasabah n where n.id_nasabah = id_nasabah AND t.id_nasabah = n.id_nasabah AND (t.kode_transaksi = r.kode_transfer OR t.kode_transaksi = p.kode_pembelian) AND (t.tgl_trans >=tgl_awal AND t.tgl_trans <=tgl_akhir) ORDER BY t.tgl_trans ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMutasi` (IN `id_nasabah` INT, IN `tgl_awal` DATE, IN `tgl_akhir` DATE)  NO SQL
BEGIN
select DISTINCT t.kode_transaksi,n.no_rek,t.tgl_trans,
                    IF (substr(t.kode_transaksi,1,1) = '1', 
                    (if (n.no_rek = r.rek_transfer,
                    CONCAT('Transfer dari ',(SELECT no_rek from nasabah where id_nasabah = t.id_nasabah)),CONCAT('Transfer ke ',r.rek_transfer))),
                    'Pembelian Pulsa') as tujuan,
                    IF (substr(t.kode_transaksi,1,1) = '1',IF(n.no_rek = r.rek_transfer,'CR','DB'),'DB') as jenis,
                    IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan,
                    IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal
                    from transaksi t, transfer r, pulsa p, nasabah n
                    where (n.id_nasabah = id_nasabah AND (t.id_nasabah = n.id_nasabah or n.no_rek = r.rek_transfer)) AND 
                    (t.kode_transaksi = r.kode_transfer OR t.kode_transaksi = p.kode_pembelian) AND 
                    (t.tgl_trans >=tgl_awal AND t.tgl_trans <= tgl_akhir) AND 
                    t.status = 'Berhasil'
                    ORDER BY `t`.`tgl_trans`  ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMutasi2` ()  NO SQL
BEGIN
select DISTINCT t.kode_transaksi,n.no_rek,t.tgl_trans,
                    IF (substr(t.kode_transaksi,1,1) = '1', 
                    (if (n.no_rek = r.rek_transfer,
                    CONCAT('Transfer dari ',(SELECT no_rek from nasabah where id_nasabah = t.id_nasabah)),CONCAT('Transfer ke ',r.rek_transfer))),
                    'Pembelian Pulsa') as tujuan,
                    IF (substr(t.kode_transaksi,1,1) = '1',IF(n.no_rek = r.rek_transfer,'CR','DB'),'DB') as jenis,
                    IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan,
                    IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal
                    from transaksi t, transfer r, pulsa p, nasabah n
                    where (n.id_nasabah = 2 AND (t.id_nasabah = n.id_nasabah or n.no_rek = r.rek_transfer)) AND 
                    (t.kode_transaksi = r.kode_transfer OR t.kode_transaksi = p.kode_pembelian) AND 
                    (t.tgl_trans >= '2018-08-13' AND t.tgl_trans <= '2018-08-21') AND 
                    t.status = 'Berhasil'
                    ORDER BY `t`.`tgl_trans`  ASC;
END$$

DELIMITER ;

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
  `kode_rahasia` varchar(100) NOT NULL,
  `no_rek` varchar(16) NOT NULL,
  `jml_saldo` int(11) NOT NULL DEFAULT '450000',
  `kode_cabang` varchar(10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `nasabah`
--

INSERT INTO `nasabah` (`id_nasabah`, `email`, `username`, `nama_lengkap`, `password`, `no_ktp`, `tgl_lahir`, `alamat`, `kode_rahasia`, `no_rek`, `jml_saldo`, `kode_cabang`, `created`) VALUES
(1, 'reinald.a.k@gmail.com', 'reinaldd', 'reinalda ar', '123456', '3323031211960005', '2018-08-06', 'temanggung', '654321', '2141516', 2000000, 'asd1', '2018-08-15 03:04:48'),
(2, 'boni@gmail.com', 'bonii', 'bonifasius', '812018', '123513163', '2018-02-05', 'magelang', '123', '2491204', 2800000, 'asd1', '2018-08-20 10:25:46'),
(3, 'asd@gmail.com', 'coba', 'coba', 'qweqweqwe', '123', '2018-08-02', 'coba', '123', '1919191919', 1500000, 'asd2', '2018-08-16 03:26:59'),
(4, 'dany@gmail.com', 'cocobaba', 'dany', '123', '123', '2018-08-02', 'yogya', '123', '123213213', 1000000, 'asd1', '2018-08-15 03:04:48'),
(6, 'cipe@gmail.com', 'cipe', 'asd asd', '123', '123', '2018-01-01', 'disana', 'asda', '037001', 4650000, 'asd1', '2018-08-21 04:17:37'),
(7, 'sam@gmail.com', 'sam', 'sam w', 'qwe', '123', '2018-08-02', 'kudus', '123', '037000', 300000, 'asd1', '2018-08-21 04:17:37'),
(8, 'billy@gmail.com', 'bil', 'billy b', 'qwe', '12312', '2018-08-02', 'taman siswa', '123', '037002', 450000, 'asd1', '2018-08-15 05:07:06'),
(9, 'argo@gmail.com', 'argo', 'argo uchiha', 'qwe', '123', '2018-08-01', 'godean', '123', '037008', 450000, 'asd1', '2018-08-15 05:25:15'),
(10, 'kadinugraha@gmail.com', '', 'kristian adi', 'qwe', '123', '2018-08-05', 'godean', '123', '037009', 450000, 'asd1', '2018-08-15 05:47:30'),
(11, 'kw@gmail.com', 'katon10', 'katon wijana', '123', '123', '2018-08-08', 'godean', '123', '037010', 400000, 'asd1', '2018-08-21 09:33:24'),
(12, 'hb@gmail.com', 'halim12', 'halim budi', 'qwe', '123', '2018-08-09', 'maguwo', 'qwe', '037011', 600000, 'asd1', '2018-09-05 08:37:23'),
(13, 'ivan@gmail.com', 'ivan12', 'ivan', 'qwe', '123', '2018-08-03', 'klitren', 'qwe', '037012', 450000, 'asd1', '2018-08-15 05:52:22'),
(14, 'kf@gmail.com', 'Kornelius13', 'Kornelius Fredy', 'qwerty123', '3323051232960007', '1996-07-20', 'bonbin sana lagi', '123456', '037013', 450000, 'asd1', '2018-09-04 09:32:20'),
(16, 'vievin.efendy@ti.ukdw.ac.id', 'Vievin14', 'Vievin Efendy', 'ff68179dddd38692293d04c091d017ff', '3372024109970003', '1997-09-01', 'Surakarta', '1c88b390f7a3d49edfbeff983c85c2f4', '037014', 850000, 'asd1', '2018-09-05 08:37:23');

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
('20004', '08978902350', 'Telkomsel', 50000),
('20015', '081212515', 'Telkomsel', 50000),
('20016', '081212515', 'Telkomsel', 50000),
('20024', '085201258593', 'Telkomsel', 50000);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `kode_transaksi` varchar(20) NOT NULL,
  `id_nasabah` int(10) NOT NULL,
  `tgl_trans` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` varchar(8) NOT NULL,
  `ket_status` varchar(70) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`kode_transaksi`, `id_nasabah`, `tgl_trans`, `status`, `ket_status`) VALUES
('10001', 1, '2018-08-13 08:06:41', 'Berhasil', ''),
('10002', 2, '2018-08-13 08:06:41', 'Berhasil', ''),
('10003', 2, '2018-08-13 08:06:41', 'Gagal', ''),
('10006', 2, '2018-08-20 08:25:47', 'Berhasil', 'Berhasil transfer'),
('10007', 2, '2018-08-20 08:26:38', 'Berhasil', 'Berhasil transfer'),
('10008', 2, '2018-08-20 08:38:26', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('10009', 2, '2018-08-20 08:39:31', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('10010', 2, '2018-08-20 08:40:46', 'Berhasil', 'Berhasil transfer'),
('10011', 2, '2018-08-20 08:41:33', 'Gagal', 'Jumlah yang ditransfer terlalu kecil'),
('10012', 2, '2018-08-20 08:56:18', 'Gagal', 'Nomor rekening tujuan tidak ditemukan'),
('10013', 2, '2018-08-20 08:59:37', 'Gagal', 'Kode rahasia salah'),
('10014', 2, '2018-08-20 09:00:16', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('10017', 2, '2018-08-21 02:47:46', 'Berhasil', 'Nomor rekening tujuan ditemukan'),
('10018', 2, '2018-08-21 02:50:12', 'Berhasil', 'Nomor rekening tujuan ditemukan'),
('10019', 2, '2018-08-21 02:51:29', 'Berhasil', 'Nomor rekening tujuan ditemukan'),
('10020', 7, '2018-08-21 04:16:23', 'Berhasil', 'Berhasil transfer'),
('10021', 7, '2018-08-21 04:17:01', 'Berhasil', 'Berhasil transfer'),
('10022', 7, '2018-08-21 04:17:37', 'Berhasil', 'Berhasil transfer'),
('10023', 11, '2018-08-21 09:33:25', 'Berhasil', 'Berhasil transfer'),
('10025', 16, '2018-09-05 08:37:23', 'Berhasil', 'Berhasil transfer'),
('15', 2, '2018-08-20 08:23:15', 'Berhasil', 'Berhasil transfer'),
('20004', 1, '2018-08-20 04:19:59', 'Berhasil', ''),
('20015', 2, '2018-08-20 09:20:35', 'Berhasil', 'Pembelian pulsa berhasil'),
('20016', 2, '2018-08-20 10:25:46', 'Berhasil', 'Pembelian pulsa berhasil'),
('20024', 16, '2018-09-05 08:34:04', 'Berhasil', 'Pembelian pulsa berhasil');

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
('10003', '765421', 30000, 'coba 3'),
('10006', '2141516', 50000, 'cek'),
('10007', '2141516', 50000, 'cek'),
('10008', '2141516', 50000, 'cek'),
('10009', '2141516', 50000, 'cek'),
('10010', '2141516', 50000, 'cek'),
('10011', '2141516', 0, 'cek'),
('10012', '001', 50000, 'cek'),
('10013', '2141516', 50000, 'cek'),
('10014', '2141516', 5000000, 'cek'),
('10017', '037001', 50000, 'asd'),
('10018', '037001', 50000, 'asd'),
('10019', '037001', 50000, 'asd'),
('10020', '037001', 50000, 'cek lagi'),
('10021', '037001', 50000, 'cek lagi'),
('10022', '037001', 50000, 'cek lagi'),
('10023', '037011', 50000, 'coba sore'),
('10025', '037011', 100000, ''),
('15', '2141516', 50000, '');

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
  MODIFY `id_nasabah` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

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
  ADD CONSTRAINT `pulsa_ibfk_1` FOREIGN KEY (`kode_pembelian`) REFERENCES `transaksi` (`kode_transaksi`) ON DELETE NO ACTION ON UPDATE CASCADE;

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
