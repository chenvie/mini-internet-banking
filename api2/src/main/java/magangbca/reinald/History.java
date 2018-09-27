package magangbca.reinald;

import javax.persistence.Id;
import java.math.BigInteger;


public class History {
    @Id
    private String kode_transaksi;

    private String tgl_trans;
    private String tujuan;
    private String keterangan;
    private String nominal;

    private String status;

    public History() {  }
    public History(String kd, String tj, String ttr, String ket, String nom, String stts) {
        this.setKode_transaksi(kd);
        this.setTujuan(tj);
        this.setTgl_trans(ttr);
        this.setKeterangan(ket);
        this.setNominal(nom);
        this.setStatus(stts);
    }

    public String getKode_transaksi() {
        return kode_transaksi;
    }

    public String getTujuan() {
        return tujuan;
    }

    public String getTgl_trans() {
        return tgl_trans;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getNominal() {
        return nominal;
    }

    public String getStatus() {
        return status;
    }

    public void setKode_transaksi(String kode_transaksi) {
        this.kode_transaksi = kode_transaksi;
    }

    public void setTgl_trans(String tgl_trans) {
        this.tgl_trans = tgl_trans;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "History{" +
                "kode_transaksi=" + kode_transaksi +
                ", tgl_trans='" + tgl_trans + '\'' +
                ", tujuan='" + tujuan + '\'' +
                ", keterangan='" + keterangan + '\'' +
                ", nominal='" + nominal + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
