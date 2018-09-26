package magangbca.reinald;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.criteria.CriteriaBuilder;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@Entity
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "pembelian", procedureName = "postTransaksiPulsa", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "norek", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "no_hp_tujuan", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "nmnl", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "prvdr", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "kode_rhs", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "stts", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ket_stts", type = String.class)
        })
})

public class Pembelian {
    @Id
    private int id;
}

@Repository
class PembelianRepo{

    @Autowired
    private EntityManager em;

    public Object[] doPembelian(String norek, String nohp, Integer nominal, String provider, String kode_rhs) {
        return (Object[]) em.createNamedStoredProcedureQuery("pembelian")
                .setParameter("norek", norek)
                .setParameter("no_hp_tujuan", nohp)
                .setParameter("nmnl", nominal)
                .setParameter("prvdr", provider)
                .setParameter("kode_rhs", kode_rhs)
                .getSingleResult();
    }
}

@RestController
class PembelianController{
    private static Logger logger = Logger.getRootLogger();

    @Autowired
    private PembelianRepo repo;

    @PostMapping(value = "/pembelian", consumes = "application/json")

    public PembelianResponse doPembelian(@RequestBody PembelianData pblData ) {
        PropertyConfigurator.configure("log4j.properties");
        Object[] res = repo.doPembelian(pblData.getNorek(), pblData.getNo_hp_tujuan(), pblData.getNominal(), pblData.getProvider(),
                 pblData.getKode_rhs());
        logger.info("Beli Pulsa nasabah No Rekening : " + pblData.getNorek() + " Nomer Tujuan : " + pblData.getNo_hp_tujuan() +
                " Nominal : " + pblData.getNominal() + " Provider : " + pblData.getProvider() + " Message : " + res[1].toString());
        return new PembelianResponse(res[0].toString(), res[1].toString());
    }
}

class PembelianData{
    private String norek;
    private String no_hp_tujuan;
    private String provider;
    private Integer nominal;
    private String kode_rhs;


    public String getProvider() {
        return provider;
    }

    public Integer getNominal() {
        return nominal;
    }

    public String getNo_hp_tujuan() {
        return no_hp_tujuan;
    }

    public String getNorek() {
        return norek;
    }

    public String getKode_rhs() {
        return kode_rhs;
    }
}

class PembelianResponse{
    private String status;
    private String message;

    public PembelianResponse(String status, String message) {
        this.setMessage(message);
        this.setStatus(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}