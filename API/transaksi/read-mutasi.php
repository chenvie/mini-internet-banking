<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// include database and object files
include_once '../config/database.php';
include_once '../objects/transaksi.php';

include_once '../monolog.php';

// instantiate database and products object
$database = new Database();
$db = $database->getConnection();

// initialize object
$transaksi = new Transaksi($db);

//mendapat
$transaksi->id_nasabah = isset($_GET['id']) ? $_GET['id'] : die();
//$transaksi->tgl = isset($_GET['tgl']) ? $_GET['tgl'] : die();
$transaksi->tgl = date('Y-m-d');

// query products
$stmt = $transaksi->readMutasi();
$num = $stmt->rowCount();

// check if more than 0 record found
if($num>0){

    // products array
    $mutasi_arr=array();
    $mutasi_arr["tanggal"]=array();
    $mutasi_arr["records"]=array();

    $mutasi_awal=array(
        "no_rek_pengirim" => $transaksi->no_rek,
        "tgl_awal" => $transaksi->limit_date,
        "tgl_akhir" => $transaksi->tgl
    );

    array_push($mutasi_arr["tanggal"], $mutasi_awal);

    // retrieve our table contents
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        // extract row
        // this will make $row['name'] to
        // just $name only
        extract($row);

        $mutasi_item=array(
            "kode_transaksi" => $kode_transaksi,
            "no_rek" => $no_rek,
            "tgl_trans" => $tgl_trans,
            "tujuan" => $tujuan,
            "jenis" => $jenis,
            "keterangan" => $keterangan,
            "nominal" => $nominal
        );

        array_push($mutasi_arr["records"], $mutasi_item);
    }

    echo json_encode($mutasi_arr);
    $log->info('Mutasi rekening', ['id nasabah' => $transaksi->id_nasabah]);
}

else{
    echo json_encode(
        array("message" => "Belum ada mutasi dalam 7 hari terakhir.")
    );
    $log->info('Belum ada mutasi', ['id nasabah' => $transaksi->id_nasabah]);
}
//echo json_encode(
//  array("tgl" => $transaksi->tgl,
//  "id" => $transaksi->id_nasabah)
//);
?>