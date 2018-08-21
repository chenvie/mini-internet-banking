<?php
class Transaksi
{

    // database connection and table name
    private $conn;
    private $table_name = "transfer";
    private $table_name2 = "nasabah";
    private $table_name3 = "transaksi";
    private $table_name4 = "pulsa";

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
    public $message;

    // constructor with $db as database connection
    public function __construct($db)
    {
        $this->conn = $db;
    }


    //transfer
    function create()
    {
        $this->jml_saldo = $this->getSaldo($this->id_nasabah);
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