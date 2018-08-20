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

$nasabah = new nasabah($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set products property values
$nasabah->username = $data->username;
$nasabah->password = $data->password;


// login
if($nasabah->login()){
    echo json_encode(
        array("login" => "True")
    );
}

// if unable to login
else{
    echo json_encode(
        array("login" => "False")
    );
}
?>