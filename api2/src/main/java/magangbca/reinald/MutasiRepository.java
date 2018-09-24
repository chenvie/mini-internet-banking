package magangbca.reinald;

import java.time.LocalDate;

public interface MutasiRepository {
    Response getSomeMutasi(String norek, LocalDate tgl_awal, LocalDate tgl_akhir);
}



