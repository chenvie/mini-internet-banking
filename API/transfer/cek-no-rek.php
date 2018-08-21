<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// get database connection
include_once '../config/database.php';

// instantiate transfer object
include_once '../objects/transfer.php';

$database = new Database();
$db = $database->getConnection();

$transfer = new Transfer($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set transfer property values
$transfer->no_rek_tujuan = $data->no_rek_tujuan;
$transfer->id_nasabah = $data->id_nasabah;
$transfer->nominal = $data->nominal;
$transfer->keterangan = $data->keterangan;

// Cek nomor rekening
if($transfer->cekNoRek()){
    echo json_encode(
        array("check" => "True",
            "message" => $transfer->no_rek_tujuan
        )
    );
}
else{
    echo json_encode(
        array("check" => "False",
            "message" => $transfer->message
        )
    );
}
?>