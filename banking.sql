-- phpMyAdmin SQL Dump
-- version 4.8.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 20, 2018 at 04:27 AM
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
CREATE DEFINER=`root`@`localhost` PROCEDURE `cekNoRek` (IN `norek` VARCHAR(20), IN `id_nsb` INT(10), OUT `stts` VARCHAR(20), OUT `msg` VARCHAR(70))  NO SQL
BEGIN 
DECLARE jml int;
DECLARE kodeT varchar(12) DEFAULT "1";

SELECT COUNT(no_rek) into jml
from nasabah
where no_rek = norek;


if jml = 1 THEN
SET stts = "Berhasil";
SET msg = "Nomor rekening tujuan ditemukan";
ELSE 
SET stts = "Gagal";
SET msg = "Nomor rekening tujuan tidak ditemukan";
end if;

if stts = "Gagal" THEN

/*generate kode trans*/
SELECT 
    count(kode_transaksi) into jml
FROM 
   transaksi;

SET jml = jml +1;
if jml > 9 THEN
SET kodeT = concat(kodeT,"00");
ELSEIF jml > 99 THEN
SET kodeT = concat(kodeT,"0");
ELSEIF jml <9 THEN
SET kodeT = concat(kodeT,"000");
ELSE
SET kodeT = kodeT;
END IF;

SET jml = CAST(jml as char(6));
SET kodeT = concat(kodeT,jml);

insert into transaksi SET kode_transaksi = kodeT,id_nasabah = id_nsb,status = stts, ket_status = msg;
END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `generateKodeTransaksi` (IN `jenis` VARCHAR(1))  NO SQL
BEGIN
DECLARE kodeT varchar(12);
DECLARE jml int;

if jenis = "P" THEN
SET kodeT = "2";
ELSE
SET kodeT = "1";
END IF;

SELECT 
    count(kode_transaksi) into jml
FROM 
   transaksi;

SET jml = jml +1;
if jml > 9 THEN
SET kodeT = concat(kodeT,"00");
ELSEIF jml > 99 THEN
SET kodeT = concat(kodeT,"0");
ELSEIF jml <9 THEN
SET kodeT = concat(kodeT,"000");
ELSE
SET kodeT = kodeT;
END IF;

SET jml = CAST(jml as char(6));
SET kodeT = concat(kodeT,jml);


SELECT kodeT as 'kode_trans';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getHistory` (IN `id_nasabah` INT(10), IN `tgl_awal` DATE, IN `tgl_akhir` DATE)  BEGIN
select DISTINCT t.kode_transaksi,t.tgl_trans, IF (substr(t.kode_transaksi,1,1) = '1',CONCAT('Transfer ke ',r.rek_transfer),'Pembelian Pulsa') as tujuan, IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan, IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal, t.status from transaksi t, transfer r, pulsa p, nasabah n where n.id_nasabah = id_nasabah AND t.id_nasabah = n.id_nasabah AND (t.kode_transaksi = r.kode_transfer OR t.kode_transaksi = p.kode_pembelian) AND (t.tgl_trans >=tgl_awal AND t.tgl_trans <=tgl_akhir) ORDER BY t.tgl_trans ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMutasi` (IN `id_nasabah` INT(100), IN `tgl_awal` DATE, IN `tgl_akhir` DATE)  NO SQL
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
                    (t.tgl_trans >= tgl_awal AND t.tgl_trans <= tgl_akhir) AND 
                    t.status = 'Berhasil'
                    ORDER BY `t`.`tgl_trans`  ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `login` (INOUT `uname` VARCHAR(20), IN `pwd` VARCHAR(100), OUT `stts` BOOLEAN, OUT `msg` VARCHAR(50), OUT `id_nsb` INT)  NO SQL
BEGIN
DECLARE jml int;
/*DECLARE uname_temp,pwd_temp varchar(20);
select username into uname_temp, password into pwd_temp */
SELECT COUNT(id_nasabah) into jml
from nasabah 
where username = uname and password = pwd;

IF jml = 1 THEN
SET stts = True;
SET msg = "Berhasil Login";
SELECT id_nasabah into id_nsb 
FROM nasabah
where username = uname;
ELSE
SET uname = "";
SET stts = False;
SET msg = "Username atau password salah";
END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `postNasabah` (IN `nama` VARCHAR(100), IN `email` VARCHAR(40), IN `pwd` VARCHAR(100), IN `no_ktp` VARCHAR(20), IN `tgl_lahir` DATE, IN `alamat` VARCHAR(100), IN `kode_rahasia` VARCHAR(6), OUT `uname` VARCHAR(20))  NO SQL
BEGIN

