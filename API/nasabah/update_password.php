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

// set product property values
$nasabah->password = $data->passwordl;
$nasabah->baru1 = $data->passwordb1;
$nasabah->baru2 = $data->passwordb2;
$nasabah->id_nasabah = $data->id_nasabah;
 
// update the product
if($nasabah->update_password()){
    echo '{';
        echo '"message": "Password was updated."';
    echo '}';
}
 
// if unable to update the product, tell the user
else{
    echo '{';
        echo '"message": "Unable to update password."';
    echo '}';
}
?>