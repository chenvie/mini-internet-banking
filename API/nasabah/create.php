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

$nasabah->nama_lengkap = $data->nama_lengkap;
//$nasabah->id_nasabah = '3'; //$data->id_nasabah;
$nasabah->email = $data->email;
// creating username, using first name and random number
//$nasabah->username = 'asd';//substr($this->nama_lengkap,0,strpos($this->nama_lengkap," ")) + rand(001,100);
$nasabah->password = $data->password;
$nasabah->no_ktp = $data->no_ktp;
$nasabah->tgl_lahir = $data->tgl_lahir;
$nasabah->alamat = $data->alamat;
$nasabah->kode_rahasia = $data->kode_rahasia;
//$nasabah->created = date('Y-m-d H:i:s');

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