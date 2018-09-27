package magangbca.reinald;

import javax.persistence.Id;
import java.math.BigInteger;


public class Mutasi {
    @Id
    private String kode_transaksi;

    private String tgl_trans;
    private String tujuan;
    private String jenis;
    private String keterangan;
    private String nominal;

    public Mutasi() {  }
    public Mutasi(String kd, String tj, String ttr, String jns, String ket, String nom) {
      this.setKode_transaksi(kd);
      this.setTujuan(tj);
      this.setTgl_trans(ttr);
      this.setJenis(jns);
      this.setKeterangan(ket);
      this.setNominal(nom);
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

    public String getJenis() {
        return jenis;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getNominal() {
        return nominal;
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

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }


    @Override
    public String toString() {
        return "Mutasi{" +
                "kode_transaksi=" + kode_transaksi +
                ", tgl_trans='" + tgl_trans + '\'' +
                ", tujuan='" + tujuan + '\'' +
                ", jenis='" + jenis + '\'' +
                ", keterangan='" + keterangan + '\'' +
                ", nominal='" + nominal + '\'' +
                '}';
    }
}
