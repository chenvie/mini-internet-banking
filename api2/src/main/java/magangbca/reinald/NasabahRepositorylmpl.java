package magangbca.reinald;

import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NasabahRepositorylmpl{
    @PersistenceContext
    private EntityManager entityManager;

    public List<String> updatePassword(int id_nasabah, String passwordl, String passwordb1, String passwordb2) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("putNasabahPassword")
                .registerStoredProcedureParameter(1, int.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT);

        query.setParameter(1, id_nasabah)
                .setParameter(2, passwordl)
                .setParameter(3, passwordb1)
                .setParameter(4, passwordb2);

        query.execute();
        String status = query.getOutputParameterValue("stts").toString();
        String message = query.getOutputParameterValue("msg").toString();

        List<String> output = new ArrayList<String>();
        output.add(status);
        output.add(message);

        return output;
    }

    public List<String> updateCode(int id_nasabah, String kode_rahasiaL, String krb1, String krb2){
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("putNasabahKodeRahasia")
                .registerStoredProcedureParameter(1, int.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT);

        query.setParameter(1, id_nasabah)
                .setParameter(2, kode_rahasiaL)
                .setParameter(3, krb1)
                .setParameter(4, krb2);

        query.execute();
        String status = query.getOutputParameterValue("stts").toString();
        String message = query.getOutputParameterValue("msg").toString();

        List<String> output = new ArrayList<String>();
        output.add(status);
        output.add(message);

        return output;
    }
}
