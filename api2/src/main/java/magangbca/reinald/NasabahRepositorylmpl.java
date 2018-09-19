package magangbca.reinald;

import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NasabahRepositorylmpl{
    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, String> updatePassword(int id_nasabah, String passwordl, String passwordb1, String passwordb2) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("putNasabahPassword")
                .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
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
        String status = query.getOutputParameterValue(5).toString();
        String message = query.getOutputParameterValue(6).toString();

        Map<String, String> result = new HashMap<String, String>();
        result.put("status", status);
        result.put("message", message);

        return result;
    }

    public Map<String, String> updateCode(int id_nasabah, String kode_rahasiaL, String krb1, String krb2){
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("putNasabahKodeRahasia")
                .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
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
        String status = query.getOutputParameterValue(5).toString();
        String message = query.getOutputParameterValue(6).toString();

        Map<String, String> result = new HashMap<String, String>();
        result.put("status", status);
        result.put("message", message);

        return result;
    }

    public Map<String, String> postNasabah(String nama, String email, String password, String no_ktp, String tgl_lhr, String alamat, String kode_rhs){
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("postNasabah2")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(2, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(3, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(4, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(5, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(6, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(7, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(8, String.class, ParameterMode.OUT).
                        registerStoredProcedureParameter(9, String.class, ParameterMode.OUT).
                        registerStoredProcedureParameter(10, String.class, ParameterMode.OUT);

        storedProcedure.setParameter(1, nama)
                .setParameter(2, email)
                .setParameter(3, password)
                .setParameter(4, no_ktp)
                .setParameter(5, tgl_lhr)
                .setParameter(6, alamat)
                .setParameter(7, kode_rhs);

        storedProcedure.execute();
        // Call the stored procedure.

        String username = (String) storedProcedure.getOutputParameterValue(8);
        String status = (String) storedProcedure.getOutputParameterValue(9);
        String message = (String) storedProcedure.getOutputParameterValue(10);

        Map<String, String> result = new HashMap<String, String>();
        result.put("status", status);
        result.put("message", message);
        result.put("username", username);

        return result;
    }

    public Map<String, String> login(String username, String password){
        StoredProcedureQuery storeProcedure = entityManager.createStoredProcedureQuery("login")
                .registerStoredProcedureParameter(1, String.class,ParameterMode.INOUT).
                        registerStoredProcedureParameter(2,String.class,ParameterMode.IN).
                        registerStoredProcedureParameter(3,String.class,ParameterMode.OUT).
                        registerStoredProcedureParameter(4,String.class,ParameterMode.OUT).
                        registerStoredProcedureParameter(5,String.class, ParameterMode.OUT);


        storeProcedure.setParameter(1, username)
                .setParameter(2, password);
        storeProcedure.execute();

        String uname = (String) storeProcedure.getOutputParameterValue(1);
        String status = (String) storeProcedure.getOutputParameterValue(3);
        String msg = (String) storeProcedure.getOutputParameterValue(4);
        String id_nsb = (String) storeProcedure.getOutputParameterValue(5);

        Map<String, String> result = new HashMap<>();
        result.put("status", status);
        result.put("username", uname);
        result.put("message", msg);
        result.put("id nasabah", id_nsb);

        return result;

    }
}
