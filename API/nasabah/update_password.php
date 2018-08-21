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
//
//if($nasabah->contains((string)date('Y-m-d',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Ymd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Ydm',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dmY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dYm',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('mYd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('mdY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Y-m-j',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Ymj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Yjm',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jmY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jYm',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('mYj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('y-m-d',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ymd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ydm',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dmy',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dym',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('myd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('mdy',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('y-m-j',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ymj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('yjm',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jmy',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jym',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('myj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Y-n-d',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Ynd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Ydn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dnY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dYn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('nYd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ndY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Y-n-j',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Ynj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Yjn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jnY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jYn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('nYj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('y-n-d',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ynd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ydn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dny',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dyn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('nyd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ndy',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('y-n-j',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ynj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('yjn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jny',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jyn',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('nyj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Y-F-d',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('YFd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('YdF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dFY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dYF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('FYd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('FdY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Y-F-j',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('YFj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('YjF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jFY',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jYF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('FYj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('y-F-d',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('yFd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('ydF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dFy',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('dyF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Fyd',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Fdy',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('y-F-j',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('yFj',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('yjF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jFy',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('jyF',strtotime($data->tgl_lahir)),$data->passwordl) or
//    $nasabah->contains((string)date('Fyj',strtotime($data->tgl_lahir)),$data->passwordl)){
//    echo json_encode(
//        array("message" => "Password tidak diperbolehkan karena mengandung tanggal lahir")
//    );
//}
//else
//    if (ctype_alnum($data->password) and strlen($data->password)>=8) {

//if (strlen($data->passwordl)>=8){
        if ($data->passwordl == $nasabah->password) {
            if ($data->passwordb1 == $data->passwordb2) {

                // set product property values
                $nasabah->password = $data->passwordl;
                $nasabah->baru1 = $data->passwordb1;
                $nasabah->baru2 = $data->passwordb2;

                //update password
                if ($nasabah->update_password()) {
                    echo json_encode(
                        array("update" => true,
                            "message" => "Update password berhasil")
                    );
                } // if unable to update the product, tell the user
                else {
                    echo json_encode(
                        array("update" => false,
                            "message" => "Update password gagal")
                    );
                }
            } else {
                echo json_encode(
                    array("update" => false,
                        "message" => "password baru tidak sama")
                );
            }
        } else {
            echo json_encode(
                array("update" => false,
                    "message" => "password lama salah")
            );
        }

//    }
//    else
//    {
//        echo json_encode(
//            array("message" => "password kurang dari 8")
//        );
//    }
?>