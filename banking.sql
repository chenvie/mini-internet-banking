-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 27, 2018 at 08:39 AM
-- Server version: 10.1.34-MariaDB
-- PHP Version: 7.2.8

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
CREATE DEFINER=`root`@`localhost` PROCEDURE `cekNoRek` (IN `norek_kirim` VARCHAR(20), IN `norek_terima` VARCHAR(16), OUT `stts` VARCHAR(20), OUT `msg` VARCHAR(70))  NO SQL
BEGIN 
DECLARE jml int;
DECLARE kodeT varchar(12) DEFAULT "1";

SELECT COUNT(no_rek) into jml
from rekening
where no_rek = norek_terima;


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



insert into transaksi SET kode_transaksi = kodeT,no_rek = norek_kirim,status = stts, ket_status = msg;
END IF;
SELECT stts, msg;
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `getHistory` (IN `norek` VARCHAR(16), IN `tgl_awal` DATE, IN `tgl_akhir` DATE)  BEGIN
select DISTINCT t.kode_transaksi,t.tgl_trans, IF (substr(t.kode_transaksi,1,1) = '1',CONCAT('Transfer ke ',r.rek_transfer),'Pembelian Pulsa') as tujuan, IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan, IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal, t.status 
from transaksi t, transfer r, pulsa p, rekening g 
where g.no_rek = norek AND t.no_rek = g.no_rek AND (t.kode_transaksi = r.kode_transfer OR t.kode_transaksi = p.kode_pembelian) AND (t.tgl_trans >=tgl_awal AND t.tgl_trans <=tgl_akhir) ORDER BY t.tgl_trans ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getListNoRek` (IN `id_nsb` INT)  NO SQL
BEGIN
SELECT no_rek,kode_rahasia,jml_saldo,kode_cabang,created from rekening where id_nasabah = id_nsb;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMutasi` (IN `norek` VARCHAR(16), IN `tgl_awal` DATE, IN `tgl_akhir` DATE)  NO SQL
BEGIN
select DISTINCT t.kode_transaksi,t.tgl_trans,
                    IF (substr(t.kode_transaksi,1,1) = '1', 
                    (if (g.no_rek = r.rek_transfer,
                    CONCAT('Transfer dari ',(SELECT no_rek from rekening where no_rek = t.no_rek)),CONCAT('Transfer ke ',r.rek_transfer))),
                    'Pembelian Pulsa') as tujuan,
                    IF (substr(t.kode_transaksi,1,1) = '1',IF(g.no_rek = r.rek_transfer,'CR','DB'),'DB') as jenis,
                    IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan,
                    IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal
                    from transaksi t, transfer r, pulsa p, rekening g
                    where (g.no_rek = norek AND (t.no_rek = g.no_rek or g.no_rek = r.rek_transfer)) AND 
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `postNasabah2` (IN `nama` VARCHAR(100), IN `_email` VARCHAR(40), IN `pwd` VARCHAR(100), IN `no_ktp` VARCHAR(20), IN `tgl_lahir` DATE, IN `alamat` VARCHAR(100), IN `kr` VARCHAR(100), OUT `uname` VARCHAR(20), OUT `stts` VARCHAR(10), OUT `msg` VARCHAR(40), OUT `norek` VARCHAR(16), OUT `id` INT)  NO SQL
BEGIN

DECLARE kc varchar(4) DEFAULT "asd1";
DECLARE counter int;
DECLARE diff int;
DECLARE idnsb int;

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
   rekening
   ORDER by no_rek DESC LIMIT 1;

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
email=_email, username=uname, nama_lengkap=nama, password=pwd, no_ktp=no_ktp, tgl_lahir=tgl_lahir, alamat=alamat;

SELECT id_nasabah INTO idnsb
FROM nasabah ORDER by id_nasabah DESC LIMIT 1;

/*membuat rekening baru*/
INSERT INTO rekening
SET
no_rek = norek, id_nasabah = idnsb, kode_rahasia = kr, kode_cabang = kc;

