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
        @NamedStoredProcedureQuery(name = "cekNoRek", procedureName = "cekNoRek", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "norek_kirim", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "norek_terima", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "stts", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "msg", type = String.class)
        })
})
class NorekValidation {
    @Id
    private int id;
}

@Repository
class NorekValidationDAO {
    @Autowired
    private EntityManager em;

    public Object[] cekNorek(String norek_kirim, String norek_terima ) {
        return (Object[]) em.createNamedStoredProcedureQuery("cekNoRek")
                .setParameter("norek_kirim", norek_kirim)
                .setParameter("norek_terima", norek_terima)
                .getSingleResult();
    }
}

@RestController
class NorekValidationController {
    @Autowired
    private NorekValidationDAO dao;

    @PostMapping(value = "/ceknorek", consumes = "application/json")
    public NorekResponse postNorekJSON(@RequestBody Norek norek) {
        Object[] res = dao.cekNorek(norek.getNorek_kirim(),norek.getNorek_terima());
        return new NorekResponse(res[0].toString(), res[1].toString());
    }
}

class Norek {
    private String norek_kirim;
    private String norek_terima;

    public String getNorek_kirim() {
        return norek_kirim;
    }

    public String getNorek_terima() {
        return norek_terima;
    }




}

class NorekResponse {
    private String status;
    private String message;

    public NorekResponse(String status, String message) {
        this.setMessage(message);
        this.setStatus(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}