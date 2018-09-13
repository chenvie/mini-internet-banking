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
$transaksi->tgl_awal = isset($_GET['tgl_awal']) ? $_GET['tgl_awal'] : die();
$transaksi->tgl_akhir = isset($_GET['tgl_akhir']) ? $_GET['tgl_akhir'] : die();

// query products
$stmt = $transaksi->readHistory();
$num = $stmt->rowCount();

// check if more than 0 record found
if($num>0){

    // products array
    $history_arr=array();
    $history_arr["tanggal"]=array();
    $history_arr["records"]=array();

    $history_awal=array(
        "tgl_awal" => $transaksi->tgl_awal,
        "tgl_akhir" => $transaksi->tgl_akhir
    );

    array_push($history_arr["tanggal"], $history_awal);

    // retrieve our table contents
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        // extract row
        // this will make $row['name'] to
        // just $name only
        extract($row);

        $history_item=array(
            "kode_transaksi" => $kode_transaksi,
            "tgl_trans" => $tgl_trans,
            "tujuan" => $tujuan,
            "keterangan" => $keterangan,
            "nominal" => $nominal,
            "status" => $status
        );

        array_push($history_arr["records"], $history_item);
    }

    echo json_encode($history_arr);
    $log->info('Cek history transaksi', ['id_nasabah' => $transaksi->id_nasabah]);
}

else{
    echo json_encode(
        array("message" => "Belum ada history transaksi pada tanggal yang dipilih")
    );
    $log->info('Tidak ada history transaksi', ['id_nasabah' => $transaksi->id_nasabah]);
}
//echo json_encode(
//  array("tgl1" => $transaksi->tgl_awal,
//      "tgl2" => $transaksi->tgl_akhir,
//  "id" => $transaksi->id_nasabah)
//);
?>