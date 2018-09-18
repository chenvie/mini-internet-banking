package magangbca.reinald;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import javax.persistence.*;

@Repository
public interface NasabahRepository extends JpaRepository<Nasabah, Integer> {

//    List<Nasabah> findByTitleContainingOrContentContaining(String text, String textAgain);

    //List<String> updatePassword(int id_nasabah, String passwordl, String passwordb1, String passwordb2);

}

