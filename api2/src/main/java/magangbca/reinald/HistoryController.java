package magangbca.reinald;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HistoryController {

    @Autowired
    HistoryRepository historyRepository;

    @GetMapping("/history/{id}/{dt1}/{dt2}")
    public Response show(@PathVariable("id") Integer id, @PathVariable("dt1") String dt1, @PathVariable("dt2")String dt2){
        return historyRepository.getSomeHistory(id,dt1,dt2);
    }
}
