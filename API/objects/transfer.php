<?php
class Transfer
{

    // database connection and table name
    private $conn;
    private $table_name = "transfer";
    private $table_name2 = "nasabah";
    private $table_name3 = "transaksi";

    // object properties
    public $id_nasabah;
    public $username;
    public $password;
    public $kode_rahasia;
    public $no_rek_tujuan;
    public $nominal;
    public $keterangan;
    public $kode_transaksi;
    public $jml_trans;
    public $cekr;
    public $jml_saldo;
    public $jml_saldo_tujuan;
    public $message;
    public $status;

    // constructor with $db as database connection
    public function __construct($db)
    {
        $this->conn = $db;
    }

    //generate nomor transaksi
    function generateKodeTrans(){
        $awalan_kode = "1000";
        $sql = "SELECT COUNT(kode_transaksi) as jml FROM " . $this->table_name3;
        $stmt = $this->conn->prepare($sql);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $this->jml_trans = $row['jml'];
        $this->jml_trans = $this->jml_trans+1;
        if ($this->jml_trans > 9) {
            $awalan_kode = "100";
        } else if ($this->jml_trans > 99) {
            $awalan_kode = "10";
        } else if ($this->jml_trans > 999){
            $awalan_kode = "1";
        }
        $c = $awalan_kode . $this->jml_trans;
        return $c;
    }

    //cek nomor rekening, jika tidak ada akan dilakukan pencatatan gagal transfer di tabel transaksi dan transfer
    function cekNoRek()
    {
        // query to insert record
        $query = "SELECT  no_rek from 
                " . $this->table_name2 . "
            where
                no_rek=:no_rek";

        // prepare query
        $stmt = $this->conn->prepare($query);

        // sanitize
        $this->no_rek_tujuan = htmlspecialchars(strip_tags($this->no_rek_tujuan));

        // bind values
        $stmt->bindParam(":no_rek", $this->no_rek_tujuan);

        // execute query
        $stmt->execute();
        $this->cekr = $stmt->rowCount();
        if ($this->cekr == 1)
        {
            $this->message = "Nomor rekening tujuan ditemukan";
            $this->status = "Berhasil";
        }
        else
        {
            $this->message = "Nomor rekening tujuan tidak ditemukan";
            $this->status = "Gagal";
        }

        if ($this->status == "Berhasil") {
            return true;
        }
        else {

            $this->kode_transaksi = $this->generateKodeTrans();

            // query to insert record for transaksi
            $query = "INSERT INTO
                " . $this->table_name3 . "
            SET
                kode_transaksi=:kode_transaksi,id_nasabah=:id_nasabah,status=:status,ket_status=:ket_status";

            // prepare query
            $stmt = $this->conn->prepare($query);

            // sanitize
            $this->kode_transaksi = htmlspecialchars(strip_tags($this->kode_transaksi));
            $this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));
            $this->status = htmlspecialchars(strip_tags($this->status));

            // bind values
            $stmt->bindParam(":id_nasabah", $this->id_nasabah);
            $stmt->bindParam(":kode_transaksi", $this->kode_transaksi);
            $stmt->bindParam(":status", $this->status);
            $stmt->bindParam(":ket_status", $this->message);

            $stmt->execute();

            // query to insert record for transfer
            $query = "INSERT INTO
                " . $this->table_name . "
            SET
                kode_transfer=:kode_transaksi,rek_transfer=:rek_transfer,nominal=:nominal,keterangan=:keterangan";

            // prepare query
            $stmt = $this->conn->prepare($query);

            // sanitize
            $this->no_rek_tujuan = htmlspecialchars(strip_tags($this->no_rek_tujuan));
            $this->nominal = htmlspecialchars(strip_tags($this->nominal));
            $this->keterangan = htmlspecialchars(strip_tags($this->keterangan));

            // bind values
            $stmt->bindParam(":kode_transaksi", $this->kode_transaksi);
            $stmt->bindParam(":rek_transfer", $this->no_rek_tujuan);
            $stmt->bindParam(":nominal", $this->nominal);
            $stmt->bindParam(":keterangan", $this->keterangan);

            $stmt->execute();

