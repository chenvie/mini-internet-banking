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
include_once '../objects/nasabah.php';

$database = new Database();
$db = $database->getConnection();

$nasabah = new Nasabah($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set products property values
$nasabah->id_nasabah = $data->id_nasabah;
$nasabah->nama_lengkap = $data->nama_lengkap;
$nasabah->email = $data->email;
$nasabah->password = $data->password;
$nasabah->no_ktp = $data->no_ktp;
$nasabah->tgl_lahir = date('Y-m-d',strtotime($data->tgl_lahir));
$nasabah->alamat = $data->alamat;
$nasabah->kode_rahasia = $data->kode_rahasia;
$nasabah->created = date('Y-m-d H:i:s');

// create new nasabah
if($nasabah->create()){
    echo '{';
    echo '"message": "Pendaftaran nasabah berhasil."';
    echo '}';
}

// if unable to create the nasabah, tell the user
else{
    echo '{';
    echo '"message": "Pendaftaran nasabah gagal."';
    echo '}';
}
?>