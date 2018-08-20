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

//if($nasabah->contains(date('Y-m-d',strtotime($data->tgl_lahir)),$data->password) or
//    $nasabah->contains(date('Ymd',strtotime($data->tgl_lahir)),$data->password) or
//    $nasabah->contains(date('Ydm',strtotime($data->tgl_lahir)),$data->password) or
//    $nasabah->contains(date('dmY',strtotime($data->tgl_lahir)),$data->password) or
//    $nasabah->contains(date('dYm',strtotime($data->tgl_lahir)),$data->password) or
//    $nasabah->contains(date('mYd',strtotime($data->tgl_lahir)),$data->password) or
//    $nasabah->contains(date('mdY',strtotime($data->tgl_lahir)),$data->password)){
//    echo json_encode(
//        array("message" => "Password tidak diperbolehkan karena mengandung tanggal lahir")
//    );
//}
//elseif($nasabah->contains(date('Y-m-d',strtotime($data->tgl_lahir)),$data->kode_rahasia) or
//    $nasabah->contains(date('Ymd',strtotime($data->tgl_lahir)),$data->kode_rahasia) or
//    $nasabah->contains(date('Ydm',strtotime($data->tgl_lahir)),$data->kode_rahasia) or
//    $nasabah->contains(date('dmY',strtotime($data->tgl_lahir)),$data->kode_rahasia) or
//    $nasabah->contains(date('dYm',strtotime($data->tgl_lahir)),$data->kode_rahasia) or
//    $nasabah->contains(date('mYd',strtotime($data->tgl_lahir)),$data->kode_rahasia) or
//    $nasabah->contains(date('mdY',strtotime($data->tgl_lahir)),$data->kode_rahasia)){
//    echo json_encode(
//        array("message" => "Kode rahasia tidak diperbolehkan karena mengandung tanggal lahir")
//    );
//}
//else
//    if (ctype_alnum($data->password) and strlen($data->password)>=8){
//    if (ctype_alnum($data->kode_rahasia) and strlen($data->kode_rahasia)==6){

        // set products property values
                //$nasabah->id_nasabah = $data->id_nasabah;
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
                    echo json_encode(
                        array("message" => "Pendaftaran berhasil",
                        "username" => $nasabah->username)
                    );
                }

        // if unable to create the nasabah, tell the user
                else{
                    echo json_encode(
                        array("message" => "Pendaftaran gagal")
                    );
                }
//            }
//    else{
//        echo json_encode(
//            array("message" => "Kode rahasia tidak mengandung karakter alphanumeric atau tidak sepanjang 6 karakter")
//        );
//
//    }
//}
//else {
//    echo json_encode(
//        array("message" => "Password tidak mengandung karakter alphanumeric atau kurang dari 8 karakter")
//    );

//}

?>