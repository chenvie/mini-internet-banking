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

include_once '../monolog.php';

$database = new Database();
$db = $database->getConnection();

$transfer = new Transfer($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set transfer property values
$transfer->username = $data->username;
$transfer->no_rek_tujuan = $data->no_rek_tujuan;
$transfer->id_nasabah = $data->id_nasabah;
$transfer->kode_rahasia = $data->kode_rahasia;
$transfer->nominal = $data->nominal;
$transfer->keterangan = $data->keterangan;

// create new transaksi transfer
if($transfer->create()){
    echo json_encode(
        array("transfer" => true,
        "message" => $transfer->message)
    );
    $log->info('Transfer sukses',['username' => $data->username]);
}
else{
    echo json_encode(
        array("transfer" => false,
            "message" => $transfer->message)
    );
    $log->info('Transfer gagal',['username' => $data->username]);
    $log->error('Transfer gagal', ['username' => $transfer->message]);
    
}

?>