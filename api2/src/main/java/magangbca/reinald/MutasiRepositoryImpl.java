package magangbca.reinald;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MutasiRepositoryImpl implements MutasiRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Mutasi> getSomeMutasi(){
//        public List<Mutasi> getSomeMutasi(Integer firstParameter, Date secondParameter, Date thirdParameter){

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("getMutasi2");
//                    .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN).
//                    registerStoredProcedureParameter(2, Date.class, ParameterMode.IN).
//                    registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);


//        storedProcedure.setParameter(1, firstParameter)
//                    .setParameter(2, secondParameter)
//                    .setParameter(3, thirdParameter);

            storedProcedure.execute();
            // Call the stored procedure.
            List<Object[]> storedProcedureResults = storedProcedure.getResultList();

            // Use Java 8's cool new functional programming paradigm to map the objects from the stored procedure results
            return storedProcedureResults.stream().map(result -> new Mutasi( result[0].toString(),result[1].toString(),result[2].toString(),result[3].toString(),result[4].toString(),result[5].toString(),(BigInteger) result[6])).collect(Collectors.toList());



    }
}
