package magangbca.reinald;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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