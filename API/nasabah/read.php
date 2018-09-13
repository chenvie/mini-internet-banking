<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// include database and object files
include_once '../config/database.php';
include_once '../objects/nasabah.php';

// instantiate database and products object
$database = new Database();
$db = $database->getConnection();

// initialize object
$nasabah = new Nasabah($db);

// query products
$stmt = $nasabah->read();
$num = $stmt->rowCount();

// check if more than 0 record found
if($num>0){

    // products array
    $nasabah_arr=array();
    $nasabah_arr["records"]=array();

    // retrieve our table contents
    // fetch() is faster than fetchAll()
    // http://stackoverflow.com/questions/2770630/pdofetchall-vs-pdofetch-in-a-loop
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        // extract row
        // this will make $row['name'] to
        // just $name only
        extract($row);

        $nasabah_item=array(
            "id" => $id_nasabah,
            "email" => $email,
            "username" => $username,
            "password" => $password,
            "nama_lengkap" => $nama_lengkap,
            "no_ktp" => $no_ktp,
            "tgl_lahir" => $tgl_lahir,
            "alamat" => $alamat,
            "kode_rahasia" => $kode_rahasia,
            "created" => $created
        );

        array_push($nasabah_arr["records"], $nasabah_item);
    }

    echo json_encode($nasabah_arr);
}

else{
    echo json_encode(
        array("message" => "No products found.")
    );
}
?>