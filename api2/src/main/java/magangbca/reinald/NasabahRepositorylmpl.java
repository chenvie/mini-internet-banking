package magangbca.reinald;

import org.springframework.stereotype.Repository;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class NasabahRepositorylmpl{
    private static Logger logger = Logger.getRootLogger();
    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, String> updatePassword(int id_nasabah, String passwordl, String passwordb1, String passwordb2) {
        PropertyConfigurator.configure("log4j.properties");
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
        logger.info("Update Password Id nasabah : " + id_nasabah + " Password lama : " + passwordl +
                " Password baru : " + passwordb1 + " Message " + message);
        return result;
    }

    public Map<String, String> updateCode(String no_rek, String kode_rahasiaL, String krb1, String krb2){
        PropertyConfigurator.configure("log4j.properties");
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("putNasabahKodeRahasia")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter(6, String.class, ParameterMode.OUT);

        query.setParameter(1, no_rek)
                .setParameter(2, kode_rahasiaL)
                .setParameter(3, krb1)
                .setParameter(4, krb2);

        query.execute();
        String status = query.getOutputParameterValue(5).toString();
        String message = query.getOutputParameterValue(6).toString();

        Map<String, String> result = new HashMap<String, String>();
        result.put("status", status);
        result.put("message", message);
        logger.info("Update Kode Rahasia No Rekening : " + no_rek + " Kode lama : " + kode_rahasiaL + " Kode baru : " +
                krb1 + " Message : " + message);
        return result;
    }

    public Map<String, String> postNasabah(String nama, String email, String password, String no_ktp, String tgl_lhr, String alamat, String kode_rhs){
        PropertyConfigurator.configure("log4j.properties");
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
                        registerStoredProcedureParameter(10, String.class, ParameterMode.OUT).
                        registerStoredProcedureParameter(11, String.class, ParameterMode.OUT).
                        registerStoredProcedureParameter(12, String.class, ParameterMode.OUT);

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
        String no_rek  = (String) storedProcedure.getOutputParameterValue(11);
        String id  = (String) storedProcedure.getOutputParameterValue(12);

        Map<String, String> result = new HashMap<String, String>();
        result.put("status", status);
        result.put("message", message);
        result.put("username", username);
        result.put("no_rek", no_rek);
        result.put("id", id);
        logger.info("User register No rekening : " + no_rek + " Nama : " + nama + " Email : " + email + " Username : " +
                username + " Password : " + password + " No KTP : " + no_ktp + " Tanggal lahir : " + tgl_lhr + " Alamat : " +
                alamat + " Kode rahasia : " + kode_rhs + " Message : " + message);
        return result;
    }

    public Map<String, String> login(String username, String password){
        PropertyConfigurator.configure("log4j.properties");
        StoredProcedureQuery storeProcedure = entityManager.createStoredProcedureQuery("login")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.INOUT).
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
        result.put("id_nasabah", id_nsb);
        logger.info("User login Username : " + username + " Password : " + password + " Message : " + msg);
        return result;

    }

    public Map<String, String> postRek(String id_nsb, String kr){
        PropertyConfigurator.configure("log4j.properties");
        StoredProcedureQuery storeProcedure = entityManager.createStoredProcedureQuery("postRekening")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(2,String.class,ParameterMode.IN).
                        registerStoredProcedureParameter(3,String.class,ParameterMode.OUT).
                        registerStoredProcedureParameter(4,String.class,ParameterMode.OUT).
                        registerStoredProcedureParameter(5,String.class, ParameterMode.OUT);


        storeProcedure.setParameter(1, id_nsb)
                .setParameter(2, kr);
        storeProcedure.execute();

        String norek = (String) storeProcedure.getOutputParameterValue(3);
        String status = (String) storeProcedure.getOutputParameterValue(4);
        String msg = (String) storeProcedure.getOutputParameterValue(5);

        Map<String, String> result = new HashMap<>();
        result.put("status", status);
        result.put("message", msg);
        result.put("no_rek", norek);
        logger.info("Tambah rekening : " + norek + " Id nasabah : " + id_nsb + " Kode rahasia : " + kr + " Message : " + msg);
        return result;

    }

    public List<Object> getRekList (Integer id_nsb){
        PropertyConfigurator.configure("log4j.properties");
        StoredProcedureQuery storeProcedure = entityManager.createStoredProcedureQuery("getListNoRek")
                .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);


        storeProcedure.setParameter(1, id_nsb);
        storeProcedure.execute();

        // Call the stored procedure.
        List<Object[]> storedProcedureResults = storeProcedure.getResultList();
        logger.info("Id nasabah : " + id_nsb);
        return storedProcedureResults.stream().map(result -> new Rekening( result[0].toString(),result[1].toString(),(Integer) result[2],result[3].toString(),result[4].toString())).collect(Collectors.toList());

    }
}
