package magangbca.reinald;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.security.PublicKey;
import java.sql.Timestamp;

@Entity
public class Rekening {
    @Id
    private String no_rek;

    private String kode_rahasia;

    public String getNo_rek() {
        return no_rek;
    }

    public void setNo_rek(String no_rek) {
        this.no_rek = no_rek;
    }

       public String getKode_rahasia() {
        return kode_rahasia;
    }

    public void setKode_rahasia(String kode_rahasia) {
        this.kode_rahasia = kode_rahasia;
    }

    public Integer getJml_saldo() {
        return jml_saldo;
    }

    public void setJml_saldo(Integer jml_saldo) {
        this.jml_saldo = jml_saldo;
    }

    public String getKode_cabang() {
        return kode_cabang;
    }

    public void setKode_cabang(String kode_cabang) {
        this.kode_cabang = kode_cabang;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    private Integer jml_saldo;
    private String kode_cabang;
    private String created;

    public Rekening(){}
    public Rekening(String nr, String kr, Integer saldo, String kc, String created){
        this.setNo_rek(nr);
        this.setKode_rahasia(kr);
        this.setJml_saldo(saldo);
        this.setKode_cabang(kc);
        this.setCreated(created);
    }
}