SET id = idnsb;
SET stts = "Berhasil";
SET msg = "Penambahan nasabah berhasil";
END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `postRekening` (IN `idnsb` INT, IN `kr` VARCHAR(100), OUT `norek` VARCHAR(16), OUT `stts` VARCHAR(10), OUT `msg` VARCHAR(40))  NO SQL
BEGIN

DECLARE kc varchar(4) DEFAULT "asd1";

/*Generate nomor rekening baru*/
SELECT 
    no_rek into norek
FROM 
   rekening
   ORDER by created DESC LIMIT 1;

SET norek = CAST(norek as int);
SET norek = norek+1;
SET norek = CAST(norek as char(6));
SET norek = CONCAT(0,norek);

/*membuat rekening baru*/
INSERT INTO rekening
SET
no_rek = norek, id_nasabah = idnsb, kode_rahasia = kr, kode_cabang = kc;

SET stts = "Berhasil";
SET msg = "Berhasil membuat rekening baru";

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `postTransaksiPulsa` (IN `norek` VARCHAR(16), IN `no_hp_tujuan` VARCHAR(20), IN `nmnl` INT(50), IN `prvdr` VARCHAR(10), IN `kode_rhs` VARCHAR(100), OUT `stts` VARCHAR(8), OUT `ket_stts` VARCHAR(70))  NO SQL
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
from rekening where no_rek = norek;

/*get kode rahasia*/
SELECT kode_rahasia into kr_temp
from rekening where kode_rahasia = kode_rhs and no_rek = norek;
    
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
    UPDATE rekening 
    SET jml_saldo = jml_saldo_out
    WHERE no_rek = norek;
    
END IF;

INSERT INTO transaksi 
SET kode_transaksi=kodeT, no_rek = norek, status=stts, ket_status=ket_stts;

IF stts = "Berhasil" THEN
INSERT INTO pulsa
SET kode_pembelian=kodeT, no_hp=no_hp_tujuan, provider=prvdr, nominal=nmnl;
END IF;
SELECT stts,ket_stts;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `postTransaksiTransfer` (IN `norek_kirim` VARCHAR(16), IN `norek_terima` VARCHAR(16), IN `nmnl` INT(50), IN `ket` TEXT, IN `kode_rhs` VARCHAR(100), OUT `stts` VARCHAR(8), OUT `ket_stts` VARCHAR(70))  NO SQL
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
from rekening where no_rek = norek_kirim;

/*get jml saldo penerima*/
SELECT jml_saldo into jml_saldo_in
from rekening where no_rek = norek_terima;

/*get kode rahasia*/
SELECT kode_rahasia into kr_temp
from rekening where kode_rahasia = kode_rhs and no_rek = norek_kirim;
    
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
    
    UPDATE rekening 
    SET jml_saldo = jml_saldo_out
    WHERE no_rek = norek_kirim;
    
    UPDATE rekening
    SET jml_saldo = jml_saldo_in
    where no_rek = norek_terima;
    
    END IF;

INSERT INTO transaksi 
SET kode_transaksi=kodeT, no_rek = norek_kirim, status=stts, ket_status=ket_stts;


IF stts = "Berhasil" THEN
INSERT INTO transfer
SET kode_transfer=kodeT, rek_transfer=norek_terima, nominal=nmnl, keterangan=ket;
END IF;
SELECT stts,ket_stts;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `putNasabahKodeRahasia` (IN `norek` VARCHAR(16), IN `krhLama` VARCHAR(100), IN `krhBaru1` VARCHAR(100), IN `krhBaru2` VARCHAR(100), OUT `stts` VARCHAR(10), OUT `msg` VARCHAR(60))  NO SQL
BEGIN
DECLARE krh_temp varchar(100);
SELECT kode_rahasia INTO krh_temp
from rekening
where no_rek = norek;

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
update rekening
set kode_rahasia = krhBaru1
where no_rek= norek;
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
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `nasabah`
--

