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
include_once '../objects/pulsa.php';

include_once '../monolog.php';

$database = new Database();
$db = $database->getConnection();

$pulsa = new Pulsa($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set products property values
//$pulsa->username = $data->username;
//$pulsa->no_hp_tujuan = $data->no_hp_tujuan;
//$pulsa->id_nasabah = $data->id_nasabah;
//$pulsa->provider = $data->provider;
//$pulsa->kode_rahasia = $data->kode_rahasia;
//$pulsa->nominal = $data->nominal;
$pulsa->create($data->username,$data->no_hp_tujuan,$data->id_nasabah,$data->provider,$data->nominal,$data->kode_rahasia);
if($pulsa->status == "Berhasil"){
    echo json_encode(
        array("pulsa" => true,
            "message" => $pulsa->message)
    );
    $log->info('Pembelian pulsa berhasil',['username' => $data->username]);
}
else{
    echo json_encode(
        array("pulsa" => false,
            "message" => $pulsa->message)
    );
    $log->error('Pembelian pulsa gagal',['username' => $data->username]);
}
// create new transaksi transfer
//if($pulsa->create()){
//    echo json_encode(
//        array("pulsa" => true,
//            "message" => $pulsa->message)
//    );
//}
//else{
//    echo json_encode(
//        array("pulsa" => false,
//            "message" => $pulsa->message)
//    );
//}
?>