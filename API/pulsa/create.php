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

$database = new Database();
$db = $database->getConnection();

$pulsa = new Pulsa($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set products property values
$pulsa->username = $data->username;
$pulsa->no_hp_tujuan = $data->no_hp_tujuan;
$pulsa->id_nasabah = $data->id_nasabah;
$pulsa->provider = $data->provider;
$pulsa->kode_rahasia = $data->kode_rahasia;
$pulsa->nominal = $data->nominal;

// create new transaksi transfer
if($pulsa->create()){
    echo json_encode(
        array("pulsa" => true,
            "message" => $pulsa->message)
    );
}
else{
    echo json_encode(
        array("pulsa" => false,
            "message" => $pulsa->message)
    );
}
?>