<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// get database connection
include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

$logs = array();
$data = json_decode(file_get_contents("php://input"));

$logs = $data->logs;

$date = date('Ymd', time());

$logFile = fopen("Log_" . $date .".txt", "a") or die("Unable to Open File!");
foreach($logs as $eachLog) {
    fwrite($logFile, PHP_EOL . $eachLog);
}
fclose($logFile);
?>