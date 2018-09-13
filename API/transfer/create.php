<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// get database connection
include_once '../config/database.php';

// instantiate products object
include_once '../objects/transfer.php';

$database = new Database();
$db = $database->getConnection();

$transfer = new Transfer($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set products property values
$transfer->username = $data->username;
$transfer->no_rek_tujuan = $data->no_rek_tujuan;
$transfer->id_nasabah = $data->id_nasabah;
$transfer->kode_rahasia = $data->kode_rahasia;
$transfer->nominal = $data->nominal;
$transfer->keterangan = $data->keterangan;
$transfer->create();

// create new transaksi transfer
if($transfer->status == "Berhasil"){
    echo json_encode(
        array("transfer" => true,
            "message" => $transfer->message)
    );
}
else{
    echo json_encode(
        array("transfer" => false,
            "message" => $transfer->message)
    );
}
?>