DECLARE norek VARCHAR(6);
DECLARE selesai bool DEFAULT TRUE;
DECLARE kc varchar(4) DEFAULT "asd1";

/*Generate nomor rekening baru*/
/*ini kalo blm ada nasabah sebelumnya bakal error, bisa diganti pake logika awalnya tapi mager thx, penting paham ya kalo ini salah, harusnya pake logika yg lain thx*/
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

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `postNasabah2` (IN `nama` VARCHAR(100), IN `_email` VARCHAR(40), IN `pwd` VARCHAR(100), IN `no_ktp` VARCHAR(20), IN `tgl_lahir` DATE, IN `alamat` VARCHAR(100), IN `kr` VARCHAR(100), OUT `uname` VARCHAR(20), OUT `stts` VARCHAR(10), OUT `msg` VARCHAR(40))  NO SQL
BEGIN

DECLARE norek VARCHAR(6);
DECLARE kc varchar(4) DEFAULT "asd1";
DECLARE counter int;
DECLARE diff int;

SELECT TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) into diff;
SELECT count(email) into counter FROM nasabah where email = _email;

IF counter = 1 THEN
SET stts = "Gagal";
SET msg = "email sudah pernah didaftarkan";
ELSEIF diff < 17 THEN
SET stts = "Gagal";
SET msg = "Umur belum mencukupi (min. 17 tahun)";
ELSEIF pwd NOT REGEXP '^[A-Za-z0-9 ]+$' THEN
SET stts = "Gagal";
SET msg = "Password mengandung karakter non alphanumeric";
ELSEIF kr NOT REGEXP '^[A-Za-z0-9 ]+$' THEN
SET stts = "Gagal";
SET msg = "Kode rahasia mengandung karakter non alphanumeric";
ELSE
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
email=_email, username=uname, nama_lengkap=nama, password=pwd, no_ktp=no_ktp, tgl_lahir=tgl_lahir, alamat=alamat, kode_rahasia=kr, no_rek=norek, kode_cabang=kc;

SET stts = "Berhasil";
SET msg = "Penambahan nasabah berhasil";
END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `postTransaksiPulsa` (IN `id_nsb` INT(10), IN `no_hp_tujuan` VARCHAR(20), IN `nmnl` INT(50), IN `prvdr` VARCHAR(10), IN `uname` VARCHAR(20), IN `kode_rhs` VARCHAR(100), OUT `stts` VARCHAR(8), OUT `ket_stts` VARCHAR(70))  NO SQL
BEGIN
DECLARE jml_saldo_out int;
DECLARE kr_temp varchar(100);
DECLARE kodeT varchar(12) DEFAULT "2";
DECLARE jml int;

/*generate kode trans*/
SELECT 
    count(kode_transaksi) into jml
FROM 
   transaksi;

SET jml = jml +1;
if jml > 9 THEN
SET kodeT = concat(kodeT,"00");
ELSEIF jml > 99 THEN
SET kodeT = concat(kodeT,"0");
ELSEIF jml <9 THEN
SET kodeT = concat(kodeT,"000");
ELSE
SET kodeT = kodeT;
END IF;

SET jml = CAST(jml as char(6));
SET kodeT = concat(kodeT,jml);

/*get jml saldo pembeli*/
SELECT jml_saldo into jml_saldo_out
from nasabah where id_nasabah = id_nsb;

/*get kode rahasia*/
SELECT kode_rahasia into kr_temp
from nasabah where kode_rahasia = kode_rhs and username = uname;
    
/*cek kode rahasia*/
if kode_rhs != kr_temp THEN
    
    SET stts = "Gagal";
    SET ket_stts = "Kode rahasia salah";
    
ELSEIF (jml_saldo_out - nmnl) < 50000 THEN
    
    SET stts = "Gagal";
    SET ket_stts = "Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda";
    
ELSE
    
    SET stts = "Berhasil";
    SET ket_stts = "Berhasil Membeli Pulsa";
    SET jml_saldo_out = jml_saldo_out - nmnl;
    
    UPDATE nasabah 
    SET jml_saldo = jml_saldo_out
    WHERE id_nasabah = id_nsb;
    
END IF;

INSERT INTO transaksi 
SET kode_transaksi=kodeT, id_nasabah=id_nsb, status=stts, ket_status=ket_stts;