INSERT INTO `nasabah` (`id_nasabah`, `email`, `username`, `nama_lengkap`, `password`, `no_ktp`, `tgl_lahir`, `alamat`, `created`) VALUES
(1, 'reinald.a.k@gmail.com', 'reinaldd', 'reinalda ar', 'reinaldariel123', '3323031211960005', '2018-08-06', 'temanggung', '2018-09-14 07:16:41'),
(2, 'boni@gmail.com', 'bonii', 'bonifasius', 'boniboni123', '123513163', '2018-02-05', 'magelang', '2018-09-20 04:58:53'),
(3, 'asd@gmail.com', 'coba', 'coba', 'qweqweqwe', '123', '2018-08-02', 'coba', '2018-08-16 03:26:59'),
(4, 'dany@gmail.com', 'cocobaba', 'dany', '123', '123', '2018-08-02', 'yogya', '2018-08-15 03:04:48'),
(6, 'cipe@gmail.com', 'cipe', 'asd asd', '123', '123', '2018-01-01', 'disana', '2018-09-12 08:50:08'),
(7, 'sam@gmail.com', 'sam', 'sam w', 'qwe', '123', '2018-08-02', 'kudus', '2018-09-10 08:38:56'),
(8, 'billy@gmail.com', 'bil', 'billy b', 'qwe', '12312', '2018-08-02', 'taman siswa', '2018-09-06 08:16:21'),
(9, 'argo@gmail.com', 'argo', 'argo uchiha', 'qwe', '123', '2018-08-01', 'godean', '2018-08-15 05:25:15'),
(10, 'kadinugraha@gmail.com', '', 'kristian adi', 'qwe', '123', '2018-08-05', 'godean', '2018-08-15 05:47:30'),
(11, 'kw@gmail.com', 'katon10', 'katon wijana', '123', '123', '2018-08-08', 'godean', '2018-08-21 09:33:24'),
(12, 'hb@gmail.com', 'halim12', 'halim budi', 'qwe', '123', '2018-08-09', 'maguwo', '2018-08-21 09:33:25'),
(13, 'ivan@gmail.com', 'ivan12', 'ivan', 'qwe', '123', '2018-08-03', 'klitren', '2018-08-15 05:52:22'),
(14, 'kf@gmail.com', 'Kornelius13', 'Kornelius Fredy', 'qwerty123', '3323051232960007', '1996-07-20', 'bonbin sana lagi', '2018-09-06 04:55:56'),
(15, 'asdasd@gmail.com', 'vincent14', 'vincent fernando', '123321', '123333333', '1996-05-05', 'nologaten', '2018-09-06 04:55:56'),
(16, 'mega@gmail.com', 'Mega15', 'Mega Insan', '123321', '1233332343', '1997-06-07', 'jakal', '2018-09-07 10:03:01'),
(18, 'deni@gmail.com', 'Deni16', 'Deni Wijaya', '1233231sad1', '1232226', '1998-06-07', 'jakal', '2018-09-07 10:09:41'),
(19, 'rafi@gmail.com', 'Rafi17', 'Rafi Dwi', '123asad1', '123222926', '1999-06-07', 'jakal', '2018-09-07 10:12:40'),
(20, '', '18', '', '', '', '1970-01-01', '', '2018-09-07 10:15:55'),
(21, 'fitra@gmail.com', 'Fitra19', 'Fitra Rio', '11923asad1', '1232225926', '1994-06-07', 'jakal', '2018-09-07 10:16:29'),
(22, 'orz@gmail.com', 'Risaldi20', 'Risaldi Hartono', '17923asad1', '123145926', '1990-06-07', 'godean', '2018-09-07 10:33:53'),
(23, 'dim@gmail.com', 'Adimas21', 'Adimas Firman', '19993asad1', '12399926', '1992-06-07', 'umy sana', '2018-09-07 10:35:05'),
(24, 'iam@gmail.com', 'Priambodo22', 'Priambodo Pangarsa', '29993asad1', '1239776', '1993-08-10', 'Gejayan', '2018-09-07 10:37:13'),
(25, 'uzan@gmail.com', 'Fauzan23', 'Fauzan Set', '2999311dac1', '15151', '1994-08-10', 'Kronggahan', '2018-09-07 10:40:11'),
(26, 'eddy@gmail.com', 'Eddy24', 'Eddy villager', '2992141', '15124551', '1995-09-10', 'Jombor', '2018-09-10 07:22:47'),
(27, 'aloy@gmail.com', 'Aloy25', 'Aloy Gombong', '12345678', '15124551', '2018-09-02', 'Jakal', '2018-09-13 07:51:08'),
(28, 'nanz@gmail.com', 'Dwinanda26', 'Dwinanda Edo', '12345678', '15124551', '1992-09-10', 'binong', '2018-09-17 09:19:26'),
(29, 'derynanz@gmail.com', 'Dery27', 'Dery Zulkarnaen', '12345678', '15124551', '1997-09-10', 'taiwan', '2018-09-17 09:38:45'),
(30, 'adit@gmail.com', 'Aditya28', 'Aditya Putrau', '12345678', '15124551', '1997-09-10', 'gatau', '2018-09-17 09:58:38'),
(31, 'kira@gmail.com', 'Ramzha29', 'Ramzha', '12345678', '15124551', '1997-09-10', 'minomartani', '2018-09-17 10:03:11'),
(32, 'yudz@gmail.com', 'Yudz30', 'Yudz', '12345678', '15124551', '1990-09-10', 'Bandung', '2018-09-17 10:28:39'),
(33, 'japz@gmail.com', 'japz31', 'japz fajar', '12345678', '15124551', '1990-09-10', 'Bandung', '2018-09-17 10:32:17'),
(34, 'choe@gmail.com', 'choerul32', 'choerul', '12345678', '15124551', '1990-09-10', 'Cimahi', '2018-09-18 03:35:38'),
(35, 'Ir@gmail.com', 'Irfany33', 'Irfany', '12345678', '15124551', '1990-09-10', 'Cimahi', '2018-09-18 03:48:19'),
(36, 'Daniel@gmail.com', 'Daniel34', 'Daniel Hartono', '12345678', '15124551', '1996-09-10', 'Jember', '2018-09-18 04:23:19'),
(37, 'krusvi@gmail.com', 'Hendri35', 'Hendri Krusvi', '12345678', '15124551', '1996-09-10', 'Jogja', '2018-09-18 04:35:54'),
(38, 'vano@gmail.com', 'Vano36', 'Vano Gurhitno', '12345678', '15124551', '1990-09-10', 'Seturan', '2018-09-18 06:39:35'),
(39, 'parkz@gmail.com', 'Daniel37', 'Daniel Ronald', '12345678', '15124551', '1986-09-10', 'Temanggung', '2018-09-18 06:57:32'),
(40, 'theo@gmail.com', 'Theofilus38', 'Theofilus Sigit', '12345678', '15124551', '1986-09-10', 'Manding', '2018-09-18 07:00:18'),
(41, 'adrian@gmail.com', 'Adrian39', 'Adrian Hardy', '12345678', '15124551', '1986-09-10', 'Semarang', '2018-09-18 07:09:54'),
(42, 'ib@gmail.com', 'Indrabayu40', 'Indrabayu', '123123indra', '3312414124', '1990-11-11', 'semarang', '2018-09-19 08:08:37'),
(43, 'kagayaku@gmail.com', 'Gilang41', 'Gilang Kg', '12345gilang', '3312414124', '1990-11-11', 'jec', '2018-09-19 08:10:42'),
(44, 'bayou@gmail.com', 'Bayu42', 'Bayu dbijak', '654321by12', '3312414124', '1990-11-11', 'magelang', '2018-09-19 08:12:01'),
(45, 'ando@gmail.com', 'Armando43', 'Armando Firdaus', '65432arm12', '3312414124', '1990-11-11', 'magelang', '2018-09-19 08:19:54'),
(46, 'hadi@gmail.com', 'Hadi44', 'Hadi wijaya', '12345678', '15124551', '1986-09-10', 'Yogyakarta', '2018-09-24 07:43:33'),
(47, 'aji@gmail.com', 'Imanuel46', 'Imanuel Aji', '12345678', '15124551', '1990-09-10', 'Yogyakarta', '2018-09-24 07:49:38'),
(48, 'vievin.efendy@ti.ukdw.ac.id', 'Vievin47', 'Vievin Efendy', 'ff68179dddd38692293d04c091d017ff', '3372024109970003', '1997-09-01', 'Surakarta', '2018-09-25 06:11:19'),
(49, 'jaehwan_kim@naver.com', 'Jaehwan48', 'Jaehwan Kim', '5994b01d753d52410bd6f17a5da647f4', '02382783209832', '1996-05-27', 'South Korea', '2018-09-25 09:41:21'),
(50, 'daehwi_lee@naver.com', 'Daehwi49', 'Daehwi Lee', '273d9bd3ef50d81d70fd9150c9404565', '283702489403', '2001-01-29', 'South Korea', '2018-09-25 09:46:19'),
(54, 'daniel_kang@naver.com', 'Daniel50', 'Daniel Kang', 'b5ea8985533defbf1d08d5ed2ac8fe9b', '01084793403', '1996-12-10', 'South Korea', '2018-09-25 09:58:54'),
(61, 'yosefresi@gmail.com', 'Yosef20', 'Yosef Resi', '34b558355be3635700741e243a9d4c2a', '123', '1990-08-08', 'qweqwe', '2018-09-27 06:32:04');

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
('20037', '081225515', 'Indosat', 50000),
('20038', '098765568', 'Telkomsel', 50000),
('20042', '081234567', 'Indosat', 50000),
('20043', '085201258593', 'Telkomsel', 25000);

