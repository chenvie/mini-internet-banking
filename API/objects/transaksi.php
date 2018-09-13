<?php
class Transaksi
{
    // database connection and table name
    private $conn;
//    private $table_name = "transfer";
    private $table_name2 = "nasabah";
//    private $table_name3 = "transaksi";
//    private $table_name4 = "pulsa";
    // object properties
    public $id_nasabah;
    public $tgl;
    public $limit_date;
    public $tgl_awal;
    public $tgl_akhir;
    public $no_rek;
    // constructor with $db as database connection
    public function __construct($db)
    {
        $this->conn = $db;
    }

    //mendapat nomor rekening dari suatu nasabah
    function getNoRek($id)
    {
        // query to read single record
        $query = "SELECT
                no_rek
            FROM
                " . $this->table_name2 . "
            WHERE
                id_nasabah = ?
            LIMIT
                0,1";
        // prepare query statement
        $stmt = $this->conn->prepare($query);
        // bind id of products to be updated
        $stmt->bindParam(1, $id);
        // execute query
        $stmt->execute();
        // get retrieved row
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        // set values to object properties
        $this->no_rek = $row['no_rek'];
    }

    //cek mutasi yang terjadi
    function readMutasi()
    {
        $this->getNoRek($this->id_nasabah);
        // select all mutasi untuk nasabah tertentu query
//        $query = "select DISTINCT t.kode_transaksi,n.no_rek,t.tgl_trans,
//                    IF (substr(t.kode_transaksi,1,1) = '1',
//                    (if (n.no_rek = r.rek_transfer,
//                    CONCAT('Transfer dari ',(SELECT no_rek from " . $this->table_name2 . " where id_nasabah = t.id_nasabah)),CONCAT('Transfer ke ',r.rek_transfer))),
//                    'Pembelian Pulsa') as tujuan,
//                    IF (substr(t.kode_transaksi,1,1) = '1',IF(n.no_rek = r.rek_transfer,'CR','DB'),'DB') as jenis,
//                    IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan,
//                    IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal
//                    from " . $this->table_name3 . " t, " . $this->table_name . " r, " . $this->table_name4 . " p, " . $this->table_name2 . " n
//                    where (n.id_nasabah =:id_nasabah AND (t.id_nasabah = n.id_nasabah or n.no_rek = r.rek_transfer)) AND
//                    (t.kode_transaksi = r.kode_transfer OR t.kode_transaksi = p.kode_pembelian) AND
//                    (t.tgl_trans >=:tgl1 AND t.tgl_trans <=:tgl2) AND
//                    t.status = 'Berhasil'
//                    ORDER BY `t`.`tgl_trans`  ASC";
        $query = "CALL getMutasi(:id_nasabah,:tgl1,:tgl2)";
        // prepare query statement
        $stmt = $this->conn->prepare($query);
        $this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));
        $this->tgl = htmlspecialchars(strip_tags($this->tgl));
        $this->limit_date = date('Y-m-d', strtotime('-7 days', strtotime($this->tgl)));
        $end_date = date('Y-m-d', strtotime('+1 days', strtotime($this->tgl)));

        $stmt->bindParam(":id_nasabah", $this->id_nasabah);
        $stmt->bindParam(":tgl1", $this->limit_date);
        $stmt->bindParam(":tgl2", $end_date);

        // execute query
        $stmt->execute();
        return $stmt;
    }

    //cek history yang sudah terjadi
    function readHistory()
    {
        // select all mutasi untuk nasabah tertentu query
//    $query = "select DISTINCT t.kode_transaksi,t.tgl_trans,
//                IF (substr(t.kode_transaksi,1,1) = '1',CONCAT('Transfer ke ',r.rek_transfer),'Pembelian Pulsa') as tujuan,
//                IF (substr(t.kode_transaksi,1,1) = '1', r.keterangan,p.no_hp) as keterangan,
//                IF (substr(t.kode_transaksi,1,1) = '1', r.nominal,p.nominal) as nominal,
//                t.status
//                from transaksi t, transfer r, pulsa p, nasabah n
//                where n.id_nasabah =:id_nasabah AND t.id_nasabah = n.id_nasabah AND
//                (t.kode_transaksi = r.kode_transfer OR t.kode_transaksi = p.kode_pembelian) AND
//                (t.tgl_trans >=:tgl_awal AND t.tgl_trans <=:tgl_akhir)
//                ORDER BY t.tgl_trans  ASC";
        $query = "CALL getHistory(:id_nasabah,:tgl_awal,:tgl_akhir)";


        // prepare query statement
        $stmt = $this->conn->prepare($query);
        $this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));
        $this->tgl_awal = htmlspecialchars(strip_tags($this->tgl_awal));
        $this->tgl_akhir = htmlspecialchars(strip_tags($this->tgl_akhir));
        $this->tgl_awal = date('Y-m-d', strtotime($this->tgl_awal));
        $this->tgl_akhir = date('Y-m-d', strtotime('+1 days', strtotime($this->tgl_akhir)));
        $stmt->bindParam(":id_nasabah", $this->id_nasabah);
        $stmt->bindParam(":tgl_awal", $this->tgl_awal);
        $stmt->bindParam(":tgl_akhir", $this->tgl_akhir);
        // execute query
        $stmt->execute();
        $this->tgl_akhir = date('Y-m-d', strtotime('-1 days', strtotime($this->tgl_akhir)));
        return $stmt;
    }
}