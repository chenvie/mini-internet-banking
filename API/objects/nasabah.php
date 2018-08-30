<?php
class Nasabah
{

    // database connection and table name
    private $conn;
    private $table_name = "nasabah";

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
    public $jml_saldo = 450000;
    public $kode_cabang = 'asd1';
    public $baru1;
    public $baru2;
    public $id;
    public $jml_nsb;
    public $ceku;
    public $cekp;

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

    //menghasilkan nomor rekening baru untuk nasabah yang berhasil daftar, 
    //dengan mengambil 3 karakter depan adalah kode cabang dan 3 karakter belakang adalah nomor urut
    function generateNoRek(){
        $awalan_rek = "03700";
        $sql = "SELECT COUNT(id_nasabah) as jml FROM nasabah";
        $stmt = $this->conn->prepare($sql);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $this->jml_nsb = $row['jml'];
        $this->jml_nsb = $this->jml_nsb+1;
        if ($this->jml_nsb > 9) {
                $awalan_rek = "0370";
            } else if ($this->jml_nsb > 99) {
                $awalan_rek = "037";
            }
        $c = $awalan_rek . $this->jml_nsb;
        return $c;
    }
    // menghasilkan username untuk nasabah yang mendaftar dengan mengambil nama depan ditambah dengan angka urutan
    function generateUsername($nama){
        $sql = "SELECT COUNT(id_nasabah) as jml FROM nasabah";
        $stmt = $this->conn->prepare($sql);
        $stmt->execute();
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $this->jml_nsb = $row['jml'];
        $this->jml_nsb = $this->jml_nsb+1;

        if (strpos($nama,' ') == null){
            $this->username = $nama;
        }else {
            $this->username = substr($nama, 0, strpos($nama, ' '));
        }
        $c = $this->username.(string)$this->jml_nsb;
        return $c;
    }

    // create new nasabah
    function create()
    {
        $this->no_rek = $this->generateNoRek();
        $this->username = $this->generateUsername($this->nama_lengkap);
        //$this->no_rek = '037002';
        // query to insert record for nasabah
        $query = "INSERT INTO
                " . $this->table_name . "
            SET
                email=:email, username=:username, nama_lengkap=:nama_lengkap, password=:password, no_ktp=:no_ktp, tgl_lahir=:tgl_lahir, alamat=:alamat, kode_rahasia=:kode_rahasia, no_rek=:no_rek, jml_saldo=:jml_saldo, kode_cabang=:kode_cabang, created=:created";

        // prepare query
        $stmt = $this->conn->prepare($query);

        // sanitize
        //$this->id_nasabah = htmlspecialchars(strip_tags($this->id_nasabah));
        $this->username = htmlspecialchars(strip_tags($this->username));
        $this->created = htmlspecialchars(strip_tags($this->created));
        $this->email = htmlspecialchars(strip_tags($this->email));
        $this->nama_lengkap = htmlspecialchars(strip_tags($this->nama_lengkap));
        $this->password = htmlspecialchars(strip_tags($this->password));
        $this->no_ktp = htmlspecialchars(strip_tags($this->no_ktp));
        $this->alamat = htmlspecialchars(strip_tags($this->alamat));
        $this->kode_rahasia = htmlspecialchars(strip_tags($this->kode_rahasia));
        $this->tgl_lahir = htmlspecialchars(strip_tags($this->tgl_lahir));
        $this->no_rek = htmlspecialchars(strip_tags($this->no_rek));
        $this->jml_saldo = htmlspecialchars(strip_tags($this->jml_saldo));
        $this->kode_cabang = htmlspecialchars(strip_tags($this->kode_cabang));

        // bind values
        //$stmt->bindParam(":id_nasabah", $this->id_nasabah);
        $stmt->bindParam(":email", $this->email);
        $stmt->bindParam(":username", $this->username);
        $stmt->bindParam(":nama_lengkap", $this->nama_lengkap);
        $stmt->bindParam(":password", $this->password);
        $stmt->bindParam(":no_ktp", $this->no_ktp);
        $stmt->bindParam(":tgl_lahir", $this->tgl_lahir);
        $stmt->bindParam(":alamat", $this->alamat);
        $stmt->bindParam(":kode_rahasia", $this->kode_rahasia);
        $stmt->bindParam(":created", $this->created);
        $stmt->bindParam(":no_rek", $this->no_rek);
        $stmt->bindParam(":jml_saldo", $this->jml_saldo);
        $stmt->bindParam(":kode_cabang", $this->kode_cabang);

        if ( $stmt->execute()) {
            return true;
        }

        return false;

    }

// login
    function login()
    {
        // query to insert record
        $query = "SELECT  username, password from 
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
        $this->ceku = $stmt->rowCount();
//        $this->querycek = preg_replace(":username",$this->username,preg_replace(":password",$this->password,$query));
//        $row = $stmt->fetch(PDO::FETCH_ASSOC);
//        $this->ceku = $row['username'];
//        $this->cekp = $row['password'];

//        if ($this->ceku == $this->username && $this->cekp == $this->password) {
        if ($this->ceku == 1    ) {

            return true;
        }

        return false;

    }

// membaca satu data nasabah yang akan digunakan di aplikasi
    function readOne()
    {

        // query to read single record
        $query = "SELECT
                id_nasabah,username,password,nama_lengkap,kode_rahasia,tgl_lahir,jml_saldo,no_rek
            FROM
                " . $this->table_name . "
            WHERE
                username = ?
            LIMIT
                0,1";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // bind id of products to be updated
        $stmt->bindParam(1, $this->username);

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
        $this->tgl_lahir = $row['tgl_lahir'];
        $this->jml_saldo = $row['jml_saldo'];
        $this->no_rek = $row['no_rek'];
    }

