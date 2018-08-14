<?php
class Nasabah
{

    // database connection and table name
    private $conn;
    private $table_name = "nasabah";
    private $table_name2 = "rekening";

    // object properties
    public $id_nasabah;
    public $email;
    public $username;
    public $password;
    public $nama_lengkap;
    public $no_ktp;
    public $tgl_lahir;
    public $alamat;
    public $kode_rahasia;
    public $created;
    public $no_rek;
    public $jml_saldo;
    public $kode_cabang;
    public $baru1;
    public $baru2;
    public $id;

    // constructor with $db as database connection
    public function __construct($db)
    {
        $this->conn = $db;
    }

    // read nasabah
    function read()
    {

        // select all query
        $query = "SELECT
                id_nasabah,email,username,nama_lengkap,password,no_ktp,tgl_lahir,alamat,kode_rahasia,created
            FROM
                " . $this->table_name . "
            ORDER BY
                created DESC";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // execute query
        $stmt->execute();

        return $stmt;
    }

// create new nasabah
    function create()
    {

        // query to insert record for nasabah
        $query = "INSERT INTO
                " . $this->table_name . "
            SET
                id_nasabah=:id_nasabah, email=:email, username=:username, nama_lengkap=:nama_lengkap, password=:password, no_ktp=:no_ktp, tgl_lahir=:tgl_lahir, alamat=:alamat, kode_rahasia=:kode_rahasia, created=:created";

        // prepare query
        $stmt = $this->conn->prepare($query);

        // sanitize
        $this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));
        //$this->username = htmlspecialchars(strip_tags($this->username));
        $this->created = htmlspecialchars(strip_tags($this->created));
        $this->email = htmlspecialchars(strip_tags($this->email));
        $this->nama_lengkap = htmlspecialchars(strip_tags($this->nama_lengkap));
        $this->password = htmlspecialchars(strip_tags($this->password));
        $this->no_ktp = htmlspecialchars(strip_tags($this->no_ktp));
        $this->alamat = htmlspecialchars(strip_tags($this->alamat));
        $this->kode_rahasia = htmlspecialchars(strip_tags($this->kode_rahasia));
        $this->tgl_lahir = htmlspecialchars(strip_tags($this->tgl_lahir));


        //$nsb = 4;
        $unm = 'cocobaba';
        //$tgll= '2018-08-01';
        // bind values
        $stmt->bindParam(":id_nasabah", $this->id_nasabah);
        $stmt->bindParam(":email", $this->email);
        $stmt->bindParam(":username", $unm);
        $stmt->bindParam(":nama_lengkap", $this->nama_lengkap);
        $stmt->bindParam(":password", $this->password);
        $stmt->bindParam(":no_ktp", $this->no_ktp);
        $stmt->bindParam(":tgl_lahir", $this->tgl_lahir);
        $stmt->bindParam(":alamat", $this->alamat);
        $stmt->bindParam(":kode_rahasia", $this->kode_rahasia);
        $stmt->bindParam(":created", $this->created);





        $num = (string)$this->rekCount();

        // execute query
//        if ($this->createRek($this->id_nasabah,$num)) {
//            return true;
//        }

        if ( $stmt->execute()) {
            return true;
        }

        return false;

    }

//    //mencari no rek sekarang
//    function rekCount(){
//        // query to check latest no rek count
//        $query = "SELECT no_rek from " . $this->table_name2;
//        // prepare query
//        $stmt = $this->conn->prepare($query);
//        // executing the query
//        $stmt->execute();
//        // getting the count of rekening
//        $num = $stmt->rowCount();
//        $stmt->close();
//        return $num+1;
//    }
//    //create rekening
//    function createRek($id_nasabah,$rek)
//    {
//        // query to insert record for rekening nasabah
//        $query = "INSERT INTO
//                " . $this->table_name2 . "
//            SET
//                no_rek=:no_rek, jml_saldo=:jml_saldo, id_nasabah=:id_nasabah, kode_cabang=:kode_cabang";
//
//        // prepare query
//        $stmt = $this->conn->prepare($query);
//        $kcb = 'asd2';
//        // bind values
//        $stmt->bindParam(":id_nasabah", $id_nasabah);
//        $stmt->bindParam(":no_rek", $rek);
//        $stmt->bindParam(":jml_saldo", 500000);
//        $stmt->bindParam(":kode_cabang", $kcb);
//        if ($stmt->execute())
//        {return true;}
//        return false;
//    }

