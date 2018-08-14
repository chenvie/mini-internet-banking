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
 
// set ID property of product to be edited
$nasabah->nsb_id = $data->nsb_id;
 
// set product property values
$nasabah->kode_rahasia = $data->kode_rahasia;

 
// update the product
if($nasabah->update_kode_rahasia()){
    echo '{';
        echo '"message": "Kode Rahasia was updated."';
    echo '}';
}
 
// if unable to update the product, tell the user
else{
    echo '{';
        echo '"message": "Unable to update Kode Rahasia."';
    echo '}';
}
?>