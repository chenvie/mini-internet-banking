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

if($nasabah->contains(date('Y-m-d',strtotime($data->tgl_lahir)),$data->passwordl) or
    $nasabah->contains(date('Ymd',strtotime($data->tgl_lahir)),$data->passwordl) or
    $nasabah->contains(date('Ydm',strtotime($data->tgl_lahir)),$data->passwordl) or
    $nasabah->contains(date('dmY',strtotime($data->tgl_lahir)),$data->passwordl) or
    $nasabah->contains(date('dYm',strtotime($data->tgl_lahir)),$data->passwordl) or
    $nasabah->contains(date('mYd',strtotime($data->tgl_lahir)),$data->passwordl) or
    $nasabah->contains(date('mdY',strtotime($data->tgl_lahir)),$data->passwordl)){
    echo json_encode(
        array("message" => "Password tidak diperbolehkan karena mengandung tanggal lahir")
    );
}
elseif ($data->passwordl == $nasabah->password) {
    if ($data->passwordb1 == $data->passwordb2) {

        // set product property values
        $nasabah->password = $data->passwordl;
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