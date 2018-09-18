package magangbca.reinald;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NasabahRepository extends JpaRepository<Nasabah, Integer> {

//    List<Nasabah> findByTitleContainingOrContentContaining(String text, String textAgain);

    String updatePassword(String id_nasabah, String passwordl, String passwordb1, String passwordb2);

}