IF stts = "Berhasil" THEN
INSERT INTO pulsa
SET kode_pembelian=kodeT, no_hp=no_hp_tujuan, provider=prvdr, nominal=nmnl;
END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `postTransaksiTransfer` (IN `id_nsb` INT(10), IN `no_rek_tujuan` VARCHAR(40), IN `nmnl` INT(50), IN `ket` TEXT, IN `uname` VARCHAR(20), IN `kode_rhs` VARCHAR(100), OUT `stts` VARCHAR(8), OUT `ket_stts` VARCHAR(70))  NO SQL
BEGIN
DECLARE jml_saldo_out, jml_saldo_in int;
DECLARE kr_temp varchar(100);
DECLARE kodeT varchar(12) DEFAULT "1";
DECLARE jml int;

/*generate kode trans*/
SELECT 
    count(kode_transaksi) into jml
FROM 
   transaksi;

SET jml = jml +1;
if jml > 9 THEN
SET kodeT = concat(kodeT,"00");
ELSEIF jml > 99 THEN
SET kodeT = concat(kodeT,"0");
ELSEIF jml <9 THEN
SET kodeT = concat(kodeT,"000");
ELSE
SET kodeT = kodeT;
END IF;

SET jml = CAST(jml as char(6));
SET kodeT = concat(kodeT,jml);


/*get jml saldo pengirim*/
SELECT jml_saldo into jml_saldo_out
from nasabah where id_nasabah = id_nsb;

/*get jml saldo penerima*/
SELECT jml_saldo into jml_saldo_in
from nasabah where no_rek = no_rek_tujuan;

/*get kode rahasia*/
SELECT kode_rahasia into kr_temp
from nasabah where kode_rahasia = kode_rhs and username = uname;
    
/*cek kode rahasia*/
if kode_rhs != kr_temp THEN
    
    SET stts = "Gagal";
    SET ket_stts = "Kode rahasia salah";
/*cek nominal*/
ELSEIF nmnl < 10000 THEN
    
    SET stts = "Gagal";
    SET ket_stts = "Jumlah yang ditransfer terlalu kecil";
/*cek sisa saldo*/
ELSEIF (jml_saldo_out - nmnl) < 50000 THEN
    
    SET stts = "Gagal";
    SET ket_stts = "Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda";
    
ELSE
    
    SET stts = "Berhasil";
    SET ket_stts = "Berhasil Transfer";
    SET jml_saldo_out = jml_saldo_out - nmnl;
    SET jml_saldo_in = jml_saldo_in + nmnl;
    
    UPDATE nasabah 
    SET jml_saldo = jml_saldo_out
    WHERE id_nasabah = id_nsb;
    
    UPDATE nasabah
    SET jml_saldo = jml_saldo_in
    where no_rek = no_rek_tujuan;
    
    END IF;

INSERT INTO transaksi 
SET kode_transaksi=kodeT, id_nasabah=id_nsb, status=stts, ket_status=ket_stts;


IF stts = "Berhasil" THEN
INSERT INTO transfer
SET kode_transfer=kodeT, rek_transfer=no_rek_tujuan, nominal=nmnl, keterangan=ket;
END IF;    
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `putNasabahKodeRahasia` (IN `id_nsb` INT(10), IN `krhLama` VARCHAR(100), IN `krhBaru1` VARCHAR(100), IN `krhBaru2` VARCHAR(100), OUT `stts` VARCHAR(10), OUT `msg` VARCHAR(60))  NO SQL
BEGIN
DECLARE krh_temp varchar(100);
SELECT kode_rahasia INTO krh_temp
from nasabah 
where id_nasabah = id_nsb;

IF krh_temp != krhLama
THEN
SET stts = "Gagal";
SET msg = "Kode rahasia lama tidak sama";
ELSEIF krhBaru1 != krhBaru2
THEN
SET stts = "Gagal";
SET msg = "Kode rahasia baru tidak sama";
ELSEIF krhBaru1 NOT REGEXP '^[A-Za-z0-9 ]+$' THEN
SET stts = "Gagal";
SET msg = "Kode rahasia mengandung karakter non alphanumeric";
ELSE
update nasabah 
set kode_rahasia = krhBaru1
where id_nasabah = id_nsb;
SET stts = "Berhasil";
SET msg = "Berhasil update Kode rahasia";
END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `putNasabahPassword` (IN `id_nsb` INT(10), IN `pwdLama` VARCHAR(100), IN `pwdBaru1` VARCHAR(100), IN `pwdBaru2` VARCHAR(100), OUT `stts` VARCHAR(10), OUT `msg` VARCHAR(60))  NO SQL
BEGIN
DECLARE pwd_temp varchar(100);
SELECT password INTO pwd_temp
from nasabah 
where id_nasabah = id_nsb;

