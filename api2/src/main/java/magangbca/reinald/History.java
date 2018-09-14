package magangbca.reinald;

import javax.persistence.Id;
import java.math.BigInteger;


public class History {
    @Id
    private String kode_transaksi;

    private String no_rek;
    private String tgl_trans;
    private String tujuan;
    private String jenis;
    private String keterangan;
    private BigInteger nominal;

    public History() {  }
    public History(String kd, String nr, String tj, String ttr, String jns, String ket, BigInteger nom) {
        this.setKode_transaksi(kd);
        this.setNo_rek(nr);
        this.setTujuan(tj);
        this.setTgl_trans(ttr);
        this.setJenis(jns);
        this.setKeterangan(ket);
        this.setNominal(nom);
    }

    public String getKode_transaksi() {
        return kode_transaksi;
    }

    public String getNo_rek() {
        return no_rek;
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

    public BigInteger getNominal() {
        return nominal;
    }

    public void setKode_transaksi(String kode_transaksi) {
        this.kode_transaksi = kode_transaksi;
    }

    public void setNo_rek(String no_rek) {
        this.no_rek = no_rek;
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

    public void setNominal(BigInteger nominal) {
        this.nominal = nominal;
    }


    @Override
    public String toString() {
        return "History{" +
                "kode_transaksi=" + kode_transaksi +
                ", no_rek='" + no_rek + '\'' +
                ", tgl_trans='" + tgl_trans + '\'' +
                ", tujuan='" + tujuan + '\'' +
                ", jenis='" + jenis + '\'' +
                ", keterangan='" + keterangan + '\'' +
                ", nominal='" + nominal + '\'' +
                '}';
    }
}
