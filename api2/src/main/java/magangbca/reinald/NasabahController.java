package magangbca.reinald;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class NasabahController {

    @Autowired
    NasabahRepository nasabahRepository;

    @Autowired
    NasabahRepositorylmpl nasabahRepositorylmpl;

    @GetMapping("/nasabah")
    public List<Nasabah> index(){
        return nasabahRepository.findAll();
    }

//    @GetMapping("/nasabah/{id}")
//    public Nasabah show(@PathVariable String id){
//        int nasabahID = Integer.parseInt(id);
//        return nasabahRepository.findOne(nasabahID);
//    }

    @GetMapping("/nasabah/{id}")
    public Response show(@PathVariable String id){
        int nasabahID = Integer.parseInt(id);

        Response resp = new Response();
        resp.setRespon(nasabahRepository.findOne(nasabahID));
        resp.setResult(nasabahRepositorylmpl.getRekList(nasabahID));
        return  resp;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Map<String, String> result = nasabahRepositorylmpl.login(username,password);

        return new ResponseEntity<Map<String, String>>(result, HttpStatus.OK);
    }

    @PostMapping("/update_password")
    public ResponseEntity<?> updatePass(@RequestBody Map<String, String> body){
        int id_nasabah = Integer.parseInt(body.get("id_nasabah"));
        String passwordl = body.get("passwordl");
        String passwordb1 = body.get("passwordb1");
        String passwordb2 = body.get("passwordb2");

        Map<String, String> result = nasabahRepositorylmpl.updatePassword(id_nasabah, passwordl, passwordb1, passwordb2);

        return new ResponseEntity<Map<String, String>>(result, HttpStatus.OK);
    }

    @PostMapping("/update_kode_rahasia")
    public ResponseEntity<?> updateCode(@RequestBody Map<String, String> body){
        String no_rek = body.get("no_rek");
        String kode_rahasiaL = body.get("kode_rahasiaL");
        String krb1 = body.get("krb1");
        String krb2 = body.get("krb2");

        Map<String, String> result = nasabahRepositorylmpl.updateCode(no_rek, kode_rahasiaL, krb1, krb2);

        return new ResponseEntity<Map<String, String>>(result, HttpStatus.OK);
    }

    @PostMapping("/nasabah")
    public ResponseEntity<?> createNsb(@RequestBody Map<String, String> body){
        String nama = body.get("nama_lengkap");
        String email = body.get("email");
        String password  = body.get("password");
        String no_ktp = body.get("no_ktp");
        String tgl_lhr = body.get("tgl_lahir");
        String alamat = body.get("alamat");
        String kode_rhs = body.get("kode_rahasia");

        Map<String, String> result = nasabahRepositorylmpl.postNasabah(nama,email,password,no_ktp,tgl_lhr,alamat,kode_rhs);

    return new ResponseEntity<Map<String, String>>(result, HttpStatus.OK);
    }

    @DeleteMapping("nasabah/{id}")
    public boolean delete(@PathVariable String id){
        int nasabahId = Integer.parseInt(id);
        nasabahRepository.delete(nasabahId);
        return true;
    }

    @PostMapping("/rekening")
    public ResponseEntity<?> createRek(@RequestBody Map<String, String> body){
        String idnsb = body.get("id_nasabah");
        String kr = body.get("kode_rahasia");

        Map<String, String> result = nasabahRepositorylmpl.postRek(idnsb,kr);

        return new ResponseEntity<Map<String, String>>(result, HttpStatus.OK);
    }
}