IF pwd_temp != pwdLama
THEN
SET stts = "Gagal";
SET msg = "Password lama tidak sama";
ELSEIF pwdBaru1 != pwdBaru2
THEN
SET stts = "Gagal";
SET msg = "Password baru tidak sama";
ELSEIF pwdBaru1 NOT REGEXP '^[A-Za-z0-9 ]+$' THEN
SET stts = "Gagal";
SET msg = "Password mengandung karakter non alphanumeric";
ELSE
update nasabah 
set password = pwdBaru1
where id_nasabah = id_nsb;
SET stts = "Berhasil";
SET msg = "Berhasil update password";
END IF;
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
(1, 'reinald.a.k@gmail.com', 'reinaldd', 'reinalda ar', 'reinaldariel123', '3323031211960005', '2018-08-06', 'temanggung', '123124', '2141516', 1950000, 'asd1', '2018-09-14 07:16:41'),
(2, 'boni@gmail.com', 'bonii', 'bonifasius', 'boniboni123', '123513163', '2018-02-05', 'magelang', '123124', '2491204', 2600000, 'asd1', '2018-09-14 07:14:26'),
(3, 'asd@gmail.com', 'coba', 'coba', 'qweqweqwe', '123', '2018-08-02', 'coba', '123', '1919191919', 1500000, 'asd2', '2018-08-16 03:26:59'),
(4, 'dany@gmail.com', 'cocobaba', 'dany', '123', '123', '2018-08-02', 'yogya', '123', '123213213', 1000000, 'asd1', '2018-08-15 03:04:48'),
(6, 'cipe@gmail.com', 'cipe', 'asd asd', '123', '123', '2018-01-01', 'disana', 'asda', '037001', 4850000, 'asd1', '2018-09-20 02:27:09'),
(7, 'sam@gmail.com', 'sam', 'sam w', 'qwe', '123', '2018-08-02', 'kudus', '123', '037000', 150000, 'asd1', '2018-09-10 08:38:56'),
(8, 'billy@gmail.com', 'bil', 'billy b', 'qwe', '12312', '2018-08-02', 'taman siswa', '123', '037002', 230000, 'asd1', '2018-09-06 08:16:21'),
(9, 'argo@gmail.com', 'argo', 'argo uchiha', 'qwe', '123', '2018-08-01', 'godean', '123', '037008', 450000, 'asd1', '2018-08-15 05:25:15'),
(10, 'kadinugraha@gmail.com', '', 'kristian adi', 'qwe', '123', '2018-08-05', 'godean', '123', '037009', 450000, 'asd1', '2018-08-15 05:47:30'),
(11, 'kw@gmail.com', 'katon10', 'katon wijana', '123', '123', '2018-08-08', 'godean', '123', '037010', 400000, 'asd1', '2018-08-21 09:33:24'),
(12, 'hb@gmail.com', 'halim12', 'halim budi', 'qwe', '123', '2018-08-09', 'maguwo', 'qwe', '037011', 500000, 'asd1', '2018-08-21 09:33:25'),
(13, 'ivan@gmail.com', 'ivan12', 'ivan', 'qwe', '123', '2018-08-03', 'klitren', 'qwe', '037012', 450000, 'asd1', '2018-08-15 05:52:22'),
(14, 'kf@gmail.com', 'Kornelius13', 'Kornelius Fredy', 'qwerty123', '3323051232960007', '1996-07-20', 'bonbin sana lagi', '123456', '037013', 350000, 'asd1', '2018-09-06 04:55:56'),
(15, 'asdasd@gmail.com', 'vincent14', 'vincent fernando', '123321', '123333333', '1996-05-05', 'nologaten', '654321', '037014', 550000, 'asd1', '2018-09-06 04:55:56'),
(16, 'mega@gmail.com', 'Mega15', 'Mega Insan', '123321', '1233332343', '1997-06-07', 'jakal', '111111', '037015', 450000, 'asd1', '2018-09-07 10:03:01'),
(18, 'deni@gmail.com', 'Deni16', 'Deni Wijaya', '1233231sad1', '1232226', '1998-06-07', 'jakal', '111111', '037016', 450000, 'asd1', '2018-09-07 10:09:41'),
(19, 'rafi@gmail.com', 'Rafi17', 'Rafi Dwi', '123asad1', '123222926', '1999-06-07', 'jakal', '111111', '037017', 450000, 'asd1', '2018-09-07 10:12:40'),
(20, '', '18', '', '', '', '1970-01-01', '', '', '037018', 450000, 'asd1', '2018-09-07 10:15:55'),
(21, 'fitra@gmail.com', 'Fitra19', 'Fitra Rio', '11923asad1', '1232225926', '1994-06-07', 'jakal', '111111', '037019', 450000, 'asd1', '2018-09-07 10:16:29'),
(22, 'orz@gmail.com', 'Risaldi20', 'Risaldi Hartono', '17923asad1', '123145926', '1990-06-07', 'godean', '121321', '037020', 450000, 'asd1', '2018-09-07 10:33:53'),
(23, 'dim@gmail.com', 'Adimas21', 'Adimas Firman', '19993asad1', '12399926', '1992-06-07', 'umy sana', '999999', '037021', 450000, 'asd1', '2018-09-07 10:35:05'),
(24, 'iam@gmail.com', 'Priambodo22', 'Priambodo Pangarsa', '29993asad1', '1239776', '1993-08-10', 'Gejayan', '999999', '037022', 450000, 'asd1', '2018-09-07 10:37:13'),
(25, 'uzan@gmail.com', 'Fauzan23', 'Fauzan Set', '2999311dac1', '15151', '1994-08-10', 'Kronggahan', '999999', '037023', 450000, 'asd1', '2018-09-07 10:40:11'),
(26, 'eddy@gmail.com', 'Eddy24', 'Eddy villager', '2992141', '15124551', '1995-09-10', 'Jombor', '999999', '037024', 450000, 'asd1', '2018-09-10 07:22:47'),
(27, 'aloy@gmail.com', 'Aloy25', 'Aloy Gombong', '12345678', '15124551', '2018-09-02', 'Jakal', '999999', '037025', 450000, 'asd1', '2018-09-13 07:51:08'),
(28, 'nanz@gmail.com', 'Dwinanda26', 'Dwinanda Edo', '12345678', '15124551', '1992-09-10', 'binong', '999999', '037026', 450000, 'asd1', '2018-09-17 09:19:26'),
(29, 'derynanz@gmail.com', 'Dery27', 'Dery Zulkarnaen', '12345678', '15124551', '1997-09-10', 'taiwan', '999999', '037027', 450000, 'asd1', '2018-09-17 09:38:45'),
(30, 'adit@gmail.com', 'Aditya28', 'Aditya Putrau', '12345678', '15124551', '1997-09-10', 'gatau', '999999', '037028', 450000, 'asd1', '2018-09-17 09:58:38'),
(31, 'kira@gmail.com', 'Ramzha29', 'Ramzha', '12345678', '15124551', '1997-09-10', 'minomartani', '999999', '037029', 450000, 'asd1', '2018-09-17 10:03:11'),
(32, 'yudz@gmail.com', 'Yudz30', 'Yudz', '12345678', '15124551', '1990-09-10', 'Bandung', '999999', '037030', 450000, 'asd1', '2018-09-17 10:28:39'),
(33, 'japz@gmail.com', 'japz31', 'japz fajar', '12345678', '15124551', '1990-09-10', 'Bandung', '999999', '037031', 450000, 'asd1', '2018-09-17 10:32:17'),
(34, 'choe@gmail.com', 'choerul32', 'choerul', '12345678', '15124551', '1990-09-10', 'Cimahi', '999999', '037032', 450000, 'asd1', '2018-09-18 03:35:38'),
(35, 'Ir@gmail.com', 'Irfany33', 'Irfany', '12345678', '15124551', '1990-09-10', 'Cimahi', '999999', '037033', 450000, 'asd1', '2018-09-18 03:48:19'),
(36, 'Daniel@gmail.com', 'Daniel34', 'Daniel Hartono', '12345678', '15124551', '1996-09-10', 'Jember', '999999', '037034', 450000, 'asd1', '2018-09-18 04:23:19'),
(37, 'krusvi@gmail.com', 'Hendri35', 'Hendri Krusvi', '12345678', '15124551', '1996-09-10', 'Jogja', '999999', '037035', 450000, 'asd1', '2018-09-18 04:35:54'),
(38, 'vano@gmail.com', 'Vano36', 'Vano Gurhitno', '12345678', '15124551', '1990-09-10', 'Seturan', '999999', '037036', 450000, 'asd1', '2018-09-18 06:39:35'),
(39, 'parkz@gmail.com', 'Daniel37', 'Daniel Ronald', '12345678', '15124551', '1986-09-10', 'Temanggung', '999999', '037037', 450000, 'asd1', '2018-09-18 06:57:32'),
(40, 'theo@gmail.com', 'Theofilus38', 'Theofilus Sigit', '12345678', '15124551', '1986-09-10', 'Manding', '999999', '037038', 450000, 'asd1', '2018-09-18 07:00:18'),
(41, 'adrian@gmail.com', 'Adrian39', 'Adrian Hardy', '12345678', '15124551', '1986-09-10', 'Semarang', '999999', '037039', 450000, 'asd1', '2018-09-18 07:09:54'),
(42, 'ib@gmail.com', 'Indrabayu40', 'Indrabayu', '123123indra', '3312414124', '1990-11-11', 'semarang', '234412', '037040', 450000, 'asd1', '2018-09-19 08:08:37'),
(43, 'kagayaku@gmail.com', 'Gilang41', 'Gilang Kg', '12345gilang', '3312414124', '1990-11-11', 'jec', '234432', '037041', 450000, 'asd1', '2018-09-19 08:10:42'),
(44, 'bayou@gmail.com', 'Bayu42', 'Bayu dbijak', '654321by12', '3312414124', '1990-11-11', 'magelang', '234432', '037042', 450000, 'asd1', '2018-09-19 08:12:01'),
(45, 'ando@gmail.com', 'Armando43', 'Armando Firdaus', '65432arm12', '3312414124', '1990-11-11', 'magelang', '232323', '037043', 450000, 'asd1', '2018-09-19 08:19:54'),
(46, 'vievin.efendy@ti.ukdw.ac.id', 'Vievin44', 'Vievin Efendy', 'ff68179dddd38692293d04c091d017ff', '3372024109970003', '1997-09-01', 'Surakarta', '1c88b390f7a3d49edfbeff983c85c2f4', '037044', 400000, 'asd1', '2018-09-20 02:27:09');

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
('20025', '082221110041', 'Indosat', 100000),
('20026', '082221110041', 'Indosat', 50000),
('20027', '082221110041', 'Indosat', 20000),
('20028', '081212515', 'Indosat', 50000),
('20029', '081212515', 'Indosat', 50000),
('20031', '0821314251', 'Indosat', 50000),
('20036', '081225515', 'Indosat', 50000),
('20037', '081225515', 'Indosat', 50000);

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
('10024', 14, '2018-09-06 04:55:56', 'Berhasil', 'Berhasil Transfer'),
('10032', 2, '2018-09-12 07:52:27', 'Gagal', 'Nomor rekening tujuan tidak ditemukan'),
('10033', 2, '2018-09-12 07:53:39', 'Gagal', 'Nomor rekening tujuan tidak ditemukan'),
('10034', 2, '2018-09-12 08:46:04', 'Berhasil', 'Berhasil Transfer'),
('10035', 2, '2018-09-12 08:50:08', 'Berhasil', 'Berhasil Transfer'),
('10038', 46, '2018-09-20 02:27:09', 'Berhasil', 'Berhasil Transfer'),
('15', 2, '2018-08-20 08:23:15', 'Berhasil', 'Berhasil transfer'),
('20004', 1, '2018-08-20 04:19:59', 'Berhasil', ''),
('20015', 2, '2018-08-20 09:20:35', 'Berhasil', 'Pembelian pulsa berhasil'),
('20016', 2, '2018-08-20 10:25:46', 'Berhasil', 'Pembelian pulsa berhasil'),
('20025', 8, '2018-09-06 07:58:40', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20026', 8, '2018-09-06 08:15:50', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20027', 8, '2018-09-06 08:16:21', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20028', 7, '2018-09-10 08:24:24', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20029', 7, '2018-09-10 08:28:24', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20030', 7, '2018-09-10 08:28:50', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('20031', 7, '2018-09-10 08:38:56', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20036', 2, '2018-09-14 07:14:27', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20037', 1, '2018-09-14 07:16:41', 'Berhasil', 'Berhasil Membeli Pulsa');

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
('10024', '037014', 100000, 'buat nando'),
('10034', '037001', 50000, 'coba'),
('10035', '037001', 50000, 'testing api'),
('10038', '037001', 50000, 'transfer'),
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
  MODIFY `id_nasabah` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

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
