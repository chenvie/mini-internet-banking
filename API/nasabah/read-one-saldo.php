<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Credentials: true");
header('Content-Type: application/json');

// include database and object files
include_once '../config/database.php';
include_once '../objects/nasabah.php';

// get database connection
$database = new Database();
$db = $database->getConnection();

// prepare product object
$nasabah = new Nasabah($db);

// set ID property of product to be edited
$nasabah->id = isset($_GET['id']) ? $_GET['id'] : die();

// read the details of product to be edited
$nasabah->readOneSaldo();

// create array
$nasabah_arr = array(
    "no rekening" => $nasabah->no_rek,
    "saldo" => $nasabah->jml_saldo
);

// make it json format
print_r(json_encode($nasabah_arr));
?>