//package magangbca.reinald;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.EntityManager;
//import javax.persistence.ParameterMode;
//import javax.persistence.PersistenceContext;
//import javax.persistence.StoredProcedureQuery;
//import java.util.List;
//
//@Service
//public class NasabahRepositoryImpl implements NasabahRepository {
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public List<Nasabah> findAll() {
//        return null;
//    }
//
//    public List<Nasabah> findAll(Sort sort) {
//        return null;
//    }
//
//    public List<Nasabah> findAll(Iterable<Integer> iterable) {
//        return null;
//    }
//
//    public <S extends Nasabah> List<S> save(Iterable<S> iterable) {
//        return null;
//    }
//
//    public void flush() {
//
//    }
//
//    public <S extends Nasabah> S saveAndFlush(S s) {
//        return null;
//    }
//
//    public void deleteInBatch(Iterable<Nasabah> iterable) {
//
//    }
//
//    @Override
//    public String postNasabah(String nama,String email,String pwd,String mo_ktp,String tgl_lahir,String alamat,String kode_rahasia){
//
//
//        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("postNasabah2")
//                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(2, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(3, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(4, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(5, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(6, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(7, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(8, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(9, String.class, ParameterMode.IN).
//                        registerStoredProcedureParameter(10, String.class, ParameterMode.IN);
//
//        storedProcedure.setParameter(1, nama)
//                .setParameter(2, email)
//                .setParameter(3, pwd)
//                .setParameter(4, pwd)
//                .setParameter(5, pwd)
//                .setParameter(6, pwd)
//                .setParameter(7, pwd);
//
//        storedProcedure.execute();
//        // Call the stored procedure.
//
//        String username = (String) storedProcedure.getOutputParameterValue(8);
//        String status = (String) storedProcedure.getOutputParameterValue(9);
//        String message = (String) storedProcedure.getOutputParameterValue(10);
//
//        return  "Nasabah{" +
//                "status=" + status +
//                ", message='" + message + '\'' +
//                ", username='" + username + '\'' +
//                '}';
//
//    }
//}
