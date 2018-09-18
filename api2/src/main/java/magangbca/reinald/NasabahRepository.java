package magangbca.reinald;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NasabahRepository extends JpaRepository<Nasabah, Integer> {

    //List<String> updatePassword(int id_nasabah, String passwordl, String passwordb1, String passwordb2);

}
