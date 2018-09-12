**API Mini Internet Banking**

- Nasabah :
    - create (post)
        - menambah/insert nasabah
        - in lewat json : 
            nama_lengkap, email, password, no_ktp, tgl_lahir, alamat, kode_rahasia
        - in lewat API :
            created, no_rek, username, kode_cabang, jml_saldo (defalut 450000)
        - in lewat query db :
            id_nasabah (auto increment)
        - out :
            true, false
    - login (post)
        - in lewat json : 
            username, password
        - out :
            true, false
    - read (get)
        - membaca semua data nasabah
        - out : json array $nasabah_arr :
            array [records] : id_nasabah, email, username, password, nama_lengkap, no_ktp, tgl_lahir, alamat, kode_rahasia, created
    - read-one (get)
        - membaca data dari salah satu nasabah yg diperlukan u/ aplikasi setalh login
        - in lewat url :
        ./API/nasabah/read-one.php?unm=x
        - x diganti dengan username nasabah 
        - dijalankan saat setelah login berhasil dan mengembalikan true, mengambil username dari textbox username saat login
        - out : json array :
            id_nasabah, username, password, nama_lengkap, kode_rahasia, tgl_lahir, jml_saldo, no_rek
    - update_kode_rahasia (post)
        - in lewat json :
            id_nasabah,kode_rahasiaL (kode rahasia lama), krb1,krb2 (kode rahasia baru 1 dan 2)
        - out lewat json array :
            - update : true, false
            - message : pesan2 kondisi update
    - update_password (post)
        - in lewat json :
            id_nasabah,passwordl (password lama), passwordb1,passwordb2 (pwd baru 1 dan 2)
        - out lewat json array :
            - update : true, false
            - message : pesan2 kondisi update

- Pulsa :
    - create (post)
        - beli pulsa
        - in lewat json : 
            username, no_hp_tujuan, id_nasabah, provider, kode_rahasia, nominal
        - json out :
             - pulsa : true, false
             - message : pesan2 kondisi pembelian pulsa

- Transfer :
    - cek-no-rek
        - untuk cek ketersediaan nomor rekening tujuan transfer
        - digunakan sebelum melakukan transfer
        - in lewat json : 
            no_rek_tujuan, id_nasabah, nominal, keterangan
        - yg dikirim tidak hanya no rek tujuan karena untuk pendataan di tabel transaksi jika gagal / salah memasukan nomor rekening
        - json out :
            - check : true, false
            - message : jika benar maka akan mengembalikan nomor rekening tujuan, jika salah mengembalikan pesan bahwa nomor rekening tujuan salah
    - create
        - melakukan transfer
        - in lewat json : 
            username, no_rek_tujuan, id_nasabah, kode_rahasia, nominal, keterangan
        - json out :
            - transfer : true, false
            - message : pesan2 kondisi transfer uang antar rekening
        
- Transaksi :
    - read-history 
        - untuk melihat history transaksi yang dilakukan satu nasabah tertentu
        - in lewat url:
            ./API/transaksi/read-history.php?id=x&tgl_awal=y&tgl_akhir=z
        - x diganti dengan id nasabah
        - y diganti dengan tanggal batas awal 
        - z diganti dengan tanggal batas akhir
        - out jika ada data, lewat json array $history_arr :
            - array [tanggal] : tgl_awal,tgl_akhir
            - array [records] : kode_transaksi,tgl_trans,tujuan,keterangan,nominal,status
        - out jika tidak ada data :
            - message : pesan tidak ada history yang bisa ditampilkan
    - read-mutasi
        - untuk melihat mutasi yang terjadi didalam rekeningnya baik masuk (Credit(CR)), atau keluar (Debet(DB)) dalam 7 hari kebelakang
        - in lewat url:
            ./API/transaksi/read-mutasi.php?id=x&tgl=y
        - x diganti dengan id nasabah
        - y diganti tanggal hari itu
        - out lewat json array $history_arr :
            - array [tanggal] : no_rek_pengirim,tgl_awal,tgl_akhir
            - array [records] : kode_transaksi,no_rek,tgl_trans,tujuan,jenis,keterangan,nominal
         - out jika tidak ada data :
            - message : pesan belum ada mutasi dalam 7 hari kebelakang
            