<?php
  header("Access-Control-Allow-Origin: *");
  header("Content-Type: application/json; charset=UTF-8");
  header("Access-Control-Allow-Methods: POST");
  header("Access-Control-Max-Age: 3600");
  header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

  $data = json_decode(file_get_contents("php://input"));
  $file = date('Ymd', time()) . '.txt';
  try {
    $current = file_get_contents($file);
  } catch(Exception $e) {
    file_put_contents($file);
  }
  $current = file_get_contents($file);
  $current .= date('H:i:s [').$data->level.'] '.$data->message;
  file_put_contents($file, $current."\r\n");
?>
