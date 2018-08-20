<?php
 class Transaksi
{
    private $conn;
    private $table_name = "transaksi";

    public $kode_transaksi;
    public $id_nasabah;
    public $tgl_trans;
    public $jenis;
    public $status;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    function read_mutasi()
    {

        // select all query
        $query = "SELECT
                kode_transaksi,id_nasabah,tgl_trans,jenis,status
            FROM
                " . $this->table_name . "
            WHERE
                tgl_trans = kode_transaksi = ?
            LIMIT
                0,1";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // bind id of products to be updated
        $stmt->bindParam(1, $this->id);

        // execute query
        $stmt->execute();

        // get retrieved row
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        // set values to object properties
        $this->kode_transaksi = $row['kode_transaksi'];
        $this->id_nasabah = $row['id_nasabah'];
        $this->tgl_trans = $row['tgl_trans'];
        $this->jenis = $row['jenis'];
        $this->status = $row['status'];
        
    }

}   