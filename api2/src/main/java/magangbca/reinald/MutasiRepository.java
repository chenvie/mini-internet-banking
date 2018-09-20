package magangbca.reinald;

import java.time.LocalDate;

public interface MutasiRepository {
    Response getSomeMutasi(Integer firstParameter, LocalDate secondParameter, LocalDate thirdParameter);
}



