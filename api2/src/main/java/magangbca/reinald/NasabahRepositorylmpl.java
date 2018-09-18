package magangbca.reinald;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class NasabahRepositorylmpl implements NasabahRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String updatePassword(String id_nasabah, String passwordl, String passwordb1, String passwordb2) {
        return null;
    }
}
