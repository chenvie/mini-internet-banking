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
public class MutasiRepositoryImpl implements MutasiRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
        public Response getSomeMutasi(String norek, LocalDate tgl_awal, LocalDate tgl_akhir){

        Date date1 = Date.valueOf(tgl_awal);
        Date date2 = Date.valueOf(tgl_akhir);
           StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("getMutasi")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(2, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(3, String.class, ParameterMode.IN);

        storedProcedure.setParameter(1, norek)
                    .setParameter(2, date2.toString())
                    .setParameter(3, date1.toString());

            storedProcedure.execute();
            // Call the stored procedure.
            List<Object[]> storedProcedureResults = storedProcedure.getResultList();

        Map<String, String> tgl = new HashMap<String, String>();
        tgl.put("no_rek", norek);
        tgl.put("tgl_awal", tgl_awal.toString());
        tgl.put("tgl_akhir", tgl_akhir.toString());

        Response resp = new Response();
        resp.setResp(new ResponseEntity<Map<String, String>>(tgl, HttpStatus.OK));
        resp.setResult(storedProcedureResults.stream().map(result -> new Mutasi( result[0].toString(),result[1].toString(),result[3].toString(),result[2].toString(),result[4].toString(),(BigInteger) result[5])).collect(Collectors.toList()));
        return resp;
    }
}


