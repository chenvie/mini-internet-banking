package magangbca.reinald;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

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
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Response getSomeHistory(Integer firstParameter, String secondParameter, String thirdParameter){


        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("getHistory")
                .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN).
                        registerStoredProcedureParameter(2, String.class, ParameterMode.IN).
                        registerStoredProcedureParameter(3, String.class, ParameterMode.IN);

        storedProcedure.setParameter(1, firstParameter)
                .setParameter(2, secondParameter)
                .setParameter(3, thirdParameter);

        storedProcedure.execute();
        // Call the stored procedure.
        List<Object[]> storedProcedureResults = storedProcedure.getResultList();

        // Use Java 8's cool new functional programming paradigm to map the objects from the stored procedure results
        Map<String, String> tgl = new HashMap<String, String>();
        tgl.put("id_nasabah", firstParameter.toString());
        tgl.put("tgl_awal", secondParameter);
        tgl.put("tgl_akhir", thirdParameter);

        Response resp = new Response();
        resp.setResp(new ResponseEntity<Map<String, String>>(tgl, HttpStatus.OK));
        resp.setResult(storedProcedureResults.stream().map(result -> new History( result[0].toString(),result[1].toString(),result[2].toString(),result[3].toString(),(BigInteger)result[4], result[5].toString())).collect(Collectors.toList()));
        return resp;


    }
}
