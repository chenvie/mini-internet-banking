package magangbca.reinald;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class HistoryRepositoryImpl implements HistoryRepository {
    private static Logger logger = Logger.getRootLogger();
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Response getSomeHistory(String norek, String tgl_awal, String tgl_akhir){
        PropertyConfigurator.configure("log4j.properties");


        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("getHistory")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(2, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(3, String.class, ParameterMode.IN);

        storedProcedure.setParameter(1, norek)
                .setParameter(2, tgl_awal)
                .setParameter(3, tgl_akhir);

        storedProcedure.execute();
        // Call the stored procedure.
        List<Object[]> storedProcedureResults = storedProcedure.getResultList();

        // Use Java 8's cool new functional programming paradigm to map the objects from the stored procedure results
        Map<String, String> tgl = new HashMap<String, String>();
        tgl.put("no_rek", norek);
        tgl.put("tgl_awal", tgl_awal);
        tgl.put("tgl_akhir", tgl_akhir);

        Response resp = new Response();
        resp.setResp(new ResponseEntity<Map<String, String>>(tgl, HttpStatus.OK));
        resp.setResult(storedProcedureResults.stream().map(result -> new History( result[0].toString(),result[2].toString(),result[1].toString(),result[3].toString(),result[4].toString(), result[5].toString())).collect(Collectors.toList()));
        logger.info("Cek History Transaksi No Rekening : " + norek + " Tanggal awal : " + tgl_awal + " Tanggal akhir : " + tgl_akhir);
        return resp;


    }
}
