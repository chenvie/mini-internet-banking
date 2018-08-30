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
include_once '../monolog.php';
 
// get database connection
$database = new Database();
$db = $database->getConnection();
 
// prepare nasabah object
$nasabah = new Nasabah($db);
 
// get id of nasabah to be edited
$data = json_decode(file_get_contents("php://input"));

// set ID property of nasabah to be edited
$nasabah->id_nasabah = $data->id_nasabah;

$nasabah->readOneKode($data->id_nasabah);

if ($data->kode_rahasiaL == $nasabah->kode_rahasia) {
    $nasabah->kode_rahasia = $data->kode_rahasiaL;

    if ($data->krb1 == $data->krb2) {

// set nasabah property values
        $nasabah->baru1 = $data->krb1;
        $nasabah->baru2 = $data->krb2;


// update the nasabah
        if ($nasabah->update_kode_rahasia()) {
            echo json_encode(
                array("update" => true,
                    "message" => "Update kode rahasia berhasil")
            );
            $log->info('Update kode rahasia',['id_nasabah' => $data->id_nasabah]);
         
        } // if unable to update the nasabah, tell the user
        else {
            echo json_encode(
                array("update" => false,
                    "message" => "Update kode rahasia gagal")
            );
            $log->error('Update kode rahasia gagal',['id_nasabah' => $data->id_nasabah]);
            
        }
    } else {
        echo json_encode(
            array("update" => false,
                "message" => "Kode rahasia baru tidak sama")
        );
        $log->error('Update kode rahasia baru tidak sama',['id_nasabah' => $data->id_nasabah]);
    }
}else
{
    echo json_encode(
            array("update" => false,
                "message" => "Kode rahasia lama salah")
    );
    $log->error('Update kode rahasia lama salah',['id_nasabah' => $data->id_nasabah]);
   
}
?>