            return false;
        }
    }

    //cek kode rahasia
    function cekKodeRahasia($uname,$kr)
    {
        $query = "SELECT  kode_rahasia from 
                " . $this->table_name2 . "
            where
                username=:username and kode_rahasia=:kode_rahasia";

        // prepare query
        $stmt = $this->conn->prepare($query);

        // sanitize
        $this->username = htmlspecialchars(strip_tags($uname));
        $kr = htmlspecialchars(strip_tags($kr));

        // bind values
        $stmt->bindParam(":username", $this->username);
        $stmt->bindParam(":kode_rahasia", $kr);

        // execute query
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $this->kode_rahasia = $row['kode_rahasia'];

        if ($kr == $this->kode_rahasia) {
            return true;
        }
        return false;
    }

    // mendapat saldo dari satu nasabah
    function getSaldo($id)
    {
        if ($id == $this->id_nasabah)
        {
            $c = 'id_nasabah=:id_nasabah';
            $c2 = ':id_nasabah';
        }
        else
        {
            $c = 'no_rek=:no_rek';
            $c2 = ':no_rek';
        }
        $sql = "SELECT jml_saldo FROM " . $this->table_name2 . " WHERE " .$c;
        $stmt = $this->conn->prepare($sql);
        $id = htmlspecialchars(strip_tags($id));
        $stmt->bindParam($c2, $id);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $cek = $row['jml_saldo'];
        return $cek;
    }

    //transfer dan pencatatan transfer di tabel transaksi dan transfer
    function create()
    {
        $this->jml_saldo = $this->getSaldo($this->id_nasabah);
        $this->jml_saldo_tujuan = $this->getSaldo($this->no_rek_tujuan);
        if($this->cekKodeRahasia($this->username,$this->kode_rahasia) == false)
        {
            $this->message = "Kode rahasia salah";
            $this->status = "Gagal";
        }
        else if($this->nominal > 0)
        {
            if (($this->jml_saldo - $this->nominal) < 50000) {
                $this->message = "Saldo tidak mencukupi, pastikan ada sisa Rp. 50.000 di rekening anda";
                $this->status = "Gagal";
            } else {
                $this->message = "Berhasil transfer";
                $this->status = "Berhasil";

                $this->jml_saldo = $this->jml_saldo - $this->nominal;

                $query = "UPDATE
                " . $this->table_name2 . "
                SET
                jml_saldo=:jml_saldo
                WHERE
                id_nasabah=:id_nasabah";

                // prepare query
                $stmt = $this->conn->prepare($query);

                // sanitize
                $this->jml_saldo = htmlspecialchars(strip_tags($this->jml_saldo));
                $this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));

                // bind values
                $stmt->bindParam(":jml_saldo", $this->jml_saldo);
                $stmt->bindParam(":id_nasabah", $this->id_nasabah);

                $stmt->execute();

                $this->jml_saldo_tujuan = $this->jml_saldo_tujuan + $this->nominal;

                $query = "UPDATE
                " . $this->table_name2 . "
                SET
                jml_saldo=:jml_saldo
                WHERE
                no_rek=:no_rek";

                // prepare query
                $stmt = $this->conn->prepare($query);

                // sanitize
                $this->jml_saldo_tujuan = htmlspecialchars(strip_tags($this->jml_saldo_tujuan));
                $this->no_rek_tujuan = htmlspecialchars(strip_tags($this->no_rek_tujuan));

                // bind values
                $stmt->bindParam(":jml_saldo", $this->jml_saldo_tujuan);
                $stmt->bindParam(":no_rek", $this->no_rek_tujuan);

                $stmt->execute();

            }
        }
        else
        {
            $this->message = "Jumlah yang ditransfer terlalu kecil";
            $this->status = "Gagal";
        }
            $this->kode_transaksi = $this->generateKodeTrans();

            // query to insert record for transaksi
            $query = "INSERT INTO
                " . $this->table_name3 . "
            SET
                kode_transaksi=:kode_transaksi,id_nasabah=:id_nasabah,status=:status,ket_status=:ket_status";

            // prepare query
            $stmt = $this->conn->prepare($query);

            // sanitize
            $this->kode_transaksi = htmlspecialchars(strip_tags($this->kode_transaksi));
            $this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));
            $this->status = htmlspecialchars(strip_tags($this->status));

            // bind values
            $stmt->bindParam(":id_nasabah", $this->id_nasabah);
            $stmt->bindParam(":kode_transaksi", $this->kode_transaksi);
            $stmt->bindParam(":status", $this->status);
            $stmt->bindParam(":ket_status", $this->message);

        $stmt->execute();

            // query to insert record for transfer
            $query = "INSERT INTO
                " . $this->table_name . "
            SET
                kode_transfer=:kode_transaksi,rek_transfer=:rek_transfer,nominal=:nominal,keterangan=:keterangan";

            // prepare query
            $stmt = $this->conn->prepare($query);

            // sanitize
            $this->no_rek_tujuan = htmlspecialchars(strip_tags($this->no_rek_tujuan));
            $this->nominal = htmlspecialchars(strip_tags($this->nominal));
            $this->keterangan = htmlspecialchars(strip_tags($this->keterangan));

            // bind values
            $stmt->bindParam(":kode_transaksi", $this->kode_transaksi);
            $stmt->bindParam(":rek_transfer", $this->no_rek_tujuan);
            $stmt->bindParam(":nominal", $this->nominal);
            $stmt->bindParam(":keterangan", $this->keterangan);

            $stmt->execute();


        if ($this->status == "Berhasil") {
            return true;
        }

        return false;


    }
}