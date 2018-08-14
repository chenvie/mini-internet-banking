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
                id_nasabah=:id_nasabah, email=:email, username=:username, nama_lengkap=:nama_lengkap, password=:password, no_ktp=:no_ktp, tgl_lahir=:tgl_lahir, alamat=:alamat, kode_rahasia=:kode_rahasia, created=CURRENT_TIMESTAMP";

        // prepare query
        $stmt = $this->conn->prepare($query);

        // sanitize
        //$this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));
        $this->email = htmlspecialchars(strip_tags($this->email));
        //$this->username = htmlspecialchars(strip_tags($this->username));
        $this->nama_lengkap = htmlspecialchars(strip_tags($this->nama_lengkap));
        $this->password = htmlspecialchars(strip_tags($this->password));
        $this->no_ktp = htmlspecialchars(strip_tags($this->no_ktp));
        //$this->tgl_lahir = htmlspecialchars(strip_tags($this->tgl_lahir));
        $this->alamat = htmlspecialchars(strip_tags($this->alamat));
        $this->kode_rahasia = htmlspecialchars(strip_tags($this->kode_rahasia));
        //$this->created = htmlspecialchars(strip_tags($this->created));

        $nsb = 4;
        $unm = 'cocoba';
        $tgll= '2018-08-01';
        // bind values
        $stmt->bindParam(":id_nasabah", $nsb);
        $stmt->bindParam(":email", $this->email);
        $stmt->bindParam(":username", $unm);
        $stmt->bindParam(":nama_lengkap", $this->nama_lengkap);
        $stmt->bindParam(":password", $this->password);
        $stmt->bindParam(":no_ktp", $this->no_ktp);
        $stmt->bindParam(":tgl_lahir", $tgll);
        $stmt->bindParam(":alamat", $this->alamat);
        $stmt->bindParam(":kode_rahasia", $this->kode_rahasia);
        $stmt->bindParam(":created", $this->created);

         //executing the query
        $stmt->execute();

        // query to check latest no rek count
        $query = "SELECT no_rek from " . $this->table_name2;
        // prepare query
        $stmt = $this->conn->prepare($query);
        // executing the query
        $stmt->execute();
        // getting the count of rekening
        $num = $stmt->rowCount();
        $num = $num+1;
        // query to insert record for rekening nasabah
        $query = "INSERT INTO
                " . $this->table_name2 . "
            SET
                no_rek=:no_rek, jml_saldo=:jml_saldo,id_nasabah=:id_nasabah,kode_cabang=:kode_cabang";

        // prepare query
        $stmt = $this->conn->prepare($query);

        // bind values
        $stmt->bindParam(":id_nasabah", $nsb);
        $stmt->bindParam(":no_rek", $num);
        $stmt->bindParam(":jml_saldo", 50000);
        $stmt->bindParam(":kode_cabang", 'asd2');

        // execute query
        if ($stmt->execute()) {
            return true;
        }

        return false;

    }

// create new nasabah
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

// update pass
// update kata rahasia

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
}