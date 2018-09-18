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
        @NamedStoredProcedureQuery(name = "cek_norek", procedureName = "cekNoRek", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "norek", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "id_nsb", type = String.class),
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

    public Object[] cekNorek(String norek, String id) {
        return (Object[]) em.createNamedStoredProcedureQuery("cek_norek")
                .setParameter("norek", norek)
                .setParameter("id_nsb", id)
                .getSingleResult();
    }
}

@RestController
class NorekValidationController {
    @Autowired
    private NorekValidationDAO dao;

    @PostMapping(value = "/ceknorek", consumes = "application/json")
    public NorekResponse postNorekJSON(@RequestBody Norek norek) {
        Object[] res = dao.cekNorek(norek.getNorek(), norek.getId_nsb());
        return new NorekResponse(res[0].toString(), res[1].toString());
    }
}

class Norek {
    private String norek;
    private String id_nsb;

    public String getNorek() {
        return norek;
    }

    public String getId_nsb() {
        return id_nsb;
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