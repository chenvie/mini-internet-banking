package magangbca.reinald;

import java.util.List;

//public interface MutasiRepository extends JpaRepository<Mutasi,String> {
public interface MutasiRepository {

    List<Mutasi> getSomeMutasi();
//    List<Mutasi> getSomeMutasi(Integer firstParameter, Date secondParameter, Date thirdParameter);
//    @Query(nativeQuery = true,
//    value = "CALL getMutasi(:id_nasabah,:tgl1,:tgl2)")
//    List<Mutasi> findAllByTujuanEqualsAndTgl_transIsBeforeAndTgl_transIsAfter(@Param("id_nasabah") Integer id_nasabah,
//                                                 @Param("tgl1") Date tgl1,
//                                                 @Param("tgl2") Date tgl2);
}