    //membaca data password dari satu nasabah, digunakan saat update password
    function readOnePwd($id)
    {

        // query to read single record
        $query = "SELECT
                password
            FROM
                " . $this->table_name . "
            WHERE
                id_nasabah = ?
            LIMIT
                0,1";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // bind id of products to be updated
        $stmt->bindParam(1, $id);

        // execute query
        $stmt->execute();

        // get retrieved row
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        // set values to object properties
        $this->password = $row['password'];
    }

    //membaca data kode rahasia dari satu nasabah, digunakan saat update kode rahasia
    function readOneKode($id)
    {

        // query to read single record
        $query = "SELECT
                kode_rahasia
            FROM
                " . $this->table_name . "
            WHERE
                id_nasabah = ?
            LIMIT
                0,1";

        // prepare query statement
        $stmt = $this->conn->prepare($query);

        // bind id of products to be updated
        $stmt->bindParam(1, $id);

        // execute query
        $stmt->execute();

        // get retrieved row
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        // set values to object properties
        $this->kode_rahasia = $row['kode_rahasia'];
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
        if($this->baru1 == $this->baru2) {

            // bind new values
            $stmt->bindParam(':password', $this->baru1);
            $stmt->bindParam(':id_nasabah', $this->id_nasabah);

            // execute the query
            if ($stmt->execute()) {
                return true;
            }
        }
        return false;
    }
//update kode rahasia
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
        $this->baru1=htmlspecialchars(strip_tags($this->baru1));
        $this->baru2=htmlspecialchars(strip_tags($this->baru2));
        $this->id_nasabah=htmlspecialchars(strip_tags($this->id_nasabah));

        //validation
        if($this->baru1 == $this->baru2) {

            // bind new values
            $stmt->bindParam(':kode_rahasia', $this->baru1);
            $stmt->bindParam(':id_nasabah', $this->id_nasabah);

            // execute the query
            if ($stmt->execute()) {
                return true;
            }
        }
        return false;
    }

    // returns true if $needle is a substring of $haystack
    function contains($needle, $haystack)
    {
        return strpos($haystack, $needle) !== false;
    }
}