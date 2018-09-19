package magangbca.reinald;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

//import java.util.Date;

@RestController
public class MutasiController {

    @Autowired
    MutasiRepository mutasiRespository;

    @GetMapping("/mutasi/{id}")
    public List<Mutasi> show(@PathVariable Integer id){
        ZoneId z = ZoneId.of("Asia/Jakarta");
        LocalDate dt1 = LocalDate.now(z).plusDays(1);
        LocalDate dt2 = LocalDate.now(z).minusDays(7);
        return mutasiRespository.getSomeMutasi(id,dt1,dt2);
    }

}
