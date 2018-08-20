<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Credentials: true");
header('Content-Type: application/json');

// include database and object files
include_once '../config/database.php';
include_once '../objects/transaksi.php';

// get database connection
$database = new Database();
$db = $database->getConnection();

// prepare product object
$transaksi = new Transaksi($db);

// set ID property of product to be edited
$transaksi->id = isset($_GET['id']) ? $_GET['id'] : die();

// read the details of product to be edited
$transaksi->read_mutasi();

// create array
$transaksi_arr = array(
    "kode_transaksi" => $transaksi->kode_transaksi,
    "id_nasabah" => $transaksi->id_nasabah,
    "tgl_trans" => $transaksi->tgl_trans,
    "jenis" => $transaksi->jenis,
    "status" => $transaksi->status
    

);

// make it json format
print_r(json_encode($transaksi_arr));
?>