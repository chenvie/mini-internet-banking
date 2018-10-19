# DOKUMENTASI URL API 

## LOGIN
1. 
url	: localhost:8080/login
2. 
method	: POST

3. input	: username, password (JSON)

4. output	: status, username, message (JSON)



## READ

1. url	: localhost:8080/nasabah
2. 
method	: GET
3. 
output	: List<nasabah> : id_nasabah, email, username, nama_lengkap, password, no_ktp, tgl_lahir, alamat, created(JSON)



## READ-ONE
1. 
url	: localhost:8080/nasabah/{id}

2. method	: GET
3. 
input	: id (param)

4. output	: nasabah : id_nasabah, email, username, nama_lengkap, password, no_ktp, tgl_lahir, alamat, created
	  		  List<Rekening> :  no_rek, kode_rahasia, jml_saldo, kode_cabang, created  
	  (JSON)

## 


BUKA REKENING (CREATE NASABAH)
1. 
url	: localhost:8080/nasabah

2. method	: POST

3. input	: nama_lengkap, email, password, no_ktp, tgl_lahir, alamat, kode_rahasia (JSON)

4. output	: no_rek, message, status, username(JSON)

## 

TAMBAH REKENING
1. 
url	: localhost:8080/rekening
2. 
method	: POST
3. 
input	: id_nasabah,kode_rahasia (JSON)

4. output	: no_rek, message, status (JSON)

## 

READ MUTASI
1. 
url	: localhost:8080/mutasi/{no_rek}

2. method	: GET
3. input	: no_rek (param)
4. 
output	: JSON Array 
	
			respon : no_rek,tgl_awal, tgl_akhir
	
			result : List<mutasi> : kode_transaksi, tgl_trans, tujuan, jenis, keterangan, nominal 



## READ HISTORY

1. url	: localhost:8080/history/{no_rek}/{tgl_awal}/{tgl_akhir}
	
		contoh format tanggal : localhost:8080/history/037015/2018-08-15/2018-09-13
2. 
method	: GET
3. 
input	: no_rek, tgl_awal, tgl_akhir (param)
4. 
output	: JSON Array 
	
			respon : no_rek, tgl_awal, tgl_akhir
	
			result : List<history> : kode_transaksi, tgl_trans, tujuan, keterangan, nominal, status(JSON)

## 


UPDATE PASSWORD

1. url	: localhost:8080/update_password
2. 
method	: POST
3. 
input	: id_nasabah, passwordl, passwordb1, passwordb2 (JSON)
4. 
output	: message, status (JSON)




## UPDATE CODE
url	: localhost:8080/update_kode_rahasia
1. 
method	: POST
2. 
input	: no_rek, kode_rahasiaL, krb1, krb2 (JSON)

3. output	: message, status (JSON)




## TRANSFER (CREATE)
1. 
url	: localhost:8080/transfer
2. 
method	: POST
3. 
input	: norek_kirim (pengirim), norek_terima (penerima), nominal, ket, kode_rhs (JSON)
4. 
output	: status, message (JSON)




## TRANSFER (CEK NO REK)
1. 
url	: localhost:8080/ceknorek
2. 
method	: POST
3. 
input	: norek_kirim (pengirim), norek_terima (penerima) (JSON)
4. 
output	: status, message (JSON)

## 


PEMBELIAN PULSA (CREATE)
1. 
url	: localhost:8080/pembelian
2. 
method	: POST

3. input	: norek (pembeli), no_hp_tujuan, provider, nominal, kode_rhs (JSON)

4. output	: status, message (JSON)

