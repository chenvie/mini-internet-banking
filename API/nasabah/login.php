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
include_once '../monolog.php';

$database = new Database();
$db = $database->getConnection();

$nasabah = new Nasabah($db);

// get posted data
$data = json_decode(file_get_contents("php://input"));

// set nasabah property values
$nasabah->username = $data->username;
$nasabah->password = $data->password;


// login
if($nasabah->login()){
    echo json_encode(
        array("login" => true
        )
    );
    $log->info('User login',['username' => $data->username]);
}

// if unable to login
else{
    echo json_encode(
        array("login" => false
            )
    );
    
    $log->info('User failed login', ['username' => $data->username]);
    //$log->warning('testing');
    //$log->error('Error login', array('username' => $data->username));
    //$log->error('I am error', array('username' => $data->username));
}
?>