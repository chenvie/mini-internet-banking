<?php


header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Credentials: true");
header('Content-Type: application/json');

// include database and object files
include_once '../config/database.php';
include_once '../objects/nasabah.php';
include_once '../monolog.php';

// get database connection
$database = new Database();
$db = $database->getConnection();

// prepare product object
$nasabah = new Nasabah($db);

// set ID property of nasabah to get the data
$nasabah->username = isset($_GET['unm']) ? $_GET['unm'] : die();

// read the details of nasabah
$nasabah->readOne();

// create array
$nasabah_arr = array(
    "id_nasabah" => $nasabah->id_nasabah,
    "username" => $nasabah->username,
    "password" => $nasabah->password,
    "nama_lengkap" => $nasabah->nama_lengkap,
    "kode_rahasia" => $nasabah->kode_rahasia,
    "tgl_lahir" => $nasabah->tgl_lahir,
    "jml_saldo" => $nasabah->jml_saldo,
    "no_rek" => $nasabah->no_rek

);

// make it json format
print_r(json_encode($nasabah_arr));
$log->debug('I am debug');
?>