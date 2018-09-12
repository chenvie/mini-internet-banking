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
//
//$nasabah->login($data->username,$data->password);
//echo json_encode(
//    array("login" => $nasabah->status));

// set products property values
//$nasabah->username = $data->uname;
//$nasabah->password = $data->pwd;
$nasabah->login($data->uname,$data->pwd);
// login
if($nasabah->username != ""){
    echo json_encode(
        array("username" => $nasabah->username,
            "login" => true
        )
    );
}

// if unable to login
else{
    echo json_encode(
        array("login" => false
        )
    );
}
?>