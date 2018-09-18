package magangbca.reinald;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/nasabah/{id}")
    public Nasabah show(@PathVariable String id){
        int nasabahID = Integer.parseInt(id);
        return nasabahRepository.findOne(nasabahID);
    }

    @PostMapping("/nasabah/update-password")
    public List<String> updatePass(@RequestBody Map<String, String> body){
        int id_nasabah = Integer.parseInt(body.get("id_nasabah"));
        String passwordl = body.get("passwordl");
        String passwordb1 = body.get("passwordb1");
        String passwordb2 = body.get("passwordb2");

        return nasabahRepositorylmpl.updatePassword(id_nasabah, passwordl, passwordb1, passwordb2);
    }

    @PostMapping("nasabah/update-kode-rahasia")
    public List<String> updateCode(@RequestBody Map<String, String> body){
        int id_nasabah = Integer.parseInt(body.get("id_nasabah"));
        String kode_rahasiaL = body.get("kode_rahasiaL");
        String krb1 = body.get("krb1");
        String krb2 = body.get("krb2");

        return nasabahRepositorylmpl.updateCode(id_nasabah, kode_rahasiaL, krb1, krb2);
    }

//    @PostMapping("/nasabah/search")
//    public List<Nasabah> search(@RequestBody Map<String, String> body){
//        String searchTerm = body.get("text");
//        return nasabahRepository.findByTitleContainingOrContentContaining(searchTerm, searchTerm);
//    }

//    @PostMapping("/nasabah")
//    public Nasabah create(@RequestBody Map<String, String> body){
//        String title = body.get("title");
//        String content = body.get("content");
//        String content2 = body.get("content2");
//        return nasabahRepository.save(new Nasabah(title, content, content2));
//    }

//    @PutMapping("/nasabah/{id}")
//    public Blog update(@PathVariable String id, @RequestBody Map<String, String> body){
//        int blogId = Integer.parseInt(id);
//        // getting blog
//        Blog blog = blogRespository.findOne(blogId);
//        blog.setTitle(body.get("title"));
//        blog.setContent(body.get("content"));
//        blog.setContent2(body.get("content2"));
//        return blogRespository.save(blog);
//    }

    @DeleteMapping("nasabah/{id}")
    public boolean delete(@PathVariable String id){
        int nasabahId = Integer.parseInt(id);
        nasabahRepository.delete(nasabahId);
        return true;
    }


}