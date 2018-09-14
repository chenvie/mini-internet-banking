package magangbca.reinald;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Nasabah {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id_nasabah;

    private String email;
    private String username;
    private String nama_lengkap;
    private String password;
    private String no_ktp;
    private Date tgl_lahir;
    private String alamat;
    private String kode_rahasia;
    private String no_rek;
    private int jml_saldo;
    private String kode_cabang;
    private Timestamp created;

    public int getId_nasabah() {
        return id_nasabah;
    }

    public void setId_nasabah(int id_nasabah) {
        this.id_nasabah = id_nasabah;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNo_ktp() {
        return no_ktp;
    }

    public void setNo_ktp(String no_ktp) {
        this.no_ktp = no_ktp;
    }

    public Date getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(Date tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKode_rahasia() {
        return kode_rahasia;
    }

    public void setKode_rahasia(String kode_rahasia) {
        this.kode_rahasia = kode_rahasia;
    }

    public String getNo_rek() {
        return no_rek;
    }

    public void setNo_rek(String no_rek) {
        this.no_rek = no_rek;
    }

    public int getJml_saldo() {
        return jml_saldo;
    }

    public void setJml_saldo(int jml_saldo) {
        this.jml_saldo = jml_saldo;
    }

    public String getKode_cabang() {
        return kode_cabang;
    }

    public void setKode_cabang(String kode_cabang) {
        this.kode_cabang = kode_cabang;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Nasabah() {  }

    @Override
    public String toString() {
        return "Nasabah{" +
                "id_nasabah=" + id_nasabah +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", nama_lengkap='" + nama_lengkap + '\'' +
                ", password='" + password + '\'' +
                ", no_ktp='" + no_ktp + '\'' +
                ", tgl_lahir='" + tgl_lahir + '\'' +
                ", alamat='" + alamat + '\'' +
                ", kode_rahasia='" + kode_rahasia + '\'' +
                ", no_rek='" + no_rek + '\'' +
                ", jml_saldo='" + jml_saldo + '\'' +
                ", kode_cabang='" + kode_cabang + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}