// login
    function login()
    {

        // query to insert record
        $query = "SELECT  username from 
                " . $this->table_name . "
            where
                username=:username and password=:password";

        // prepare query
        $stmt = $this->conn->prepare($query);

        // sanitize
        $this->username = htmlspecialchars(strip_tags($this->username));
        $this->password = htmlspecialchars(strip_tags($this->password));

        // bind values
        $stmt->bindParam(":username", $this->username);
        $stmt->bindParam(":password", $this->password);

        // execute query
        $stmt->execute();

        // instantiate database and products object
        $database = new Database();
        $db = $database->getConnection();

        // initialize object
        $nasabah = new Nasabah($db);

        // query products
        $stmt = $nasabah->read();
        $num = $stmt->rowCount();

        if ($num = 1) {
            return true;
        }

        return false;

    }

// used when filling up the update products form
    function readOne()
    {

        // query to read single record
        $query = "SELECT
                id_nasabah,username,password,nama_lengkap,kode_rahasia
            FROM
                " . $this->table_name . "
            WHERE
                id_nasabah = ?
            LIMIT
                0,1";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // bind id of products to be updated
        $stmt->bindParam(1, $this->id);

        // execute query
        $stmt->execute();

        // get retrieved row
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        // set values to object properties
        $this->id_nasabah = $row['id_nasabah'];
        $this->username = $row['username'];
        $this->password = $row['password'];
        $this->nama_lengkap = $row['nama_lengkap'];
        $this->kode_rahasia = $row['kode_rahasia'];
    }

// baca saldo rekening satu nasabah
    function readOneSaldo()
    {

        // query to read single record
        $query = "SELECT
                no_rek,jml_saldo
            FROM
                " . $this->table_name2 . "
            WHERE
                id_nasabah = ?
            LIMIT
                0,1";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // bind id of products to be updated
        $stmt->bindParam(1, $this->id);

        // execute query
        $stmt->execute();

        // get retrieved row
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        // set values to object properties
        $this->no_rek = $row['no_rek'];
        $this->jml_saldo = $row['jml_saldo'];
    }
//update password
    function update_password(){

        // update query
        $query = "UPDATE
                    " . $this->table_name . "
                SET
                    password = :password
                    
                WHERE
                    id_nasabah = :id_nasabah";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // sanitize
        $this->password=htmlspecialchars(strip_tags($this->password));
        $this->baru1=htmlspecialchars(strip_tags($this->baru1));
        $this->baru2=htmlspecialchars(strip_tags($this->baru2));
        $this->id_nasabah=htmlspecialchars(strip_tags($this->id_nasabah));

        //validation
        if($this->pwdbaru1 == $this->pwdbaru2) {

            // bind new values
            $stmt->bindParam(':password', $this->pwdbaru1);
            $stmt->bindParam(':id_nasabah', $this->id_nasabah);

            // execute the query
            if ($stmt->execute()) {
                return true;
            }
        }
        return false;
    }
//update kata rahasia
    function update_kode_rahasia(){

        // update query
        $query = "UPDATE
                    " . $this->table_name . "
                SET
                    kode_rahasia = :kode_rahasia
                    
                WHERE
                    id_nasabah = :id_nasabah";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // sanitize
        $this->kode_rahasia=htmlspecialchars(strip_tags($this->kode_rahasia));
        $this->baru1=htmlspecialchars(strip_tags($this->krb1));
        $this->baru2=htmlspecialchars(strip_tags($this->krb2));
        $this->id_nasabah=htmlspecialchars(strip_tags($this->id_nasabah));

        //validation
        if($this->pwdbaru1 == $this->pwdbaru2) {

            // bind new values
            $stmt->bindParam(':password', $this->pwdbaru1);
            $stmt->bindParam(':id_nasabah', $this->id_nasabah);

            // execute the query
            if ($stmt->execute()) {
                return true;
            }
        }
        return false;
    }
}