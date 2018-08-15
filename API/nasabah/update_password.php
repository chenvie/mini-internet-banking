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


// set ID property of nasabah to be edited
$nasabah->id_nasabah = $data->id_nasabah;

$nasabah->readOnePwd($data->id_nasabah);

if ($data->password == $nasabah->password) {
    $nasabah->password = $data->password;

    if ($data->passwordb1 == $data->passwordb2) {

// set product property values
        $nasabah->baru1 = $data->passwordb1;
        $nasabah->baru2 = $data->passwordb2;


// update the product
        if ($nasabah->update_password()) {
            echo json_encode(
                array("message" => "Update password berhasil")
            );
        } // if unable to update the product, tell the user
        else {
            echo json_encode(
                array("message" => "Update password gagal")
            );
        }
    } else {
        echo json_encode(
            array("message" => "password baru tidak sama")
        );
    }
}else
{
    echo json_encode(
        array("message" => "password lama salah")
    );
}
?>