-- --------------------------------------------------------

--
-- Table structure for table `rekening`
--

CREATE TABLE `rekening` (
  `no_rek` varchar(16) NOT NULL,
  `id_nasabah` int(10) NOT NULL,
  `kode_rahasia` varchar(100) NOT NULL,
  `jml_saldo` int(11) NOT NULL DEFAULT '450000',
  `kode_cabang` varchar(10) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `rekening`
--

INSERT INTO `rekening` (`no_rek`, `id_nasabah`, `kode_rahasia`, `jml_saldo`, `kode_cabang`, `created`) VALUES
('0249120', 61, '4297f44b13955235245b2497399d7a93', 450000, 'asd1', '2018-09-27 06:32:04'),
('037000', 7, '123', 150000, 'asd1', '2018-09-10 08:38:56'),
('037001', 6, 'asda', 4800000, 'asd1', '2018-09-12 08:50:08'),
('037002', 8, '123', 230000, 'asd1', '2018-09-06 08:16:21'),
('037008', 9, '123', 450000, 'asd1', '2018-08-15 05:25:15'),
('037009', 10, '123', 450000, 'asd1', '2018-08-15 05:47:30'),
('037010', 11, '123', 400000, 'asd1', '2018-08-21 09:33:24'),
('037011', 12, 'qwe', 500000, 'asd1', '2018-08-21 09:33:25'),
('037012', 13, 'qwe', 450000, 'asd1', '2018-08-15 05:52:22'),
('037013', 14, '123456', 350000, 'asd1', '2018-09-06 04:55:56'),
('037014', 15, '654321', 550000, 'asd1', '2018-09-06 04:55:56'),
('037015', 16, '111111', 250000, 'asd1', '2018-09-07 10:03:01'),
('037016', 18, '111111', 450000, 'asd1', '2018-09-07 10:09:41'),
('037017', 19, '111111', 450000, 'asd1', '2018-09-07 10:12:40'),
('037018', 20, '', 450000, 'asd1', '2018-09-07 10:15:55'),
('037019', 21, '111111', 450000, 'asd1', '2018-09-07 10:16:29'),
('037020', 22, '121321', 450000, 'asd1', '2018-09-07 10:33:53'),
('037021', 23, '999999', 450000, 'asd1', '2018-09-07 10:35:05'),
('037022', 24, '999999', 450000, 'asd1', '2018-09-07 10:37:13'),
('037023', 25, '999999', 450000, 'asd1', '2018-09-07 10:40:11'),
('037024', 26, '999999', 450000, 'asd1', '2018-09-10 07:22:47'),
('037025', 27, '999999', 450000, 'asd1', '2018-09-13 07:51:08'),
('037026', 28, '999999', 450000, 'asd1', '2018-09-17 09:19:26'),
('037027', 29, '999999', 450000, 'asd1', '2018-09-17 09:38:45'),
('037028', 30, '999999', 450000, 'asd1', '2018-09-17 09:58:38'),
('037029', 31, '999999', 450000, 'asd1', '2018-09-17 10:03:11'),
('037030', 32, '999999', 450000, 'asd1', '2018-09-17 10:28:39'),
('037031', 33, '999999', 450000, 'asd1', '2018-09-17 10:32:17'),
('037032', 34, '999999', 450000, 'asd1', '2018-09-18 03:35:38'),
('037033', 35, '999999', 450000, 'asd1', '2018-09-18 03:48:19'),
('037034', 36, '999999', 450000, 'asd1', '2018-09-18 04:23:19'),
('037035', 37, '999999', 450000, 'asd1', '2018-09-18 04:35:54'),
('037036', 38, '999999', 450000, 'asd1', '2018-09-18 06:39:35'),
('037037', 39, '999999', 450000, 'asd1', '2018-09-18 06:57:32'),
('037038', 40, '999999', 450000, 'asd1', '2018-09-18 07:00:18'),
('037039', 41, '999999', 450000, 'asd1', '2018-09-18 07:09:54'),
('037040', 42, '234412', 450000, 'asd1', '2018-09-19 08:08:37'),
('037041', 43, '234432', 450000, 'asd1', '2018-09-19 08:10:42'),
('037042', 44, '234432', 450000, 'asd1', '2018-09-19 08:12:01'),
('037043', 45, '232323', 450000, 'asd1', '2018-09-19 08:19:54'),
('037044', 2, '111222', 550000, 'asd1', '2018-09-21 10:54:17'),
('037045', 46, '999999', 450000, 'asd1', '2018-09-24 07:49:00'),
('037046', 47, '999999', 450000, 'asd1', '2018-09-24 07:49:38'),
('037047', 48, '1c88b390f7a3d49edfbeff983c85c2f4', 425000, 'asd1', '2018-09-25 06:11:19'),
('037048', 49, '98467a817e2ff8c8377c1bf085da7138', 450000, 'asd1', '2018-09-25 09:41:21'),
('037049', 50, 'f39c8f313f3449a39d36c761d028efc7', 450000, 'asd1', '2018-09-25 09:46:19'),
('037050', 54, '4c969d7049af7e978d8b617c5014d7f9', 450000, 'asd1', '2018-09-25 09:58:54'),
('037051', 48, 'vievin01', 450000, 'asd1', '2018-09-25 10:47:36'),
('123213213', 4, '123', 1000000, 'asd1', '2018-08-15 03:04:48'),
('1919191919', 3, '123', 1500000, 'asd2', '2018-08-16 03:26:59'),
('2141516', 1, '123124', 1950000, 'asd1', '2018-09-14 07:16:41'),
('2491204', 2, '123124', 2600000, 'asd1', '2018-09-20 04:58:53');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `kode_transaksi` varchar(20) NOT NULL,
  `no_rek` varchar(16) NOT NULL,
  `tgl_trans` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` varchar(8) NOT NULL,
  `ket_status` varchar(70) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`kode_transaksi`, `no_rek`, `tgl_trans`, `status`, `ket_status`) VALUES
('10001', '2141516', '2018-09-21 06:45:54', 'Berhasil', ''),
('10002', '2491204', '2018-09-21 06:45:54', 'Berhasil', ''),
('10003', '2491204', '2018-09-21 06:45:54', 'Gagal', ''),
('10006', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('10007', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('10008', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('10009', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('10010', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('10011', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Jumlah yang ditransfer terlalu kecil'),
('10012', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Nomor rekening tujuan tidak ditemukan'),
('10013', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Kode rahasia salah'),
('10014', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('10017', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Nomor rekening tujuan ditemukan'),
('10018', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Nomor rekening tujuan ditemukan'),
('10019', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Nomor rekening tujuan ditemukan'),
('10020', '037000', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('10021', '037000', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('10022', '037000', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('10023', '037010', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('10024', '037013', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Transfer'),
('10032', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Nomor rekening tujuan tidak ditemukan'),
('10033', '2491204', '2018-09-21 06:45:54', 'Gagal', 'Nomor rekening tujuan tidak ditemukan'),
('10034', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Transfer'),
('10035', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Transfer'),
('10039', '037015', '2018-09-21 10:28:33', 'Berhasil', 'Berhasil Transfer'),
('10040', '2491204', '2018-09-24 09:50:31', 'Berhasil', 'Berhasil Transfer'),
('10041', '2491204', '2018-09-24 10:00:03', 'Berhasil', 'Berhasil Transfer'),
('15', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil transfer'),
('20004', '2141516', '2018-09-21 06:45:54', 'Berhasil', ''),
('20015', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Pembelian pulsa berhasil'),
('20016', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Pembelian pulsa berhasil'),
('20025', '037002', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20026', '037002', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20027', '037002', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20028', '037000', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20029', '037000', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20030', '037000', '2018-09-21 06:45:54', 'Gagal', 'Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda'),
('20031', '037000', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20036', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20037', '2141516', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20038', '2491204', '2018-09-21 06:45:54', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20042', '2491204', '2018-09-24 10:58:01', 'Berhasil', 'Berhasil Membeli Pulsa'),
('20043', '037047', '2018-09-25 09:07:27', 'Berhasil', 'Berhasil Membeli Pulsa');

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
('10039', '2491204', 50000, 'test terima'),
('10040', '037044', 50000, 'coba API'),
('10041', '037044', 50000, 'coba API'),
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
  ADD KEY `id_nasabah` (`id_nasabah`);

--
-- Indexes for table `pulsa`
--
ALTER TABLE `pulsa`
  ADD PRIMARY KEY (`kode_pembelian`);

--
-- Indexes for table `rekening`
--
ALTER TABLE `rekening`
  ADD PRIMARY KEY (`no_rek`),
  ADD KEY `id_nasabah` (`id_nasabah`),
  ADD KEY `kode_cabang` (`kode_cabang`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`kode_transaksi`),
  ADD KEY `kode_transfer` (`kode_transaksi`),
  ADD KEY `no_rek` (`no_rek`);

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
  MODIFY `id_nasabah` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `pulsa`
--
ALTER TABLE `pulsa`
  ADD CONSTRAINT `pulsa_ibfk_1` FOREIGN KEY (`kode_pembelian`) REFERENCES `transaksi` (`kode_transaksi`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `rekening`
--
ALTER TABLE `rekening`
  ADD CONSTRAINT `rekening_ibfk_1` FOREIGN KEY (`id_nasabah`) REFERENCES `nasabah` (`id_nasabah`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `rekening_ibfk_2` FOREIGN KEY (`kode_cabang`) REFERENCES `cabang_bank` (`kode_cabang`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`no_rek`) REFERENCES `rekening` (`no_rek`);

--
-- Constraints for table `transfer`
--
ALTER TABLE `transfer`
  ADD CONSTRAINT `transfer_ibfk_1` FOREIGN KEY (`kode_transfer`) REFERENCES `transaksi` (`kode_transaksi`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
