package magangbca.reinald;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Map;

@RestController
public class NasabahController {

    @Autowired
    NasabahRepository nasabahRepository;

    @GetMapping("/nasabah")
    public List<Nasabah> index(){
        return nasabahRepository.findAll();
    }

    @GetMapping("/nasabah/{id}")
    public Nasabah show(@PathVariable String id){
        int nasabahID = Integer.parseInt(id);
        return nasabahRepository.findOne(nasabahID);
    }

//    @PostMapping("/nasabah/search")
//    public List<Nasabah> search(@RequestBody Map<String, String> body){
//        String searchTerm = body.get("text");
//        return nasabahRepository.findByTitleContainingOrContentContaining(searchTerm, searchTerm);
//    }

@PersistenceContext
private EntityManager entityManager;
@PostMapping("/nasabah")
    public Response create(@RequestBody Map<String, String> body){
        String nama = body.get("nama_lengkap");
        String email = body.get("email");
        String password  = body.get("password");
        String no_ktp = body.get("no_ktp");
        String tgl_lhr = body.get("tgl_lahir");
        String alamat = body.get("alamat");
        String kode_rhs = body.get("kode_rahasia");

    StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("postNasabah2")
            .registerStoredProcedureParameter(1, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(2, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(3, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(4, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(5, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(6, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(7, String.class, ParameterMode.IN).
                    registerStoredProcedureParameter(8, String.class, ParameterMode.OUT).
                    registerStoredProcedureParameter(9, String.class, ParameterMode.OUT).
                    registerStoredProcedureParameter(10, String.class, ParameterMode.OUT);

    storedProcedure.setParameter(1, nama)
            .setParameter(2, email)
            .setParameter(3, password)
            .setParameter(4, no_ktp)
            .setParameter(5, tgl_lhr)
            .setParameter(6, alamat)
            .setParameter(7, kode_rhs);

    storedProcedure.execute();
    // Call the stored procedure.

    String username = (String) storedProcedure.getOutputParameterValue(8);
    String status = (String) storedProcedure.getOutputParameterValue(9);
    String message = (String) storedProcedure.getOutputParameterValue(10);

    Response resp = new Response(status,message,username);

    return resp;
    }

    @PutMapping("/nasabah/up/{id}")
    public Nasabah update(@PathVariable String id, @RequestBody Map<String, String> body){
        int nsb_id = Integer.parseInt(id);
        // getting blog
        Nasabah nsb = nasabahRepository.findOne(nsb_id);
        nsb.setKode_rahasia(body.get("np"));
        return nasabahRepository.save(nsb);
    }

    @DeleteMapping("nasabah/{id}")
    public boolean delete(@PathVariable String id){
        int nasabahId = Integer.parseInt(id);
        nasabahRepository.delete(nasabahId);
        return true;
    }
}