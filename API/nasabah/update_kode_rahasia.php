<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// include database and object files
include_once '../config/database.php';
include_once '../objects/nasabah.php';

// get database connection
$database = new Database();
$db = $database->getConnection();

// prepare product object
$nasabah = new Nasabah($db);

// get id of product to be edited
$data = json_decode(file_get_contents("php://input"));


// set data
$nasabah->update_kode_rahasia($data->id_nasabah,$data->kode_rahasiaL,$data->krb1,$data->krb2);
echo json_encode(
    array("message" => $nasabah->message));
//
//// set ID property of nasabah to be edited
//$nasabah->id_nasabah = $data->id_nasabah;
//
//$nasabah->readOneKode($data->id_nasabah);
//
//if ($data->kode_rahasiaL == $nasabah->kode_rahasia) {
//    $nasabah->kode_rahasia = $data->kode_rahasiaL;
//
//    if ($data->krb1 == $data->krb2) {
//
//// set product property values
//        $nasabah->baru1 = $data->krb1;
//        $nasabah->baru2 = $data->krb2;
//
//
//// update the product
//        if ($nasabah->update_kode_rahasia()) {
//            echo json_encode(
//                array("message" => "Update kode rahasia berhasil")
//            );
//        } // if unable to update the product, tell the user
//        else {
//            echo json_encode(
//                array("message" => "Update kode rahasia gagal")
//            );
//        }
//    } else {
//        echo json_encode(
//            array("message" => "Kode rahasia baru tidak sama")
//        );
//    }
//}else
//{
//    echo json_encode(
//            array("message" => "Kode rahasia lama salah")
//    );
//}
?>