package magangbca.reinald;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@Entity
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "pembelian", procedureName = "postTransaksiPulsa", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "id_nsb", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "no_hp", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "prov", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "nmnl", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "uname", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "kode_rhs", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "stts", type = String.class)
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

    public Object[] doPembelian(String nohp, String id, String provider, String nominal, String uname, String kode_rhs) {
        return (Object[]) em.createNamedStoredProcedureQuery("pembelian")
                .setParameter("id_nsb", id)
                .setParameter("no_hp", nohp)
                .setParameter("prov", provider)
                .setParameter("nmnl", nominal)
                .setParameter("uname", uname)
                .setParameter("kode_rhs", kode_rhs)
                .getSingleResult();
    }
}

@RestController
class PembelianController{

    @Autowired
    private PembelianRepo repo;

    @PostMapping(value = "/pembelian", consumes = "application/json")

    public PembelianResponse doPembelian(@RequestBody PembelianData pblData ) {
        Object[] res = repo.doPembelian(pblData.getNohp(), pblData.getId_nsb(), pblData.getProv(), pblData.getNmnl(),
                pblData.getUname(), pblData.getKode_rhs());
        return new PembelianResponse(res[0].toString(), res[1].toString());
    }
}

class PembelianData{
    private String nohp;
    private String id_nsb;
    private String prov;
    private String nmnl;
    private String uname;
    private String kode_rhs;

    public String getNohp() {
        return nohp;
    }

    public String getId_nsb() {
        return id_nsb;
    }

    public String getProv() {
        return prov;
    }

    public String getNmnl() {
        return nmnl;
    }

    public String getUname() {
        return uname;
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