package magangbca.reinald;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MutasiController {

    @Autowired
    MutasiRepository mutasiRespository;

    @GetMapping("/mutasi")
    public List<Mutasi> index(){

      //  Date date1 = new Date(2016,8,13);
       // Date date2 = new Date(2019,8,20);

//        String tgl1 = "2018-08-13";
//        Date asd = "2018-08-13";
//        String tgl2 = "2018-08-20";
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date de1 = formatter.format(tgl1);
//        tgl2 = formatter.format(tgl2);

//        return mutasiRespository.findAllByTujuanEqualsAndTgl_transIsBeforeAndTgl_transIsAfter(2, date1,date2);
//        return mutasiRespository.getSomeMutasi(2, date1,date2);
        return mutasiRespository.getSomeMutasi();
    }

}
